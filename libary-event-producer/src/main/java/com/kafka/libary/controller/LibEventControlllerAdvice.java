package com.kafka.libary.controller;

import com.kafka.libary.domain.LibaryEvent;
import com.kafka.libary.domain.LibraryEventType;
import com.kafka.libary.producer.LibarayEventProducer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@ControllerAdvice
@Slf4j
public class LibEventControlllerAdvice {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?>    handReqValidation(MethodArgumentNotValidException ex){
        List<FieldError> errorList=ex.getBindingResult().getFieldErrors();
        String errorMsg=errorList.stream().
                map(fieldError -> fieldError.getField()+":::"+fieldError.getDefaultMessage()).sorted().collect(Collectors.joining(","));
        log.info("error msg::{}"+errorMsg);
        return new ResponseEntity<>(errorMsg,HttpStatus.BAD_REQUEST);

    }


}
