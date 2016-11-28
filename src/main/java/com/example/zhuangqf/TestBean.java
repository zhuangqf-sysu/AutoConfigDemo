package com.example.zhuangqf;

/**
 * Created by zhuangqf on 11/27/16.
 */
public class TestBean {

    private String testString;

    public TestBean(String testString){
        this.testString = testString;
    }

    public String showTest(){
        return testString;
    }

}
