package com.bigdata.mqtt.consumer;

import org.springframework.messaging.MessageHandler;

/**
 *
 */
public interface MqttConsumerManager {

    /**
     * @param consumer
     * @throws ClassNotFoundException
     */
    void add(MqttConsumerProperties.Consumer consumer) throws ClassNotFoundException;

    /**
     * @param consumer
     * @param handler
     */
    void add(MqttConsumerProperties.Consumer consumer, MessageHandler handler);

    void remove(String id);
}
