**一.xml实现AOP大致流程**  
1.创建业务接口和业务实现类(含有Pointcut)  
2.创建切面类(功能增强类,含有advacne)  
3.在xml配置weaving关系——告诉Spring容器,哪个Bean的哪个方法是Pointcut,这个Pointcut要匹配哪个advance,以及advance的类型
***
**二.xml中配置weaving关系**  
1.<beans>的加入**xmlns:aop属性**,**xsi:schemaLocation属性**添加一句
```
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
加入这句 xmlns:aop="http://www.springframework.org/schema/aop" 
       xsi:schemaLocation=
       "http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd
加入这句  http://www.springframework.org/schema/aop http://www.springframework.org/schema/aop/spring-aop.xsd  
">
```  
2.配置切面
```j
<aop:config>
    <aop:aspect ref="action">
        <aop:before method="before" pointcut="execution(public void demo1.Bread.eat())"/>
    </aop:aspect>
</aop:config>
```
><<aop:config>>
> ><<aop:aspect>>——配置切面
> > > ref——切面类的id  
> > ><<aop>aop>——设置advance为切面类中的哪个方法,设置通知类型,设置Pointcut(切点表达式)

**<aop<aop>>的格式如下**  
<aop:通知类型 method="xx" pointcut="切点表达式">

* 通知类型    
  前置通知 <<aop:before>>——代理对象中,advance在Pointcut之前执行  
  后置通知 <<aop:after-returning>>——代理对象中,advance在Pointcut之后执行,如果advance执行前抛出异常,advance不执行  
  环绕通知 <<aop:around>>——**看下面的xml环绕通知详解**  
  异常抛出通知 <<aop:throwing>>——抛出异常才会执行advance  
  最终通知 <<aop:after>>——无论是否抛出异常，advance都在pointcut后执行   
  
**xml环绕通知详解**  
设置为环绕通知的话,动态代理对象中只会织入 advance，不会织入pointcut,所以如果调用pointcut,拦截后转发给代理对象，只会执行advance  
如果想要能够执行pointcut的话,在advance的参数表中增加参数(ProceedingJoinPoint p),并在advance方法体中通过**p.proceed()**执行pointcut  
```
    public void around(ProceedingJoinPoint p) throws Throwable {
        System.out.println("环绕前");
        Object o=p.proceed();
        System.out.println("环绕后");
    }
```
* method——表明advance是切面类中的哪个方法
* 切点表达式  
格式 execution ( [修饰符] 返回值类型 包名.类名.方法名(参数表) )  
  1. 返回值类型，包名，类名，方法可用*表示任意
  2. 包名与类名之间一个点(.)表示当前包下,两个点(..)表示当前包及其子包下 
  3. 参数表可用两个点(..)表示接受任意个任意类型参数
  
***
**三.提取切点表达式**    
当多个<aop<aop>>中的切点表达式相同时，可以提取切点表达式，实现复用  
1. 
```
<aop:pointcut id="AA" expression="复用的切点表达式"/>
```

2. 其他的<aop<aop>>可以通过**pointcut-ref**引用上面的切点表达式
```
<aop:服务类型 method="" pointcut-ref="AA"/>  
```
  

