package com.copartition.demo.ktable.api;

import org.apache.kafka.streams.StoreQueryParameters;
import org.apache.kafka.streams.state.QueryableStoreTypes;
import org.apache.kafka.streams.state.ReadOnlyWindowStore;
import org.apache.kafka.streams.state.WindowStoreIterator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.config.StreamsBuilderFactoryBean;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Map;

@RestController
@RequestMapping("/totals")
public class CustomerQueryController {

    @Autowired
    private StreamsBuilderFactoryBean factoryBean;

    @GetMapping("/{customerId}")
    public ResponseEntity<?> getTotal(
            @PathVariable String customerId,
            @RequestParam Instant from,
            @RequestParam Instant to) {

        ReadOnlyWindowStore<String, BigDecimal> store = factoryBean.getKafkaStreams().store(
                StoreQueryParameters.fromNameAndType("customer-amount-store", QueryableStoreTypes.windowStore())
        );

        BigDecimal total = BigDecimal.ZERO;
        try (WindowStoreIterator<BigDecimal> results = store.fetch(customerId, from, to)) {
            while (results.hasNext()) {
                total = total.add(results.next().value);
            }
        }

        return ResponseEntity.ok(Map.of("customerId", customerId, "total", total));
    }

}
