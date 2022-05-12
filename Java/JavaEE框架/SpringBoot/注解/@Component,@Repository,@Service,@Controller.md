1. @Component,@Repository,@Service, @Controller  
   基本不用的四个直接
   @Component:修饰的类会自动创建bean并放入Spring容器
   @Repository,@Service, @Controller:效果类似@Component,但还有告诉开发者这是dao层，service层，control层的bean
2. @RestController:是@Controller是@ResponseBody的集合   
   表明这是个Controller bean,该bean可以响应http请求,并且该bean中,所有方法的返回值都会转换为JSON格式并直接填入Http响应体里
3. @Configuration:表示该类是一个配置类,含有配置信息
4. @Bean:一般用于注解方法,表示该方法的返回值放入Spring容器,@Bean一般用于 配置类中   