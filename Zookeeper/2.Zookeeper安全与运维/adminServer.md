**Zookeeper的运维有两种方式**

* **JMX**
* **通过adminServer执行四字命令**





### adminServer

#### 相关配置

* **admin.protUnification**

  让admin的port可以接受HTTP和HTTPS，默认关闭

* **amin.enableServer**

  false：关闭AdminServer，默认开启

* **admin.serverAddress**

  embedded Jetty服务监听的地址，默认为0.0.0.0

* **admin.serverPort**

  embedded Jetty服务监听的端口，默认为8080

* **admin.idleTimeout**

  一个连接最大的等待时间，默认为30000ms

* **admin.commandURL**

  相对于根URL，列出所有命令的的URL



#### four-word-command

AdminServer是一个提供了4词command HTTP接口的embedded Jetty服务

通过**admin.serverAddress:admin.serverPort/commands/command_name** 可以获得Zookeeper集群的信息

* conf

  输出Zookeeper服务器使用的基本配置信息——包含clientPort,dataDir,tickTime等

* cons

  输出这台服务器上所有的客户端连接的详细信息，包括每个客户端的客户端IP，会话ID和最后一次与服务器交互的操作类型

* crst

  重置所哟客户端的连接统计信息

* dump

  输出当前集群的所有会话信息，包括会话的会话ID，以及每个会话创建的临时节点的信息

* envi

  输出Zookeeper服务器运行时的环境信息，包括os.version，java.version和user.home等

* ruok

  输出当前Zookeeper服务器是否正在运行，如果正在运行，返回"imok"，否则没有任何响应输出

  （但是不准，不推荐使用）

* stat

  返回Zookeeper服务器的运行时状态信息，包括Zookeeper的版本，打包信息，集群数据节点等，同时还会打印客户端连接信息

* srvr

  同stat，不过不会打印客户端连接信息

* srst

  重置所有的服务器统计信息

* wchs

  输出当前服务器上管理的Watcher的概要信息

* wchc

  输出当前服务器上管理的Watcher的详细信息，以会话为单位进行归组

* wchp

  同上，不过以姐节点路径为单位进行归组

* mntr

  输出更为详尽的服务器统计信息
  
* isro/is_read_only

  当前节点是否只读

* hash

  数字摘要

* lsnp/last_snaphot

  获取最后一次快照的信息

* icfg/initial_configuration

  服务器启动初始配置

* obsr/observers

  获取Observer信息

* sysp/system_properties

  跟envi相似，不同的是还会多返回zookeeper开头的自定义配置

* lead/leader

  当前节点是否为Leader

* voting_view

  集群选票信息

* zabstate

  ZAB状态信息汇总


