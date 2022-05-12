**默认路径**  
Springboot对于静态资源的位置是有规定的    
1. "classpath:/META-INF/resources/"   
2. "classpath:/resources/"   
3. "classpath:/static/"   
4. "classpath:/public/"      

我们可以在resources根目录下新建对应的文件夹，都可以存放我们的静态文件；  
比如我们访问 localhost:8080/1.js , 他会去   
resources根目录/META-INF/resources/   
resources根目录/resources/   
resources根目录/static/   
resources根目录/public/   
四个目录下寻找1.js,不包含这4个目录的子目录
*** 

**自定义路径**  
```
spring.resources.static-locations=classpath:/coding/,classpath:/kuang/
这样设置后，访问localhost:8080/1.js，他会去resources根目录/codeing/和resources根目录/kuang/下寻找资源
```
一旦自己定义了静态文件夹的路径，原来的自动配置就都会失效了！






