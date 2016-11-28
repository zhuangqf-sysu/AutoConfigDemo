package com.example.zhuangqf.config;

import com.example.zhuangqf.endpoint.TestEndpoint;
import org.glassfish.jersey.server.ResourceConfig;
import org.springframework.stereotype.Component;

/**
 * Created by zhuangqf on 11/18/16.
 */
@Component
public class JerseyConfig extends ResourceConfig {

    public JerseyConfig(){
        register(TestEndpoint.class);
    }

}
