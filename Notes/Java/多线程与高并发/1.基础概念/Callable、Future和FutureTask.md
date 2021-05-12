**1. Callable接口**  
```java
@FunctionalInterface
public interface Callable<V> {
    V call() throws Exception;
}
```  
Callable接口用于创建线程任务   

***
**2. Future接口**
```java
public interface Future<V> {
    //如果任务还没开始，执行cancel(...)方法将返回false；
    // 如果任务已经启动，执行cancel(true)方法将以中断执行此任务线程的方式来试图停止任务，如果停止成功，返回true；
    // 当任务已经启动，执行cancel(false)方法将不会对正在执行的任务线程产生影响(让线程正常执行到完成)，此时返回false；
    // 当任务已经完成，执行cancel(...)方法将返回false。
    // mayInterruptRunning参数表示是否中断执行中的线程。
    boolean cancel(boolean mayInterruptIfRunning);
    
    //如果任务完成前被取消，则返回true
    boolean isCancelled();
    
    //如果任务执行结束，无论是正常结束或是中途取消还是发生异常，都返回true。
    boolean isDone();
    
    //获取异步执行的结果，如果没有结果可用，此方法会阻塞直到异步计算完成。
    V get() throws InterruptedException, ExecutionException;
    
    //获取异步执行结果，如果没有结果可用，此方法会阻塞，
    // 但是会有时间限制，如果阻塞时间超过设定的timeout时间，该方法将抛出异常。
    V get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException;
}
```
用于获取异步执行(Runnable任务和Callable任务)的结果 
***
**3. FutureTask类** 
```
public class FutureTask<V> implements RunnableFuture<V>

public interface RunnableFuture<V> extends Runnable, Future<V> {
    void run();
}
```
FutureTask实现了RunnableFuture接口(也实现了Runnable，Future接口)
* Future既可以用于获取异步执行的结果
* 也可以当作一个线程启动，或者作为一个Runnable任务提交给线程池   
***
**4. Callable任务的执行**  
* ExecutorService提供了submit()，使得线程池可以执行Callable任务，并返回任务执行结果     
   ```java
    public interface ExecutorService extends Executor{
        <T> Future<T> submit(Callable<T> task);
        <T> Future<T> submit(Runnable task, T result);
        Future<?> submit(Runnable task);
    }
   ```