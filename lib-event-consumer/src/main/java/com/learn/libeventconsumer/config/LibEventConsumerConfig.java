package com.learn.libeventconsumer.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.dao.RecoverableDataAccessException;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.boot.autoconfigure.kafka.ConcurrentKafkaListenerContainerFactoryConfigurer;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.listener.*;
import org.springframework.util.backoff.FixedBackOff;

import java.util.*;

@EnableKafka
@Configuration
@Slf4j
public class LibEventConsumerConfig {

    @Bean
    ConcurrentKafkaListenerContainerFactory<?, ?> kafkaListenerContainerFactory(
            ConcurrentKafkaListenerContainerFactoryConfigurer configurer,
            ConsumerFactory<Object, Object> kafkaConsumerFactory) {
        ConcurrentKafkaListenerContainerFactory<Object, Object> factory = new ConcurrentKafkaListenerContainerFactory<>();
        configurer.configure(factory, kafkaConsumerFactory);
        // factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL);
        //factory.setConcurrency(3);
        /*
        (consumerRecord, exception) ->
                log.info("Error exception in consumer config {} and record is {}",exception,consumerRecord)
        )
         */
        //factory.setCommonErrorHandler(new CommonLoggingErrorHandler());
        //factory.setCommonErrorHandler(new DefaultErrorHandler(new FixedBackOff(1000, 2)));
        factory.setCommonErrorHandler(new DefaultErrorHandler((consumerRecord, exception) -> {
            DefaultErrorHandler defaultErrorHandler = new DefaultErrorHandler();

            //defaultErrorHandler.addNotRetryableExceptions(IllegalArgumentException.class);
            defaultErrorHandler.setClassifications(exception(), false);

            log.info("error in handling exception {} and recived record {}", exception, consumerRecord);
        }, new FixedBackOff(3000, 4)));

        //factory.setCommonErrorHandler(errorHandler());
//        factory.setRecoveryCallback(context -> {
//            Arrays.asList(context.attributeNames()).forEach(System.out::println);
//            if (context.getLastThrowable().getCause() instanceof RecoverableDataAccessException) {
//                log.info("recovery");
//
//                Arrays.asList(context.attributeNames()).forEach(System.out::println);
//            } else {
//                log.info("non recovery");
//                throw new RuntimeException(context.getLastThrowable().getMessage());
//            }
//            return null;
//        });
        return factory;
    }

    private CommonErrorHandler errorHandler() {
        DefaultErrorHandler defaultErrorHandler = new DefaultErrorHandler(new FixedBackOff(1000, 2));
        //defaultErrorHandler.addNotRetryableExceptions(IllegalArgumentException.class);
        defaultErrorHandler.setClassifications(exception(), false);
        //defaultErrorHandler.removeNotRetryableException(IllegalArgumentException.class);
        return defaultErrorHandler;
    }

    private Map<Class<? extends Throwable>, Boolean> exception() {
        Map<Class<? extends Throwable>, Boolean> hasMap = new HashMap<>();
        hasMap.put(RecoverableDataAccessException.class, true);
        hasMap.put(IllegalArgumentException.class, false);
        return hasMap;
    }


}
