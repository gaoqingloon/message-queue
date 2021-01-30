package com.bigdata.kafka.consumer.test;

import com.bigdata.kafka.consumer.listener.KafkaMessageListenerFactory;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;
import org.springframework.kafka.listener.GenericMessageListener;

@SpringBootApplication
public class KafkaConsumerTestApplication {

    public static void main(String[] args) {
        SpringApplication.run(KafkaConsumerTestApplication.class, args);
    }

    @Bean
    public KafkaMessageListenerFactory myListenerFactory() {
        return () -> myMessageListener();
    }

    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public GenericMessageListener<?> myMessageListener() {
        return new CustomMessageListener();
    }
}
