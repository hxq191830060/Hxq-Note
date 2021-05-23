Http请求报文——>请求行，请求头，请求体   
Get不能有请求体，Post才能有请求体   
请求体的类型——由请求头中的Content-type决定   

Content-type： 
* form-data：请求体中既可以上传文件等二进制数据，也可以上传键值对
* x-www-form-urlencoded：请求体中只是键值对
* raw ：可以上传任意格式的文本   
   * JSON：请求体中是JSON对象
   * XML：请求体中是XML
   * HTML：
   * JavaScript
   * Text
* binary：请求体中是二进制数据
* GraphQL  


@RequestBody用来接收Content-type为JSON的Http请求报文的请求体    
```
请求体中如下
{
    "id": 1,
    "name": "hxq",
    "score": 100,
    "age": 20
}
```
public void fun(@RequestBody String str)——>那么整个JSON请求体，会转变为字符串——>str   
public void fun(@RequestBody User user)——>创建一个User对象，然后将JSON请求体中，与User对象属性同名的key的value赋给User对象的属性   



@RequestParam用来接收Content-type为form-data/x-www-form-urlencoded的Http请求报文的请求体   
```
请求体如下  
id ：1
name：hxq
age：20
```
public void fun(@RequestParam("id")Integer a,@RequestParam("name")String str)——>将请求体中key为id的value赋予a，key为name的value赋予str