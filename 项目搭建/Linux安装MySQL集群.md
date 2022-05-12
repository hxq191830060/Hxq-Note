# 1. 安装MySQL

```shell
#根据Linux版本获取对应的mysql安装包
wget https://dev.mysql.com/get/mysql80-community-release-el8-1.noarch.rpm

yum install mysql80-community-release-el8-1.noarch.rpm

#更新yum源
yum update

#安装mysql
yum install mysql-server

#启动mysql
systemctl enable mysql
systemctl start mysql
systemctl status mysql

#显示mysql生成的随机密码
grep 'temporary password' /var/log/mysqld.log

#登录mysql
mysql -u root -p

#修改密码策略
ALTER USER 'root'@'localhost' IDENTIFIED BY 'Root_21root'; //因为我们随便修改密码时,一般都不满足它的策略
set global validate_password.policy=0; //（等级）

#修改为我们自己要的密码
ALTER USER 'root'@'localhost' IDENTIFIED BY '123456';

#创建新的用户用于开放远程登录
create user 'root'@'%' identified by 'root123'; //1、先创建权限记录
grant all privileges on *.* to 'root'@'%' with grant option; //2、授权
alter user 'root'@'%' identified with mysql_native_password by 'root123';//3.将用户的加密方式改为mysql_native_password

#创建主从连接的用户
create user 'slave'@'%' identified by 'slave123'; //1、先创建权限记录
grant all privileges on *.* to 'slave'@'%' with grant option; //2、授权
alter user 'slave'@'%' identified with mysql_native_password by 'slave123';//3.将用户的加密方式改为mysql_native_password。
```





# 2. 配置主从复制

全部的MySQL都要修改/etc/my.cnf——修改暴露的端口号

```
port=33306
```



全部MySQL都要修改/etc/my.cnf——配置serverId

```
server-id=x(master为1，slave不重复)
```





slave修改/etc/my.cnf——让slave只读不写

```
read_only=1
super_read_only=1
```



master修改/etc/my.cnf

```
log-bin //开启binlog
```

