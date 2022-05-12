## JVM选项规则

* java -version：标准选项，任何JVM/任何平台都可以使用
* java **-X**ms10m：非标准选项，部分版本识别
* java **-XX**:+PrintGCDetails：不稳定参数，不同JVM有差异，随时可能被移除

>+：代表开启
>
>-：代表关闭



* 1.8+优先使用G1收集器

  >java -jar -XX:+UseG1GC -Xms2G -Xmx2G -Xss256k -XX:MaxGCPauseMillis=300 -Xloggc:/logs/gc.log -XX:+PrintGCTimeStamps -XX:+PrintGCDetails test.jar
  >
  >* -XX:+UserG1GC：使用G1收集器
  >* -Xms2G，-Xmx2G：设置堆空间大小，这两项要设置相同，减少内存交换
  >* -Xss128k/256k：设置虚拟机栈大小，一般128K够用，超过256K考虑优化
  >* -XX:MaxGCPauseMillis=300：最多300毫秒的STW(Stop The World)时间，增大可减少GC次数，提高吞吐
  >* -Xloggc：GC日志
  >* -XX:+PrintGCTimeStamps：GC打印时间戳
  >* -XX:+PrintGCDetails：GC打印细节
  >* 