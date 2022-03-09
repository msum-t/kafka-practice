package com.kafka.libary.producer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kafka.libary.domain.Book;
import com.kafka.libary.domain.LibaryEvent;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.TopicPartition;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.SettableListenableFuture;

import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LibarayEventProducerTest {

    @Mock
    KafkaTemplate<Integer,String> kafkaTemplate;

    @Spy
    ObjectMapper objectMapper;


    @InjectMocks
    LibarayEventProducer libarayEventProducer;

    @BeforeEach
    void setUp() {
    }

    @Test
    void sendLibEventApproch2_faliure() throws JsonProcessingException {
        LibaryEvent libaryEvent=LibaryEvent.builder()
                .lEventId(null)
                .book(Book.builder().boookId(1234).bookName("Kafka using spring boot").authorName("chetan").build())
                .book(new Book( ))
                .build();

        SettableListenableFuture future = new SettableListenableFuture<>();
        future.setException(new RuntimeException("Exception calling kakfa:::!!"));

        when(kafkaTemplate.send(isA(ProducerRecord.class))).thenReturn(future);

        assertThrows(Exception.class,()->libarayEventProducer.sendLibEventApproch2(libaryEvent).get());

    }

    @Test
    void sendLibEventApproch2_sucess() throws JsonProcessingException, ExecutionException, InterruptedException {
        LibaryEvent libaryEvent=LibaryEvent.builder()
                .lEventId(null)
                .book(Book.builder().boookId(1234).bookName("Kafka using spring boot").authorName("chetan").build())
                .book(new Book( ))
                .build();

        SettableListenableFuture future = new SettableListenableFuture<>();
        ProducerRecord<Integer,String > producerRecord=new ProducerRecord<>("library-event",libaryEvent.getLEventId(),objectMapper.writeValueAsString(libaryEvent));
        RecordMetadata recordMetadata=new RecordMetadata(new TopicPartition("library-event",1),1,1,342,System.currentTimeMillis(),1,1);
        future.set(new SendResult<Integer,String>(producerRecord,recordMetadata));

        when(kafkaTemplate.send(isA(ProducerRecord.class))).thenReturn(future);
//when
        ListenableFuture<SendResult<Integer, String>> listenableFuture = libarayEventProducer.sendLibEventApproch2(libaryEvent);

        SendResult<Integer, String> integerStringSendResult = listenableFuture.get();

        assert integerStringSendResult.getRecordMetadata().partition()==1;

    }
}