/*******************************************************************************
 * Copyright (c) 2020, 2020 Hirain Technologies Corporation.
 ******************************************************************************/
package com.bigdata.mqtt.test;

import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHandler;
import org.springframework.messaging.MessagingException;

/**
 *
 */
@Slf4j
public class CustomMessageHandler implements MessageHandler {

    /**
     * @see org.springframework.messaging.MessageHandler#handleMessage(org.springframework.messaging.Message)
     */
    @Override
    public void handleMessage(Message<?> message) throws MessagingException {
        String topic = message.getHeaders().get("mqtt_receivedTopic").toString();
        log.info("handler:{},topic:{},message:{}", getClass().getName() + "[" + Integer.toHexString(hashCode()) + "]", topic,
                message.getPayload().toString());
    }

}
