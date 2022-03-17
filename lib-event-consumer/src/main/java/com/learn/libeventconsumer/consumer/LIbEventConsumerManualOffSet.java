package com.learn.libeventconsumer.consumer;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.listener.AcknowledgingMessageListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

//@Component
@Slf4j
public class LIbEventConsumerManualOffSet implements AcknowledgingMessageListener<Integer,String> {

    @KafkaListener(topics = "library-event")
    public void onMsg(ConsumerRecord<Integer,String> consumerRecord){

        log.info("ConsumerRecord:: {}",consumerRecord);

    }

    @Override
    @KafkaListener(topics = "library-event")
    public void onMessage(ConsumerRecord<Integer, String> consumerRecord, Acknowledgment acknowledgment) {
        log.info("ConsumerRecord:: {}",consumerRecord);
        acknowledgment.acknowledge();
    }
}
