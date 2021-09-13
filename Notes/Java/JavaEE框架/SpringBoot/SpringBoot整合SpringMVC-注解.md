**1. 注解**   
@Controller：添加在类上，表明这个控制器对象(自动放入Ioc容器) 
  * name：bean名  

@RestController：添加在类上(@Controller和@ResponseBody的组合)，将该类中所有方法的返回值都会进行JSON/XML序列化,返回给前端(99.9%情况用这个)  

@RequestMappring：添加在类/方法上，标记该类/方法对应的接口的路径
   * 常用属性
       * path：接口路径  
       * values：同path，path的别名  
       * method：匹配指定的请求方法(Get,Post,Head,Delete等)，为空则匹配所有请求方法   
   * 不常用属性
       * name：接口名
       * params：属性，请求参数需要包含值的参数的名字   
    
* GetMapping：对应Get方法的@RequestMapping
* PostMapping ：对应Post方法的@RequestMapping
* PutMappring：对应Put方法的@RequestMapping
·······不一一列举
  
  

* @RequestParam(常用)：**用来取请求体中的请求参数**，添加在方法参数上，将对应的请求参数的值传递给标记的方法参数   
   * name：对应的请求参数的名字，如果为空，则默认为标记的方法参数的名字 
   * value：同name，别名
   * required：请求参数是否必须穿，默认为true，表示必须传
   * defaultValue ：参数默认值
    
* @PathVariable(基本不用)：**用来取请求URL中的路径参数**(例如localhost:8080/users/{id})，属性跟@RequestParam基本相同    

**@RequestParam与@PathVariable的区别**   
请求报文——请求URL，请求行，请求头，请求体   
@RequestParam(name)——在**请求体**中找名为name的请求参数，将请求参数的值赋予方法参数   
@PathVariable(name)——用来取**请求URL**中的路径参数，看下面
```
@RequestMappring("/test/{id}")
public void fun(@PathVariable("id")Integer a)
localhost:8080/test/1, localhost:8080/test/2,等URL都会访问该方法，然后请求URL中的1/2会赋予方法参数a 
```


