在分布式计算中，leader选举是指定一个节点来进行任务分配

任务开始前，所有的节点都不知道哪个节点会成为leader，在进行leader选举算法后，会选举一个leader节点出来

**LeaderLatch和LeaderSelector就是用来选举一个节点作为leader的**



### LeaderLatch

```
    public LeaderLatch(CuratorFramework client, String latchPath);
    public LeaderLatch(CuratorFramework client, String latchPath, String id);
    public LeaderLatch(CuratorFramework client, String latchPath, String id, CloseMode closeMode);
    
    //client：客户端实例
    //latchPath：用于选举的路径
    //id：参与者的id
    //closeMode：LeaderLatch显示关闭时的行为（有SILENT和NOTIFY_LEADER两种)
    //SILENT：关闭时，不触发Lisener    NOTIFY_LEADER：关闭时，触发Lisener
```

**原理**

每个LeaderLatch通过start()开启后，会在latchPath下创建临时顺序节点（如果latchPath不存在，那么会先创建latchPath的临时节点；最好保证latchPath下没有子节点）

如果自己的临时顺序节点是最小的，那么该LeaderLatch会把hasLeadership设为true，成为Leader，并触发添加的LeaderLatcherLisener的isLeader()；

如果不是最小的，那么该LeaderLatch会监听比自己小一号的节点，当这个节点消失时，LeaderLatch会再次判断自己是不是最小的。

当LeaderLatch调用close()时，会删除自己创建的临时顺序节点，同时会把hashLeadership设为false，并根据CloseMode决定和自己是不是leader来判断是否触发LeaderLatcherLisener的notLeader()



**属性**

* **AtomicBoolean hasLeadership**：表明该持有LeaderLatch的客户端是否为leader



**方法**

* **start()**：开启选举，在latchPath下创建临时顺序节点

  * 如果自己创建的临时顺序节点是最小的，那么该客户端成为leader，并把自己的LeaderLatch中的hasLeadership由false变为true（如果添加了listener，**触发LeaderLatchLisener的isLeader()** ）
  * 如果自己创建的临时顺序节点不是最小的，那么会监听比自己小一号的节点，如果该节点消失，则LeaderLatch再次判断自己是不是最小的。

* **addListener(LeaderLatcherLisener listener)**：为选举过程添加Lisener

  ```java
  public interface LeaderLatchListener
  {
    //当客户端的LeaderLatch的hasLeadership为false变为true时触发
    public void isLeader();
  
    //当客户端的LeaderLatch的hasLeadership为true变为false时触发
    public void notLeader();
  }
  ```

* **addListener(LeaderLatcherLisener listener)**：为选举过程添加Lisener

* **close()**：关闭选举，删除创建的临时顺序节点，同时把LeaderLatch的hasLeadership设置为false，并根据CloseMode的类型和自己是不是Leader来判断是否触发**LeaderLatcherLisener的notLeader()**

  * closeMode为ILENY：不做任何操作
  * closeMode为NOTIFY_LEADER：如果添加了lisener，**触发LeaderLatchLisener的notLeader()**




### LeaderSelector

利用Curator中的**InterProcessMutex分布式锁**进行抢锁，抢到锁的就是leader

**方法**

* **start()**：异步开启，尝试取得锁，在后台完成（调用后立即返回），具体细节看源码

  其他方法看源码