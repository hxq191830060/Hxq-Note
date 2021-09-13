1. 事前准备三个东西
* ①实体类——放在java目录下，实现getter，setter，有参构造
* ②dao接口——放在java目录下
* ③映射配置文件——放在resource目录下
* [可选]④主配置文件——放在resource目录下  
**dao接口与映射配置文件名字要一致**   


2. 启动类加入注解@MapperScan(或者dao接口用注解@Mapper修饰)    
```
@MapperScan(basePackages = "com.example.springbootstudydemo.dao")
basePackages——为dao接口所在包的路径
```  

3. yml中配置**数据库信息**和**映射文件位置**    
```
spring:
  //数据原配置信息
  datasource:
    url: jdbc:mysql://localhost:3306/courselearning?setUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=GMT%2B8&allowPublicKeyRetrieval=true
    driver-class-name: com.mysql.cj.jdbc.Driver
    username: root
    password: 123456
    
//mybatis配置信息
mybatis:
  //Mybatis主配置文件的路径，默认路径为resources目录下，可选
  config-location: classpath:mybatis-config.xml 
  
  //映射配置文件的位置，默认路径为resources/，一定要有
  mapper-locations: classpath:mapper/*.xml  、
  
  //dao实体类的包路径，可选
  type-aliases-package: cn.iocoder.springboot.lab12.mybatis.dataobject
 
```