package com.bigdata.kafka.consumer.test;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.kafka.listener.BatchMessageListener;

import java.util.List;

/**
 */
@Slf4j
public class CustomMessageListener implements BatchMessageListener<Object, Object> {

    @Autowired
    private ApplicationEventPublisher publisher;

    /**
     * @see org.springframework.kafka.listener.GenericMessageListener#onMessage(java.lang.Object)
     */
    @Override
    public void onMessage(List<ConsumerRecord<Object, Object>> records) {
        records.forEach(record -> {
            log.info(record.value().toString());
            publisher.publishEvent(record.value());
        });
    }

}
