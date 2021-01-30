package com.bigdata.distribute.impl;

import com.bigdata.distribute.Distributer;
import com.bigdata.distribute.Endpoint;
import com.bigdata.distribute.Packet;
import com.bigdata.distribute.annotation.EndPointInfo;
import com.bigdata.distribute.config.EndpointProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.AnnotationUtils;

import javax.annotation.PostConstruct;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 *
 */
@Slf4j
public class DistributerImpl extends AbstractDistributer {

    private Map<String, Endpoint> endPointMap;

    /**
     * @param properties
     */
    public DistributerImpl(EndpointProperties properties) {
        super(properties);
    }

    @PostConstruct
    public void init() {
        endPointMap = new ConcurrentHashMap<>();
        endPointMap.putAll(collectByAnnotation());
        endPointMap.putAll(collectByConfig());
    }

    /**
     * @return
     */
    private Map<String, Endpoint> collectByConfig() {
        return getEndpointConfigs().stream().collect(Collectors.toMap(
                config -> config.getPath() != null ? config.getPath() : config.getEndpointClass(), config -> forName(config.getEndpointClass())));
    }

    /**
     * @return
     */
    private Map<String, Endpoint> collectByAnnotation() {
        Map<String, Endpoint> map = getEndpoints();
        return map.entrySet().stream().filter(entry -> {
            return AnnotationUtils.findAnnotation(entry.getValue().getClass(), EndPointInfo.class) != null;
        }).collect(Collectors.toMap(entry -> {
            Endpoint endpoint = entry.getValue();
            EndPointInfo info = AnnotationUtils.findAnnotation(endpoint.getClass(), EndPointInfo.class);
            if (info != null) {
                return info.value();
            }
            return endpoint.getClass().getName();
        }, Entry::getValue));
    }

    /**
     * @see Distributer#submit(Packet)
     */
    @Override
    public void submit(Packet packet) {
        Endpoint endpoint = endPointMap.get(packet.getKey());
        if (endpoint != null) {
            log.info("{} handle {}", packet.getKey(), packet.getPayload());
            endpoint.handle(packet);
        } else {
            log.error("no endpoint for key {}", packet.getKey());
        }
    }

}
