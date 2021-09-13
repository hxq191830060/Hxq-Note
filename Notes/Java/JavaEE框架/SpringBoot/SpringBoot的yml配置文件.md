**1. 语法结构**   
语法结构 ：key:空格value   
**2. 基础语法**  
* 空格不能省略   
* 以缩进来控制层级关系，只要是左边对齐的一列数据都是同一个层级的。
* 字符串默认不用加上双引号或者单引号
*  双引号，不会转义字符串里面的特殊字符，特殊字符会作为本身想表示的意思
* 单引号，会转义特殊字符，特殊字符最终会变成和普通字符一样输出  
* 对象、Map格式   
```
k: 
    key1: value1
    key2: value2
```
* 数组(List,Set)
```
pets:
 - cat
 - dog
 - pig
```  

**3. 注入配置文件——@ConfigurationProperties默认从全局配置文件获取值**    
yml配置文件可以直接给实体类注入匹配值     
```
person:
  name: qinjiang
  age: 3
  happy: false
  birth: 2000/01/01
  maps: {k1: v1,k2: v2}
  lists:
   - code
   - girl
   - music
  dog:
    name: 旺财
    age: 1
``` 
```java
/*
@ConfigurationProperties作用：
将配置文件中配置的每一个属性的值，映射到这个组件中；
告诉SpringBoot将本类中的所有属性和配置文件中相关的配置进行绑定
参数 prefix = “person” : 将配置文件中的person下面的所有属性一一对应
*/
@Component //注册bean
@ConfigurationProperties(prefix = "person")
public class Person {
    private String name;
    private Integer age;
    private Boolean happy;
    private Date birth;
    private Map<String,Object> maps;
    private List<Object> lists;
    private Dog dog;
}
```

**实体类的要求——①必须在bean中注册 ②必须实现有参构造**     

**4. 从配置文件获取属性——@PropertySource:加载指定的配置文件**   
```java
@Data
@NoArgsConstructor
@AllArgsConstructor
@Component
@PropertySource(value = "classpath:person.properties")
public class Person {
    @Value("${name}")
    String name;
    @Value("${age}")
    int age;
}
```