package com.example.zhuangqf.endpoint;

import com.example.zhuangqf.TestBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.*;

/**
 * Created by zhuangqf on 11/18/16.
 */
@Component
public class TestEndpoint {

    @Autowired
    TestBean testBean;

    @GET
    @Path("/test")
    public String test(){
        System.out.println(testBean.showTest());
        return testBean.showTest();
    }


}
