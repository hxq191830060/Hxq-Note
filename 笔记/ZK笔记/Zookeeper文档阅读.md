### 第一部分

* 为分布式应用提供 **分布式协调服务**

* Zookeeper数据结构为Znode，存储在内存中

* 组成Zookeeper集群的服务器们必须知道彼此的存在——他们在内存中保存由 state image，在持久化存储(可以是磁盘)保存事务日志和内存数据库的快照

* Znode结构

  * stat：包括了版本号，时间戳
    * czxid
    * mzxid
    * pzxid
    * version
    * cversion
    * aversion
    * ctime：该节点创建时的时间
    * mtime：该节点最后一次修改的时间
    * ephemeralOwner：如果节点是临时节点，那么该字段为创建节点的Session的SessionID，否则为0
    * dataLength：该节点data的长度
    * numChildren：该节点的子节点数目
  * data：每个节点都有一个ACL列表，ACL列表 规定了可以对Znode的data做哪些操作
  * children node

* **Zookeeper的数据一致性**

  * 顺序一致性：来自一个客户端的更新操作按照发送的顺序处理
  * 原子性：节点数据的更新要么都成功，要么都失败，不会只更改一部分data
  * 系统视图唯一性：client不管连接到哪个服务器，都看到唯一的系统视图（各个服务器的数据一致）
  * 可靠性：一旦更新操作完成，数据将被持久化到服务器上，不会丢失
  * 及时性：Client看到的视图在一定时间范围内是最新的

* 服务器组成

  * Request Processor(只有leader才有，处理写请求用的)
  * Automic Broadcast：原子广播
  * Replicated Database：内存上的数据库，包含有整棵data tree各个Server的Replicated Database保持数据一致

  读操作：Client直接从相连的Server的Replicated Database中读取数据

  写操作：Client将写请求发送给相连的Server，相连的Server把写请求转发给leader，leader会计算该请求成功时系统的状态，然后将写请求转换为**一个捕获这个新状态的proposal，并为proposal生成一个ZXid**，将 proposal发给所有的follower，**各个follow开始写操作（先把操作内容写入磁盘上的日志，然后才会更新内存中的数据）**，如果写成功，返回一个ACK给leader，leader统计ACK，如果超过半数，则写成功，返回代码给客户端**（使用原子传播协议）**



### 第二部分

Zookeeper以多种方式跟踪事件

* **ZXid（事务ID）——保证事务的顺序一致性**

  集群内全局有序，每个更新操作在leader那会转换为一个proposal，每个proposal都会分配一个ZXid（64位，前32位是epoch，表识Leader是否改变，选举新的Leader，epoch+=1；后32位用来计数，每产生一个proposal，后32位+1）

  * czxid：创建节点的事务的ZXid
  * mzxid：最近一次修改节点数据的事务的ZXid
  * pzxid：该节点的子节点最后一次创建/删除的事务的ZXid

* 版本号

  节点的数据每改变一次，节点的三个版本号中的一个就会增长

  * version：节点数据变化的次数
  * cversion：节点的子节点的数据变化的次数
  * aversion：节点的ACL变化的次数

* Ticks

  当使用Zookeeper集群模式时，服务器们使用ticks来事件需要的时间，tick time*2=Session timeout

* Real Time





**Zookeeper Session（重要）**

