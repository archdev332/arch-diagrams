package com.copartition.demo.ktable;

import com.copartition.demo.domain.Order;
import com.copartition.demo.serde.BigDecimalSerde;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.utils.Bytes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.Grouped;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.kstream.TimeWindows;
import org.apache.kafka.streams.kstream.Windowed;
import org.apache.kafka.streams.state.WindowStore;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.support.serializer.JsonSerde;

import java.math.BigDecimal;
import java.time.Duration;

@Configuration
@ConditionalOnProperty(
        name = "feature.kafka-streams.ktable-aggregation",
        havingValue = "true"
)
public class OrderAmountAggregationExample {

  private static final Logger log = org.slf4j.LoggerFactory.getLogger(OrderAmountAggregationExample.class);
  // Define SerDes for our data types
  private final Serde<String> stringSerde = Serdes.String();
  private final JsonSerde<Order> orderSerde = new JsonSerde<>(Order.class);

  @Bean
  public KStream<String, Order> orderStream(StreamsBuilder builder, @Value("${kafka.topics.orders}") String ordersTopic) {
    // Read from orders topic
    KStream<String, Order> orders = builder.stream(
            ordersTopic,
            Consumed.with(stringSerde, orderSerde));

    // Filter null amounts and log
    orders
            .filter((key, order) -> order.getAmount() != null)
            .peek((key, order) -> log.info("Incoming Order: " + order));

    // Windowed aggregation: total amount per customer in 10-minute windows
    TimeWindows window10Min = TimeWindows.ofSizeWithNoGrace(Duration.ofMinutes(10));

    KTable<Windowed<String>, BigDecimal> totalAmountPerCustomerInWindow =
            orders
                    .groupByKey(Grouped.with(stringSerde, orderSerde))
                    .windowedBy(window10Min)
                    .aggregate(
                            () -> BigDecimal.ZERO,
                            (key, order, aggregate) -> aggregate.add(order.getAmount()),
                            Materialized.<String, BigDecimal, WindowStore<Bytes, byte[]>>as("customer-amount-store") // local store name
                                    .withKeySerde(stringSerde)
                                    .withValueSerde(new BigDecimalSerde())
                    );

    // Output to log (could also be sent to another topic)
    totalAmountPerCustomerInWindow
            .toStream()
            .peek((windowedKey, total) -> {
              String customerId = windowedKey.key();
              String window = "[" +
                      windowedKey.window().startTime() + " - " +
                      windowedKey.window().endTime() + "]";
              log.info("Windowed Total for key " + customerId + " " + window + ": amount = " + total);
            });

    return orders;
  }

}

// FAQ:

// 1. Where the windowed values (state) are actually stored
// - The results of the windowed aggregation (e.g., total amount per customer per window) are stored in a local state store, backed by a Kafka changelog topic

// 2. Why do I need a custom BigDecimalSerde for Kafka Streams? Doesn’t Kafka support it out of the box?
// - Kafka’s built-in Serdes include only basic types String, Integer, Long, Double, ByteArray, etc

// 3. How long values will be stored in KTable ?
// - It depends on whether the KTable is windowed or not
//     Non-windowed KTable (e.g. .groupBy().aggregate() without .windowedBy())
//          It acts like a changelog table — always stores the latest value per key.
//          Backed by a local state store + changelog topic.
//          Retention:
//          Values are retained as long as the app is running.
//          Changelog topic retention is typically infinite unless configured otherwise.
//          You can configure cleanup via the changelog topic settings
//     Windowed KTable (like your example using .windowedBy(...))
//          Now this is more nuanced — because each key has multiple versions per time window.
//          By default, windowed state stores retain values for: windowSize + gracePeriod
//          This defines the retention period for each window.
//
//          TimeWindows.of(Duration.ofMinutes(10)).grace(Duration.ofMinutes(1))
//          Kafka Streams will retain each window for 11 minutes total.
//          After that, the window and its data are removed from local state and changelog topic.

// 4. What means "Opening store customer-amount-store.1744824000000 in regular mode" in log
// - customer-amount-store is the name of your state store
//   1744824000000 is a timestamp in epoch milliseconds, representing the start time of a specific window
//   in regular mode - Kafka Streams can open stores in: Regular mode: for normal reads/writes during stream processing
//   and Restore mode: when it's rebuilding the store from a changelog (e.g., on restart or rebalance) (Finished restoring changelog order-payment-processor-customer-amount-store-changelog-2)

// 5. Why kafka streams using RocksDB instead HashMap ?
//        Feature	                            HashMap	    RocksDB
//        In-memory only	                       ✅	       ❌
//        Disk-backed (persistent)	               ❌	       ✅
//        Handles large state	                   ❌	       ✅
//        Supports windowed aggregations           ❌	       ✅
//        Supports failover/recovery               ❌	       ✅
//        Off-heap storage (low GC)	               ❌	       ✅

// Kafka Streams supports in-memory stores, like Stores.inMemoryKeyValueStore("my-store")

// 6. Can i use Ktable for storing global state ?
// - Yes, you can store global state in Kafka Streams — but not with a regular KTable.
// For truly global state, you’ll want to use a GlobalKTable. Let me break down when and how to use it vs. KTable, and what the tradeoffs are

// How GlobalKTable Works Internally
//    It consumes the entire topic (all partitions).
//    Every Kafka Streams instance subscribes to all partitions of the topic, not just one like in KTable.
//    Records are applied in the order they arrive — but arrival timing may differ across instances.
//    Backed by a local read-only state store on disk (RocksDB by default).
//    GlobalKTable is Eventually Consistent

// How It Works (Step-by-Step)
//    1. Reads from the source topic
//        All app instances subscribe to all partitions (unlike KTable, which only reads assigned ones).
//        Kafka Streams uses separate consumers (not part of the main processing topology) to load this data.
//    2. Builds a local state store
//        Each record updates a local RocksDB store.
//        The store holds the latest value for each key.
//        Store is typically read-only from your app logic.
//    3. Applies updates sequentially
//        All changes are processed in offset order.
//        Keys are updated or deleted as new events arrive.
//    4. Used for joins (KStream–GlobalKTable)
//        Every stream processor has full access to the table.
//        Allows lookups across all keys, not just local partitioned ones.
//        No need to repartition the stream.
//    Recovery Behavior
//        On restart, the GlobalKTable will replay the entire topic to rebuild the local store.
//        No changelog topic needed — the original topic is the changelog.
//        Each instance holds its own copy of the state — not sharded.

// 7. Is a KTable unique per partition or application instance?
// - A KTable is partitioned by key, and each Kafka Streams instance holds the partitions it is assigned.
// So the data in a KTable is sharded per partition, and spread across instances in a Kafka Streams app
//      Example
//        Let’s say your KTable is built from a topic customers-topic, and it's partitioned by customer ID.
//
//        Partition 0: customerId=123, customerId=456
//        Partition 1: customerId=789, customerId=abc
//        Partition 2: customerId=xyz, customerId=999
//
//        If App A owns partitions 0 & 1:
//
//        App Instance	    Has Keys
//        App A	            123, 456, 789, abc
//        App B	            xyz, 999
//        So if a stream on App A tries to join with a customer ID from partition 2,
//        it won’t find it — unless the key was migrated or it was a GlobalKTable

// 8. Can RocksDB have a key/value not yet written to the changelog topic?
//        Yes — temporarily, in some edge cases:
//        Kafka Streams updates RocksDB (write to local store).
//        It schedules a record to be sent to the changelog topic.
//        This record is sent out during the next flush/commit cycle (based on commit.interval.ms).
//        If the app crashes before committing, the changelog won’t have that change — but RocksDB will (until the process dies)

// Use cases:

//  1. User Profile Updates (Latest State)
//        🧠 Use case: Maintain the latest profile information for each user across multiple systems (e.g., name, email, subscription tier).
//
//        Source: user-profile-updates topic
//        KTable holds the latest user info per userId
//        Use it to enrich clickstream or transaction events from a KStream (e.g., user-clicks)
//        👉 Stream-table join to add user info to real-time events.
//
//  2. Inventory Management / Product Catalog
//        🛒 Use case: Keep a real-time, always-updated view of inventory levels or product data.
//
//        Source: product-updates topic (e.g., price, name, stock level)
//        KTable<String, Product> holds the latest product state per productId
//        Used for:
//        Order validation
//        Enrichment of purchase events
//        Detecting inventory mismatches

//  3. Account Balance Tracking
//        💸 Use case: Maintain real-time account balances based on incoming transactions.
//
//        Source: transactions as KStream
//        Use aggregation on KGroupedStream to produce a KTable<String, BigDecimal>
//        View updated balances in real-time
//        Queryable for APIs or dashboards

//  4. Order Status Lifecycle
//        📦 Use case: Keep track of the latest status of orders — e.g., placed → processed → shipped → delivered.
//
//        Source: order-events topic
//        Key = orderId, value = OrderStatus
//        KTable<String, OrderStatus> always holds the latest status per order
//        Enables:
//        Fast lookups
//        Real-time order tracking dashboards
//        Joining with delivery events
//  5. Real-Time Fraud Detection State
//        🕵️ Use case: Maintain context/state per user or transaction for detecting fraud patterns.
//
//        Store recent activity like login locations, spending patterns, or device info
//        Update KTable with behavior summaries
//        Use to join with new events and apply fraud-detection rules
//        Example: If the KTable shows a user just logged in from Germany, and now a transaction comes from Nigeria — red flag 🚨
//
//        Bonus: Visualization / Analytics
//        📊 KTables are also heavily used to power real-time dashboards, with:
//
//        Aggregated metrics
//        Windowed trends
//        Rolling counters