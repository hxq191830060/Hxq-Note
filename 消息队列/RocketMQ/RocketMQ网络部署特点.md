![RocketMQ网络部署架构](p/RocketMQ网络部署架构.png)

* NameServer是一个无状态节点，集群部署，节点之间不会进行数据同步
* Broker分为Master和Slave
  * Master——Master Broker与NameServer集群中的所有节点建立长连接，定期注册路由信息到所有的NameServer
  * Slave——所有的Slave都会同步Master的数据，但是只有 BrokerId=1的Slave才会参与消息的读负载
* Producer无状态，可集群部署，Producer与NameServer集群中的其中一个节点（随机选择）建立长连接，定期从NameServer获取Topic路由信息，并向提供Topic 服务的Master建立长连接，且定时向Master发送心跳

* Consumer与NameServer集群中的其中一个节点（随机选择）建立长连接，定期从NameServer获取Topic路由信息，并向提供Topic服务的Master、Slave建立长连接，且定时向Master、Slave发送心跳
  * Consumer可以从Master Broker订阅消息，也可以从Slave Broker订阅消息
  * 消费者在向Master拉取消息时，Master服务器会根据拉取偏移量与最大偏移量的距离（判断是否读老消息，产生读I/O），以及从服务器是否可读等因素建议下一次是从Master还是Slave拉取