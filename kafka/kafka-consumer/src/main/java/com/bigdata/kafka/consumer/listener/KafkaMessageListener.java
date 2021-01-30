package com.bigdata.kafka.consumer.listener;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.listener.BatchMessageListener;

import java.util.List;

/**
 *
 */
@Slf4j
public class KafkaMessageListener implements BatchMessageListener<Object, Object> {

    /**
     * @see org.springframework.kafka.listener.GenericMessageListener#onMessage(java.lang.Object)
     */
    @Override
    public void onMessage(List<ConsumerRecord<Object, Object>> records) {
        records.forEach(record -> {
            log.info(record.value().toString());
        });
    }

}
