1. 事前准备三个东西
* ①实体类——放在java目录下，实现getter，setter，有参构造
* ②dao接口——放在java目录下
* ③映射配置文件——放在resource目录下        
**dao接口与映射配置文件名字要一致**   


2. 启动类加入注解@MapperScan
```
@MapperScan(basePackages = "com.example.springbootstudydemo.dao")
basePackages——为dao接口所在包的路径
```  

3. yml中配置**数据库信息**和**映射文件位置**    
```
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/courselearning?setUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=GMT%2B8&allowPublicKeyRetrieval=true
    driver-class-name: com.mysql.cj.jdbc.Driver
    username: root
    password: 123456
mybatis:
  mapper-locations: classpath:mapper/*.xml  、
  //默认路径为resources目录下，所以这里是resources/mapper/*.xml
```