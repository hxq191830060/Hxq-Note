**一,xml中开启组件扫描和AOP自动代理**
```
    <context:component-scan base-package="demo1"/>组件扫描
    <aop:aspectj-autoproxy/>AOP自动代理
```
***
**二,利用注解配置weaving**   
1. @Aspect  
   标注类，表名这个类是切面类，即这个类中含有advance
2. @通知注解("切点表达式")  
   标注方法，表示该方法是advance,pointcut在切点表达式中配置  
   1. 前置通知——@Before
    2. 后置通知——@AfterReturing
    3. 环绕通知——@Around
    4. 异常抛出通知——@AfterThrowing
    5. 最终通知——@After  
    **跟XML中的通知类型一样的作用**
       
***
**三,利用注解提取切面表达式**  
1. @Pointcut("切点表达式")  
   标注方法  
   
2. @通知注解(方法()或者类名.方法())