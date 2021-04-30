1. Spring本质上是一个容器，里面存放java对象，放在Spring容器里面的java对象称为bean   
  在Spring开发中，我们通过XML/注解/java类三种方式在Spring容器中注册bean   
  SpringBoot就是为了简化配置——我们不需要配置XML了


2. SpringBoot项目启动只有一个类，如下
```java
@SpringBootApplication
public class SpringbootDemoApplication {
    public static void main(String[] args) {
        SpringApplication.run(SpringbootDemoApplication.class, args);
    }
}
```  
SpringBoot项目能够启动的关键在于 **@SpringBootApplication**   
有了**SpringBootApplication**,我们就有了一个无需配置，可以直接使用的Spring容器了  
```java
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@SpringBootConfiguration//继承自@Configuration，标志该类为配置类
@EnableAutoConfiguration//启动自动配置，SpringBoot自动扫描项目下的所有配置类，获取配置信息创建Spring容器
@ComponentScan( //指定扫描的包,SpringBoot会扫描指定的包及其子包下的所有类和配置文件
    excludeFilters = {@Filter(
    type = FilterType.CUSTOM,
    classes = {TypeExcludeFilter.class}
), @Filter(
    type = FilterType.CUSTOM,
    classes = {AutoConfigurationExcludeFilter.class}
)}
)
public @interface SpringBootApplication {}
```   

