package com.poc.apiorchestrator.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.lambdaworks.redis.RedisConnection;
import com.poc.apiorchestrator.contexts.KafkaDispatcher;
import com.poc.apiorchestrator.contexts.KafkaProperties;
import com.poc.apiorchestrator.contexts.Redis;
import com.poc.apiorchestrator.converters.Mapper;
import com.poc.apiorchestrator.converters.ObjectToGson;
import com.poc.apiorchestrator.dto.Data;
import com.poc.apiorchestrator.dto.Event;
import com.poc.apiorchestrator.rest.email.ClientApiEmailMocked;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ExecutionException;

@Component
public class DocumentService{

    private SignatureService signatureService;
    private RedisConnection redis;
    private ClientApiEmailMocked clientApiEmailMocked;



    public DocumentService( @Lazy SignatureService signatureService, ClientApiEmailMocked clientApiEmailMocked) {
        this.signatureService = signatureService;
        Redis redisClass = new Redis();
        this.redis = redisClass.connect();
        this.clientApiEmailMocked=clientApiEmailMocked;
    }

    public void sendMessage(String idDocument) throws ExecutionException, InterruptedException {
        Event event = new Event();
        UUID uuid = UUID.randomUUID();
        String uuidAsString = uuid.toString();
        event.setId(uuidAsString);
        LocalDateTime localDate = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
        event.setTime(localDate.format(formatter));
        event.setType("br.com.example.orchestrator.cancel");
        event.setSpecVersion("1.0");
        event.setSubject("Command cancel document");
        event.setSource("/product/domain/subdomain/service");
        event.setDataContentType("application/json");
        event.setCorrelationId("");
        Data data = new Data();
        data.setIdDocument(idDocument);
        event.setData(data);
        ObjectToGson objectToGson = new ObjectToGson();
        KafkaDispatcher kafkaDispatcher = new KafkaDispatcher();
        kafkaDispatcher.send(event);
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
        String topic = "br.com.example.document.canceled";
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
                    Thread.sleep(100);
                    if(verifyCancel(event.getData().getIdDocument())){
                        remove(event.getData().getIdDocument());
                        System.out.println("Service finished");
                        clientApiEmailMocked.postEmailandIdDcoument(event.getData());
                    }else {
                        redis.set(event.getData().getIdDocument(), "document");
                    }
                }
            }

        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            consumer.close();
        }
    }



}
