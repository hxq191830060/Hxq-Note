## 1. JIT

JIT（Just In Time Compilation）即使编译

当代码执行的次数超过一定的阈值时，会将Java字节码转换为本地代码，大幅度提高代码执行性能



## 2. JVM调优

### 2.1 调优命令

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



### 2.2 调优工具

* jconsole：对JVM中的内存，线程和类的监控
* jvisualvm：JDK自带的全能工具，可以分析内存快照，监控内存变化，GC变化
* JvisualVM
* MAT

### 2.3 调优参数

* -Xmx：堆内存的最小大小

* -Xms：堆内存的最大大小
* -XX:NewSize：新生代的最小大小
* -XX:MaxNewSize：新生代的最大空间大小

* -XX:MetaspaceSize：元空间初始大小
* -XX:MaxMetaspaceSize：元空间最大大小

* -XX:MaxDirectMemorySize：直接内存的最大限制
* -XX:NewRatio：新生代和老年代占比
* -XX:SurvivorRatio：Eden与Survivor空间的占比
* –XX:+UseParNewGC：指定使用 ParNew + Serial Old 垃圾回收器组合
* -XX:+UseParallelOldGC：指定使用 ParNew + ParNew Old 垃圾回收器组合
* -XX:+UseConcMarkSweepGC：指定使用 CMS + Serial Old 垃圾回收器组合
* -XX:+PrintGC：开启打印 gc 信息
* -XX:+PrintGCDetails：打印 gc 详细信息
* TLAB
  * -XX:+/-UseTLAB：指定是否开启TLAB
  * -XX:TLABWasteTargetPercent：TLAB占用Eden空间的百分比
  * -XX:-ResizeTLAB：禁用自动调整TLAB大小
* G1
  * -XX:HeapRegionSize：指定单个Region大小
  * -XX:G1NewSizePercent：指定新生代初始占比
  * -XX:G1MaxNewSizePercent：指定新生代最大占比
  * -XX:G1HeapWastePercent：在混合回收的时候如果一次混合回收的**空闲region超过该比例**就立刻停止垃圾回收的操作





## 3. 问题排查

### 3.1 线上FGC

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

