**一.MyBatis**
1. Mybatis是持久层框架,通过XML或者注解,将各种Statement配置起来,通过Java对象和Statement中的sql动态参数进行映射生成最终执行的sql语句,最终由Mybatis框架执行sql语句,并将结果映射为java对象  
2. Mybatis避免了几乎所有的jdbc代码和手动参数设置以及获取结果集  

**二.ORM**  
Object Relational Mapping 对象映射关系  

将数据库中 表的字段与Java实体类的属性联系起来——**表的字段的类型,名称与Java实体类的属性的类型,名称一致**    
这样就可以,**将表中的一行记录封装进一个Java实体对象中**   