package com.kafka.libary.producer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kafka.libary.domain.LibaryEvent;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.Header;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

import java.util.List;

@Configuration
@Slf4j
public class LibarayEventProducer {

    @Autowired
    private KafkaTemplate<Integer,String> kafkaTemplate;

    @Autowired
    private ObjectMapper objectMapper;


    public void sendLibEvent(LibaryEvent libaryEvent) throws JsonProcessingException {
        ListenableFuture<SendResult<Integer,String>>  listenableFuture=kafkaTemplate.sendDefault(libaryEvent.getLEventId(),objectMapper.writeValueAsString(libaryEvent));
        listenableFuture.addCallback(new ListenableFutureCallback<SendResult<Integer, String>>() {
            @Override
            public void onFailure(Throwable ex) {
                try {
                    handlerFailure(libaryEvent.getLEventId(),objectMapper.writeValueAsString(libaryEvent),ex);
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onSuccess(SendResult<Integer, String> result) {
                try {
                    handlerSuccess(libaryEvent.getLEventId(),objectMapper.writeValueAsString(libaryEvent),result);
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                }

            }
        });

    }

    public void sendLibEventSyncronous(LibaryEvent libaryEvent) throws JsonProcessingException,Exception {
        kafkaTemplate.sendDefault(libaryEvent.getLEventId(),objectMapper.writeValueAsString(libaryEvent)).get();

    }
    public ListenableFuture<SendResult<Integer,String>> sendLibEventApproch2(LibaryEvent libaryEvent) throws JsonProcessingException {
        ProducerRecord<Integer,String> producerRecord=buildProducerRecord(libaryEvent.getLEventId(),objectMapper.writeValueAsString(libaryEvent),"library-event");
        ListenableFuture<SendResult<Integer,String>>  listenableFuture=kafkaTemplate.send(producerRecord);
        listenableFuture.addCallback(new ListenableFutureCallback<SendResult<Integer, String>>() {
            @Override
            public void onFailure(Throwable ex) {
                try {
                    handlerFailure(libaryEvent.getLEventId(),objectMapper.writeValueAsString(libaryEvent),ex);
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onSuccess(SendResult<Integer, String> result) {
                try {
                    handlerSuccess(libaryEvent.getLEventId(),objectMapper.writeValueAsString(libaryEvent),result);
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                }

            }
        });
        return listenableFuture;

    }

    private ProducerRecord<Integer, String> buildProducerRecord(Integer lEventId, String writeValueAsString, String s) {
        List<Header> headerList=List.of(new RecordHeader("event-source","scanner".getBytes()));
        return new ProducerRecord<>(s,null,lEventId,writeValueAsString,headerList);
    }


    private void handlerSuccess(Integer id, String value, SendResult<Integer, String> sendResult){
        log.info("Message send Successfully!! for : {} and the value {} , partition  is {}",id,value,sendResult.getRecordMetadata().partition());
    }

    private void handlerFailure(Integer id, String value, Throwable ex){
        log.error("Error sending msg with exception ::{}",ex.getMessage());
        try {
            throw  ex;
        }
        catch (Throwable throwable){
            throwable.printStackTrace();
        }
    }
}
