package com.bigdata.mqtt.consumer;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
@Data
@ConfigurationProperties("mqtt")
public class MqttConsumerProperties {

    private List<Consumer> consumers = new ArrayList<>();

    public static class Consumer {

        /**
         * 必填，保持唯一
         */
        private String id;

        /**
         * 必填
         */
        private String url;

        /**
         * 保持唯一，默认为url+当前秒
         */
        private String clientId = url + System.currentTimeMillis();

        private String username;

        private String password;

        /**
         * 默认为30000
         */
        private int completionTimeout = 30000;

        /**
         * 默认为60，单位秒
         */
        private int keepAlive = 60;

        /**
         * 默认为false
         */
        private boolean payloadAsBytes = false;

        /**
         * 订阅的主题
         */
        private List<Topic> topics = new ArrayList<>();

        /**
         * 是否为加密连接，默认为false，非加密
         */
        private boolean sslEnable = false;

        /**
         * 需要是{@link org.springframework.messaging.MessageHandler }的子类，messageHandler为空时，从MqttMessageHandlerFactory中获取messageHandler;
         */
        private String messageHandler;

        private SSLProperties ssl;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getClientId() {
            return clientId;
        }

        public void setClientId(String clientId) {
            this.clientId = clientId;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public int getCompletionTimeout() {
            return completionTimeout;
        }

        public void setCompletionTimeout(int completionTimeout) {
            this.completionTimeout = completionTimeout;
        }

        public int getKeepAlive() {
            return keepAlive;
        }

        public void setKeepAlive(int keepAlive) {
            this.keepAlive = keepAlive;
        }

        public boolean isPayloadAsBytes() {
            return payloadAsBytes;
        }

        public void setPayloadAsBytes(boolean payloadAsBytes) {
            this.payloadAsBytes = payloadAsBytes;
        }

        public List<Topic> getTopics() {
            return topics;
        }

        public void setTopics(List<Topic> topics) {
            this.topics = topics;
        }

        public boolean isSslEnable() {
            return sslEnable;
        }

        public void setSslEnable(boolean sslEnable) {
            this.sslEnable = sslEnable;
        }

        public String getMessageHandler() {
            return messageHandler;
        }

        public void setMessageHandler(String messageHandler) {
            this.messageHandler = messageHandler;
        }

        public SSLProperties getSsl() {
            return ssl;
        }

        public void setSsl(SSLProperties ssl) {
            this.ssl = ssl;
        }
    }

    public static class Topic {

        private String topic;

        private int qos;

        public String getTopic() {
            return topic;
        }

        public void setTopic(String topic) {
            this.topic = topic;
        }

        public int getQos() {
            return qos;
        }

        public void setQos(int qos) {
            this.qos = qos;
        }
    }

    public static class SSLProperties {

        private String password;

        private String caFile;

        private String clientFile;

        private String keyFile;

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public String getCaFile() {
            return caFile;
        }

        public void setCaFile(String caFile) {
            this.caFile = caFile;
        }

        public String getClientFile() {
            return clientFile;
        }

        public void setClientFile(String clientFile) {
            this.clientFile = clientFile;
        }

        public String getKeyFile() {
            return keyFile;
        }

        public void setKeyFile(String keyFile) {
            this.keyFile = keyFile;
        }
    }

}
