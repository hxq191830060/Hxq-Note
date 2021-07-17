### CountDownLatch

**CountDownLatch是一个同步工具类**——用来协调多个线程之间的同步

CountDownLatch能够使一个线程在等待另外一些线程完成各自工作后再继续执行。

使用一个计数器实现——计算器初始值为**线程数量**，一个线程完成自己的任务后，计数器的值就会-1，当计数器值为0时，在CountDownLatch上await()的线程都会被唤醒。



* **方法**

  * **await()**

    **await(long timeout,TimeUnit unit)**

    如果一个线程调用了CountDownLatch的await()，那么该线程会在CountDownLatch上进入Waiting状态

    调用await()的线程如果想要醒来

    ①等待CountDownLatch的计数器为0

    ②超出指定的等待时间

    ③线程被中断

  * **countDown()**

    让CountDownLatch的计数器-1，如果计数器达到0，会唤醒CountDownLatch上所有await()的线程