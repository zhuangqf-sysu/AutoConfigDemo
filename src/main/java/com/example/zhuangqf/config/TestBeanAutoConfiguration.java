package com.example.zhuangqf.config;

import com.example.zhuangqf.TestBean;
import com.example.zhuangqf.properties.TestBeanProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by zhuangqf on 11/27/16.
 */
@Configuration
@EnableConfigurationProperties(TestBeanProperties.class)
public class TestBeanAutoConfiguration {

    @Autowired TestBeanProperties testBeanProperties;

    @Bean
    @ConditionalOnMissingBean(TestBean.class)
    public TestBean testBean() {
        return new TestBean(testBeanProperties.getTestString());
    }
}
