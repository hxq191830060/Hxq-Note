## 1. 导入依赖

```
<dependency>
        <groupId>com.github.ulisesbocchio</groupId>
        <artifactId>jasypt-spring-boot-starter</artifactId>
        <version>2.1.0</version>
</dependency>
```



## 2. 配置密钥

```yml
jasypt:
  encryptor:
    password: xxxxxxxxx
```



## 3. 利用测试用例来对敏感数据加密

```java
@RunWith(SpringRunner.class)
@SpringBootTest
public class DatabaseTest {

    @Autowired
    private StringEncryptor encryptor;

    @Test
    public void getPass() {
        String url = encryptor.encrypt("jdbc:mysql://localhost:3306/mydb?autoReconnect=true&serverTimezone=GMT%2B8&useUnicode=true&characterEncoding=utf-8");
        String name = encryptor.encrypt("root");
        String password = encryptor.encrypt("123456");
        System.out.println("database url: " + url);
        System.out.println("database name: " + name);
        System.out.println("database password: " + password);
        Assert.assertTrue(url.length() > 0);
        Assert.assertTrue(name.length() > 0);
        Assert.assertTrue(password.length() > 0);
    }
}
```



## 4. 用加密后的字符串在配置文件中替换明文

```yml
server:
  port: 8080
spring:
  # 数据库相关配置
  datasource:
    driver-class-name: com.mysql.cj.jdbc.Driver
    # 这里加上后缀用来防止mysql乱码,serverTimezone=GMT%2b8设置时区
    url: ENC(h20YiPrvNnuuTGjlrE1RVpudMuIQAS6ZPSVo1SPiYVyLen7/TWI5rXVRkStA3MDcoVHQCmLa70wYU6Qo8wwtnsmaXa5jykD3MNhAp5SGJxHsTG5u7tflPdnNmOufyhdsYPxBGWAgibYs9R7yBfrvtwBTRbe096APd3bnG3++Yro=)
    username: ENC(sT6BztXbJEa71eg3pPGYMQ==) #ENC是固定写法，表示要用密钥来解密
    password: ENC(MpSZFJ9ftq+3+VUANZjr0Q==) #ENC是固定写法，表示要用密钥来解密
  jpa:
    hibernate:
      ddl-auto: update
    show-sql: true
  # 返回的api接口的配置，全局有效
  jackson:
   # 如果某一个字段为null，就不再返回这个字段
    default-property-inclusion: non_null
    date-format: yyyy-MM-dd HH:mm:ss
    serialization:
      write-dates-as-timestamps: false
    time-zone: GMT+8
# jasypt加密的密匙
jasypt:
  encryptor:
    password: Y6M9fAJQdU7jNp5MW
```



## 5. 更安全的做法

1. 将密钥作为启动参数传入

   ```
   java -jar xxx.jar  -Djasypt.encryptor.password=Y6M9fAJQdU7jNp5MW
   ```

2. 在/etc/profile中配置密钥

   ```
   export JASYPT_PASSWORD = Y6M9fAJQdU7jNp5MW
   ```

   ```
   java -jar -Djasypt.encryptor.password=${JASYPT_PASSWORD} xxx.jar
   ```

   