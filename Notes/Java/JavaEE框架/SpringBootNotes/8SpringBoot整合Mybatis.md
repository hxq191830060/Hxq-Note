1. 数据访问层——dao接口  
* @Repository标注接口为dao层bean
* dao接口内定义CURD方法  
* 创建对应的映射配置文件  
2. 实体类——存储表数据  
3. SpringBoot的启动类加上@MapperScan("包地址")——指明要扫描的包，以获取映射配置文件
4. application.properties中指定映射文件的位置  
   application.properties中配置数据库连接信息 
```
mybatis.mapper-locations=classpath:mapper/*.xml
```
```
# 配置数据库驱动
spring.datasource.driver-class-name=com.mysql.jdbc.Driver
# 配置数据库url
spring.datasource.url=jdbc:mysql://127.0.0.1:3306/shop?useUnicode=true&characterEncoding=utf-8&serverTimezone=UTC
# 配置数据库用户名
spring.datasource.username=root
# 配置数据库密码
spring.datasource.password=Easy@0122
```