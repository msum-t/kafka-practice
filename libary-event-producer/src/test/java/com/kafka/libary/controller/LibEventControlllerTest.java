package com.kafka.libary.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kafka.libary.domain.Book;
import com.kafka.libary.domain.LibaryEvent;
import com.kafka.libary.producer.LibarayEventProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

@WebMvcTest(LibEventControlller.class)
@AutoConfigureMockMvc
class LibEventControlllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private LibarayEventProducer libarayEventProducer;

    @Test
    void postLib() throws Exception {
        LibaryEvent libaryEvent = LibaryEvent.builder()
                .lEventId(null)
                .book(Book.builder().boookId(1234).bookName("Kafka using spring boot").authorName("chetan").build())
                //.book(null)
                .build();

        when(libarayEventProducer.sendLibEventApproch2(isA(LibaryEvent.class))).thenReturn(null);
        mockMvc.perform(post("/v1/libraryevent")
                        .content(objectMapper.writeValueAsString(libaryEvent)).contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isCreated())

        ;
    }

    @Test
    void postLib_4xx() throws Exception {
        LibaryEvent libaryEvent = LibaryEvent.builder()
                .lEventId(null)
                .book(Book.builder().boookId(1234).bookName("Kafka using spring boot").authorName("chetan").build())
                .book(new Book())
                .build();

        when(libarayEventProducer.sendLibEventApproch2(isA(LibaryEvent.class))).thenReturn(null);
        mockMvc.perform(post("/v1/libraryevent")
                        .content(objectMapper.writeValueAsString(libaryEvent)).contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().is4xxClientError())
                .andExpect(content().string("book.authorName:::must not be blank,book.bookName:::must not be blank,book.boookId:::must not be null"))

        ;
    }


    @Test
    void postLib_put_ok() throws Exception {
        LibaryEvent libaryEvent = LibaryEvent.builder()
                .lEventId(1234)
                .book(Book.builder().boookId(1234).bookName("Kafka using spring boot").authorName("chetan").build())
                //.book(null)
                .build();

        when(libarayEventProducer.sendLibEventApproch2(isA(LibaryEvent.class))).thenReturn(null);
        mockMvc.perform(put("/v1/libraryevent")
                        .content(objectMapper.writeValueAsString(libaryEvent)).contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isCreated());
    }


    @Test
    void postLib_put_bad() throws Exception {
        LibaryEvent libaryEvent = LibaryEvent.builder()
                .lEventId(null)
                .book(Book.builder().boookId(1234).bookName("Kafka using spring boot").authorName("chetan").build())
                //.book(null)
                .build();

        when(libarayEventProducer.sendLibEventApproch2(isA(LibaryEvent.class))).thenReturn(null);
        mockMvc.perform(put("/v1/libraryevent")
                        .content(objectMapper.writeValueAsString(libaryEvent)).contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

}