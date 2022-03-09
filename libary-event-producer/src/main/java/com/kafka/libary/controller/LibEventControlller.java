package com.kafka.libary.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.kafka.libary.domain.LibaryEvent;
import com.kafka.libary.domain.LibraryEventType;
import com.kafka.libary.producer.LibarayEventProducer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@Slf4j
public class LibEventControlller {

    @Autowired
    private LibarayEventProducer libarayEventProducer;

    @PostMapping("v1/libraryevent")
    public ResponseEntity<LibaryEvent> saveEvent(@RequestBody @Valid LibaryEvent libaryEvent) throws Exception {
        log.info("Before send library event!!");
        // approach 1
        //libraryEventProducer.sendLibEvent(libaryEvent);
        // approach 2
        //libraryEventProducer.sendLibEventSyncronous(libaryEvent);
        // approach 3
        libaryEvent.setLibraryEventType(LibraryEventType.NEW);
        libarayEventProducer.sendLibEventApproch2(libaryEvent);
        log.info("After send library event!!");
        return ResponseEntity.status(HttpStatus.CREATED).body(libaryEvent);
    }

    @PutMapping("/v1/libraryevent")
    public ResponseEntity<LibaryEvent> putEvent(@RequestBody @Valid LibaryEvent libaryEvent) throws Exception {
        log.info("Before send library event!!");
        // approach 1
        //libraryEventProducer.sendLibEvent(libaryEvent);
        // approach 2
        //libraryEventProducer.sendLibEventSyncronous(libaryEvent);
        // approach 3
        System.out.println(libaryEvent.getLEventId());
        if(libaryEvent.getLEventId()==null){
            return ResponseEntity.badRequest().build();
        }
        libaryEvent.setLibraryEventType(LibraryEventType.UPDATE);
        libarayEventProducer.sendLibEventApproch2(libaryEvent);
        log.info("After send library event!!");
        return ResponseEntity.status(HttpStatus.CREATED).body(libaryEvent);
    }
}
