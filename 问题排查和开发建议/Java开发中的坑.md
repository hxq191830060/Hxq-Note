## 1. 空指针问题

### 1.1 包装类空指针

```java
public static Interger add(Interger i){
  return i+1;
}
```

### 1.2 ConcurrentHashMap这样的容器不支持key，value为null

```java
Map<String,String> map=new ConcurrentHashMap<>();
map.put(null,null);
```



## 2. 日期格式问题

日期格式化时，年份设置不能用 **YYYY**，要用 **yyyy**



## 3. 浮点数计算的坑

浮点数计算不能使用float，double，必须使用BigDecimal

而且使用BigDecimal的时候，必须使用 字符串的构造方法

```java
public class DoubleTest {
    public static void main(String[] args) {
        System.out.println(new BigDecimal("0.1").add(new BigDecimal("0.2")));
        System.out.println(new BigDecimal("1.0").subtract(new BigDecimal("0.8")));
        System.out.println(new BigDecimal("4.015").multiply(new BigDecimal("100")));
        System.out.println(new BigDecimal("123.3").divide(new BigDecimal("100")));
    }
}
```



## 4.Integer缓存的坑

```java
public class IntegerTest {

    public static void main(String[] args) {
        Integer a = 127;
        Integer b = 127;
        System.out.println("a==b:"+ (a == b)); //true
        
        Integer c = 128;
        Integer d = 128;
        System.out.println("c==d:"+ (c == d)); //false
    }
}
```

Interger有维护一个缓存池，某个范围内的Integer会直接从缓存池取对象

超过该范围会new新对象



## 5. 使用ThreadLocal，线程重用导致信息错乱

Tomcat的工作线程是基于线程池的

线程池会重用固定的几个线程，一旦线程重用，那么很可能首次从 ThreadLocal 获取的值是之前其他用户的请求遗留的值。这时，ThreadLocal 中的用户信息就是其他用户的信息。

所以使用ThreadLocal来存放一些数据时，需要特别注意在代码运行结束后，显示地清空设置的数据

```java
public Map right(@RequestParam("userId") Integer userId) {
    String before  = Thread.currentThread().getName() + ":" + currentUser.get();
    currentUser.set(userId);
    try {
        String after = Thread.currentThread().getName() + ":" + currentUser.get();
        Map result = new HashMap();
        result.put("before", before);
        result.put("after", after);
        return result;
    } finally {
        //在finally代码块中删除ThreadLocal中的数据，确保数据不串
        currentUser.remove();
    }
}
```



## 6. 线程池提交过程中，出现异常怎么办？

```java
public class ThreadExceptionTest {

    public static void main(String[] args) {
        ExecutorService executorService = Executors.newFixedThreadPool(10);

        IntStream.rangeClosed(1, 10).forEach(i -> executorService.submit(()-> {
                    if (i == 5) {
                        System.out.println("发生异常啦");
                        throw new RuntimeException("error");
                    }
                    System.out.println("当前执行第几:" + Thread.currentThread().getName() );
                }
        ));
        executorService.shutdown();
    }
}
当前执行第几:pool-1-thread-1
当前执行第几:pool-1-thread-2
当前执行第几:pool-1-thread-3
当前执行第几:pool-1-thread-4
发生异常啦
当前执行第几:pool-1-thread-6
当前执行第几:pool-1-thread-7
当前执行第几:pool-1-thread-8
当前执行第几:pool-1-thread-9
当前执行第几:pool-1-thread-10
```

异常会被吞掉，可以采用以下几种方案处理

1. try-catch捕获
2. 通过Future对象的get方法接收抛出的异常，再处理
3. 为工作者线程设置UncaughtExceptionHandler，在uncaughtException( )中处理异常
4. 充血ThreadPoolExecutor的afterExecute( )，处理传递的异常引用

