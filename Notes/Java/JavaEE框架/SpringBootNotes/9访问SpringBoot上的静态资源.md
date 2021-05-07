application.yml中的spring.mvc.static-path-pattern 规定了Brower应该以什么路径来访问SpringBoot项目中的静态资源   

默认：spring.mvc.static-path-pattern:/**  等价于 static/**
也就是说，默认情况下游览器只能访问static目录下的资源  
例子：  
localhost:8080/a.html——访问SpringBoot项目中 resources目录下的 static/a.html
localhost:8080/pages/b.html——访问SpringBoot项目中 resources目录下的 static/pages/a.html