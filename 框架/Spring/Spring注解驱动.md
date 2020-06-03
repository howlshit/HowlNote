1.导入组件

```
@Component：普通组件

@Service：service层

@Repository：dao层

@Controller：controller层（默认id为类名的小写驼峰）

@Import：快速导入外部组件（默认id为全限定类名）

@ImportResource(locations = {"classpath:application.xml"})：将以前的xml配置文件读取并应用

FactoryBean：实现接口，用@Bean导入容器,导入的是工厂产生的类
```



2.java配置类相关注解

```
@Configuration：声明为配置类

@Bean：返回值注册进容器

@ComponentScan：扫描注册组件

@WishlyConfiguration：@Configuration与@ComponentScan的组合注解
```



3.@Bean的属性支持

```
@Bean(initMethod = "",destroyMethod = "")

@Scope 设置Spring容器如何新建Bean实例（方法上，得有@Bean）

@Lazy：针对单例实现懒加载

@PostConstruct：由JSR-250提供，bean的initMethod

@PreDestory：由JSR-250提供，等价于bean的destroyMethod
```



4.注入bean的注解

```
@Autowired：由Spring提供，按类型装入

@Qualifier：指定组件id，而不是按照类型注入

@Primary：指定自动装配的首选

@Inject：由JSR-330提供

@Resource：由JSR-250提供，与@Autowired区别于按byName装入
```



5.@PropertySource和@Value注解 

```
@Value("Howl")
String name;

@Value("#{systemProperties['os.name']}")
String osName;

@Value("#{ T(java.lang.Math).random() * 100 }") 
String randomNumber;

@Value("#{domeClass.name}")
String name;

@Value("classpath:com/hgs/hello/test.txt")
String Resource file;

@Value("http://www.cznovel.com")
Resource url;

// 使用外部配置文件，前提把配置文件加载到环境变量中
Value("${book.name}")
String bookName;

@PropertySource 放在配置类上：读取properties外部配置文件K/V保存到运行的环境变量中，用${}取出
@PropertySource("classpath:com/hgs/hello/test/test.propertie")
```



6.切面（AOP）相关注解

```
@Aspect：声明一个切面类

@Before：前置通知

@After：后置通知

@AfterReturning：返回通知

@AfterThrowing：异常通知

@Around：在方法执行之前与之后执行

@PointCut：声明切点 在java配置类中使用@EnableAspectJAutoProxy注解开启Spring对AspectJ代理的支持（类上）
```



7.环境切换

```
@Profile：通过设定配置环境

@Conditional：实现Condition接口，从而决定该bean是否被实例化
```



8.异步相关

```
@EnableAsync：配置类中，通过此注解开启对异步任务的支持

@Async：在实际执行的bean方法使用该注解来申明其是一个异步任务（方法上或类上所有的方法都将异步，需要@EnableAsync开启异步任务）
```



9.定时任务相关

```
@EnableScheduling：在配置类上使用，开启计划任务的支持

@Scheduled：来申明这是一个任务，包括cron,fixDelay,fixRate等类型，使用cron表达式：每10秒执行
```



10.@Enable*注解说明

```
这些注解主要用来开启对xxx的支持。

@EnableAspectJAutoProxy：开启对AspectJ自动代理的支持

@EnableAsync：开启异步方法的支持

@EnableScheduling：开启计划任务的支持

@EnableWebMvc：开启Web MVC的配置支持

@EnableConfigurationProperties：开启对@ConfigurationProperties注解配置Bean的支持

@EnableJpaRepositories：开启对SpringData JPA Repository的支持

@EnableTransactionManagement：开启注解式事务的支持

@EnableCaching：开启注解式的缓存支持
```



11.测试相关注解

```
@RunWith：运行器，Spring中通常用于对JUnit的支持
@RunWith(SpringJUnit4ClassRunner.class)

@ContextConfiguration：用来加载配置ApplicationContext，其中classes属性用来加载配置类
@ContextConfiguration(classes={TestConfig.class})
```



12.补充

```java
@EventListener：添加监听器，当然要注册进容器
```



13.SpringMVC部分

```
@EnableWebMvc：在配置类中开启Web MVC的配置支持

@Controller：声明该类为SpringMVC中的Controller

@ResponseBody：响应返回字符串

@RestController：该注解为一个组合注解，相当于@Controller和@ResponseBody的组合

@RequestMapping：用于映射Web请求，包括访问路径和参数（类或方法上）
@GetMapping
@PostMapping

@RequestBody：请求发送json数据时，而参数在请求体中，而不是在url。那么该注解补充get方法只获取url参数的缺点，调用setter方法映射进对象中

@PathVariable：用于接收路径参数，resful风格

@CookieValue：放方法参数中，将映射到参数上

@RequestParam：绑定请求中name属性相同的变量（使用了反射）

@RestControllerAdvice
@ControllerAdvice：该注解将对于控制器的全局配置放置在同一个位置。注解了@Controller的类的方法可使用@ExceptionHandler、@InitBinder、@ModelAttribute注解到方法上， 这对所有注解了@RequestMapping的控制器内的方法有效。

结合下面的注解可实现：

@ExceptionHandler：用于全局处理控制器里的异常

@InitBinder：用来设置WebDataBinder，WebDataBinder用来自动绑定前台请求参数到Model中。

@ModelAttribute：本来的作用是绑定键值对到Model里，在@ControllerAdvice中是让全局的@RequestMapping都能获得在此处设置的键值对。
```



14.springboot注解

```
@SpringBootApplication

@EnableAutoConfiguration

@SpringBootConfiguration

@EnableConfigurationProperties：开启下面的功能

@ConfigurationProperties(prefix="person")：需要下面的依赖
其是@Value的升级版。注解类上，宽松批量注入属性，不像@Value一个个来书写注解
```

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-configuration-processor</artifactId>
</dependency>
```











# 1. 组件注册

* 包扫描+组件注解：@Component
* @Bean：导入第三方包里面的组件
* @Import：快速给容器导入一个组件
* FactoryBean：实现接口注入容器



### 1.1 配置类上的基本注解

@Configuration：声明当前类为配置类

@Bean：声明方法的**返回值**为一个bean，id默认是方法名

@ComponentScan：用于对Component进行扫描，里面有包含与排除规则

```java
@Configuration
@ComponentScan(value = {"com.howl.springannotation.controller","com.howl.springannotation.service"},
        excludeFilters = {
        @ComponentScan.Filter(type = FilterType.ANNOTATION,classes = Controller.class)
})
public class MainConfig {

    @Bean(value = "person01")
    public Person person(){
        return new Person("Howl",20);
    }
}
```





## 1.2 @Bean的属性

@Scope：设置Bean实例的作用域，默认单例（IOC容器启动会调用方法创建对象）

@Lazy：针对单实例实现懒加载

```java
@Configuration
public class MainConfig2 {

    @Bean
    @Scope(value = "singleton")
    @Lazy
    public Person person(){
        System.out.println("person was created");
        return new Person("Howl",20);
    }
}
```





## 2.3 @Conditional

满足条件才注册bean，其接收Condition类的数组。而Condition是个接口，需要我们去实现。

**@Conditional可在方法上，也可在类上**

```java
public class MyCondition implements Condition {

    /**
     * @param context：判断条件能使用的上下文环境
     * @param metadata：标注了@Conditional的注释信息
     * 参数是使用该类的地方帮我们传进去的
     * @return
     */
    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
//        context.getClassLoader();
//        context.getBeanFactory();
//        context.getClass();
//        context.getRegistry();
//        String os = context.getEnvironment().getProperty("os.name");
//		  return os.contains("Windows");
        return false;
    }
}
```

```java
@Configuration
public class MainConfig {

    @Bean
    @Conditional({MyCondition.class})
    public Person person01(){
        return new Person("Howl",20);
    }
}
```





## 2.4 Import、ImportSelector、Registrar、FactoryBean

* @Import(value = {Person.class,User.class})：直接写类，id默认全类名
* @Import(value = {MyImportSelector.class})：写ImportSelector实现类，id默认全类名
* @Import(value = {MyImportBeanDefinitionRegistrar.class})：手动注册bean
* FactoryBean：实现该接口，用@Bean注解导入该类，那么就会将其内部的类也加入容器。获取工厂可加前缀&

```java
public class MyImportSelector implements ImportSelector {

    /**
     * @param importingClassMetadata：可获取注解类的所有注解信息
     * @return：返回值就是要导入的组件
     */
    @Override
    public String[] selectImports(AnnotationMetadata importingClassMetadata) {
        return new String[]{"com.howl.springannotation.bean.Student","com.howl.springannotation.bean.Worker"};
    }
}
```

```java
public class MyImportBeanDefinitionRegistrar implements ImportBeanDefinitionRegistrar {

    /**
     * @param importingClassMetadata：当前类的注解信息
     * @param registry：BeanDefinition的注册类
     */
    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        boolean teacher = registry.containsBeanDefinition("Teacher");
        if (!teacher) {
            RootBeanDefinition root = new RootBeanDefinition(Teacher.class);
            registry.registerBeanDefinition("Teacher", root);
        }
    }
}
```

```java
public class PersonFactoryBean implements FactoryBean<Person> {

    // 返回一个对象会添加到容器中
    @Override
    public Person getObject() throws Exception {
        return new Person();
    }

    @Override
    public Class<?> getObjectType() {
        return Person.class;
    }

    @Override
    public boolean isSingleton() {
        returnjava true;
    }
}
```

```java
@Configuration
@Import(value = {User.class, MyImportSelector.class, MyImportBeanDefinitionRegistrar.class})
public class MainConfig4 {

    @Bean
    public PersonFactoryBean personFactoryBean() {
        return new PersonFactoryBean();
    }
}
```













# 2. 属性赋值

* @Value：
  * 基本数值
  * SpEL    #{}
  * 配置文件    ${}



```java
public class Person {

    @Value("Howl")
    private String name;
    @Value("${person.age}")
    private Integer age;

    // 。。。。
}
```

```java
@PropertySource(value = "classpath:person.properties")
@Configuration
public class MainConfig5 {

    @Bean
    public Person person(){
        return new Person();
    }

}
```













# 3. 自动装配

- @Autowired：用AutowiredAnnotationBeanPostProcssor完成自动注入的
  - 放属性上
  - 放setter（方法）上：调用方法完成赋值，方法的参数从容器中获取
  - 放构造器上：默认的组件会调用无参构造器创建对象再进行初始化赋值等操作；若当前类只有一个有参构造器，那么@Autowired可以省略的（前提没有默认，否则首选默认）
  - 配置@Bean，那么方法参数可以自动注入而不用@Autowired
- XXXAware：自定义组件想要使用Spring容器底层的组件（ApplicationContext,BeanFactory），只需实现接口即可，在创建对象的时候，会调用接口规定的方法。其实也是setter方法参数自动注入

```java
@Autowired
BookService bookService;

@Autowired
public void setBookService(BookService bookService) {
    this.bookService = bookService;
}

@Autowired
public BookController(BookService bookService) {
    this.bookService = bookService;
}

@Bean
public BookController bookController(BookService bookService){
    return new BookController(bookService);
}
```













# 4. 环境切换

默认是default标识，但是下面代码三步执行，第二部就是加载配置文件，没有设置环境，所以得手动做这三步

AnnotationConfigApplicationContext ac = new AnnotationConfigApplicationContext(MainConfig7.class)

```java
public AnnotationConfigApplicationContext(Class<?>... componentClasses) {
   this();
   register(componentClasses);		// 加载配置文件
   refresh();
}
```

```java
AnnotationConfigApplicationContext ac = new AnnotationConfigApplicationContext();
ac.getEnvironment().setActiveProfiles("dev");
ac.register(MainConfig7.class);
ac.refresh();
```













# 5. Bean生命周期

bean创建-----初始化-----销毁



一. 构造

* 单实例：容器启动时创建（调用构造器）

* 多实例：每次获取时创建（调用构造器）



二.初始化

* 对象创建完成，并复赋值好后，调用初始化方法



三.销毁

* 单实例：容器关闭时调用
* 多实例：容器不管理这个bean，GC来回收





**指定初始化和销毁方法：**

* @Bean(initMethod = "init",destroyMethod = "destory")

**让Bean实现接口：**

* InitializingBean
* DisposableBean

**JSR-250提供注解**

* @PostConstruct
* @PreDestroy

**后置处理器：对每一个bean都有效**

* BeanPostProcessor

```java
public class Worker implements InitializingBean, DisposableBean {

    public Worker() {
        System.out.println("worker constructor");
    }

    @PostConstruct
    public void init(){
        System.out.println("worker init");
    }

    @PreDestroy
    public void destory(){
        System.out.println("worker destory");
    }

    @Override
    public void destroy() throws Exception {
        System.out.println("接口的销毁方法");
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        System.out.println("接口的初始化方法");
    }
}
```

```java
public class MyBeanProcessor implements BeanPostProcessor {
    /**
     * @param bean：创建的实例
     * @param beanName：实例的名字
     * @return ：返回需要用到的实例，类似于@Bean
     * @throws BeansException
     */
    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        System.out.println(bean + beanName + "---------内容呢额-----------");
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        System.out.println(bean + beanName + "-----------内容呢额---------");
        return bean;
    }
}
```





## AOP

被切入的方法，切面类，各种通知、原理是动态代理



切面类：

```java
@Aspect
public class LogAspect {

    @AfterReturning(value = "execution(public void com.howl..*(..))", returning = "result")
    public int after(JoinPoint joinPoint, Object result) {
        System.out.println("前置通知" + joinPoint.getSignature().getName() + result);
        return 10;
    }
}
```

配置类：重点在于开启注解

```java
@EnableAspectJAutoProxy  // 开启切面注解
@Configuration
public class AopConfig {

    @Bean  // 注意Bean上的@Autowired是自动注入，可以不写的
    public LogAspect logAspect(JoinPoint joinPoint){
        return new LogAspect();
    }

    @Bean
    public TestService testService(){
        return new TestService();
    }
}
```











## 6. 声明式事务

```java
// 1. 导入相关依赖
// 2. 注册配置数据源
// 3. 给需要事务的方法或类标注@Transactional
// 4. 开启基于注解的事务管理功能 @EnableTransactionManagement
// 5. 注册Spring平台的事务管理器来控制事务，重点他要管理数据源，才能管理每一条连接，才能管理事务
@EnableTransactionManagement
@Configuration
public class DbConfig {

    @Bean
    public DataSource dataSource(){
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setUsername("root");
        dataSource.setPassword("root");
        dataSource.setDriverClassName("com.mysql.jdbc.Driver");
        dataSource.setUrl("jdbc:mysql://localhost:3306/test");
        return dataSource;
    }

    // 可以放参数上，那样会自动注入Bean对象
    // 或调用方法，配置类中的方法调用会被认为是依赖注入
    @Bean
    public PlatformTransactionManager transactionManager(DataSource dataSource){
        return new DataSourceTransactionManager(dataSource());
    }
}
```









## 7. 监听器

用在方法上，当然这个类要加入进容器才行

```java
public class test {

    @EventListener(classes = {ApplicationEvent.class})
    public void evenListener(ApplicationEvent event){
        System.out.println("注解下的监听器");
    }
}
```







## 8. @ConfigurationProperties的使用

* 编写properties文件
* 编写properties类，类上加注解来宽松匹配
* 配置类上开启宽松匹配功能，并导入properties文件进环境

```properties
student.name=howl
student.age=20
```

```java
@ConfigurationProperties(prefix = "student")
// @Component
public class StudentProperties {

    private String name;
    private int age;

	// 利用setter方法注入
}
```

```java
@EnableConfigurationProperties
@PropertySource("classpath:application.properties")
@Configuration
public class MainConfig10 {

    @Bean
    public Student student(){
        return new Student();
    }
}
```



