package com.example.zhuangqf.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Created by zhuangqf on 11/27/16.
 */
@ConfigurationProperties(prefix = "com.example.zhuangqf")
public class TestBeanProperties {

    private String testString;

    public String getTestString() {
        return testString;
    }

    public void setTestString(String testString) {
        this.testString = testString;
    }
}
