package com.bigdata.mqtt.producer;

/**
 *
 */
public interface MqttProducerManager {

    MqttMessageTemplate add(MqttProducerProperties.Producer producer);

    void remove(String id);

}
