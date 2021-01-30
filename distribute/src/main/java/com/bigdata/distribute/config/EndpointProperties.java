/*******************************************************************************
 * Copyright (c) 2020, 2020 Hirain Technologies Corporation.
 ******************************************************************************/
package com.bigdata.distribute.config;

import com.bigdata.distribute.EndpointMatcher;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.List;

/**
 * @Version 1.0
 * @Author jianwen.xin@hirain.com
 * @Created Dec 4, 2020 9:30:50 AM
 * @Description <p>
 * @Modification <p>
 * Date Author Version Description
 * <p>
 * Dec 4, 2020 jianwen.xin@hirain.com 1.0 create file
 */
@ConfigurationProperties("distribute")
@Data
public class EndpointProperties {

    private List<EndpointConfig> endpoints = new ArrayList<>();

    public static class EndpointConfig {

        /**
         * 必填
         */
        private String path;

        /**
         * 匹配规则
         */
        private EndpointMatcher matcher = EndpointMatcher.FULL;

        /**
         * 必填
         * com.hirain.phm.distribute.Endpoint的子类，需要注入Spring容器
         */
        private String endpointClass;

        public String getPath() {
            return path;
        }

        public void setPath(String path) {
            this.path = path;
        }

        public EndpointMatcher getMatcher() {
            return matcher;
        }

        public void setMatcher(EndpointMatcher matcher) {
            this.matcher = matcher;
        }

        public String getEndpointClass() {
            return endpointClass;
        }

        public void setEndpointClass(String endpointClass) {
            this.endpointClass = endpointClass;
        }

    }
}
