package com.cdc.demo;

import static java.util.Objects.nonNull;

import com.cdc.demo.event.UserEvent;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class DebeziumKafkaConsumer {

  private final ObjectMapper objectMapper;

  @KafkaListener(topics = "cdc-events.public.accounts", groupId = "debezium-group")
  public void consume(ConsumerRecord<String, String> record) {
    try {
      // Check for null value (tombstone message)
      String value = record.value();
      if (value == null) {
        log.info("Received tombstone message with key: {}", record.key());
        return; // Skip processing for tombstone messages
      }

      // Парсим JSON
      JsonNode jsonNode = objectMapper.readTree(record.value());

      // Достаем payload
      JsonNode payload = jsonNode.get("payload");
      if (payload == null) {
        log.warn("Received event without payload: {}", record.value());
        return;
      }

      // Извлекаем нужные поля
      JsonNode before = payload.get("before");
      JsonNode after = payload.get("after");
      String operation = payload.has("op") ? payload.get("op").asText() : "unknown";

      // Логируем только нужные данные
      log.info("CDC Event - op: {}, before: {}, after: {}", operation, before, after);

    } catch (Exception e) {
      log.error("Failed to parse CDC event: {}", record.value(), e);
    }
  }

  @KafkaListener(topics = "cdc-events.public.users", containerFactory = "userEventFactory")
  public void consume(List<ConsumerRecord<String, UserEvent>> messages) {
    System.out.println(messages.size());
    messages.stream()
        .map(ConsumerRecord::value)
        .filter(Objects::nonNull)
        .forEach(this::logEvent);
  }

  private void logEvent(UserEvent payload) {
    log.info("Received CDC event: {}, before: {}, after: {}",
        payload.getAction(),
        nonNull(payload.getBefore()) ? payload.getBefore() : "null",
        nonNull(payload.getAfter()) ? payload.getAfter() : "null");
  }
}
