1. 通过Kubernetes平台查看内存使用率异常的node
2. 登录node，top -c，然后按M查看各个进程的内存占用，记录下pid
3. ls /proc/pid/task|wc -l 查看进程中的线程数
4. 进入容器，通过jmap -head pid查看JVM堆参数以及JVM堆使用情况
5. jmap -histo pid > file，打印各个类的实例数目和内存大小
6. jmap -dump 将堆转储为dump文件，方便进行分析



* 使用过的JVM内存调优参数
  * -Xms：堆内存的最大大小
  * -Xmx：堆内存的最小大小
  * -Xmn：年轻代大小
  * -XX:MetaSpaceSize：元空间初始大小
  * -XX:MaxMetaspaceSize：元空间最大大小