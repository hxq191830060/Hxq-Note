1. top查看是不是Java程序占用CPU，获取进程PID
2. top -Hp pid查看Java进程中线程对CPU的占用，获得线程的pid
3. jstack thread_pid > thread_stack.log 保存该线程的线程栈

还可以用JConsole远程连接Java程序，监控各个线程的执行情况