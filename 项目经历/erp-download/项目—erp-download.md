业务系统日常下载数据量大，频繁出现系统导出数据限制报错，影响业务运营效率，同时占用研发同学时间来导出数据，故增加离线下载功能，解决数据导出时数据限制问题

# 1. 下载任务提交

![下载任务提交](picture\下载任务提交.png)

1. 用户向业务系统发起离线下载请求（用户需要提供模板id，要查询的request参数，以及动态表头所需要提供的替换值），业务系统将离线下载请求转发给下载中心
2. 下载中心创建一个下载任务，存入数据库，并将任务提交给线程池，提交完成后，向业务系统返回 **添加成功**（后面的下载任务异步完成）
3. 线程池开始执行任务，根据模板ID从数据库取出模板详细信息，通过模板详细可以知道从哪个group的哪个Service的哪个方法可以获得需要的数据，然后利用request发起泛化调用，获得数据（这步会进行1+total/pageSize次，第一次会先获得数据总量total，然后根据模板中的分页限制，分页查询数据）
4. 将获得的数据写入到本地文件中（csv/xlsx），将文件上传到FDS服务器，获得一个下载的url
5. 将url存入数据库中，删除本地的文件

# 1. 实现的功能

## 1.1 静态配置接口

只提供一个用于获取静态配置的方法——静态配置（所有下载任务的状态码和状态名，支持的所有文件类型，文件描述和文件名）

## 1.2 健康检查接口

只有一个方法——用于健康检查

## 1.3 模板接口

提供erp_download_template的增删改查功能

## 1.4 下载接口（核心）

### 1.4.1 addDownloadTask()和dispatchDownloadTask()

addDownloadTask()底层调用的是dispatchDownloadTask()的逻辑，所以主要看dispatchDownloadTask()

* 任务是提交给**调度线程池**

* **调度线程池**中的**每一个调度线程**都对应一个**运行线程池**（调度线程池中有5个调度线程，那么就有5个运行线程池）

![创建下载任务.drawio](picture\创建下载任务.drawio.png)

### 1.4.2 retryDownloadTask()

仅可以对状态为ERROR的下载任务进行重试，该方法仅会把任务的状态更新为 DISPATCH_FAILED

然后由定时任务去重试

# 2. 定时任务

## 2.1 第一个定时任务

5分钟一次，对调度失败的任务进行重新调度

1. 通过Redis分布锁进行锁占用，如果占用成功，往下运行（保证同一时间只有一个实例在运行该逻辑，导致任务重复调度）
2. 取出数据库中所有状态为DISPATCH_FAILED的任务，对于每个任务，调用dispatchDownloadTask()进行重试
3. 释放分布式锁

## 2.2 第二个定时任务

1. 每分钟执行一次，打印调度线程池和所有的运行线程池的监控数据

2. 通过Redis分布式锁进行锁占用，如果占用成功，往下运行（保证同一时间只有一个实例在运行该逻辑）

3. 获得所有RUNNING的任务

4. 对于每个RUNNING的任务，检查其在Redis中是否存在对应的键值对，如果不存在，说明该任务在运行时系统宕机了，导致任务没执行完（按照代码逻辑，如果任务在Redis中不存在对应的键值对，那么任务的状态应该是COMPLETE），将任务的状态更新为DISPATCH_FAILED

5. 释放分布式锁

## 2.3 第三个定时任务

一天一次，删除30天前上传到fds的文件

# 3. 难点

## 3.1 任务状态说明

1. 任务刚创建后是**CREATE**

2. 任务提交给调度线程池后变为 **DISPATCH**（此时任务处于阻塞队列中）
   
   任务状态为 **DISPATCH**时，只要发生了错误，状态就变为 **DISPATCH_FAILED**

3. 当有调度线程开始执行任务——在Redis中进行分布式锁抢占（设置一个有过期时间的键值对），任务状态就变为RUNNING
   
   任务状态为 **RUNNING**时，只要发生了错误，状态机就变为 **ERROR**

4. 任务执行完后，变为 **COMPLETE**，然后删除Redis中对应的键值对

## 3.2 任务在运行时服务器宕机了，如何保证任务可以在服务器恢复后继续执行

**通过Redis和定时任务完成**

1. 提交下载任务到调度线程池后，下载任务等待空闲线程来执行，当下载任务被空闲线程执行时，会在Redis中设置一个有过期时间的键值对，当该任务执行完毕后，会将该键值对删除
   * 如果该键值对存在，那么说明任务还未执行完
   * 如果该键值对不存在，但是任务状态为RUNNING，那么说明任务正在执行时，服务器宕机了
2. 任务有6个状态——CREATE，DISPATCH，DISPATCH_FAILED，RUNNING，COMPLETE，ERROR，服务器宕机时任务一定这6个状态中的一个
   * COMPLETE——任务已经停止执行了，宕机不影响
   * ERROR——任务已经停止执行了，宕机不影响
   * DISPATCH_FAILED——有定时任务来重新调度，宕机不影响
   * CREATE——此时任务刚刚创建（刚写入数据库），但是还未提交给调度线程池，如果此时宕机，那么就会回滚
   * RUNNING——正在执行下载任务，如果此时宕机，那么任务会被强制终止，RUNNING状态的任务在Redis中存在一个唯一键值对，定时任务会发现处于RUNNING状态但是不存在对应键值对的任务，将其状态更改为DISPATCH_FAILED，然后由另一个定时任务负责对DISPATCH_FAILED的下载任务进行重新调度
   * DISPATCH——任务已经提交给了调度线程池，但还没有执行，处于阻塞队列中，如果此时宕机，那么任务会丢失，有定时任务会定期扫描DISPATCH的任务，将其状态改为DISPATCH_FAILED，然后由另一个定时任务负责对DISPATCH_FAILED的下载任务进行重新调度

## 3.3 Redis中分布式锁的应用

1. 保证定时任务逻辑一次只会由一个实例执行（取得分布式锁才能执行完整的定时任务逻辑）
2. 保证一个下载任务只会被一个实例执行（取得下载任务对应的分布式锁才能将其状态更新为RUNNING）
3. 用于表明RUNNING任务在执行过程中是否遭遇系统宕机

# 4. 离线下载模版

## 4.1 离线下载模板字段说明

| 字段                     | 含义                | 备注                                                |
|:----------------------:|:-----------------:|:-------------------------------------------------:|
| template_id            | 模版id              |                                                   |
| template_name          | 模版名称              |                                                   |
| service_name           | dubbo服务名          |                                                   |
| service_method         | dubbo方法名          |                                                   |
| service_method_request | dubbo方法参数名        | 仅支持单个参数                                           |
| service_group          | dubbo服务所在的group   |                                                   |
| service_version        | dubbo服务的版本        |                                                   |
| page_size              | 每次请求的数据量          |                                                   |
| size_limit             | 一次性导出数据总量的限制      | 如果数据总量超过total，会导出失败                               |
| file_name              | 导出文件名称            |                                                   |
| result_list_dir        | 用于生成文件的list的文职    | ![result_list_dri](picture\result_list_dri.png)   |
| header                 | 表头                | ![header](picture\header.png)                     |
| file_header_type       | 表头类型——1：普通  ，2：动态 | ![file_header_type](picture\file_header_type.png) |
| user                   | 模板创建人             |                                                   |
| status                 | 状态                |                                                   |

## 4.2 离线下载模板表结构

```sql
CREATE TABLE `erp_download_template` (
  `template_id` int(11) NOT NULL AUTO_INCREMENT COMMENT '模板id',
  `template_name` varchar(255) NOT NULL DEFAULT '' COMMENT '模板名称',
  `service_name` varchar(255) NOT NULL DEFAULT '' COMMENT '服务名',
  `service_method` varchar(255) NOT NULL DEFAULT '' COMMENT '服务方法名',
  `service_method_request` varchar(255) NOT NULL DEFAULT '' COMMENT '服务方法参数名',
  `service_group` varchar(255) DEFAULT NULL COMMENT '服务方法group',
  `service_version` varchar(255) DEFAULT NULL COMMENT '服务版本',
  `page_size` int(11) NOT NULL DEFAULT '0' COMMENT '单次请求数量',
  `size_limit` int(11) NOT NULL DEFAULT '1000000' COMMENT '模板数量限制',
  `result_list_dir` varchar(255) NOT NULL DEFAULT '' COMMENT '查询结果list位置',
  `file_name` varchar(255) DEFAULT NULL COMMENT '下载文件名称',
  `header` text NOT NULL COMMENT '下载表头',
  `file_header_type` tinyint(3) NOT NULL DEFAULT '1' COMMENT '表头类型 1:普通表头 2:动态表头',
  `user` varchar(255) NOT NULL DEFAULT '' COMMENT '模板创建人',
  `status` tinyint(3) NOT NULL DEFAULT '0' COMMENT '模板状态：0:正常  1:废弃',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  PRIMARY KEY (`template_id`)
) ENGINE=InnoDB AUTO_INCREMENT=19 DEFAULT CHARSET=utf8mb4 COMMENT='供应链离线下载模板配置表';
```

## 4.3 离线下载任务表结构

```SQL
CREATE TABLE `erp_download_task` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `template_id` int(11) NOT NULL COMMENT '模板id',
  `request` text NOT NULL COMMENT '请求参数',
  `response` varchar(255) DEFAULT '' COMMENT '请求返回值',
  `error_stack` text COMMENT '请求异常堆栈',
  `replace_map` text COMMENT '请求替换map，动态表头必填',
  `status` varchar(64) NOT NULL DEFAULT '' COMMENT '任务状态',
  `user` varchar(255) NOT NULL DEFAULT '' COMMENT '创建人',
  `operator` varchar(255) NOT NULL DEFAULT '' COMMENT '最近一次修改人',
  `url` varchar(512) DEFAULT NULL COMMENT '下载url',
  `tmp_dir` varchar(512) DEFAULT NULL COMMENT '本地中间文件路径',
  `file_name` varchar(255) DEFAULT NULL COMMENT '文件名称',
  `file_type` tinyint(3) NOT NULL COMMENT '下载文件类型',
  `start_time` datetime DEFAULT NULL,
  `end_time` datetime DEFAULT NULL,
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  PRIMARY KEY (`id`),
  KEY `idx_user` (`user`)
) ENGINE=InnoDB AUTO_INCREMENT=547 DEFAULT CHARSET=utf8mb4 COMMENT='供应链离线下载任务表';
```
