默认情况 ，Zookeeper的网络通信是没有加密的，但是Zookeeper提供了SSL特性，但是**仅应用在Client和Server端之间的交互**，Server和Server之间的交互不支持（新版本支持了）



Zookeeper最初是通过JavaNIO实现的，后来用Netty代替NIO，因为Netty更好的支持SSL

SSL只在Netty通信中支持，所以要使用SSL，必须**启用Netty**



**要启用SSL，必须启动Netty**，分为**Client配置**和**Server配置**

* **Client**

  * **启用Netty**

    ```
    zookeeper.clientCnxnSocket="org.apache.zookeeper.ClientCnxnSocketNetty"
    ```

  * **启用SSL**

    ```
    zookeeper.client.secure=true
    ```

    **注意，设置secure属性后，client只能连接Server的secureClientPort**

  * **设置SSL**

    ```
    zookeeper.ssk.keyStore.location="path"
    zookeeper.ssl.keyStore.password="keystore_password"
    zookeeper.ssl.trustStroe.location="path"
    zookeeper.ssl.trustStroe.password="truststroe_password"
    ```

    

* **Server**

  * **启用Netty**

    ```
    zookeeper.serverCnxnFactory="org.apache.zookeeper.server.NettyServerCnxnFactory"
    ```

  * **Server的zoo.cfg中设置secureClientPort**

    ```
    secureClientPort=2181
    ```





```
使用实例
Server的zoo.cfg中添加：
secureClientPort=2281

bin/zkServer.sh配置如下
export SERVER_JVMFLAGS=”
-Dzookeeper.serverCnxnFactory=org.apache.zookeeper.server.NettyServerCnxnFactory
-Dzookeeper.ssl.keyStore.location=/root/zookeeper/ssl/testKeyStore.jks
-Dzookeeper.ssl.keyStore.password=testpass
-Dzookeeper.ssl.trustStore.location=/root/zookeeper/ssl/testTrustStore.jks
-Dzookeeper.ssl.trustStore.password=testpass”

bin/zkCli.sh配置如下
export CLIENT_JVMFLAGS=”
-Dzookeeper.clientCnxnSocket=org.apache.zookeeper.ClientCnxnSocketNetty
-Dzookeeper.client.secure=true
-Dzookeeper.ssl.keyStore.location=/root/zookeeper/ssl/testKeyStore.jks
-Dzookeeper.ssl.keyStore.password=testpass
-Dzookeeper.ssl.trustStore.location=/root/zookeeper/ssl/testTrustStore.jks
-Dzookeeper.ssl.trustStore.password=testpass”
```



**X509AuthenticationProvider**

默认情况下，SSL认证是由X509AuthenticationProvider提供的，对应的schema为**x509**

X509AuthenticationProvider是基于javax.net.ssl.X509KeyManager与javax.net.ssl.X509TrustManager提供的Host的证书认证机制

X509AuthenticationProvider仅仅当zookeeper.serverCnxnFactory配置为NettyServerCnxnFactory时才可以使用，Zookeeper内置的NIO实现类NIOServerCnxnFactory不支持SSL

Client认证成功之后，会创建一个Zookeeper Session，Client可以设置ACLs的schema为“x509"

“x509"可以使用Client认证成功后的X500 Principal作为ACL ID，ACL信息中包含Client认证后的确切的Principal名称