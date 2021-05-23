**1. 数据校验**   
@Validated来校验数据，如果数据异常则会统一抛出异常，方便异常中心统一处理    
```
@NotNull(message="名字不能为空")
private String userName;
@Max(value=120,message="年龄最大不能查过120")
private int age;
@Email(message="邮箱格式错误")
private String email;

空检查
@Null       验证对象是否为null
@NotNull    验证对象是否不为null, 无法查检长度为0的字符串
@NotBlank   检查约束字符串是不是Null还有被Trim的长度是否大于0,只对字符串,且会去掉前后空格.
@NotEmpty   检查约束元素是否为NULL或者是EMPTY.
    
Booelan检查
@AssertTrue     验证 Boolean 对象是否为 true  
@AssertFalse    验证 Boolean 对象是否为 false  
    
长度检查
@Size(min=, max=) 验证对象（Array,Collection,Map,String）长度是否在给定的范围之内  
@Length(min=, max=) string is between min and max included.

日期检查
@Past       验证 Date 和 Calendar 对象是否在当前时间之前  
@Future     验证 Date 和 Calendar 对象是否在当前时间之后  
@Pattern    验证 String 对象是否符合正则表达式的规则

.......等等
```

***

**2. properties的多环境配置**    
我们在主配置文件编写的时候，文件名可以是 application-{profile}.properties/yml , 用来指定多个环境版本；

例如：

application-test.properties 代表测试环境配置

application-dev.properties 代表开发环境配置

但是Springboot并不会直接启动这些配置文件，它默认使用application.properties主配置文件；

我们需要通过一个配置来选择需要激活的环境：

比如在配置文件中指定使用dev环境，我们可以通过设置不同的端口号进行测试；
我们启动SpringBoot，就可以看到已经切换到dev下的配置了；
在**application.properties**中添加**spring.profiles.active=dev**     

***   
**3. yml的多环境配置**   
```
server:
  port: 8081
#选择要激活那个环境块
spring:
  profiles:
    active: prod

---
server:
  port: 8083
spring:
  profiles: dev #配置环境的名称


---

server:
  port: 8084
spring:
  profiles: prod  #配置环境的名称
``` 

***
**4. 配置文件加载路径**  
springboot启动时，会扫描以下位置的application.properties或者application.yml文件作为Spring boot的默认配置文件：   
优先级1：项目路径下的config文件夹配置文件   
优先级2：项目路径下配置文件   
优先级3：资源路径下的config文件夹配置文件   
优先级4：资源路径下配置文件   
