[toc]

## 1. 路由表解读

```
[root@VM-4-3-centos ~]# route
Kernel IP routing table
Destination     Gateway         Genmask         Flags Metric Ref    Use Iface
default         gateway         0.0.0.0         UG    0      0        0 eth0
10.0.4.0        0.0.0.0         255.255.252.0   U     0      0        0 eth0
10.100.0.0      0.0.0.0         255.255.255.0   U     0      0        0 cni0
10.100.1.0      10.100.1.0      255.255.255.0   UG    0      0        0 flannel.1
link-local      0.0.0.0         255.255.0.0     U     1002   0        0 eth0
172.17.0.0      0.0.0.0         255.255.0.0     U     0      0        0 docker0

```

* **Destination**

  目标网络或目标主机，如果**Destination为default**，那么Gateway为**默认网关**

* **Gateway**

  网关地址，0.0.0.0表示当前记录对应的Destination与本机在同一网段下，通信时不需要经过网关

* **Genmask**

  Destination字段的网络掩码

  如果Destination为本机时，需设置为255.255.255.255,

  如果Destination为default，需设置为0.0.0.0

* **Iface**

  网卡名字



## 2. Linux路由种类

### 2.1 主机路由

路由表中，Destination为某个主机的IP

```
[root@VM_139_74_centos ~]# route -n
Kernel IP routing table
Destination     Gateway         Genmask         Flags Metric Ref    Use Iface
10.0.0.10       10.139.128.1    255.255.255.255 UGH   0      0        0 eth0
```



### 2.2 网络路由

路由表中，Destination为某个子网网络

```
[root@VM_139_74_centos ~]# route -n
Kernel IP routing table
Destination     Gateway         Genmask         Flags Metric Ref    Use Iface
10.0.0.0        10.139.128.1    255.255.255.0   UG    0      0        0 eth0
```



### 2.3 默认路由

路由表中Destination为default

如果目标主机的IP或者网络**不在路由表**中，那么走的就是**默认路由**

```
[root@VM_139_74_centos ~]# route
Kernel IP routing table
Destination     Gateway         Genmask         Flags Metric Ref    Use Iface
default         gateway         0.0.0.0         UG    0      0        0 eth0
```

