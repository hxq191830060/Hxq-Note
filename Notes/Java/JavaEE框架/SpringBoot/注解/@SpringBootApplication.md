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
