package com.copartition.demo;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.header.Headers;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class KafkaConsumerService {

  @KafkaListener(topics = "${kafka.topics.orders}")
  public void consumeOrdersMessage(ConsumerRecord<String, String> record) {
    printEvent(record);
  }

  @KafkaListener(topics = "${kafka.topics.payments}")
  public void consumePaymentsMessage(ConsumerRecord<String, String> record) {
    printEvent(record);
  }

  private void printEvent(ConsumerRecord<String, String> record) {
    String key = record.key();
    String value = record.value();
    long offset = record.offset();
    int partition = record.partition();
    String topic = record.topic();
    Headers headers = record.headers();

    // Process the message with all this metadata
    System.out.println("Received message: " +
        " key: " + key +
        " value: " + value +
        " from topic: " + topic +
        " partition: " + partition +
        " offset: " + offset);
  }
}
