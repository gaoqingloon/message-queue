package com.bigdata.kafka.producer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 *
 */
@Configuration
@EnableConfigurationProperties(KafkaProducerProperties.class)
public class KafkaProducerAutoConfiguration {

    @Autowired
    private KafkaProducerProperties producerProperties;

    @Bean
    public KafkaProducerTemplateManager kafkaProducerTemplateManager() {
        return new KafkaProducerTemplateManager(producerProperties);
    }
}
