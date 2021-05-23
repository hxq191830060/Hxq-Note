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