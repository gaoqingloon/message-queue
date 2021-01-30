package com.bigdata.mqtt.consumer.handler;

import org.springframework.messaging.MessageHandler;

/**
 *
 */
public interface MqttMessageHandlerFactory {

    MessageHandler create();
}
