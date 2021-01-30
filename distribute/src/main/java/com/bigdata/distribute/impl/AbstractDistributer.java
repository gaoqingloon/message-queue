package com.bigdata.distribute.impl;

import com.bigdata.distribute.Distributer;
import com.bigdata.distribute.Endpoint;
import com.bigdata.distribute.config.EndpointProperties;
import com.bigdata.distribute.config.EndpointProperties.EndpointConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.List;
import java.util.Map;

/**
 *
 */
@Slf4j
public abstract class AbstractDistributer implements Distributer, ApplicationContextAware {

    private ApplicationContext applicationContext;

    private EndpointProperties properties;

    /**
     * @param properties
     */
    public AbstractDistributer(EndpointProperties properties) {
        this.properties = properties;
    }

    protected ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    protected Map<String, Endpoint> getEndpoints() {
        return applicationContext.getBeansOfType(Endpoint.class);
    }

    protected List<EndpointConfig> getEndpointConfigs() {
        return properties.getEndpoints();
    }

    protected Endpoint forName(String endpointClass) {
        try {
            Class<?> clz = Class.forName(endpointClass);
            if (!Endpoint.class.isAssignableFrom(clz)) {
                throw new ClassCastException(endpointClass + " is not a subclass of " + Endpoint.class.getName());
            }
            Endpoint endPoint = (Endpoint) getApplicationContext().getBean(clz);
            return endPoint;
        } catch (ClassNotFoundException e) {
            log.error(e.getMessage(), e);
        }
        return null;
    }
}
