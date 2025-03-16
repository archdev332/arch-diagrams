package com.cdc.demo;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.regex.Pattern;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafkaStreams;

@Configuration
@Slf4j
@EnableKafkaStreams
public class KafkaStreamsConfig {

  @Autowired
  private ObjectMapper objectMapper;

//  @Bean
  public KStream<String, String> kStream(StreamsBuilder streamsBuilder) {
    // Define source topics (the original CDC topics)
    KStream<String, String> sourceStream = streamsBuilder.stream(
        Pattern.compile("cdc-events\\.public\\..+")
    );

    // Route messages to different target topics
    sourceStream.to(
        (key, value, recordContext) -> {
          // Get the original topic name
          String originalTopic = recordContext.topic();

          // Extract the table name from the original topic
          String tableName = originalTopic.substring(originalTopic.lastIndexOf('.') + 1);

          // Create new topic name with different prefix
          return "cdc-events-other.public." + tableName;
        }
    );

    return sourceStream;
  }

  @Bean
  public KStream<String, String> buildTopology(StreamsBuilder streamsBuilder) {
    // Read from original CDC topics
    KStream<String, String> cdcEvents = streamsBuilder.stream(
        Pattern.compile("cdc-events\\.public\\..+"),
        Consumed.with(Serdes.String(), Serdes.String())
    );

    // Filter out null values first
    KStream<String, String> nonNullEvents = cdcEvents.filter((key, value) -> value != null);

    // Transform the message to extract only the "payload" field
    KStream<String, String> transformedEvents = nonNullEvents.mapValues(value -> {
      try {
        // Parse JSON
        JsonNode valueNode = objectMapper.readTree(value);
        JsonNode payloadNode = valueNode.path("payload");

        // Convert back to string
        return objectMapper.writeValueAsString(payloadNode);
      } catch (Exception e) {
        log.error("Error extracting payload", e);
        return null;
      }
    }).filter((key, value) -> value != null); // Remove null values in case of errors

    transformedEvents.to((key, value, recordContext) -> {
      try {
        // Parse JSON
        JsonNode valueNode = objectMapper.readTree(value);

        // Extract operation type and table from the default schema
        String op = valueNode.path("op").asText();
        String table = valueNode.path("source").path("table").asText();

        // Extract operation type and table from the schema after the ExtractNewRecordState been applied
        //     "payload": {
        //        "id": 22,
        //        "name": "Deva",
        //        "email": "deva@test.com",
        //        "created_at": "2025-03-16T13:14:54.524743Z",
        //        "__deleted": "false",
        //        "__op": "c",
        //        "__table": "users",
        //        "__lsn": 26879016,
        //        "__source_ts_ms": 1742130894526
        //    }
        //  String op = valueNode.path("payload").path("__op").asText();
        //  String table = valueNode.path("payload").path("__table").asText();

        return switch (op) {
          case "c" -> "cdc-creates." + table;
          case "u" -> "cdc-updates." + table;
          case "d" -> "cdc-deletes." + table;
          default -> "cdc-other." + table;
        };
      } catch (Exception e) {
        log.error("Error determining target topic", e);
        return "cdc-errors";
      }
    });

    return cdcEvents;
  }
}
