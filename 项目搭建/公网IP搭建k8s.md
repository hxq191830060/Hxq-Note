### 0. 开放端口

* master
  * TCP
    * 6443
    * 2379-2380
    * 10250
    * 10257
    * 10259
  * UDP
    * 8472
* Worker
  * TCP
    * 10250
    * 30000-32767
  * UDP
    * 8472





### 2. 更改主机名（每台主机都操作）

每台主机上，都修改主机名

```
hostnamectl set-hostname 主机名
```



### 3. 配置/etc/hosts（每台主机都操作）

在每台主机上，配置/etc/hosts文件

写入每台主机的主机名与公网IP的映射





### 4. 禁用交区和selinux，关闭防火墙

```shell
# 安装 nfs-utils
# 必须先安装 nfs-utils 才能挂载 nfs 网络存储
yum install -y nfs-utils
yum install -y wget


systemctl stop firewalld
systemctl disable firewalld

setenforce 0
sed -i "s/SELINUX=enforcing/SELINUX=disabled/g" /etc/selinux/config

swapoff -a
yes | cp /etc/fstab /etc/fstab_bak
cat /etc/fstab_bak |grep -v swap > /etc/fstab
```



```shell
sudo modprobe br_netfilter

cat <<EOF | sudo tee /etc/modules-load.d/k8s.conf
br_netfilter
EOF

cat <<EOF | sudo tee /etc/sysctl.d/k8s.conf
net.bridge.bridge-nf-call-ip6tables = 1
net.bridge.bridge-nf-call-iptables = 1
net.ipv4.ip_forward = 1
EOF
sudo sysctl --system

cat <<EOF tee /proc/sys/net/ipv4/ip_forward
1
EOF

lsmod | grep br_netfilter

```



### 5. 安装容器运行时Docker

* 安装Docker

  ```shell
  sudo yum remove docker \
                    docker-client \
                    docker-client-latest \
                    docker-common \
                    docker-latest \
                    docker-latest-logrotate \
                    docker-logrotate \
                    docker-engine
                    
                    
  sudo yum install -y yum-utils
  sudo yum-config-manager --add-repo http://mirrors.aliyun.com/docker-ce/linux/centos/docker-ce.repo
  sudo yum install docker-ce docker-ce-cli containerd.io
  ```

  

* 将Docker的cgroup driver替换为systemed

  ```shell
  sudo mkdir /etc/docker
  cat <<EOF | sudo tee /etc/docker/daemon.json
  {
    "exec-opts": ["native.cgroupdriver=systemd"],
    "log-driver": "json-file",
    "log-opts": {
      "max-size": "100m"
    },
    "storage-driver": "overlay2"
  }
  EOF
  ```

* 重启Docker，设计开机自启动

  ```shell
  sudo systemctl enable docker
  sudo systemctl daemon-reload
  sudo systemctl restart docker
  ```







### 6. 安装kubeadm,kubectl,kubelet

* 给yum添加Kubernetes软件源

  ```shell
  cat <<EOF > /etc/yum.repos.d/kubernetes.repo
  [kubernetes]
  name=Kubernetes
  baseurl=http://mirrors.aliyun.com/kubernetes/yum/repos/kubernetes-el7-x86_64
  enabled=1
  gpgcheck=0
  repo_gpgcheck=0
  gpgkey=http://mirrors.aliyun.com/kubernetes/yum/doc/yum-key.gpg
         http://mirrors.aliyun.com/kubernetes/yum/doc/rpm-package-key.gpg
  EOF
  ```

* 安装

  ```shell
  yum install kubelet kubeadm kubectl
  ```

* 建立虚拟网卡

  ```
  ifconfig eth0:1 公网IP #临时创建虚拟网卡，重启服务器会消失
  ```

  

* 找到 10-kubeadm.conf 的位置

  ```shell
  find / -name 10-kubeadm.conf
  ```

  在10-kubeadm.conf末尾追加 --node-ip=公网IP

  ```shell
  # Note: This dropin only works with kubeadm and kubelet v1.11+
  [Service]
  Environment="KUBELET_KUBECONFIG_ARGS=--bootstrap-kubeconfig=/etc/kubernetes/bootstrap-kubelet.conf --kubeconfig=/etc/kubernetes/kubelet.conf"
  Environment="KUBELET_CONFIG_ARGS=--config=/var/lib/kubelet/config.yaml"
  # This is a file that "kubeadm init" and "kubeadm join" generates at runtime, populating the KUBELET_KUBEADM_ARGS variable dynamically
  EnvironmentFile=-/var/lib/kubelet/kubeadm-flags.env
  # This is a file that the user can use for overrides of the kubelet args as a last resort. Preferably, the user should use
  # the .NodeRegistration.KubeletExtraArgs object in the configuration files instead. KUBELET_EXTRA_ARGS should be sourced from this file.
  EnvironmentFile=-/etc/sysconfig/kubelet
  ExecStart=
  ExecStart=/usr/bin/kubelet $KUBELET_KUBECONFIG_ARGS $KUBELET_CONFIG_ARGS $KUBELET_KUBEADM_ARGS $KUBELET_EXTRA_ARGS --node-ip=101.34.16.177
  ```

* 开机自启动

  ```shell
  sudo systemctl start kubelet
  sudo systemctl enable kubelet
  ```

* 查看kubelet版本

  ```
  yum info kubelet版本
  kubeadm config images list #查看需要的版本
  ```

  



### 7. 初始化master(master only)

```shell
kubeadm init \
    --kubernetes-version=v1.23.4 \
    --apiserver-advertise-address=101.34.184.116 \
    --control-plane-endpoint=101.34.184.116 \
    --image-repository registry.cn-hangzhou.aliyuncs.com/google_containers \
    --service-cidr=10.96.0.0/16 \
    --pod-network-cidr=10.100.0.1/16
    
    
mkdir -p $HOME/.kube
sudo cp -i /etc/kubernetes/admin.conf $HOME/.kube/config
sudo chown $(id -u):$(id -g) $HOME/.kube/config
```

* 修改/etc/kubernetes/manifests/kube-apiserver.yaml

  添加`--bind-address=0.0.0.0`和修改`--advertise-addres=master公网IP`



### 8. 构建网络(master only)

* 获取网络插件flannel的yaml文件

  ```shell
  wget https://raw.githubusercontent.com/coreos/flannel/master/Documentation/kube-flannel.yml
  ```

* 修改yaml文件

  1. 修改net-conf.json中的network为 master init时的pod-network-cidr（10.100.0.1/16）
  2. 在args和env中添加

  ```yaml
  args:
   - --public-ip=$(PUBLIC_IP)
   - --iface=eth0
  
  env:
   - name: PUBLIC_IP
     valueFrom:
       fieldRef:
         fieldPath: status.podIP
  
  ```

* 安装插件

  ```
  kubectl apply -f kube-flannel.yml
  ```

### 

### 9. 让Worker加入集群(work only)

```
kubeadm join 101.34.184.116:6443 --token ugdkok.gtcfzvhisyi2gp8k \
        --discovery-token-ca-cert-hash sha256:19ad13d9ac8a34f485f52b989eb8974e92f6a9f37d752ffbc2dd073a1829604e 
```





kubeadm join 101.34.184.116:6443 --token o8exe1.50u64edlsgh13aaf \
        --discovery-token-ca-cert-hash sha256:76cbf300d43a96f2990e3686e42ee24f95c38a17356cdef9e5
