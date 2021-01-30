package com.bigdata.distribute.impl;

import com.bigdata.distribute.Distributer;
import com.bigdata.distribute.Endpoint;
import com.bigdata.distribute.EndpointMatcher;
import com.bigdata.distribute.Packet;
import com.bigdata.distribute.annotation.EndPointInfo;
import com.bigdata.distribute.config.EndpointProperties;
import lombok.Data;
import org.springframework.core.annotation.AnnotationUtils;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 *
 */
public class MultiTargetDistrbuterImpl extends AbstractDistributer {

    private List<EndpointWrapper> endpoints;

    /**
     * @param properties
     */
    public MultiTargetDistrbuterImpl(EndpointProperties properties) {
        super(properties);
    }

    @PostConstruct
    public void init() {
        endpoints = new CopyOnWriteArrayList<>();
        endpoints.addAll(collectByAnnotation());
        endpoints.addAll(collectByConfig());
    }

    /**
     * @return
     */
    private List<EndpointWrapper> collectByAnnotation() {
        return getEndpoints().entrySet().stream().filter(entry -> {
            return AnnotationUtils.findAnnotation(entry.getValue().getClass(), EndPointInfo.class) != null;
        }).map(entry -> {
            Endpoint endpoint = entry.getValue();
            EndPointInfo endPointInfo = AnnotationUtils.findAnnotation(endpoint.getClass(), EndPointInfo.class);
            EndpointPattern pattern = new EndpointPattern();
            pattern.setPath(endPointInfo.value());
            pattern.setMatcher(endPointInfo.matcher());
            return EndpointWrapper.wrapper(endpoint, pattern);
        }).collect(Collectors.toList());
    }

    /**
     * @return
     */
    private List<EndpointWrapper> collectByConfig() {
        return getEndpointConfigs().stream().map(config -> {
            EndpointPattern pattern = new EndpointPattern();
            pattern.setMatcher(config.getMatcher());
            pattern.setPath(config.getPath());
            return EndpointWrapper.wrapper(forName(config.getEndpointClass()), pattern);
        }).collect(Collectors.toList());
    }

    /**
     * @see Distributer#submit(Packet)
     */
    @Override
    public void submit(Packet packet) {
        endpoints.stream().filter(endpoint -> endpoint.getEndpoint() != null && endpoint.getPattern().match(packet.getKey()))
                .forEach(wrapper -> wrapper.getEndpoint().handle(packet));
    }

    @Data
    static class EndpointWrapper {

        private Endpoint endpoint;

        private EndpointPattern pattern;

        /**
         * @param endpoint2
         * @param pattern2
         * @return
         */
        static EndpointWrapper wrapper(Endpoint endpoint, EndpointPattern pattern) {
            EndpointWrapper wrapper = new EndpointWrapper();
            wrapper.setEndpoint(endpoint);
            wrapper.setPattern(pattern);
            return wrapper;
        }
    }

    class EndpointPattern {

        private String path;

        private EndpointMatcher matcher;

        /**
         * @param key
         * @return
         */
        public boolean match(String key) {
            switch (matcher) {
                case FULL:
                    return path.equals(key);
                case REGEX:
                    Pattern pattern = Pattern.compile(path);
                    return pattern.matcher(key).matches();
                default:
                    break;
            }
            return true;
        }

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

    }
}
