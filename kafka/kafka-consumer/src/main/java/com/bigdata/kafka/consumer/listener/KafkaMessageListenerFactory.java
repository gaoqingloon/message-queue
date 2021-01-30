package com.bigdata.kafka.consumer.listener;

import org.springframework.kafka.listener.GenericMessageListener;

/**
 *
 */
public interface KafkaMessageListenerFactory {

    GenericMessageListener<?> create();
}
