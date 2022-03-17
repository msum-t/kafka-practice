package com.learn.libeventconsumer.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.learn.libeventconsumer.entity.LibaryEvent;
import com.learn.libeventconsumer.repo.LibEventRepo;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.RecoverableDataAccessException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Slf4j
public class LibEventService {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private LibEventRepo libEventRepo;


    public void processConsumer(ConsumerRecord<Integer,String> consumerRecord) throws JsonProcessingException {
        LibaryEvent libaryEvent = objectMapper.readValue(consumerRecord.value(), LibaryEvent.class);
        log.info("lib event::{}",libaryEvent);

        if(libaryEvent.getLEventId()!=null && libaryEvent.getLEventId()==0){
            throw new RecoverableDataAccessException("Temprary network issue");
        }


        switch (libaryEvent.getLibraryEventType()){
            case NEW:
                //save
                save(libaryEvent);
                break;
            case UPDATE:
                //update operation
                validate(libaryEvent.getLEventId());
                save(libaryEvent);
                break;
            default:
                log.info("invalid log info!!");

        }
    }

    private void validate(Integer lEventId) {
        if (lEventId==null){
            throw new IllegalArgumentException("Lib event id is missing");
        }
        Optional<LibaryEvent> byId = libEventRepo.findById(lEventId);

        if(!byId.isPresent()){
            throw new IllegalArgumentException("not valid lib event");
        }

        log.info("validation done for {}",byId.get());

    }

    private void save(LibaryEvent libaryEvent) {
        libaryEvent.getBook().setLibaryEvent(libaryEvent);

        libEventRepo.save(libaryEvent);
        log.info("success persist lib event {}",libaryEvent);
    }

}
