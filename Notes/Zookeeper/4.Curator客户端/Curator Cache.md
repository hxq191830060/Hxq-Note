**Curator事件有两种模式**

* **观察模式**：Watcher监听器**（一次性的）**

  ```
          client.getData().usingWatcher(new Watcher() {
              @Override
              public void process(WatchedEvent watchedEvent) {
                  
              }
          })
  ```

  WatchEvent只提供了KeeperState，EventType，path三种属性

  

* **缓存监听模式**

  引入了一种本地缓存数据的Cache机制，实现对Zookeeper服务端事件的监听

  **Cache在客户端缓存了关注的节点的数据(可以理解为本地视图)，会不断的与服务器上的节点数据进行对比(可以理解为服务端视图)，根据对比结果来判断是否触发Event**

  





**Cache的原理——本地缓存的数据(本地视图)与Zookeeper的服务端数据（服务端视图）进行对比，根据对比结果判断是否发送Event**

------



### **NodeCache（只监听单个节点的数据变化）**

**构造方法**

```java
public NodeCache(CuratorFramework client, String path);
public NodeCache(CuratorFramework client, String path, boolean dataIsCompressed);

* client：Curator客户端实例
* path：监听的节点路径
* dataIsCompressed：是否进行数据压缩
```

在本地缓存监听的节点的数据，关注服务器上该节点，如果服务器上该节点发生create/update/delete事件，那么会从服务器pull down节点数据来更新本地缓存(delete事件的话，data为null)，**同时注册一个NodeCacheLisener，调用其nodeChanged()**(节点不存在，也可以加NodeCache，这样该节点创建时就可以触发NodeCache）

**触发Lisener时，会调用NodeCacheLisener的nodeChanged()方法**

```java
public interface NodeCacheListener
{
    /**
     * Called when a change has occurred
     */
    public void nodeChanged() throws Exception;
}
```

**NodeCache必须通过start()开启**

```
//开启Cache，Cache默认不开启，必须调用该方法开启
public void start() throws Exception{
    start(false);
}

public void start(boolean buildInitial)throws Exception;
```

buildInitial

* true：启动Cache前，从服务器获取监听的节点信息来初始化视图
* false：视图初始化为空，启动Cache（如果服务器中关注的监听的节点存在，那么开启Cache后就会触发一次Event）

**触发条件**

* 节点创建，更改数据，删除
* 子节点如何都不会触发



------



### **PathChildrenCache（只监听指定节点的一级子节点的数据变化）**

**构造方法**

```
public PathChildrenCache(CuratorFramework client, String path, boolean cacheData)
public PathChildrenCache(CuratorFramework client, String path, boolean cacheData, ThreadFactory threadFactory)
public PathChildrenCache(CuratorFramework client, String path, boolean cacheData, boolean dataIsCompressed, ThreadFactory threadFactory)
public PathChildrenCache(CuratorFramework client, String path, boolean cacheData, boolean dataIsCompressed, final ExecutorService executorService)
public PathChildrenCache(CuratorFramework client, String path, boolean cacheData, boolean dataIsCompressed, final CloseableExecutorService executorService)

* client：客户端实例
* path；监听的节点路径
* cacheData：是否把节点内容缓存起来，如果配置为true。那么客户端在接收到节点列表变更的同时，也能够获取到节点的数据内容；如果配置为false则无法获取到节点的数据内容。
* threadFactory：利用这个参数，开发者可以构造一个专门的线程池，来处理事件
* executorService：自定义线程池
```

监听一个节点，在本地缓存该节点的所有子节点的数据(不缓存该节点的数据),当服务器上该节点的子节点发生create/update/delete事件时，从服务器pull down子节点数据来更新本地缓存，**同时注册一个PathChildrenCacheListener，调用其childEvent()**

```java
public interface PathChildrenCacheListener
{
    /**
     * Called when a change has occurred
     *
     * @param client the client
     * @param event describes the change
     * @throws Exception errors
     */
    public void childEvent(CuratorFramework client, PathChildrenCacheEvent event) throws Exception;
}
```



**PathChildrenCache必须通过start()开启**——有三种模式（通过枚举类StartMode)

* NORMAL(默认开启方式)：将本地视图初始化为空，启动Cache（如果服务器上监听的节点存在，那么开启Cache后就会触发一次Event）
* BULD_INITIAL_CACHE：从服务器获取监听的节点的数据来初始化本地视图，再启动Cache
* POST_INITIALIZED_EVENT：本地视图初始化为空，启动Cache，触发一个**PathChildrenCacheEvent.Type.INITIALIZED **



**触发条件**

* 任何一个一级子节点创建，修改数据，删除
* 自己的修改和删除不会触发





------



### TreeCache（监听节点及其所有的各级的子节点的数据变化）

**构造方法**

```java
//TreeCache只对外提供这一个构造方法
public TreeCache(CuratorFramework client, String path)
{
    this(client, path, true, false, Integer.MAX_VALUE, Executors.newSingleThreadExecutor(defaultThreadFactory), false, false, new DefaultTreeCacheSelector());
}



/**该构造方法不对外暴露
 * @param client           the client
 * @param path             path to watch（监听的节点路径）
 * @param cacheData        if true, node contents are cached in addition to the stat（默认为true，用于配置是否把节点内容缓存起来，如果配置为true。那么客户端在接收到节点列表变更的同时，也能够获取到节点的数据内容；如果配置为false则无法获取到节点的数据内容。）
 * @maxDepth：  监听的最大深度，/t/t1/t2,监听/t,那么深度为3,
 * @param dataIsCompressed if true, data in the path is compressed（默认为false，是否进行数据压缩）
 * @param executorService  Closeable ExecutorService to use for the TreeCache's background thread
 * @param createParentNodes true to create parent nodes as containers（默认为false）
 * @param disableZkWatches true to disable Zookeeper watches（默认为false）
 * @param selector         the selector to use（默认为DefaultTreeCacheSelector）
 */
 
TreeCache(CuratorFramework client, String path, boolean cacheData, boolean dataIsCompressed, int maxDepth, final ExecutorService executorService, boolean createParentNodes, boolean disableZkWatches, TreeCacheSelector selector)
{
    this.createParentNodes = createParentNodes;
    this.selector = Preconditions.checkNotNull(selector, "selector cannot be null");
    this.root = new TreeNode(validatePath(path), null);
    Preconditions.checkNotNull(client, "client cannot be null");
    this.client = client.newWatcherRemoveCuratorFramework();
    this.cacheData = cacheData;
    this.dataIsCompressed = dataIsCompressed;
    this.maxDepth = maxDepth;
    this.disableZkWatches = disableZkWatches;
    this.executorService = Preconditions.checkNotNull(executorService, "executorService cannot be null");
}
```



TreeCache监听自身+自身所有的各级子节点

在本地缓存监听节点+监听节点所有的各级节点的数据(本地视图),当服务器上数据发生变化时，从服务器pull down数据来更新缓存的数据，同时**注册一个TreeCacheLisener，调用其childEvent**

```java
public interface TreeCacheListener
{
    /**
     * Called when a change has occurred
     *
     * @param client the client
     * @param event  describes the change
     * @throws Exception errors
     */
    
    public void childEvent(CuratorFramework client, TreeCacheEvent event) throws Exception;
}

public class TreeCacheEvent
{
    private final Type type; //事件类型
    private final ChildData data; //发生事件的节点，在事件发生后的data+stat
    private final ChildData oldData;//发生事件的节点，在事件发生前的data+stat
    
}
```



------



### CuratorCache（对NodeCache，PathChildrenCache，TreeCache做了整合）

```java
public interface CuratorCache extends Closeable, CuratorCacheAccessor
{
    /**
     * Cache模式选择枚举
     */
    enum Options
    {    
        //传入该Option，只会监听当前节点
        SINGLE_NODE_CACHE,


        //传入该Option，压缩数据
        COMPRESSED_DATA,


        //通常，关闭Cache的时候，缓存会clear，但传入该Option后，关闭Cache，缓存不会清空
        DO_NOT_CLEAR_ON_CLOSE
    }

    //返回一个CuratorCache，可以传入任意个的Options，默认情况下，监听当前节点+其所有的各级的子节点
    static CuratorCache build(CuratorFramework client, String path, Options... options)
    {
        return builder(client, path).withOptions(options).build();
    }

    /**
     * Start a Curator Cache builder
     *
     * @param client Curator client
     * @param path path to cache
     * @return builder
     */
    static CuratorCacheBuilder builder(CuratorFramework client, String path)
    {
        return new CuratorCacheBuilderImpl(client, path);
    }

    /**
     * Start a Curator Cache Bridge builder. A Curator Cache Bridge is
     * a facade that uses {@link org.apache.curator.framework.recipes.cache.CuratorCache} if
     * persistent watches are available or {@link org.apache.curator.framework.recipes.cache.TreeCache}
     * otherwise (i.e. if you are using ZooKeeper 3.5.x).
     *
     * @param client Curator client
     * @param path path to cache
     * @return bridge builder
     */
    static CuratorCacheBridgeBuilder bridgeBuilder(CuratorFramework client, String path)
    {
        return new CuratorCacheBridgeBuilderImpl(client, path);
    }
```

