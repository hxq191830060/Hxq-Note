
**1. WebMvcConfiguration是一个接口**    
里面定义了许多方法(拦截器，跨域，页面跳转，静态资源等)    

**2. 使用WebMvcConfiguration**   
自定义一个配置类(@Configuration)，实现WebMvcConfiguration接口，并根据需要覆写方法   

**3. 接口提供的方法**  
```java
public interface WebMvcConfigurer {
    void configurePathMatch(PathMatchConfigurer var1);
 
    void configureContentNegotiation(ContentNegotiationConfigurer var1);
 
    void configureAsyncSupport(AsyncSupportConfigurer var1);
 
    void configureDefaultServletHandling(DefaultServletHandlerConfigurer var1);//默认静态资源处理器
 
    void addFormatters(FormatterRegistry var1);
 
    void addInterceptors(InterceptorRegistry var1);//添加拦截器
 
    void addResourceHandlers(ResourceHandlerRegistry var1);//自定义静态资源映射目录
 
    void addCorsMappings(CorsRegistry var1);//跨域
 
    void addViewControllers(ViewControllerRegistry var1);//实现页面跳转
 
    void configureViewResolvers(ViewResolverRegistry var1);//视图解析器
 
    void addArgumentResolvers(List<HandlerMethodArgumentResolver> var1);
 
    void addReturnValueHandlers(List<HandlerMethodReturnValueHandler> var1);
 
    void configureMessageConverters(List<HttpMessageConverter<?>> var1);//信息转换器
 
    void extendMessageConverters(List<HttpMessageConverter<?>> var1);
 
    void configureHandlerExceptionResolvers(List<HandlerExceptionResolver> var1);
 
    void extendHandlerExceptionResolvers(List<HandlerExceptionResolver> var1);
 
    Validator getValidator();
 
    MessageCodesResolver getMessageCodesResolver();
}
``` 
**4. 实现案例**  
* **拦截器**    
  * addInterceptor()：添加一个拦截器实例
  * addPathPatterns()：配置拦截器拦截路径
  * excludePathPatterns()：设置不需要拦截的过滤规则  
    ```
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        super.addInterceptors(registry);
        registry.addInterceptor(new TestInterceptor()).addPathPatterns("/**").excludePathPatterns("/emp/toLogin","/emp/login","/js/**","/css/**","/images/**");
    }
    ```
    
* **页面跳转**  
    ```
        @Override
        public void addViewControllers(ViewControllerRegistry registry) {
            registry.addViewController("/toLogin").setViewName("login");
        }
    ``` 
  在这里重写的addViewControllers方法，并不会覆盖**WebMvcAutoConfiguration(SpringBoot的自动配置)**中的addViewControllers(在此方法中，SpringBoot将 / 映射为 index.html)   
  这也就意味着自定义配置跟SpringBoot的自动配置同时生效   


* **自定义静态资源映射目录** 
  ```
  @Configuration
  public class MyWebMvcConfigurerAdapter implements WebMvcConfigurer {
      /**
       * 配置静态访问资源
       * @param registry
       */
      @Override
      public void addResourceHandlers(ResourceHandlerRegistry registry) {
          registry.addResourceHandler("/my/**").addResourceLocations("classpath:/my/");
      }
  }
  ```
  addResourceHandler()：对外暴露的访问路径(localhost:8080/my/任意)       
  addResourceLocations()：文件存放在服务器上的路径  

