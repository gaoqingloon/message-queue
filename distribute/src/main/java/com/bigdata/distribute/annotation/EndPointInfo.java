/*******************************************************************************
 * Copyright (c) 2020, 2020 Hirain Technologies Corporation.
 ******************************************************************************/
package com.bigdata.distribute.annotation;

import com.bigdata.distribute.EndpointMatcher;
import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * @Version 1.0
 * @Author jianwen.xin@hirain.com
 * @Created Dec 3, 2020 5:04:01 PM
 * @Description <p>
 * @Modification <p>
 * Date Author Version Description
 * <p>
 * Dec 3, 2020 jianwen.xin@hirain.com 1.0 create file
 */
@Retention(RUNTIME)
@Target(TYPE)
public @interface EndPointInfo {

    @AliasFor("value")
    String key() default "";

    @AliasFor("key")
    String value() default "";

    EndpointMatcher matcher() default EndpointMatcher.FULL;
}
