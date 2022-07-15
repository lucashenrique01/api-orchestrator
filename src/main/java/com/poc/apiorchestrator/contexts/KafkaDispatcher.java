package com.poc.apiorchestrator.contexts;

import com.poc.apiorchestrator.converters.ObjectToGson;
import com.poc.apiorchestrator.dto.Event;
import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;

import java.io.Closeable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class KafkaDispatcher implements Closeable {

    private final KafkaProducer<String, String> producer;
    private KafkaProperties kafkaProperties;

    public KafkaDispatcher() {
        this.kafkaProperties = new KafkaProperties();
        this.producer = new KafkaProducer<>(this.kafkaProperties.getPropertiesDispatcher());
    }

    public void send(Event payload) throws ExecutionException, InterruptedException {
        Future<RecordMetadata> future = sendAsync(payload);
        future.get();
    }

    public Future<RecordMetadata> sendAsync(Event payload) {
        ObjectToGson objectToGson = new ObjectToGson();
        ProducerRecord record = new ProducerRecord<>(payload.getType(),
                payload.getId(),
                objectToGson.eventToJson(payload));
        Callback callback = (data, ex) -> {
            if (ex != null) {
                ex.printStackTrace();
                return;
            }
            System.out.println("sucesso enviando " + data.topic() + ":::partition " + data.partition() + "/ offset " + data.offset() + "/ timestamp " + data.timestamp());
        };
        return producer.send(record, callback);
    }

    @Override
    public void close() {
        producer.close();
    }
}