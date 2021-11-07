**SpringMVC解决跨域请求有三种方式**

* **使用@CrossCors**
* **使用CorsRegistry.java注册表**
* **使用CorsFilter.java过滤器**



### 1. 使用@CrossCors(不推荐)

**@CrossCros**添加在类或者方法上——标记类/方式的Cors信息

* **常用属性**
  * **origins**：设置允许的请求来源，默认为*
  * **value**：同origins
  * **allowCredentials**：是否允许客户端请求发送Cookie，默认为false，不允许发送Cookie
  * **maxAge**：本次**预检请求(前端在发起请求前，会先发送OPTIONS预检请求，要求服务器确认是否能够处理该请求)**的有效期，默认1800s
* **不常用属性**
  * **methods**：设置允许的请求方式，默认为GET+POST
  * **allowedHeaders**：允许的请求头，默认为*
  * **exposedHeaders**：允许的响应头，默认为*

```java
// TestController.java

@RestController
@RequestMapping("/test")
@CrossOrigin(origins = "*", allowCredentials = "true") // 允许所有来源，允许发送 Cookie
public class TestController {

    /**
     * 获得指定用户编号的用户
     *
     * @return 用户
     */
    @GetMapping("/get")
    @CrossOrigin(allowCredentials = "false") // 允许所有来源，不允许发送 Cookie
    public UserVO get() {
        return new UserVO().setId(1).setUsername(UUID.randomUUID().toString());
    }

}
```

**问题**

前端使用符合 CORS 规范的网络库时，例如说 Vue 常用的网络库 [axios](https://github.com/axios/axios) ，在发起[非简单请求](https://www.ruanyifeng.com/blog/2016/04/cors.html)时，会自动先先发起 `OPTIONS` “预检”请求，要求服务器确认是否能够这样请求。这样，这个请求就会被 **SpringMVC 的拦截器所处理**，导致**跨域失效**



### 2. 使用CorsRegistry.java注册表(原理同1,不推荐)

```java
// SpringMVCConfiguration.java

@Override
public void addCorsMappings(CorsRegistry registry) {
    // 添加全局的 CORS 配置
    registry.addMapping("/**") // 匹配所有 URL ，相当于全局配置
            .allowedOrigins("*") // 允许所有请求来源
            .allowCredentials(true) // 允许发送 Cookie
            .allowedMethods("*") // 允许所有请求 Method
            .allowedHeaders("*") // 允许所有请求 Header
//                .exposedHeaders("*") // 允许所有响应 Header
            .maxAge(1800L); // 有效期 1800 秒，2 小时
}
```





### 3. 使用CorsFileter过滤器

使用 **CorsFilter.java 过滤器**，处理跨域请求    

在SpringMVC的配置类中，添加
```java
// SpringMVCConfiguration.java

@Bean
public FilterRegistrationBean<CorsFilter> corsFilter() {
    // 创建 UrlBasedCorsConfigurationSource 配置源，类似 CorsRegistry 注册表
    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    // 创建 CorsConfiguration 配置，相当于 CorsRegistration 注册信息
    CorsConfiguration config = new CorsConfiguration();
    config.setAllowedOrigins(Collections.singletonList("*")); // 允许所有请求来源
    config.setAllowCredentials(true); // 允许发送 Cookie
    config.addAllowedMethod("*"); // 允许所有请求 Method
    config.setAllowedHeaders(Collections.singletonList("*")); // 允许所有请求 Header
    // config.setExposedHeaders(Collections.singletonList("*")); // 允许所有响应 Header
    config.setMaxAge(1800L); // 有效期 1800 秒，2 小时
    source.registerCorsConfiguration("/**", config);
    // 创建 FilterRegistrationBean 对象
    FilterRegistrationBean<CorsFilter> bean = new FilterRegistrationBean<>(
            new CorsFilter(source)); // 创建 CorsFilter 过滤器
    bean.setOrder(0); // 设置 order 排序。这个顺序很重要哦，为避免麻烦请设置在最前
    return bean;
}
```