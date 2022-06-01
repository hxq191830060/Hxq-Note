# 1. JIT

JIT（Just In Time Compilation）即使编译

当代码执行的次数超过一定的阈值时，会将Java字节码转换为本地代码，大幅度提高代码执行性能



# 2. JVM调优

## 2.1 JVM监控工具

* jps：JVM Process Status Tool，显示指定系统内所有的HotSpot虚拟机进程
* jstat：JVM statistics Monitoring，用于监视虚拟机运行时状态信息的命令，可以显示虚拟机进程中的类装载，内存，垃圾手机，JIT编译等运行数据
* jmap：JVM Memory Map，用于生成heap dump文件
* jhat：JVM Heap Analysis Tool，用于分析jmap生成的dump
* jstack：用于生成Java虚拟机当前时刻的线程快照
* jinfo：JVM Configuration info实时查看和调整虚拟机运行参数

```shell
jstat -gcutil -h20 pid 1000 查看堆内存各区域的使用率和GC情况
jmap -histo pid ｜ head -n20 查看堆内存中存活的对象，并按照空间排序
jmap -dump:format=b,file=heap  dump堆内存文件
```



## 2.2 调优参数

### 2.2.1 内存参数

* -Xmx：设置堆内存的最大大小

>-Xmx3550m：JVM堆最大为3550MB

* -Xms：设置堆内存的初始大小

>-Xms3550m：JVM堆初始大小为3550MB，建议与-Xmx相同，避免每次垃圾回收完后JVM重新分配内存

* -Xmn：设置年轻代的大小（推荐配置为堆的3/8）

>-Xmn2g：设置年轻代大小为2g

* -Xss：设置线程的栈大小

>-Xss128k：每个线程的栈大小为128k，JDK5后默认为1MB，JDK5前默认为256k

* -XX:NewSize：年轻代的最小内存
* -XX:MaxNewSize：年轻代的最大内存
* -XX:MetaspaceSize：元空间初始内存
* -XX:MaxMetaspaceSize：元空间最大内存
* -XX:MaxDirectMemorySize：直接内存的最大限制
* -XX:NewRatio：年轻代与老年代的比值

>-XX:NewRatio=4：设置年轻代和老年代的比值为4

* -XX:SurvivorRatio：Eden与Survivor空间的占比

>-XX:SurvivorRatio=4：设置Eden于Survivor的比值为4

* -XX:MaxTenuringThreshold：年轻代中对象的最大年龄（超过该年龄进入老年代）

>-XX:MaxTenuringThreshold=0：对象创建不会在eden中，而是直接进入老年代

### 2.2.2 GC参数

* -XX:+PrintGC：开启打印 gc 信息
* -XX:+PrintGCDetails：打印 gc 详细信息
* -XX:+PrintGCDateStamps：输出GC时间戳
* -XX:+PrintGCTimeStamps：用于输出GC时间戳
* -XX:+PrnitHeapAtGC：进行GC前后打印出堆的信息
* -XX:+PrintGCApplicationStoppedTime：打印GC暂停时间
* -Xloggc:path：日志文件的输出路径
* **日志分割参数**
  * -XX:+UseGCLogFileRotation：开启日志文件分割
  * -XX:NumberOfGCLogFiles=14：最多分割多少个文件
  * -XX:GCLogFileSize=100M：每个文件大小，超过该大小会触发分割




### 2.2.3 垃圾回收器参数

* **吞吐量优先的GC**

  * **新生代并行收集器**

    * -XX:+UseParallelGC：设置**新生代**垃圾收集器为**并行收集器**（仅对年轻代有效，设置年轻代收集器为**Parallel Scavenge**）
    * -XX:ParallelGCThreads：配置并行收集器的线程数目（与-XX:+UseParallelGC一起使用）
    
  * **老年代并行收集器**

    * -XX:+UseParallelOldGC：设置**老年代**垃圾收集器为**并行收集器**（仅对老年代有效，设置老年收集器为**Parallel Old**）

  * -XX:MaxGCPauseMillis：设置每次**年轻代**垃圾回收的最长时间（配合-XX:+UseParallelGC）

    >-XX:MaxGCPauseMillis=100：每次年轻代垃圾回收的最长时间为100ms

  * -XX:+UseAdaptiveSizePolicy：GC自适应调节策略，开启这个参数后，JVM会自动调整年轻代大小，eden和survivor的比例（建议使用并行收集器时，并且一直打开，配合-XX:+UseParallelGC）

* **响应时间优先的GC**

  * -XX:+UseConcMarkSweepGC：设置老年代为并发收集（设置老年代收集器为**CMS**）

  * -XX:+UseParNewGC：设置年轻代为并行收集，可与CMS同时使用（设置年轻代收集器为**ParNew**）

    >-XX:+UseConcmarkSweepGC -XX:+UseParNewGC -XX:ParallelGCThreads=20

  * -XX:CMSFullGCBeforeCompaction：由于并发收集器不对内存空间进行压缩整理，会产生内存碎片，此值设置运行n次Full GC后对内存空间进行压缩整理

    >-XX:CMSFullGCBeforeCompaction=5 -XX:UseCMSCompactAtFullCollectiono

  * -XX:+UseCMSCompactAtFullCollections：打开对老年代的压缩整理

* **Garbage First(G1)**

  * -XX:HeapRegionSize：指定单个Region大小
  * -XX:G1NewSizePercent：指定新生代初始占比
  * -XX:G1MaxNewSizePercent：指定新生代最大占比
  * -XX:G1HeapWastePercent：在混合回收的时候如果一次混合回收的**空闲region超过该比例**就立刻停止垃圾回收的操作



### 2.2.4 TLAB参数

* -XX:+/-UseTLAB：指定是否开启TLAB
* -XX:TLABWasteTargetPercent：TLAB占用Eden空间的百分比
* -XX:-ResizeTLAB：禁用自动调整TLAB大小



### 2.2.5 容器环境下推荐的参数

>```ruby
>-XX:+UseContainerSupport -XX:MaxRAMPercentage=70.0 -XX:+PrintGCDetails -XX:+PrintGCDateStamps -Xloggc:/home/admin/nas/gc-${POD_IP}-$(date '+%s').log -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=/home/admin/nas/dump-${POD_IP}-$(date '+%s').hprof
>```

* -XX:+UseContainerSupport：允许JVM读取cgroup限制，当容器超过内存限制时，会抛出OOM异常，而不是强制关闭容器
* -XX:MaxRAMPErcentage：设置JVM使用容器的内存百分比（建议最大不超过75%，推荐为70）
* -XX:+HeapDumpOnOutOfMemory：JVM发生OOM时，自动生成dump文件
* -XX:+HeapDumpPath：dump文件的路径



# 3. 问题排查

## 3.1 线上FGC

1. 清楚是哪些原因导致了FGC
   * 大对象：大对象占用过多的内存，直接进入老年代
   * 内存泄漏：内存无法被回收，先引发FGC，后引发OOM
   * 程序频繁生成一些长生命周期的对象，当这些对象存活时间过长进入老年代，最后可能触发FGC
   * 代码中显式调用了gc方法
   * JVM参数设置问题
2. 排查指南
   * 查看监控，了解出现的问题和FGC频率
   * 了解JVM的参数设置，包括：堆空间各个区域的大小设置，新生代和老年代分别采用了哪些垃圾收集器，然后分析JVM参数设置是否合理
   * 对可能的原因做排除法（元空间打满，内存泄漏，代码显示调用gc等）
   * 针对大对象或者长生命周期对象导致的FGC，可通过 jmap -histo 命令并结合dump堆内存文件作进一步分析，需要先定位到可疑对象



## 3.2 内存占用过大排查过程

1. 通过Kubernetes平台查看内存使用率异常的node
2. 登录node，top -c，然后按M查看各个进程的内存占用，记录下pid
3. ls /proc/pid/task|wc -l 查看进程中的线程数
4. 进入容器，通过jmap -head pid查看JVM堆参数以及JVM堆使用情况
5. jmap -histo pid > file，打印各个类的实例数目和内存大小
6. jmap -dump 将堆转储为dump文件，方便进行分析
