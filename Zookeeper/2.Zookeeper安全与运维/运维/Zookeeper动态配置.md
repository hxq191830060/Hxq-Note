3.5.0之前，Zookeeper的配置是静态的——在启动时加载配置，在运行时不可变

3.5.0之后，Zookeeper支持自动配置——不会中断服务并且保持数据一致性

------

3.5.0开始

配置参数

* 静态配置参数（从静态配置文件中读取，并且在运行时不可修改）
* 动态配置参数（可以在运行时修改）——**存储在/zookeeper/config节点中**

配置文件

* 静态配置文件
* 动态配置文件

配置方式

* 新的方式

  静态配置文件中通过**dynamicConfigFile**链接动态配置文件

  ```
  zoo.cfg的内容如下
  
  tickTime=2000
  dataDir=/zookeeper/data/zookeeper1
  initLimit=5
  syncLimit=2
  dynamicConfigFile=/zookeeper/conf/zoo_replicated1.cfg.dynamic
  ```

  

* 旧的方式

  该配置文件为静态配置文件，其中含有动态配置参数

  动态配置参数会剥离出来，形成动态配置文件

  ```
  zoo.cfg的内容如下
  
  tickTime=2000
  dataDir=/zookeeper/data/zookeeper1
  initLimit=5
  syncLimit=2
  clientPort=2791
  server.1=125.23.63.23:2780:2783:participant
  server.2=125.23.63.24:2781:2784:participant
  server.3=125.23.63.25:2782:2785:participant
  ```



因为动态配置与静态配置分离，所以我们修改动态配置参数后，推送到Zookeeper服务器，就会覆盖动态配置文件





**reconfig和getConfig**

* data tree中有个**/zookeeper/config**节点，存储着动态配置信息

  查看该节点数据，即可获得动态配置信息，并且最后一行是version，表示动态配置的版本（最近一次reconfig的zxid）

* **getConfig**

  **/zookeeper/config**节点，所有的用户都可以通过getConfig( )方法或者直接读取/zookeeper/config节点来获取配置信息

* **reconfig**

  动态配置的API——**类ZookeeperAdmin**

  动态配置存储在/zookeeper/config中，修改动态配置即修改/zookeeper/config的data，需要有/zookeeper/config的写权限(ACL)

  * super user和明确配置了/zookeeper/config的写权限的用户可以写/zookeeper/config来进行reconfig
  * **skipACL**禁用ACL，任何用户都可以写/zookeeper/config来进行reconfig



------

动态配置使用demo

https://backendhouse.github.io/post/zookeeper_dynamic_config/

https://zhuanlan.zhihu.com/p/364879977

3.5.0后，加入了动态配置功能

核心有两个

* 配置分为静态配置和动态配置，配置文件剥离为静态配置文件zoo.cfg和动态配置文件

  静态配置文件zoo.cfg中必须**reconfigEnabled=true**才能开启动态配置，并且用**dynamicConfigFile**来导入动态配置文件

* 动态配置除了存储在动态配置文件中，也会存储在/zookeeper/config中，该节点所有用户都有权利查看，但只有super user有权利进行写，也就是只有super user有权利进行动态配置，所有我们可以通过在zoo.cfg中添加**skipACL=yes**让所有用户都可以进行动态配置



假设我先启动三台Zookeeper Server，zoo.cfg配置文件如下书写

```
tickTime=2000
dataDir=/zookeeper/data/zookeeper1
initLimit=5
syncLimit=2
clientPort=2791
//以下为动态配置的关键
reconfigEnabled=true
//跳过权限验证
skipACL=true
//动态配置文件中写有集群成员信息
dynamicConfigFile=/zookeeper/conf/zoo_replicated1.cfg.dynamic
```

* 添加

  现在我要加入第四台Server，该Server的配置文件与集群中最后一个加入的Server相同，但是需要在动态配置文件中的集群成员信息中加入自己的信息

  1. 启动第四台Server，第四台Server会尝试加入集群，但会被拒绝

     第四台Server与leader同步数据，成为no voting follower

  2. 对集群中的Server执行**reconfig -add命令**或者**ZookeeperAdmin的reconfig()**

     集群中的每台Server都会生成一份新的动态配置文件zoo.cfg.dynamic.序列号，然后zoo.cfg中的dynamicConfigFile或指向该新的动态配置文件

  3. 当所有Server的动态配置文件中都加入了新的Server的信息，标志着第四台Server成功加入集群，第四台Server变为follower

  

* 删除

  现在我要删除一台Server，那么对该Server执行**reconfig -remove命令**或者**ZookeeperAdmin的reconfig()**

  执行完毕后所有的Server都会生成一份新的动态配置文件zoo.cfg.dynamic.序列号，然后zoo.cfg中的dynamicConfigFile或指向该新的动态配置文件

  注意，被删除的Server并不会关闭，而是变为non-voting-follower（不希望连接到该Server的客户端立即断开，从而导致其他Server的连接请求过多），**仍可以对外提供服务，也可以接受到Leader的消息**

