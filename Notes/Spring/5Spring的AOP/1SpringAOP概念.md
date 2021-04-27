**一.SpringAOP相关概念**  
1.AOP(Aspect-Oriented Programming) 面向切面编程，AOP的核心——切面(Aspect)  
2.切面(Aspect)=pointcut+advice  
用@Aspect标注类 或者 在xml中用<aop：aspect> 来定义一个切面  
3. 连接点(Join Point)——在Spring中，每个方法都是一个连接点
4. 切点(Pointcut)——通过**切点表达式**指定某几个连接点为切点  
5. 增强/通知(advice)——与Pointcut结合的某个方法  
6. 织入(weaving)——用Pointcut和advice结合，创建代理对象的过程
7. AOP Proxy——Spring通过动态代理创建的动态代理类，结合了Pointcut和advice的代码逻辑 
8. 目标对象(Target)——动态代理对象，织入了Pointcut和advice的代码逻辑  


   ![alt 属性文本](picture/img.png)  
 


通过上图，我们可以发现，我们可以在不改变类A的情况下，通过切面类，在Pointcut之外增加一些功能(advance),所以切面类也叫功能增强类，advance()也叫功能增强方法

**对于动态代理的实现，如果是基于接口的——JDK动态代理，如果不是基于接口的——Cglib**

