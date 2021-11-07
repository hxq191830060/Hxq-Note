以下有几种方式，可以将配置文件中的值赋予类中的字段



### 1. @ConfigurationProperties

#### 1. 导入依赖

```
       <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-configuration-processor</artifactId>
            <version>2.4.5</version>
            <optional>true</optional>
        </dependency>
```

#### 2. 配置文件中定义属性

```yaml
order: 
  a: 10
  b: 20
```

#### 3. 创建类

* @ConfigurationProperties(prefix="order")——读取配置文件order下的所有数据
* 类的各个字段必须有setter方法，才能成功将配置文件中的赋予字段

```java
@Component
@ConfigurationProperties(prefix = "order")
public class OrderProperties {
    private Integer a;
    private Integer b;
    
    //省略setter
}
```



**编译完成后，会生成spring-configuration-metadata.json文件**



### 2. 多环境配置

每个环境都有一个配置文件，每个环境创建一个配置文件 **application-${profile}.yaml**，**${profile}**为环境明

* 各个环境都拥有的配置放在 **application.yaml**下
* 各个环境独有的配置放在 **application-${profile}.yaml**下



#### 启动

在IDEA中想要采用不同环境下的配置进行启动，只需要在启动时加入参数 **--spring.profiles.active=XXX**即可



#### 打包

多环境打包就不是这样了



