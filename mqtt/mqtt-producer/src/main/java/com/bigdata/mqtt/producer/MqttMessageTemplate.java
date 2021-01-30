package com.bigdata.mqtt.producer;

import org.springframework.integration.mqtt.support.MqttHeaders;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.GenericMessage;

import java.util.HashMap;
import java.util.Map;

/**
 *
 */
public class MqttMessageTemplate {

    private MessageChannel channel;

    private String defaultTopic;

    public MqttMessageTemplate(MessageChannel channel, String defaultTopic) {
        this.channel = channel;
        this.defaultTopic = defaultTopic;
    }

    public void send(String data) {
        send(defaultTopic, data);
    }

    public void send(String topic, String data) {
        sendMessage(topic, data);
    }

    public void send(byte[] data) {
        send(defaultTopic, data);
    }

    public void send(String topic, byte[] data) {
        sendMessage(topic, data);
    }

    private <T> void sendMessage(String topic, T data) {
        Map<String, Object> headers = createHeaders(topic);
        GenericMessage<T> message = new GenericMessage<>(data, headers);
        channel.send(message);
    }

    /**
     * @param topic
     * @return
     */
    private Map<String, Object> createHeaders(String topic) {
        Map<String, Object> map = new HashMap<>();
        map.put(MqttHeaders.TOPIC, topic != null ? topic : defaultTopic);
        return map;
    }
}
