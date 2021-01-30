package com.bigdata.mqtt.consumer;

import com.bigdata.mqtt.consumer.handler.DefaultMqttMessageHandler;
import com.bigdata.mqtt.consumer.handler.MqttMessageHandlerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.integration.dsl.context.IntegrationFlowContext;
import org.springframework.messaging.MessageHandler;

/**
 *
 */
@Configuration
@EnableConfigurationProperties(MqttConsumerProperties.class)
public class MqttConsumerAutoConfiguration {

    @Autowired
    private MqttConsumerProperties properties;

    @Autowired
    private IntegrationFlowContext flowContext;

    @Autowired
    private MqttMessageHandlerFactory messageHandlerFactory;

    @Bean
    public MqttConsumerIntegrationManager integrationFlowPostProcessor() {
        MqttConsumerIntegrationManager manager = new MqttConsumerIntegrationManager(properties, flowContext);
        manager.setHandlerFactory(messageHandlerFactory);
        return manager;
    }

    @Bean
    @ConditionalOnMissingBean(value = MqttMessageHandlerFactory.class)
    public MqttMessageHandlerFactory messageHandlerFactory() {
        return () -> defaultMessageHandler();
    }

    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public MessageHandler defaultMessageHandler() {
        return new DefaultMqttMessageHandler();
    }
}
