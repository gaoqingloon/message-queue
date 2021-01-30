package com.bigdata.kafka.consumer.test;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.listener.MessageListener;

/**
 *
 */
@Slf4j
public class SingleMessageListener implements MessageListener<Object, Object> {

    /**
     * @see org.springframework.kafka.listener.GenericMessageListener#onMessage(java.lang.Object)
     */
    @Override
    public void onMessage(ConsumerRecord<Object, Object> data) {
        log.info(this + ":" + data.value().toString());
    }

}
