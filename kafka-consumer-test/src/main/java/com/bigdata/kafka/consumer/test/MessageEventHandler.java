package com.bigdata.kafka.consumer.test;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;

/**
 *
 */
@Configuration
public class MessageEventHandler {

    @EventListener
    public void on(String message) {
        System.err.println(message);
    }
}
