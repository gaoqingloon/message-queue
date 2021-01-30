package com.bigdata.mqtt.producer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.dsl.context.IntegrationFlowContext;

/**
 *
 */
@Configuration
@EnableConfigurationProperties(MqttProducerProperties.class)
public class MqttProducerAutoConfiguration {

    @Autowired
    private MqttProducerProperties properties;

    @Autowired
    private IntegrationFlowContext flowContext;

    @Bean
    public MqttProducerIntegrationManager mqttProducerIntegrationManager() {
        return new MqttProducerIntegrationManager(properties, flowContext);
    }
}
