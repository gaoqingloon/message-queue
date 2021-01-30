/*******************************************************************************
 * Copyright (c) 2020, 2020 Hirain Technologies Corporation.
 ******************************************************************************/
package com.bigdata.distribute.config;

import com.bigdata.distribute.Distributer;
import com.bigdata.distribute.impl.MultiTargetDistrbuterImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Version 1.0
 * @Author jianw
 * import com.hirain.phm.distribute.impl.DistributerImpl;en.xin@hirain.com
 * @Created Dec 3, 2020 5:16:40 PM
 * @Description <p>
 * @Modification <p>
 * Date Author Version Description
 * <p>
 * Dec 3, 2020 jianwen.xin@hirain.com 1.0 create file
 */
@Configuration
@EnableConfigurationProperties(EndpointProperties.class)
public class DistributeAutoConfiguration {

    @Autowired
    private EndpointProperties properties;

    @Bean
    @ConditionalOnMissingBean(Distributer.class)
    public Distributer distributer() {
        return new MultiTargetDistrbuterImpl(properties);
    }
}
