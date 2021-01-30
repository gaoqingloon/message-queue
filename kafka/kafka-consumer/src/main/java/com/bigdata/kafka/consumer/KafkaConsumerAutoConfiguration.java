package com.bigdata.kafka.consumer;

import com.bigdata.kafka.consumer.listener.KafkaMessageListener;
import com.bigdata.kafka.consumer.listener.KafkaMessageListenerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

/**
 *
 */
@Configuration
@EnableConfigurationProperties(KafkaConsumerProperties.class)
public class KafkaConsumerAutoConfiguration {

    @Autowired
    private KafkaConsumerProperties properties;

    @Bean
    public KafkaConsumerContainerManager kafkaConsumerContainerManager() {
        return new KafkaConsumerContainerManager(properties, null);
    }

    @Bean
    @ConditionalOnMissingBean(value = KafkaMessageListenerFactory.class)
    public KafkaMessageListenerFactory kafkaMessageListenerFactory() {
//        return new KafkaMessageListenerFactory() {
//            @Override
//            public GenericMessageListener<?> create() {
//                return kafkaMessageListener();
//            }
//        };
        return () -> kafkaMessageListener();
    }

    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public KafkaMessageListener kafkaMessageListener() {
        return new KafkaMessageListener();
    }
}
