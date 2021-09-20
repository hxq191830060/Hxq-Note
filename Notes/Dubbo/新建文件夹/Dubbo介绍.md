### **提供的服务**

* RPC通信

  * 流式通信

* 微服务治理

  * 服务发现

    微服务组件之间独立演进并任意部署，消费端**无需知道**生产端的**部署位置和IP**即可完成通信

    服务发现有以下实现方法

    * 使用独立的注册中心组件，如Nacos，Zookeeper，Consul，Etcd等
    * 把服务的组织与注册交给底层容器平台

  * 流量管控

    **服务发现**使得Dubbo请求可以发送到任意一个IP实例上，这个过程流量随机分配，当然我们可以采用**流量管控**策略，基于这些策略可以实现多种路由方案

    * 负载均衡
    * 流量路由
    * 请求超时
    * 流量降级
    * 重试
    * .............



### **提供的通信模型**

* 同步的Request-Response
* 消费端异步请求(Client Side Asynchronous Request-Response)
* 提供端异步执行(Server Side Asynchronous Request-Response)
* 消费端请求流(Request Streaming)
* 提供端响应流(Response Streaming)
* 双向流式通信(Bidirectional Streaming)
