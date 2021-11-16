# 8. 主从复制

* 主从复制，就是将一台Redis服务器的数据，复制到其他Redis服务器，前者称为master，后者称为slav

* 数据的复制是单向的，只能由master到slaver

* master以写作主，slave以读为主

  master可写，slave只能读不能写

  master中所有的数据和信息都会自动被slave保存

* 如果master宕机，slave仍能提供读服务，master重启后会再次加入集群

* 如果slave宕机后，slave重启会自动同步master数据

* 默认情况下，每个Redis都是master，我们只需要配置从机即可

![主从复制](D:\桌面\Notes\Notes\数据库\Redis\p\主从复制.png)



## 8.1 主从复制作用

1. 数据冗余：主从复制实现了数据的热备份
2. 故障恢复：当master出现问题时，可以由slave提供服务，实现快速的故障恢复
3. 负载均衡：在主从复制的基础上，配合读写分离，可以由master提供写服务，slave提供读服务，分担服务器负载
4. 高可用基石：主从复制是哨兵模式和集群模式能够实施的基础



## 8.2 搭建主从复制集群

### 查看当前Redis server的集群信息

```
info replication
```



### 命令行配置(临时配置，不推荐)

```
从机运行如下配置
slaveof host port
```



### 配置文件配置

```
replicaof <masterip>
```

# 