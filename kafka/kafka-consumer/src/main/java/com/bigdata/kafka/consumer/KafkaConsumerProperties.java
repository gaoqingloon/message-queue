package com.bigdata.kafka.consumer;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
@Data
@ConfigurationProperties("kafka")
public class KafkaConsumerProperties {

    private List<KafkaConsumer> consumers = new ArrayList<>();

    public static class KafkaConsumer {

        /**
         * 必填，保持唯一性
         */
        private String id;

        /**
         * 必填
         */
        private String servers;

        /**
         * 默认值true
         */
        private boolean enableAutoCommit = true;

        /**
         * 单位毫秒，默认值6000
         */
        private int sessionTimeout = 6000;

        private int autoCommitInterval = 100;

        /**
         * 必填
         */
        private String groupId;

        /**
         * 默认值latest
         */
        private String autoOffsetReset = "latest";

        /**
         * 默认值1
         */
        private int concurrency = 1;

        /**
         * 默认值org.apache.kafka.common.serialization.StringDeserializer
         */
        private String keyDeserializer = "org.apache.kafka.common.serialization.StringDeserializer";

        /**
         * 默认值org.apache.kafka.common.serialization.StringDeserializer
         */
        private String valueDeserializer = "org.apache.kafka.common.serialization.StringDeserializer";

        /**
         * 需要是{@link org.springframework.kafka.listener.GenericMessageListener}的子类，messageListener为空时，会从KafkaMessageListenerFactory中获取Listener
         */
        private String messageListener;

        private List<String> topics = new ArrayList<>();

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getServers() {
            return servers;
        }

        public void setServers(String servers) {
            this.servers = servers;
        }

        public boolean isEnableAutoCommit() {
            return enableAutoCommit;
        }

        public void setEnableAutoCommit(boolean enableAutoCommit) {
            this.enableAutoCommit = enableAutoCommit;
        }

        public int getSessionTimeout() {
            return sessionTimeout;
        }

        public void setSessionTimeout(int sessionTimeout) {
            this.sessionTimeout = sessionTimeout;
        }

        public int getAutoCommitInterval() {
            return autoCommitInterval;
        }

        public void setAutoCommitInterval(int autoCommitInterval) {
            this.autoCommitInterval = autoCommitInterval;
        }

        public String getGroupId() {
            return groupId;
        }

        public void setGroupId(String groupId) {
            this.groupId = groupId;
        }

        public String getAutoOffsetReset() {
            return autoOffsetReset;
        }

        public void setAutoOffsetReset(String autoOffsetReset) {
            this.autoOffsetReset = autoOffsetReset;
        }

        public int getConcurrency() {
            return concurrency;
        }

        public void setConcurrency(int concurrency) {
            this.concurrency = concurrency;
        }

        public String getKeyDeserializer() {
            return keyDeserializer;
        }

        public void setKeyDeserializer(String keyDeserializer) {
            this.keyDeserializer = keyDeserializer;
        }

        public String getValueDeserializer() {
            return valueDeserializer;
        }

        public void setValueDeserializer(String valueDeserializer) {
            this.valueDeserializer = valueDeserializer;
        }

        public String getMessageListener() {
            return messageListener;
        }

        public void setMessageListener(String messageListener) {
            this.messageListener = messageListener;
        }

        public List<String> getTopics() {
            return topics;
        }

        public void setTopics(List<String> topics) {
            this.topics = topics;
        }

    }
}
