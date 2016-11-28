#SpringBoot AutoConfiguration
Spring boot auto-configuration 初探
 - 1、打印 AUTO-CONFIGURATION REPORT
 - 2、从@SpringBootApplication开始；
 - 3、spring.factories;
 - 4、org.springframework.boot.autoconfigure.*;
 - 5、实践

##1、打印 AUTO-CONFIGURATION REPORT
    mvn spring-boot:run -Dlogging.level.org.springframework.boot.autoconfigure.logging=DEBUG
在每个Spring	Boot ApplicationContext中都存在一个相当有用的ConditionEvaluationReport。如果开启DEBUG日志输出,你将会看到它:

    =========================
    AUTO-CONFIGURATION REPORT
    =========================
    
    
    Positive matches:
    -----------------
    
       DataSourceAutoConfiguration matched
          - @ConditionalOnClass classes found: javax.sql.DataSource,org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType (OnClassCondition)
    
       DataSourceAutoConfiguration#dataSourceInitializer matched
          - @ConditionalOnMissingBean (types: org.springframework.boot.autoconfigure.jdbc.DataSourceInitializer; SearchStrategy: all) found no beans (OnBeanCondition)
    
       DataSourceAutoConfiguration.PooledDataSourceConfiguration matched
          - supported DataSource class found (DataSourceAutoConfiguration.PooledDataSourceCondition)
          - @ConditionalOnMissingBean (types: javax.sql.DataSource,javax.sql.XADataSource; SearchStrategy: all) found no beans (OnBeanCondition)
    
       ......    
    
    Negative matches:
    -----------------
    
       ActiveMQAutoConfiguration did not match
          - required @ConditionalOnClass classes not found: javax.jms.ConnectionFactory,org.apache.activemq.ActiveMQConnectionFactory (OnClassCondition)
    
       AopAutoConfiguration did not match
          - required @ConditionalOnClass classes not found: org.aspectj.lang.annotation.Aspect,org.aspectj.lang.reflect.Advice (OnClassCondition)
    
       ArtemisAutoConfiguration did not match
          - required @ConditionalOnClass classes not found: javax.jms.ConnectionFactory,org.apache.activemq.artemis.jms.client.ActiveMQConnectionFactory (OnClassCondition)
    
       ......
    
    Exclusions:
    -----------
    
        None
    
    
    Unconditional classes:
    ----------------------
    
        org.springframework.boot.autoconfigure.PropertyPlaceholderAutoConfiguration
    
        org.springframework.boot.autoconfigure.web.WebClientAutoConfiguration
    
        org.springframework.boot.autoconfigure.context.ConfigurationPropertiesAutoConfiguration
    
        org.springframework.boot.autoconfigure.info.ProjectInfoAutoConfiguration
这份report清楚完整地报告了spring boot做了哪些auto-config以及它们各自需要满足的条件，没有做哪些auto-config(因为不满足哪些条件)，开发者可以根据这份报告对程序进行调试，以求达到自己想要的效果。

##2、从@SpringBootApplication开始；
###（1）、@SpringBootApplication
我们的App启动类注解了@SpringBootApplication。从源码上来看，这个注解糅合了
    
    @SpringBootConfiguration
    @EnableAutoConfiguration
    @ComponentScan
这三个注解，其中`@SpringBootConfiguration`是`@Configuration`的一个替代;@EnableAutoConfiguration的作用是`启用S​​pring应用程序上下文的自动配置，尝试猜测和配置可能需要的bean`，即Spring Boot的auto-config是由这个注解实现的;`@ComponentScan`的作用是扫描`@Configuration`注解的类的目录及子目录下的Components并装载。


###（2）@EnableAutoConfiguration、：
其源码如下:
    
    @Target({ElementType.TYPE})
    @Retention(RetentionPolicy.RUNTIME)
    @Documented
    @Inherited
    @AutoConfigurationPackage
    @Import({EnableAutoConfigurationImportSelector.class})
    public @interface EnableAutoConfiguration {
        String ENABLED_OVERRIDE_PROPERTY = "spring.boot.enableautoconfiguration";
    
        Class<?>[] exclude() default {};
    
        String[] excludeName() default {};
    }
EnableAutoConfigurationImportSelector：使用的是spring-core模块中的SpringFactoriesLoader#loadFactoryNames()方法，它的作用是在类路径上扫描META-INF/spring.factories文件中定义的类。

##3、spring.factories
在org.springframework.boot.autoconfigure包下找到/META-INF/spring.factories:
    
    # Initializers
    org.springframework.context.ApplicationContextInitializer=\
    org.springframework.boot.autoconfigure.SharedMetadataReaderFactoryContextInitializer,\
    org.springframework.boot.autoconfigure.logging.AutoConfigurationReportLoggingInitializer
    
    # Application Listeners
    org.springframework.context.ApplicationListener=\
    org.springframework.boot.autoconfigure.BackgroundPreinitializer
    
    # Auto Configure
    org.springframework.boot.autoconfigure.EnableAutoConfiguration=\
    org.springframework.boot.autoconfigure.admin.SpringApplicationAdminJmxAutoConfiguration,\
    org.springframework.boot.autoconfigure.aop.AopAutoConfiguration,\
    ...... 
    
    # Failure analyzers
    org.springframework.boot.diagnostics.FailureAnalyzer=\
    org.springframework.boot.autoconfigure.jdbc.DataSourceBeanCreationFailureAnalyzer
    
    # Template availability providers
    org.springframework.boot.autoconfigure.template.TemplateAvailabilityProvider=\
    org.springframework.boot.autoconfigure.freemarker.FreeMarkerTemplateAvailabilityProvider,\
    ......
`Auto Configure`模块是spring boot启动时加载的配置类，让我们看一下JerseyAutoConfiguration:

    @Configuration
    @ConditionalOnClass(
        name = {"org.glassfish.jersey.server.spring.SpringComponentProvider", "javax.servlet.ServletRegistration"}
    )
    @ConditionalOnBean(
        type = {"org.glassfish.jersey.server.ResourceConfig"}
    )
    @ConditionalOnWebApplication
    @AutoConfigureOrder(-2147483648)
    @AutoConfigureBefore({DispatcherServletAutoConfiguration.class})
    @AutoConfigureAfter({JacksonAutoConfiguration.class})
    @EnableConfigurationProperties({JerseyProperties.class})
    public class JerseyAutoConfiguration implements ServletContextAware {
        ...
    }
让我们先来看一下`JerseyAutoConfiguration`上的几个注解：
 - `@Configuration`指明这是个配置类
 - `@Conditional`此注释使得只有在特定条件满足时才启用一些配置
    Spring还提供了很多Condition给我们用
    @ConditionalOnBean（仅仅在当前上下文中存在某个对象时，才会实例化一个Bean）
    @ConditionalOnClass（某个class位于类路径上，才会实例化一个Bean）
    @ConditionalOnExpression（当表达式为true的时候，才会实例化一个Bean）
    @ConditionalOnMissingBean（仅仅在当前上下文中不存在某个对象时，才会实例化一个Bean）
    @ConditionalOnMissingClass（某个class类路径上不存在的时候，才会实例化一个Bean）
    @ConditionalOnNotWebApplication（不是web应用）
也就是说，只用满足以下两个条件，spring boot才会帮我们自动配置。当我们配置Jersey时，我们在pom中引入了相关的package，包括`org.glassfish.jersey.server.spring.SpringComponentProvider`, `javax.servlet.ServletRegistration`满足了第一个条件；然后我们还写了`public class JerseyConfig extends ResourceConfig`注册endpoint，第二个条件也满足了，spring boot启动的是web application,地三个条件也满足了：

    JerseyAutoConfiguration matched
      - @ConditionalOnClass classes found: org.glassfish.jersey.server.spring.SpringComponentProvider,javax.servlet.ServletRegistration (OnClassCondition)
      - found web application StandardServletEnvironment (OnWebApplicationCondition)
      - @ConditionalOnBean (types: org.glassfish.jersey.server.ResourceConfig; SearchStrategy: all) found the following [jerseyConfig] (OnBeanCondition)

    JerseyAutoConfiguration#jerseyServletRegistration matched
      - @ConditionalOnMissingBean (names: jerseyServletRegistration; SearchStrategy: all) found no beans (OnBeanCondition)
      - matched (OnPropertyCondition)

    JerseyAutoConfiguration#requestContextFilter matched
      - @ConditionalOnMissingBean (types: org.springframework.boot.web.servlet.FilterRegistrationBean; SearchStrategy: all) found no beans (OnBeanCondition)

    JerseyAutoConfiguration.JacksonResourceConfigCustomizer matched
      - @ConditionalOnClass classes found: org.glassfish.jersey.jackson.JacksonFeature (OnClassCondition)
      - @ConditionalOnSingleCandidate (types: com.fasterxml.jackson.databind.ObjectMapper; SearchStrategy: all) found a primary candidate amongst the following [jacksonObjectMapper] (OnBeanCondition)
`JerseyAutoConfiguration`中用@Bean自动注入了以上三个变量，@Bean注解的方法上也可以使用@Conditional。

##3、动手试一试
我们已经知道了spring-boot auto-configuration的流程，spring-boot也允许我们实现自己的auto-config。
###（1）写一个简单的TestBean，假设它是我们需要引入的库包：

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
在这个类中，我们并没有用到spring相关的东西，包括注解。
###（2）编写我们的TestBeanAutoConfiguration

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
`@EnableConfigurationProperties(TestBeanProperties.class)`告诉spring boot帮我们自动加载TestBeanProperties.class，这个类是用来读取properties外部文件的。
###（3）TestBeanProperties.class：
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
这个类从项目依赖的properties文件中读取com.example.zhuangqf前缀的key（properties文件以key-value格式存属性）
###（4）application.properties：

    org.springframework.boot.autoconfigure.EnableAutoConfiguration=com.example.zhuangqf.config.TestBeanAutoConfiguration
    com.example.zhuangqf.testString=test
第一句告诉spring boot自动加载`com.example.zhuangqf.config.TestBeanAutoConfiguration`中的配置，第二句配置（3）中的testString
###(5)测试
在之前的endpoint中加:

    @Autowired
    TestBean testBean;

    @GET
    @Path("/test")
    public String test(){
        System.out.println(testBean.showTest());
        return testBean.showTest();
    }
启动程序，访问对应的网址：
    
    test
查看report：
    
   TestBeanAutoConfiguration#testBean matched
      - @ConditionalOnMissingBean (types: com.zhuangqf.TestBean; SearchStrategy: all) found no beans (OnBeanCondition)
我们自定义的类成功auto-config！！！

##参考：
 - Spring Boot参考指南https://www.gitbook.com/book/qbgbook/spring-boot-reference-guide-zh/discussions
 - http://sivalabs.in/2016/03/how-springboot-autoconfiguration-magic/
 - http://sivalabs.in/category/spring/
 - http://blog.anthavio.net/2016/03/fun-with-spring-boot-auto-configuration.html
 - https://www.ordina.be/nl-nl/blogs/2015/andreas/master-spring-boot-auto-configuration-talk/
 - https://blog.frankel.ch/designing-your-own-spring-boot-starter-part-1/#gsc.tab=0
 - https://blog.frankel.ch/designing-your-own-spring-boot-starter-part-2/#gsc.tab=0