package com.bigdata.mqtt.producer;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
@Data
@ConfigurationProperties(prefix = "mqtt")
public class MqttProducerProperties {

    private List<Producer> producers = new ArrayList<>();

    public static class Producer {

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
         * 默认为60，单位秒
         */
        private int keepAlive = 60;

        /**
         * 默认为false
         */
        private boolean async = false;

        private String defaultTopic;

        /**
         * 是否为加密连接，默认为false，非加密
         */
        private boolean sslEnable = false;

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

        public int getKeepAlive() {
            return keepAlive;
        }

        public void setKeepAlive(int keepAlive) {
            this.keepAlive = keepAlive;
        }

        public boolean isAsync() {
            return async;
        }

        public void setAsync(boolean async) {
            this.async = async;
        }

        public String getDefaultTopic() {
            return defaultTopic;
        }

        public void setDefaultTopic(String defaultTopic) {
            this.defaultTopic = defaultTopic;
        }

        public boolean isSslEnable() {
            return sslEnable;
        }

        public void setSslEnable(boolean sslEnable) {
            this.sslEnable = sslEnable;
        }

        public SSLProperties getSsl() {
            return ssl;
        }

        public void setSsl(SSLProperties ssl) {
            this.ssl = ssl;
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
