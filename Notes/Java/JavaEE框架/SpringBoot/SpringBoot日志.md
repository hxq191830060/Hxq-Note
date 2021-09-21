**日志体系**

* **日志门面**
  * JCL（common-logging)
  * Slf4j（Simple Logging Facade for Java)
* **日志实现**
  * JUL
  * Log4j
  * Log4j2
  * Logback



**最佳实践**

* 在意性能——Slf4j+logback
* 使用Log4j，但是发现性能问题——改为Slf4j+log4j2



**SpringBoot日志实现**

* SpringBoot默认使用JCL和Logback



**SpringBoot日志配置**

* SpringBoot的日志系统会自动根据classpath下的依赖选择合适的日志，首选logback

* 如果使用SpringBoot进行日志配置，对日志的配置写在application.yml文件中

* 使用SpringBoot进行日志配置，粒度大，如果想要实现更细粒度的日志配置，需要使用**日志实现的原生配置**，例如

  **Logback**的**classpath:logback.xml**，**Log4j**的**classpath：log4h.xml**，如何这些日志配置文件存在于classpath下，SpringBoot会自动加载这些配置文件



**Logback日志配置**

* 默认的Logback配置文件名有2种
  * logback.xml：该配置文件会被日志框架加载
  * logback-spring.xml：该配置文件不会被日志框架加载，而是由SpringBoot去解析
* SpringBoot为Logback提供了4个默认的配置文件，在包springframework.boot.logging.logback中
  * defaults.xml：提供了公共的日志配置，日志输出规则等
  * console-appender.xml：使用CONSOLE_LOG_PATTERN添加一个ConsoleAppender
  * file-appender.xml：添加一个RollingFileAppender
  * base.xml：为兼容旧版SpringBoot提供



**Log4j配置**

* 如果想要使用Log4j或者Log4j2，除了要导入响应的依赖外，还需要排除掉已有的Logback（SpringBoot首选Logback）
* 在classpath下创建log4j.xml或者log4j2.xml（SpringBoot没有为Log4j提供默认的配置文件，需要我们人工配置，配置文件会由日志框架读取）

