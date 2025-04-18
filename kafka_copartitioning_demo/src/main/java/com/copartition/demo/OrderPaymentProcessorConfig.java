package com.copartition.demo;

import com.copartition.demo.domain.Order;
import com.copartition.demo.domain.OrderPaymentStatus;
import com.copartition.demo.domain.OrderStatus;
import com.copartition.demo.domain.Payment;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.JoinWindows;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Produced;
import org.apache.kafka.streams.kstream.StreamJoined;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.support.serializer.JsonSerde;

import java.time.Duration;

@Configuration
@ConditionalOnProperty(
        name = "feature.kafka-streams.joined-streams",
        havingValue = "true"
)
public class OrderPaymentProcessorConfig {

  private static final Logger log = org.slf4j.LoggerFactory.getLogger(OrderPaymentProcessorConfig.class);
  // Define SerDes for our data types
  private final Serde<String> stringSerde = Serdes.String();
  private final JsonSerde<Order> orderSerde = new JsonSerde<>(Order.class);
  private final JsonSerde<Payment> paymentSerde = new JsonSerde<>(Payment.class);
  private final JsonSerde<OrderPaymentStatus> statusSerde = new JsonSerde<>(OrderPaymentStatus.class);

  private final StreamJoined<String, Order, Payment> joinedStreamSerdes = StreamJoined.with(
      stringSerde,      // Key SerDe (now orderId), Must match the type returned by selectKey()
      orderSerde,       // Left value SerDe (Order)
      paymentSerde      // Right value SerDe (Payment)
  );

  @Bean
  public KStream<String, OrderPaymentStatus> orderPaymentStream(StreamsBuilder builder,
      @Value("${kafka.topics.orders}") String ordersTopic,
      @Value("${kafka.topics.payments}") String paymentsTopic) {

    // Create KStreams from the input topics
    // Note: Initially both streams are keyed by customerId to ensure copartitioning
    // Read from orders topic
    KStream<String, Order> orderStream = builder.stream(
        ordersTopic,
        Consumed.with(stringSerde, orderSerde));
   // Read from payments topic
    KStream<String, Payment> paymentStream = builder.stream(
        paymentsTopic,
        Consumed.with(stringSerde, paymentSerde));

    // Re-key the payment stream using orderId from the payment object
    // This assumes each payment has an orderId field to link it to a specific order
    KStream<String, Payment> paymentByOrderIdStream = paymentStream
        .selectKey((customerId, payment) -> payment.getOrderId());

    // Re-key the order stream to use orderId as the key
    KStream<String, Order> orderByOrderIdStream = orderStream
        .selectKey((customerId, order) -> order.getOrderId());

    // Perform the join between orders and payments using orderId as the key
    // This ensures a specific order matches with its specific payment
    KStream<String, OrderPaymentStatus> joinedStream = orderByOrderIdStream.join(
        paymentByOrderIdStream,
        this::newOrderPaymentStatus,  // Join function
        JoinWindows.ofTimeDifferenceWithNoGrace(Duration.ofMinutes(30)),
        joinedStreamSerdes
    ).peek((key, orderPaymentStatus) -> log.info("Joined orderPaymentStatus: {}", orderPaymentStatus));

    // Process the joined stream (e.g., filter, transform)
    KStream<String, OrderPaymentStatus> processedStream = joinedStream
        .filter((customerId, status) -> status.getStatus() == OrderStatus.PAID)
        .selectKey((customerId, status) -> status.getOrderId()); // Change key to orderId if needed

    // Send joined results to the "order-payment-status" topic
    processedStream.to("order-payment-status", Produced.with(stringSerde, statusSerde));

    return processedStream;
  }

  private OrderPaymentStatus newOrderPaymentStatus(Order order, Payment payment) {
    return OrderPaymentStatus.builder()
        .orderId(order.getOrderId())
        .customerId(order.getCustomerId())
        .orderAmount(order.getAmount())
        .paymentId(payment.getPaymentId())
        .paymentAmount(payment.getAmount())
        .paymentMethod(payment.getMethod())
        .status(order.getAmount().equals(payment.getAmount()) ? OrderStatus.PAID : OrderStatus.AMOUNT_MISMATCH)
        .build();
  }
}
