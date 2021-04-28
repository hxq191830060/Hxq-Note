**注意事项**
1. 不管采用那种方式,**主配置文件**和**log4j.properties**都是需要的 
2. dao接口定义了CURD操作和返回类型  
   如果采用**XML**实现——一个dao接口对应一个**映射配置文件**,每个CURD操作在映射配置文件中对应一个**CURD**标签
   如果采用**注解**实现——dao接口中的每个CURD操作,都要用**注解**标注
3. 尽量采取dao接口来实现CURD,尽量不要使用DAO实现类  
4. 查询操作一定要定义一个实体类才存储查询结果,实体类属性的类型,名称 与表字段的属性,名称**一定要一致** 

***
**XML开发配置**
1. **配置log4j.properties**  
```
log4j.rootLogger=INFO, Console, File

log4j.appender.Console=org.apache.log4j.ConsoleAppender
log4j.appender.Console.layout=org.apache.log4j.PatternLayout
log4j.appender.Console.layout.ConversionPattern=%d{ABSOLUTE} %-5p [%c{3}] %m%n


log4j.appender.File=org.apache.log4j.DailyRollingFileAppender
log4j.appender.File.File=spring.log
log4j.appender.File.Append=false
log4j.appender.File.layout=org.apache.log4j.PatternLayout
log4j.appender.File.layout.ConversionPattern=%d{ABSOLUTE} %-5p [%c] %m%n
```  
2. **主配置文件**
```
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE configuration
        PUBLIC "-//mybatis.org//DTD Config 3.0//EN"
        "http://mybatis.org/dtd/mybatis-3-config.dtd">

<!--mybatis主配置文件-->
<configuration>
    <!--配置环境,default的值必须与<environment>中的某一个的id相同-->
    <environments default="mysql">
        <!--配置mysql环境-->
        <environment id="mysql">
            <!--配置事务类型-->
            <transactionManager type="JDBC"/>
            <!--配置数据源(数据库连接池)-->
            <dataSource type="POOLED">
                <!--配置连接数据库的4大参数-->
                <property name="driver" value="com.mysql.jdbc.Driver"/>
                <property name="url" value="jdbc:mysql://localhost:3306/test?setUnicode=true&amp;characterEncoding=utf8&amp;useSSL=false&amp;serverTimezone=GMT%2B8&amp;allowPublicKeyRetrieval=true"/>
                <property name="username" value="root"/>
                <property name="password" value="123456"/>
            </dataSource>
        </environment>
    </environments>

    <!--指定映射配置文件的位置
        映射配置文件就是每个dao独立的配置文件-->
    <mappers>
        <mapper resource="com/dao/IUserDao.xml"/>
    </mappers>
</configuration>
```
3. **映射配置文件**——一个dao接口对应一个映射配置文件   
**映射配置文件在resources目录下的位置 要与 dao接口在java目录下的位置 一致**   
   
   映射配置文件中<mapper>内的namespace为映射配置文件对应的dao接口的**完全限定名**   
dao接口中的 定义删除方法——delete标签; 定义查询方法——select标签; 定义修改方法——update标签; 定义增加方法——insert标签   
   这四个标签中的id:表名该标签对应dao接口中的哪个方法 ;resultType:查询方法的返回结果要封装为哪个实体类对象     
   这四个标签内写入SQL语句
```
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE mapper
        PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN"
        "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<!--映射配置文件，这个是IUser_Dao的配置文件
    namespace为IUser_Dao的全限定名-->
<mapper namespace="com.dao.IUserDao">
    <!--配置dao接口中定义的所有CURD方法
        id为dao接口中定义的方法名称
        resultType表示查询的结果映射为该类的对象-->
    <select id="getAll" resultType="com.domain.User">
        select * from test.user;
    </select>
</mapper>
```  

*** 
**注解实现**
1. 注解实现也是需要**主配置文件**和**log4j.properties**,结构与XML实现一样基本一样  
   不同之处在于<mapper<mapper>>  
```
    <!--如果这里使用注解的话，mapper中要用class属性，表明被注解的dao接口的全限定名-->
    <mappers>
        <mapper class="com.dao.IUserDao"/>
    </mappers>
```
2. dao接口中定了各种CURD方法,用@Select("sql"),@Delete("sql"),@Insert("sql"),@Update("sql")标注  
```
    @Select("select id,uname,phone,password,picture,balance,user_role from user_info")
    public List<User>getAll();
```