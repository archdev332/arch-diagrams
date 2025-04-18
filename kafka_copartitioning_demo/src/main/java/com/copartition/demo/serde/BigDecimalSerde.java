package com.copartition.demo.serde;

import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serializer;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;

public class BigDecimalSerde implements Serde<BigDecimal> {
    @Override
    public Serializer<BigDecimal> serializer() {
        return (topic, data) ->
                data == null ? null : data.toPlainString().getBytes(StandardCharsets.UTF_8);
    }

    @Override
    public Deserializer<BigDecimal> deserializer() {
        return (topic, data) ->
                data == null ? null : new BigDecimal(new String(data, StandardCharsets.UTF_8));
    }
}

