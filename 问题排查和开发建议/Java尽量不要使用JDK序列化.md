尽量少使用JDK序列化

> 实现Serializable接口即可

* JDK序列化是将对象转换为二进制流，其产生的二进制流只有Java项目才可以识别，跨平台性差，失去了异构系统的跨平台性

* JDK序列化是不安全的
* JDK序列化执行效率明显低于JSON序列化



**推荐的序列化方案**

* SpringBoot内置的Jackson的JSON序列化
* Dubbo内置的hessian序列化方案
* Google的Protocol Buffers序列化