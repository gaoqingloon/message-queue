package com.bigdata.mqtt.test;

import com.bigdata.mqtt.consumer.handler.MqttMessageHandlerFactory;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;
import org.springframework.messaging.MessageHandler;

@SpringBootApplication(scanBasePackages = "com.bigdata.mqtt")
public class MqttConsumerTestApplication {

    public static void main(String[] args) {
        SpringApplication.run(MqttConsumerTestApplication.class, args);
    }

    @Bean
    public MqttMessageHandlerFactory myHandlerFactory() {
        return () -> myMessageHandler();
    }

    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public MessageHandler myMessageHandler() {
        return new CustomMessageHandler();
    }
}
