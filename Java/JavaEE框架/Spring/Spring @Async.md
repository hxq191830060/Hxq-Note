## 1. @Async的使用

@Async标在方法上，可以异步地调用该方法（通过AOP实现，要注意AOP失效的场景也会让@Async失效）

调用者调用@Async标注的方法后会立刻返回，方法的实际执行提交给Spring TaskExecutor，由其中的线程执行



## 2. @Async默认使用的线程池

@Async默认使用的线程池是 **SimpleAsyncTaskExecutor**

该线程池默认**来一个任务创建一个线程**，若系统中不断地创建线程，会导致系统占用内存过高

针对线程创建问题，**SimpleAsyncTaskExecutor**提供了限流机制，通过**concurrencyLimit**属性来控制开关，当concurrencyLimit>=0时开启限流机制，**默认关闭限流机制即concurrencyLimit=-1**，当关闭情况下，**会不断创建新的线程来处理任务**。基于默认配置，SimpleAsyncTaskExecutor并不是严格意义的线程池，达不到线程复用的功能





## 3. 使用@Async时，建议自定义异步线程池

### 3.1 实现AsyncConfigurer

```java
@Slf4j
@Configuration
public class AsyncConfiguration implements AsyncConfigurer {


    //配置线程池各个参数
    public ThreadPoolTaskExecutor executor(){
        ThreadPoolTaskExecutor executor=new ThreadPoolTaskExecutor();

        executor.setMaxPoolSize(10);
        executor.setCorePoolSize(5);
        executor.setQueueCapacity(10);
        
        executor.setRejectedExecutionHandler(new RejectedExecutionHandler() {
            @Override
            public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
                log.error("线程池满了");
            }
        });

        executor.setThreadNamePrefix("自定义线程池-");
        executor.initialize();
        return executor;
    }

    //将线程池注入spring
    @Override
    public Executor getAsyncExecutor(){
        return executor();
    }

    //配置线程池中执行任务时任务抛出异常后对异常的处理逻辑
    @Override
    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler(){
        return (ex,method,params)->{
          log.error("method: {}, params: {}",method,params,ex);
        };
    }

}
```



### 3.2 直接注入

```java
@Configuration
@Slf4j
public class TaskPoolConfig {

    @Bean("taskEventExecutor")
    public TaskExecutor getAsyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        // 设置核心线程数
        executor.setCorePoolSize(5);

        // 设置最大线程数
        executor.setMaxPoolSize(10);

        // 设置可接收等待的任务队列
        executor.setQueueCapacity(20);

        // 设置线程前缀名称
        executor.setThreadNamePrefix("自定义测试线程池2-");

        // 线程池拒绝时尝试处理失败再发送一封邮件即可
        // 需要注意的是该处理是在当前调用线程上完成, 可能给业务线程带来更大的响应延迟
        executor.setRejectedExecutionHandler((runnable, threadPoolExecutor) -> {
            log.warn("taskEventExecutor exec reject");
            try {
                // 超时5秒尝试入队一次失败后再处理
                if (!threadPoolExecutor.isShutdown()) {
                    threadPoolExecutor.getQueue().offer(runnable, 5, TimeUnit.SECONDS);
                }
            } catch (InterruptedException ex) {
                log.warn("taskEventExecutor exec reject try enqueue fail");
                // 如果是临时超载, 当前系统可承载的线程数量在1000以内则尝试新建线程来执行
                ThreadMXBean mxBean = ManagementFactory.getThreadMXBean();
                if (mxBean.getThreadCount() < 1000) {
                    // r.run(); 当前线程调用者来执行该任务, 即 CallerRunsPolicy 策略会阻塞用户线程, 原则上不可取
                    // e.execute(r); 继续往线程池中尝试推送, 既然线程池被拒绝说明已满成功概率很低, 因为产线队列已足够大
                    // 另起线程来执行, 会造成很多临时线程抢占系统资源, 比较危险, 但考虑到线程在一定范围内应该有一个可接受的范围
                    new Thread(runnable, "TempThreadExecTask").start();
                    log.warn("taskEventExecutor exec reject try new Thread");
                } else {
                    String runnableInfo = "Task: " + runnable.toString();
                    log.error("taskEventExecutor {}", runnableInfo);
                    String threadPoolInfo = String.format("ThreadPoolInfo corePoolSize: %d, maximumPoolSize: %d, taskCount: %d", threadPoolExecutor.getCorePoolSize(), threadPoolExecutor.getMaximumPoolSize(), threadPoolExecutor.getTaskCount());
                    log.error("taskEventExecutor {}", threadPoolInfo);

                    String content = "线程池信息: <p>" + threadPoolInfo + "</p>";
                    content += "任务信息: <p>" + runnableInfo + "</p>";
                }
            }
        });

        // 异步线程池在容器关闭时可能会丢失事件, 故提供一个线程池关闭超时的时间
        executor.setWaitForTasksToCompleteOnShutdown(true);

        // 重启或者关闭时时间会多出120秒
        // 但如果线程池中的任务已经执行完毕, 则不会自动等待到超时时间依然执行关闭
        // 如果线程池中的任务执行超过60秒则会被中断执行
        executor.setAwaitTerminationSeconds(3000);

        // 初始化线程池
        executor.initialize();
        return executor;
    }
}
```

