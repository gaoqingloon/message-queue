package com.bigdata.kafka.consumer;

import org.springframework.kafka.listener.MessageListener;

/**
 *
 */
public interface KafkaConsumerManager {

    /**
     * @param consumer
     * @throws ClassNotFoundException
     */
    void add(KafkaConsumerProperties.KafkaConsumer consumer) throws ClassNotFoundException;

    /**
     * @param consumer
     * @param messageListener
     */
    void add(KafkaConsumerProperties.KafkaConsumer consumer, MessageListener<?, ?> messageListener);

    void remove(String id);

}
