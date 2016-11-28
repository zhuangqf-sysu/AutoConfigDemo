package com.example.zhuangqf;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.support.SpringBootServletInitializer;


@SpringBootApplication
@EntityScan("com.example.zhuangqf.entity")
@MapperScan("com.example.zhuangqf.mapper")
public class App extends SpringBootServletInitializer {

    public static void main( String[] args ) {
        App myApp = new App();
        myApp.configure(new SpringApplicationBuilder(App.class)).run(args);
    }

}
