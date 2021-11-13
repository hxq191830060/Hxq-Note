### 1. 更改主机名（每台主机都操作）

每台主机上，都修改主机名

```
hostnamectl set-hostname 主机名
```



### 2. 配置/etc/hosts（每台主机都操作）

在每台主机上，配置/etc/hosts文件

写入每台主机的主机名与公网IP的映射



### 3. 配置虚拟网卡（每台主机都操作）

```
ifconfig eth0:1 公网IP #临时创建虚拟网卡，重启服务器会消失
```



### 4. 安装Docker,Kubectl,Kubeadm,Kubelet（每台主机都操作）

* 创建脚本 1.sh

  ```shell
  export REGISTRY_MIRROR=https://registry.cn-hangzhou.aliyuncs.com
  
  # 卸载旧版本
  yum remove -y docker \
  docker-client \
  docker-client-latest \
  docker-ce-cli \
  docker-common \
  docker-latest \
  docker-latest-logrotate \
  docker-logrotate \
  docker-selinux \
  docker-engine-selinux \
  docker-engine
  
  # 设置 yum repository
  yum install -y yum-utils \
  device-mapper-persistent-data \
  lvm2
  yum-config-manager --add-repo http://mirrors.aliyun.com/docker-ce/linux/centos/docker-ce.repo
  
  # 安装并启动 docker
  yum install -y docker-ce-19.03.8 docker-ce-cli-19.03.8 containerd.io
  systemctl enable docker
  systemctl start docker
  
  # 安装 nfs-utils
  # 必须先安装 nfs-utils 才能挂载 nfs 网络存储
  yum install -y nfs-utils
  yum install -y wget
  
  # 关闭 防火墙
  systemctl stop firewalld
  systemctl disable firewalld
  
  # 关闭 SeLinux
  setenforce 0
  sed -i "s/SELINUX=enforcing/SELINUX=disabled/g" /etc/selinux/config
  
  # 关闭 swap
  swapoff -a
  yes | cp /etc/fstab /etc/fstab_bak
  cat /etc/fstab_bak |grep -v swap > /etc/fstab
  
  # 修改 /etc/sysctl.conf
  # 如果有配置，则修改
  sed -i "s#^net.ipv4.ip_forward.*#net.ipv4.ip_forward=1#g"  /etc/sysctl.conf
  sed -i "s#^net.bridge.bridge-nf-call-ip6tables.*#net.bridge.bridge-nf-call-ip6tables=1#g"  /etc/sysctl.conf
  sed -i "s#^net.bridge.bridge-nf-call-iptables.*#net.bridge.bridge-nf-call-iptables=1#g"  /etc/sysctl.conf
  sed -i "s#^net.ipv6.conf.all.disable_ipv6.*#net.ipv6.conf.all.disable_ipv6=1#g"  /etc/sysctl.conf
  sed -i "s#^net.ipv6.conf.default.disable_ipv6.*#net.ipv6.conf.default.disable_ipv6=1#g"  /etc/sysctl.conf
  sed -i "s#^net.ipv6.conf.lo.disable_ipv6.*#net.ipv6.conf.lo.disable_ipv6=1#g"  /etc/sysctl.conf
  sed -i "s#^net.ipv6.conf.all.forwarding.*#net.ipv6.conf.all.forwarding=1#g"  /etc/sysctl.conf
  # 可能没有，追加
  echo "net.ipv4.ip_forward = 1" >> /etc/sysctl.conf
  echo "net.bridge.bridge-nf-call-ip6tables = 1" >> /etc/sysctl.conf
  echo "net.bridge.bridge-nf-call-iptables = 1" >> /etc/sysctl.conf
  echo "net.ipv6.conf.all.disable_ipv6 = 1" >> /etc/sysctl.conf
  echo "net.ipv6.conf.default.disable_ipv6 = 1" >> /etc/sysctl.conf
  echo "net.ipv6.conf.lo.disable_ipv6 = 1" >> /etc/sysctl.conf
  echo "net.ipv6.conf.all.forwarding = 1"  >> /etc/sysctl.conf
  # 执行命令以应用
  sysctl -p
  
  # 配置K8S的yum源
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
  
  # 卸载旧版本
  yum remove -y kubelet kubeadm kubectl
  
  # 安装kubelet、kubeadm、kubectl
  # 将 ${1} 替换为 kubernetes 版本号，例如 1.17.2
  yum install -y kubelet-${1} kubeadm-${1} kubectl-${1}
  
  # 修改docker Cgroup Driver为systemd
  # # 将/usr/lib/systemd/system/docker.service文件中的这一行 ExecStart=/usr/bin/dockerd -H fd:// --containerd=/run/containerd/containerd.sock
  # # 修改为 ExecStart=/usr/bin/dockerd -H fd:// --containerd=/run/containerd/containerd.sock --exec-opt native.cgroupdriver=systemd
  # 如果不修改，在添加 worker 节点时可能会碰到如下错误
  # [WARNING IsDockerSystemdCheck]: detected "cgroupfs" as the Docker cgroup driver. The recommended driver is "systemd". 
  # Please follow the guide at https://kubernetes.io/docs/setup/cri/
  sed -i "s#^ExecStart=/usr/bin/dockerd.*#ExecStart=/usr/bin/dockerd -H fd:// --containerd=/run/containerd/containerd.sock --exec-opt native.cgroupdriver=systemd#g" /usr/lib/systemd/system/docker.service
  
  # 设置 docker 镜像，提高 docker 镜像下载速度和稳定性
  # 如果您访问 https://hub.docker.io 速度非常稳定，亦可以跳过这个步骤
  curl -sSL https://kuboard.cn/install-script/set_mirror.sh | sh -s ${REGISTRY_MIRROR}
  
  # 重启 docker，并启动 kubelet
  systemctl daemon-reload
  systemctl restart docker
  ```

* 启动脚本

  ```
  sh 1.sh 你需要的k8s版本号
  ```

### 5. 配置Kubelet配置文件并启动Kubelet（每台主机都操作）

* 找到 10-kubeadm.conf 的位置

  ```shell
  find / -name 10-kubeadm.conf
  ```

* 在10-kubeadm.conf末尾追加 --node-ip=公网IP

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
  ExecStart=/usr/bin/kubelet $KUBELET_KUBECONFIG_ARGS $KUBELET_CONFIG_ARGS $KUBELET_KUBEADM_ARGS $KUBELET_EXTRA_ARGS --node-ip=101.34.16.177cle
  
  ```

* 启动Kubelet

  ```shell
  systemctl enable kubelet && systemctl start kubelet
  ```



### 6. 初始化master节点（只有master执行）

* init master

```shell
kubeadm init \
    --kubernetes-version=v1.18.9 \
    --apiserver-advertise-address=101.34.184.116 \
    --control-plane-endpoint=101.34.184.116 \
    --image-repository registry.cn-hangzhou.aliyuncs.com/google_containers \
    --service-cidr=10.96.0.0/16 \
    --pod-network-cidr=10.100.0.1/16
```

```
mkdir -p $HOME/.kube
sudo cp -i /etc/kubernetes/admin.conf $HOME/.kube/config
sudo chown $(id -u):$(id -g) $HOME/.kube/config
```

* 修改/etc/kubernetes/manifests/kube-apiserver.yaml

  添加`--bind-address=0.0.0.0`和修改`--advertise-addres=master公网IP`

  

  

### 7. master构建网络（只有master执行）

* 获取网络插件flannel的yaml文件

  ```shell
  wget https://kuboard.cn/install-script/flannel/flannel-v0.14.0.yaml
  ```

* 修改yaml文件

  1. 修改net-conf.json中的network为 master init时的pod-network-cidr
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

### 8. Worker加入进群

* master产生秘钥

  ```shell
  kubeadm token create --print-join-command
  ```

  

* worker执行

  ```shell
  kubeadm join master公网IP:6443 --token qdzutk.ouxqaes6o3zluyn3     --discovery-token-ca-cert-hash sha256:a433426d1a91ea47cd11511977794f040bc9d6a715edeadc65a771859a91e691
  ```




### 9. 参考链接

https://www.caiyifan.cn/p/d6990d10.html

https://kuboard.cn/install/history-k8s/install-k8s-1.18.x.html

https://kuboard.cn/install/install-k8s.html#%E6%A3%80%E6%9F%A5%E5%88%9D%E5%A7%8B%E5%8C%96%E7%BB%93%E6%9E%9C
