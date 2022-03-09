package junit.com.kafka.lib.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kafka.libary.controller.LibEventControlller;
import com.kafka.libary.domain.Book;
import com.kafka.libary.domain.LibaryEvent;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@WebMvcTest(LibEventControlller.class)
@AutoConfigureMockMvc
class LibEventControlllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void postLib() throws Exception {
        LibaryEvent libaryEvent=LibaryEvent.builder()
                .lEventId(null)
                .book(Book.builder().boookId(1234).bookName("Kafka using spring boot").authorName("chetan").build())
                .build();

        mockMvc.perform(post("v1/libraryevent")
                .content(objectMapper.writeValueAsString(libaryEvent)).contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isCreated())

        ;
    }
}