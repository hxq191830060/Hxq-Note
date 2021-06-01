使用SpringMVC时，我们可以使用HandlerInterceptor，拦截SpringMVC处理请求的过程，自定义前置，后置操作    
例如说  
* 日志拦截器：记录请求与响应
* 认证拦截器：解析前端传入的用户标识
* 授权拦截器：可以通过每个API接口需要的授权信息，进行判断，当前请求是否允许访问
* 限流拦截器：通过每个API接口的限流配置，进行判断，当前请求是否超过允许的请求频率，避免恶意的请求，打爆整个系统

自定义拦截器类   
①实现接口HandlerInterceptor     
方法执行顺序：preHandle——>控制器方法——>postHandle——>afterCompletion  

```java
public interface HandlerInterceptor {
    //访问控制器方法前执行
    //如果该方法返回true，请求才会到达控制器
    //如果该方法返回false，请求结束
    default boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        return true;
    }
    //访问控制器方法后执行
    default void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, @Nullable ModelAndView modelAndView) throws Exception {
    }
    //postHandle后执行  
    default void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, @Nullable Exception ex) throws Exception {
    }
}
```
②需要配置类，使拦截器生效    
* @Configuration修饰  
* 实现WebMvcConfigurer接口   
```java
@Configuration
public class HelloWebConfig implements WebMvcConfigurer {
    @Autowired
    HelloInterceptor interceptor;
    @Bean
    public HelloInterceptor(){
        return new HelloInterceptor();
    }
    @Override
    public void addInterceptors(InterceptorRegistry registry){
        //对"/hello"的访问添加拦截器HelloInterceptor
        registry.addInterceptor(interceptor)).addPathPatterns("/hello");
    }
}

```

------

**使用拦截器中猜到的坑**   

**preHandle()**在访问控制器方法前执行       

* preHandle()的HttpServletRequest request由请求报文封装而来，    

* request中有一个InputStream——请求报文的二进制流 ，request通过该InputStream将请求报文的内容封装进HttpServletRequest对象   

* 到达拦截器时

  * 如果是GET请求，那么请求报文的全部信息都封装进request中，可以通过request.getParameter()获取请求体的参数   

  * 如果是POST请求，那么请求报文的报文头封装进了request中，请求体并没有封装进request中

    无法通过request.getParameter()获取请求提参数，此时通过request.getInputStream()进行输出，可以发现，输出的内容为请求体内容

* 控制器方法中的@RequestBody或@RequestParam会对request中的InputStream进行读取，读取请求体部分，传递给注解的参数  

* 问题来了，如果是POST请求，现在拦截器需要获取请求体参数，控制器方法也需要获取请求体参数，两者都是通过request中的InputStream来获取请求体参数，但是由于**InputStream只能读一次**，所以如果拦截器中使用InputStream来获取请求体参数，那么控制器方法就无法获取请求体参数，会报错——**Required request body is missing**

* 那么如何解决这个问题呢？