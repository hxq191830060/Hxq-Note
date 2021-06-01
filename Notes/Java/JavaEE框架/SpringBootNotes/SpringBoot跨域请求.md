**1. 什么是跨域请求**   
* **同域**：两个域使用的协议，IP，端口号都相同，就是同域  
* **跨域请求**：发出资源请求的域 与 资源所在的域 不同域
    ```
    aaa.com 请求 aaa.com/books,这是同域请求
    aaa.com 请求 bbb.com/pays,这是跨域请求
    ``` 
  
**2. 为什么要特殊处理跨域请求**   
同域请求一般是安全的   
但是跨域请求有很高的安全隐患，不法分子可以通过跨域请求发出CSRF攻击
所以游览器要对跨域请求作出限制(同源策略) 

**3. 同源策略——游览器最基础最核心的安全策略**  ~~~~
* DOM层面的同源策略   
* Cookie和XMLHttpRequest层的同源策略

**CORS Header**
* Access-Control-Allow-Origin: http://www.xxx.com   
  允许http://www.xxx.com域发起跨域请求  
* Access-Control-Max-Age：86400   
  
* Access-Control-Allow-Methods：GET, POST, OPTIONS, PUT, DELETE
* Access-Control-Allow-Headers: content-type
* Access-Control-Allow-Credentials: true
