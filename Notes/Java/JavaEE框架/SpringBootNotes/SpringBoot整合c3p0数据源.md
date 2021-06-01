**添加依赖**   
```
<dependency>
    <groupId>com.mchange</groupId>
    <artifactId>c3p0</artifactId>
    <version>0.9.5.2</version>
</dependency>
``` 

**配置数据源——两种方法**  
**1. application.yml配置**     
   添加**spring.datasource.type: com.mchange.v2.c3p0.ComboPooledDataSource**    

**2. 注解配置**——向Spring容器中添加**DataSource对象**和**SqlSessionFactoryBean对象**   
* DataSource对象——通过它获取数据库连接
   ```java
   @SpringBootConfiguration//标记该类为Springboot的配置类
   public class DataSouceConf {
   
       @Value("${spring.datasource.driver-class-name}")
       private String driverClass;
       @Value("${spring.datasource.url}")
       private String url;
       @Value("${spring.datasource.username}")
       private String username;
       @Value("${spring.datasource.password}")
       private String password;
       //以上为配置DataSource需要的数据库四大参数，直接从配置文件获取
       
       //向Spring容器中添加DataSource对象
       @Bean
       public DataSource getDataSource() throws PropertyVetoException {
           //创造C3p0数据源对象
           ComboPooledDataSource dataSource=new ComboPooledDataSource();
   
           //C3p0数据源设置四大参数
           dataSource.setDriverClass(driverClass);
           dataSource.setJdbcUrl(url);
           dataSource.setUser(username);
           dataSource.setPassword(password);
   
           //设置数据源为关闭连接后不自动提交
           dataSource.setAutoCommitOnClose(false);
   
           //将数据源放入Spring容器(@Bean注解)
           return dataSource;
       }
   }
   ```
* SqlSessionFactoryBean对象——用于创建SqlSession对象   
  * 配置数据源(must)
  * 配置Dao接口的包路径(must)
  * 配置映射配置文件路径(must)
  * 配置主配置文件(可选)
  * 配置类型别名(可选)
  * 配置插件，例如分页插件(可选)
    ```java
    @SpringBootConfiguration//这是SpringBoot的配置类
    public class SqlSessionFactoryBeanConf {
    
        //配置SqlSessionFacotryBean需要两个参数
        //一个DAO接口的包路径，一个是映射配置文件的路径
        //DAO接口的包路径
        String MapperInterfacePackage="com.example.springbootmybatis.Mapper";
    
        //映射配置文件的路径
        String MapperXMLPath="classpath:Mapper/*.xml";
    
        @Autowired
        DataSource dataSource;
    
    
        //向Spring容器中添加SqlSessionFactoryBean对象
        @Bean
        public SqlSessionFactoryBean getSqlSessionFactoryBean() throws IOException {
            //创建SqlSessionFactoryBean对象
            SqlSessionFactoryBean sqlSessionFactoryBean=new SqlSessionFactoryBean();
    
            //SqlSessionFactoryBean需要设置三个属性——Dao接口的包路径，映射配置文件的路径，数据源
    
            //设置Dao接口的包路径
            sqlSessionFactoryBean.setTypeAliasesPackage(MapperInterfacePackage);
    
            //设置映射配置文件的路径
            sqlSessionFactoryBean.setMapperLocations(new PathMatchingResourcePatternResolver()
            .getResources(MapperXMLPath));
    
            //设置数据源
            sqlSessionFactoryBean.setDataSource(dataSource);
            return sqlSessionFactoryBean;
        }
    }
    ```