### 1. 获取压缩包

```shell
wget http://download.redis.io/releases/redis-5.0.8.tar.gz
```



### 2. 解压

```shell
tar -zxcv 压缩包名
```



### 3. 配置环境

```
yum install gcc-c++

进入redis目录

#安装Redis
make
```



### 4. Redis默认目录

Redis相关程序默认安装在/usr/local/bin下



### 5. 启动Redis

* 将redis-conf拷贝一份

* Redis默认启动采用默认配置，并且不是后台运行方式

* 我们采用 **./redis-server /path/to/redis.conf**启动Redis

* 为了让Redis以后台方式启动，需要修改redis.conf中的257行

  ——**daemonize yes**

  