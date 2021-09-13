### **Session实现登录**

1、用户向服务器发送用户名和密码。

2、服务器验证通过后，在当前对话（session）里面保存相关数据，比如用户角色、登录时间等等。

3、服务器向用户返回一个 session_id，写入用户的 Cookie。

4、用户随后的每一次请求，都会通过 Cookie，将 session_id 传回服务器。

5、服务器收到 session_id，找到前期保存的数据，由此得知用户的身份。

* **问题**

  如果服务由服务器集群提供的话，就必须共享Session数据





### **JWT--Json Web Tokens**

用户向auth服务器进行登录，auth服务器生成一个JSON对象

```json
{
  "姓名": "张三",
  "角色": "管理员",
  "到期时间": "2018年7月1日0点0分"
}
```



服务器为JSON生成一个JWT，如下

```
eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.
eyJmcm9tX3VzZXIiOiJCIiwidGFyZ2V0X3VzZXIiOiJBIn0.
rSWamyAYwuHCo7IFAgd1oRpSP7nzL7BF5t7ItqpKViM

```
JWT用.分成三个部分

* Header 

  解析后是一个JSON对象啊，通常如下结构（Header就是该对象用Base64算法转成的String）

  ```json
  {
      "alg": "HS256",
      "typ": "JWT"
  }
  ```

  * alg：生成签名的算法，默认为HMAC SHA256(写成HS256)
  * typ：表示token的类型，JWT的typ一律为 "JWT"

* Payload

  解析后也是一个JSON，存储实际需要传递的数据（Payload就是该对象用Base64算法转成的String）

  JWT规定了7个官方字段，除此之外，可以自定义一些字段

  * iss(issuer)：签发人
  * exp(expiration time):过期时间
  * sub(subject)：主题
  * aud(audience)：受众
  * nbf(Not Before)：生效时间
  * iat(Issued At)：签发时间
  * jti(JWT ID)：编号

* Signature

  对Header和Payload的签名，防止数据篡改

  首先要指定一个秘钥，秘钥只有服务器知道，然后使用Header指定的签名算法,

  利用该算法和秘钥，对Header和Payload进行计算，得到Signature





生成的JWT发送给用户，用户之后访问服务器都带上JWT (Header',Payload',Signature')，服务器利用秘钥和Header'中指定的算法，对Header'和Payload‘进行计算得到 Signature''，只要signature’‘跟signature’相同，就能确认用户的身份,如果signature''与signature'不相同，就会返回401



