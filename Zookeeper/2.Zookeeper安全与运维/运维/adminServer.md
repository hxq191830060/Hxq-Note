**Zookeeper的运维有两种方式**

* **JMX**
* **通过adminServer执行四字命令**

### adminServer

**配置（单机集群一定要更改port，不然只能访问一台的信息）**

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



**command_name**

* *connection_stat_reset/crst*: Reset all client connection statistics. No new fields returned.
* *configuration/conf/config* : Print basic details about serving configuration, e.g. client port, absolute path to data directory.
* *connections/cons* : Information on client connections to server. Note, depending on the number of client connections this operation may be expensive (i.e. impact server performance). Returns "connections", a list of connection info objects.
* *hash*: Txn digests in the historical digest list. One is recorded every 128 transactions. Returns "digests", a list to transaction digest objects.
* *dirs* : Information on logfile directory and snapshot directory size in bytes. Returns "datadir_size" and "logdir_size".
* *dump* : Information on session expirations and ephemerals. Note, depending on the number of global sessions and ephemerals this operation may be expensive (i.e. impact server performance). Returns "expiry_time_to_session_ids" and "session_id_to_ephemeral_paths" as maps.
* *environment/env/envi* : All defined environment variables. Returns each as its own field.
* *get_trace_mask/gtmk* : The current trace mask. Read-only version of *set_trace_mask*. See the description of the four letter command *stmk* for more details. Returns "tracemask".
* *initial_configuration/icfg* : Print the text of the configuration file used to start the peer. Returns "initial_configuration".
* *is_read_only/isro* : A true/false if this server is in read-only mode. Returns "read_only".
* *last_snapshot/lsnp* : Information of the last snapshot that zookeeper server has finished saving to disk. If called during the initial time period between the server starting up and the server finishing saving its first snapshot, the command returns the information of the snapshot read when starting up the server. Returns "zxid" and "timestamp", the latter using a time unit of seconds.
* *leader/lead* : If the ensemble is configured in quorum mode then emits the current leader status of the peer and the current leader location. Returns "is_leader", "leader_id", and "leader_ip".
* *monitor/mntr* : Emits a wide variety of useful info for monitoring. Includes performance stats, information about internal queues, and summaries of the data tree (among other things). Returns each as its own field.
* *observer_connection_stat_reset/orst* : Reset all observer connection statistics. Companion command to *observers*. No new fields returned.
* *ruok* : No-op command, check if the server is running. A response does not necessarily indicate that the server has joined the quorum, just that the admin server is active and bound to the specified port. No new fields returned.
* *set_trace_mask/stmk* : Sets the trace mask (as such, it requires a parameter). Write version of *get_trace_mask*. See the description of the four letter command *stmk* for more details. Returns "tracemask".
* *server_stats/srvr* : Server information. Returns multiple fields giving a brief overview of server state.
* *stats/stat* : Same as *server_stats* but also returns the "connections" field (see *connections* for details). Note, depending on the number of client connections this operation may be expensive (i.e. impact server performance).
* *stat_reset/srst* : Resets server statistics. This is a subset of the information returned by *server_stats* and *stats*. No new fields returned.
* *observers/obsr* : Information on observer connections to server. Always available on a Leader, available on a Follower if its acting as a learner master. Returns "synced_observers" (int) and "observers" (list of per-observer properties).
* *system_properties/sysp* : All defined system properties. Returns each as its own field.
* *voting_view* : Provides the current voting members in the ensemble. Returns "current_config" as a map.
* *watches/wchc* : Watch information aggregated by session. Note, depending on the number of watches this operation may be expensive (i.e. impact server performance). Returns "session_id_to_watched_paths" as a map.
* *watches_by_path/wchp* : Watch information aggregated by path. Note, depending on the number of watches this operation may be expensive (i.e. impact server performance). Returns "path_to_session_ids" as a map.
* *watch_summary/wchs* : Summarized watch information. Returns "num_total_watches", "num_paths", and "num_connections".
* *zabstate* : The current phase of Zab protocol that peer is running and whether it is a voting member. Peers can be in one of these phases: ELECTION, DISCOVERY, SYNCHRONIZATION, BROADCAST. Returns fields "voting" and "zabstate".