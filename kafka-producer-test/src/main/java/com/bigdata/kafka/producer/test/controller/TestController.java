package com.bigdata.kafka.producer.test.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Lazy;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 */
@RestController
public class TestController {

    @Autowired(required = false)
    @Qualifier("test_KafkaTemplate")
    @Lazy
    private KafkaTemplate<Object, Object> kafkaTemplate;

    @GetMapping("/test")
    public String send(String topic, String message) {
        kafkaTemplate.send(topic, message);
        return "send";
    }
}
