**Spring内置的定时任务**

1. 在启动类上加上 **@EnableScheduling** ——开启定时任务功能   
2. 创建一个定时任务类，在Spring容器中注册，并用 **@Scheduled** 修饰方法
```java
@Component
public class ScheduledTask {
    @Scheduled(fixedRate = 2000)
    public void fixedRateMethod(){
        System.out.println(new Date());
    }
}
//每隔2秒，SpringBoot会自动调用一次fixedRateMethod();
```  
3. @Scheduled  
```java
@Target({ElementType.METHOD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Repeatable(Schedules.class)
public @interface Scheduled {
    String CRON_DISABLED = "-";

    String cron() default "";

    String zone() default "";

    long fixedDelay() default -1L;

    String fixedDelayString() default "";

    long fixedRate() default -1L;

    String fixedRateString() default "";

    long initialDelay() default -1L;

    String initialDelayString() default "";
}
```
* fixedRate——单位毫秒，从调用方法开始记时，计时到fixedRate毫秒，再次调用方法    
* fixedDelay——单位毫秒，从方法结束开始计时，计时到fixedRate毫秒，再次调用方法 
* Cron表达式  
    * Crom表达式从左到右一共6个位置，分别代表——秒，时，分，日，月，星期
    * 接下来亿第一个位置为例  
       * 如果该位置为0——表示第0秒执行
       * 如果该位置为*——表示每秒都会执行  
       * 如果该位置为？——表示该位置的取值不影响定时任务 
    * @Scheduled(cron="0 * * * * *")——表示每分钟的00秒执行任务   
    * @Scheduled(cron="0 0 0 * * ?")——表示每天的00：00：00执行任务   
    


***   
**Quarts定时任务开发**——需要在查