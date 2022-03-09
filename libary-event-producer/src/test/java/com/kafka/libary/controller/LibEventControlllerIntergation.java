package com.kafka.libary.controller;

import com.kafka.libary.domain.Book;
import com.kafka.libary.domain.LibaryEvent;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.serialization.IntegerDeserializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.KafkaUtils;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.context.TestPropertySource;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@EmbeddedKafka(topics = {"library-event"},partitions = 3)
@TestPropertySource(properties = {"spring.kafka.producer.bootstrap-servers=${spring.embedded.kafka.brokers}",
        "spring.kafka.admin.properties.bootstrap.servers=${spring.embedded.kafka.brokers}"})
class LibEventControlllerIntergation {

    @Autowired
    TestRestTemplate testRestTemplate;

    @Autowired
    private EmbeddedKafkaBroker  kafkaBroker;

    private Consumer<Integer,String> consumer;

    @BeforeEach
    void setUp() {
        Map<String,Object> hasMap=new HashMap<>(KafkaTestUtils.consumerProps("group1","true",kafkaBroker));
        consumer= new DefaultKafkaConsumerFactory<>(hasMap, IntegerDeserializer::new, StringDeserializer::new).createConsumer();
        kafkaBroker.consumeFromAllEmbeddedTopics(consumer);


    }

    @AfterEach
    void tearDown() {
        consumer.close();
    }

    @Test
    @Timeout(1)
    void postLibEvent() throws InterruptedException {
        LibaryEvent libaryEvent=LibaryEvent.builder()
                        .lEventId(null)
                .book(Book.builder().boookId(1234).bookName("Kafka using spring boot").authorName("chetan").build())
                        .build();
        HttpHeaders httpHeaders=new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<LibaryEvent> libaryEventHttpEntity=new HttpEntity<>(libaryEvent,httpHeaders);

        ResponseEntity<LibaryEvent> responseEntity = testRestTemplate.exchange("/v1/libraryevent",
                HttpMethod.POST,
                libaryEventHttpEntity, LibaryEvent.class);

        assertEquals(HttpStatus.CREATED,responseEntity.getStatusCode());
        ConsumerRecord<Integer, String> singleRecord = KafkaTestUtils.getSingleRecord(consumer, "library-event");
        System.out.println(singleRecord.value());
        //Thread.sleep(3000);
        String excepted="{\"libraryEventType\":\"NEW\",\"book\":{\"boookId\":1234,\"bookName\":\"Kafka using spring boot\",\"authorName\":\"chetan\"},\"leventId\":null}";
        String value = singleRecord.value();
        assertEquals(excepted,value);

    }

    @Test
    @Timeout(1)
    void postLibEvent_Put() throws InterruptedException {
        LibaryEvent libaryEvent=LibaryEvent.builder()
                .lEventId(123)
                .book(Book.builder().boookId(1234).bookName("Kafka using spring boot").authorName("chetan").build())
                .build();
        HttpHeaders httpHeaders=new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<LibaryEvent> libaryEventHttpEntity=new HttpEntity<>(libaryEvent,httpHeaders);

        ResponseEntity<LibaryEvent> responseEntity = testRestTemplate.exchange("/v1/libraryevent",
                HttpMethod.PUT,
                libaryEventHttpEntity, LibaryEvent.class);

        assertEquals(HttpStatus.CREATED,responseEntity.getStatusCode());
        ConsumerRecord<Integer, String> singleRecord = KafkaTestUtils.getSingleRecord(consumer, "library-event");
        System.out.println(singleRecord.value());
        //Thread.sleep(3000);
        String excepted="{\"libraryEventType\":\"UPDATE\",\"book\":{\"boookId\":1234,\"bookName\":\"Kafka using spring boot\",\"authorName\":\"chetan\"},\"leventId\":123}";
        String value = singleRecord.value();
        assertEquals(excepted,value);

    }
}