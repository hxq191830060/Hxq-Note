**1. 导入依赖**
***
**2. 分页参数介绍**  
文档链接   
https://pagehelper.github.io/docs/howtouse/#1-%E5%BC%95%E5%85%A5%E5%88%86%E9%A1%B5%E6%8F%92%E4%BB%B6
***
**3. 配置分页参数参数**   
* yml配置文件中配置
    ```
    pagehelper:
      reasonable: true
      support-methods-arguments: true
      params: count=countsql
      helper-dialect: mysql
    ```
  
* 配置类配置,向Spring容器中注入PageHelper对象
    ```java
    @SpringBootConfiguration
    public class MybatisConfig {
        @Bean
        public PageHelper pageHelper() {
            PageHelper pageHelper = new PageHelper();
            Properties p = new Properties();
            p.setProperty("offsetAsPageNum", "true");
            p.setProperty("rowBoundsWithCount", "true");
            p.setProperty("reasonable", "true");
            pageHelper.setProperties(p);
            return pageHelper;
        }
    }
    ```
  
* 配置多数据源时，通过PageInterceptor将配置注入SqlSessionFactoryBean对象  

```
    // 创建SqlSessionFactory对象
    @Bean(name = "testDsSqlSessionFactory")
    public SqlSessionFactory testDsSqlSessionFactory(@Qualifier("testDataSource") DataSource clusterDataSource)
            throws Exception {
        final SqlSessionFactoryBean sessionFactory = new SqlSessionFactoryBean();
        sessionFactory.setDataSource(clusterDataSource);

        // 加载全局的配置文件
        sessionFactory.setConfigLocation(
                new DefaultResourceLoader().getResource("classpath:mybatis-config.xml"));

        // 配置类型别名
        sessionFactory.setTypeAliasesPackage(ALIAS);

        // 分页插件属性配置(也可以在配置文件里面配置)
        PageInterceptor interceptor = new PageInterceptor();
        Properties properties = new Properties();
        properties.setProperty("reasonable", "true");
        properties.setProperty("supportMethodsArguments", "true");
        properties.setProperty("returnPageInfo", "check");
        properties.setProperty("params", "count=countSql");
        interceptor.setProperties(properties);
        // 添加插件
        sessionFactory.setPlugins(interceptor);

        sessionFactory.setMapperLocations(new PathMatchingResourcePatternResolver()
                .getResources(DataSourceConfig.MAPPER_LOCATION));
        return sessionFactory.getObject();
    }
```
***
**分页注意事项**
* 只有紧跟在PageHelper.startPage方法后的第一个Mybatis的查询（Select）方法会被分页
* 对于带有for update的sql，会抛出运行时异常，对于这样的sql建议手动分页
* 
* 
