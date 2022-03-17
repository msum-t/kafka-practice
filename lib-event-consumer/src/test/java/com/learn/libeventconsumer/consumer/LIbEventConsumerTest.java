package com.learn.libeventconsumer.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.learn.libeventconsumer.service.LibEventService;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.config.KafkaListenerEndpointRegistrar;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.MessageListenerContainer;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.ContainerTestUtils;
import org.springframework.test.context.TestPropertySource;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@EmbeddedKafka(topics = {"library-event"},partitions = 3)
@TestPropertySource(properties = {"spring.kafka.producer.bootstrap-servers=${spring.embedded.kafka.brokers}"
,"spring.kafka.consumer.bootstrap-servers=${spring.embedded.kafka.brokers}"})
class LIbEventConsumerTest {

    @Autowired
    EmbeddedKafkaBroker kafkaBroker;


    @Autowired
    KafkaTemplate<Integer,String> kafkaTemplate;

    //@SpyBean(proxyTargetAware = false)
    @MockBean
    LIbEventConsumer lIbEventConsumer;

    //@SpyBean(proxyTargetAware = false)
    @MockBean
    LibEventService service;


    @Autowired
    KafkaListenerEndpointRegistry endPointReg;

    @BeforeEach
    void setUp() {
        for (MessageListenerContainer container: endPointReg.getAllListenerContainers()) {
            ContainerTestUtils.waitForAssignment(container,kafkaBroker.getPartitionsPerTopic());

        }

    }

    @Test
    void onMsg() throws ExecutionException, InterruptedException, JsonProcessingException {
       //given
        String excepted="{\"libraryEventType\":\"NEW\",\"book\":{\"boookId\":1234,\"bookName\":\"Kafka using spring boot\",\"authorName\":\"chetan\"},\"leventId\":null}";


        kafkaTemplate.sendDefault(excepted).get();

        //when
        CountDownLatch countDownLatch=new CountDownLatch(1);
        countDownLatch.await(5, TimeUnit.SECONDS);

        //then
        verify(lIbEventConsumer,times(1)).onMsg(isA(ConsumerRecord.class));
        verify(service,times(1)).processConsumer(isA(ConsumerRecord.class));

    }

    @Test
    void onMsgUpdate() throws ExecutionException, InterruptedException, JsonProcessingException {
        //given
        String excepted="{\"libraryEventType\":\"NEW\",\"book\":{\"boookId\":1234,\"bookName\":\"Kafka using spring boot\",\"authorName\":\"chetan\"},\"leventId\":null}";


        kafkaTemplate.sendDefault(excepted).get();

        //when
        CountDownLatch countDownLatch=new CountDownLatch(1);
        countDownLatch.await(5, TimeUnit.SECONDS);

        //then
        verify(lIbEventConsumer,times(1)).onMsg(isA(ConsumerRecord.class));
        verify(service,times(1)).processConsumer(isA(ConsumerRecord.class));

    }




}