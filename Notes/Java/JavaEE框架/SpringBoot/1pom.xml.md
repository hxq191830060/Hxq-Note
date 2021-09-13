SpringBoot项目的pom.xml结构
```
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    <!--上面这些是固定部分，表示这个maven配置文件-->

    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>2.4.5</version>
        <relativePath/> <!-- lookup parent from repository -->
    </parent>
    <!--指定我们使用的是SpringBoot以及SpringBoot的版本号
        从官网继承来的父依赖-->

    <groupId>com.example</groupId>
    <artifactId>SpringbootDemo</artifactId>
    <version>0.0.1-SNAPSHOT</version>
    <name>SpringbootDemo</name>
    <description>Demo project for Spring Boot</description>
    <!--这些是我们创建项目时输入的信息,公司id，项目id，版本号，项目名称，项目描述等-->

    <properties>
        <java.version>1.8</java.version>
    </properties>
    <!--该项目的属性，这里列出了java版本-->

    <!--下面是该项目的依赖,SpringBoot有许多starter供我们使用-->
    <dependencies>
      
    </dependencies>

    <!--这里是插件配置,用来构建，运行，打包SpringBoot项目-->
    <build>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
                <configuration>
                    <excludes>
                        <exclude>
                            <groupId>org.projectlombok</groupId>
                            <artifactId>lombok</artifactId>
                        </exclude>
                    </excludes>
                </configuration>
            </plugin>
        </plugins>
    </build>

</project>

```