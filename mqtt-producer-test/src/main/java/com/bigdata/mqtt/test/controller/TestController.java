package com.bigdata.mqtt.test.controller;

import com.bigdata.mqtt.producer.MqttMessageTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Lazy;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 */
@RestController
public class TestController {

    @Autowired(required = false)
    @Lazy
    @Qualifier("test_template")
    private MqttMessageTemplate producer;

    @GetMapping("/test")
    public String send(String topic, String message) {
        producer.send(topic, message);
        return "send";
    }

}
