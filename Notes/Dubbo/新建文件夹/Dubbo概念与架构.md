### RPC通信

Dubbo3提供了Triple(Dubbo3)，Dubbo2协议

集成了gRPC，Thrift，jsonRPC，Hessian2，REST



**协议**

* 协议是RPC的核心，规范了数据在网络中的传输内容和格式

* 协议的内容包含三部分

  * 数据交换格式

    定义了RPC的请求和响应对象在网络传输中的字节流内容

  * 协议格式

    定义**包含字段列表**和**各字段语义**以及**不同字段的排序方式**

  * 协议通过定义规则，格式和语义来约定数据如何在网络间传输



### 服务流量管理

通过Dubbo定义的路由规则，实现对流量分布的控制

* 流量管理的本质

  将**请求**根据制定好的**路由规则**分发到**应用服务**上

  ![What is traffic control](https://dubbo.apache.org/imgs/v3/concepts/what-is-traffic-control.png)

  * 路由规则可以有多个，不同路由规则之间存在优先级

    如：Router(1)>Router(2)>......>Router(n)

  * 一个路由规则可以路由到不同的应用服务

    如：Router(2)可以路由到Service(1),Service(2)

  * 路由规则也可以将一个请求**不分发**到任何服务上

  * 应用服务可以是单个实例，也可以是一个应用集群 

  

* Dubbo的流量管理

  Dubbo提供了支持mesh方式的流量管理测流

  **实现如下（详细的解释看文档）**

  * Dubbo将整个流量管理分成[VirtualService](https://dubbo.apache.org/zh/docs/references/routers/virtualservice/)和[DestinationRule](https://dubbo.apache.org/zh/docs/references/routers/destination-rule/)两部分

    当Consumer接收到一个请求时，会根据[VirtualService](https://dubbo.apache.org/zh/docs/references/routers/virtualservice/)中定义的[DubboRoute](https://dubbo.apache.org/zh/docs/references/routers/virtualservice/#dubboroute)和[DubboRouteDetail](https://dubbo.apache.org/zh/docs/references/routers/virtualservice/#dubboroutedetail)匹配到对应的[DubboDestination](https://dubbo.apache.org/zh/docs/references/routers/virtualservice/#dubbodestination)中的[subnet](https://dubbo.apache.org/zh/docs/references/routers/destination-rule/#subset)，最后根据[DestinationRule](https://dubbo.apache.org/zh/docs/references/routers/destination-rule/)中配置的[subnet](https://dubbo.apache.org/zh/docs/references/routers/destination-rule/#subset)信息中的labels找到对应需要具体路由的Provider集群

    * [VirtualService](https://dubbo.apache.org/zh/docs/references/routers/virtualservice/)主要处理入站流量分流的规则，支持服务级别和方法级别的分流。
    * [DubboRoute](https://dubbo.apache.org/zh/docs/references/routers/virtualservice/#dubboroute)主要解决服务级别的分流问题。同时，还提供的重试机制、超时、故障注入、镜像流量等能力。
    * [DubboRouteDetail](https://dubbo.apache.org/zh/docs/references/routers/virtualservice/#dubboroutedetail)主要解决某个服务中方法级别的分流问题。支持方法名、方法参数、参数个数、参数类型、header等各种维度的分流能力。同时也支持方法级的重试机制、超时、故障注入、镜像流量等能力。
    * [DubboDestination](https://dubbo.apache.org/zh/docs/references/routers/virtualservice/#dubbodestination)用来描述路由流量的目标地址，支持host、port、subnet等方式。
    * [DestinationRule](https://dubbo.apache.org/zh/docs/references/routers/destination-rule/)主要处理目标地址规则，可以通过hosts、[subnet](https://dubbo.apache.org/zh/docs/references/routers/destination-rule/#subset)等方式关联到Provider集群。同时可以通过[trafficPolicy](https://dubbo.apache.org/zh/docs/references/routers/destination-rule/#trafficpolicy)来实现负载均衡。



### 部署架构

**整体架构**

![//imgs/v3/concepts/threecenters.png](https://dubbo.apache.org/imgs/v3/concepts/threecenters.png)

Dubbo定义了一些中心化组件（并非都是必要的）

* 注册中心

  协调Consumer和Provider之间的地址注册与发现

* 配置中心

  * 存储Dubbo启动阶段的全局配置，保证配置的跨环境共享与全局一致性
  * 负责服务治理规则(路由规则，动态配置等)的存储与推送

* 元数据中心

  * 接受Provider上报的服务接口元数据，为Admin等控制台提供运维能力
  * 作为服务发现机制的补充，提供额外的接口/方法级别配置信息的同步能力，相当于注册中心的额外拓展