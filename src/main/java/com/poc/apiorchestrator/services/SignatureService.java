package com.poc.apiorchestrator.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.lambdaworks.redis.RedisConnection;
import com.poc.apiorchestrator.contexts.KafkaProperties;
import com.poc.apiorchestrator.contexts.Redis;
import com.poc.apiorchestrator.converters.Mapper;
import com.poc.apiorchestrator.dto.Event;
import com.poc.apiorchestrator.rest.email.ClientApiEmailMocked;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;


import java.nio.charset.StandardCharsets;
import java.time.Duration;;
import java.util.Arrays;
import java.util.Objects;

@Component
public class SignatureService{

    private DocumentService documentService;
    private ClientApiEmailMocked clientApiEmailMocked;

    private RedisConnection redis = new Redis().connect();


    public SignatureService(@Lazy DocumentService documentService, ClientApiEmailMocked clientApiEmailMocked) {
        this.documentService = documentService;
        this.clientApiEmailMocked=clientApiEmailMocked;
    }

    public Boolean verifyCancel(String idDocument){
        String result = (String) redis.get(idDocument);
        if(Objects.isNull(result) || result.equals("")){
            return false;
        }
        return true;
    }

    public void remove(String idDocument){
       this.redis.del(idDocument);
    }

    public void listening(){
        KafkaProperties kafkaProperties = new KafkaProperties();
        String topic = "br.com.example.signature.canceled";
        final Consumer<String, String> consumer = new org.apache.kafka.clients.consumer.KafkaConsumer<>(kafkaProperties.getProperties());
        consumer.subscribe(Arrays.asList(topic));
        try {
            while (true){
                ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(100));
                for (ConsumerRecord<String, String> record : records) {
                    String value = record.value();
                    Mapper objectMapper = new Mapper();
                    Event event = objectMapper.getMapper()
                            .readValue(new String(value.getBytes(StandardCharsets.UTF_8)), Event.class);
                    Thread.sleep(50);
                    if(verifyCancel(event.getData().getIdDocument())){
                        remove(event.getData().getIdDocument());
                        System.out.println("Service finished");
                        clientApiEmailMocked.postEmailandIdDcoument(event.getData());
                    }else {
                        redis.set(event.getData().getIdDocument(), "signature");
                    }
                }

            }
        } catch (JsonMappingException e) {
            throw new RuntimeException(e);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            consumer.close();
        }
    }

}
