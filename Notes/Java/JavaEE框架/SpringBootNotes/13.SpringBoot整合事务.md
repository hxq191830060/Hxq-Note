创建一个类来处理事务，处理事务的方法用@Transactional标注    
```java
@Service
public class userTransactional {
    @Resource
    usermapper mapper;

    @Transactional
    public void doTransacation(){
        mapper.set(20,"hxq");
        mapper.insert(new user("hhh",200));
        List<user> users=mapper.getusers();
        users.stream().forEach(x->System.out.println(x));
        System.out.println();
        mapper.delete("hhh");
        users=mapper.getusers();
        users.stream().forEach(x->System.out.println(x));
    }
}

```