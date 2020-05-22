# 1. Spring Boot简介

* 采用约定大于配置，简化Spring开发步骤与复杂的部署流程

* 快速创立可独立运行的Spring项目以及集成主流框架

* 嵌入式Servlet容器，无需打war包

* starter自动依赖与版本控制

* 大量的自动配置，可修改默认值

* 需要xml，无代码生成，开箱即用

* 准生产环境的运行时应用监控

* 与其他框架天然集成

* 整合Spring技术栈的大框架













# 2. 入门解析



1.创建maven工程jar



2.导入父项目与依赖

```
<parent>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-parent</artifactId>
    <version>1.5.9.RELEASE</version>
</parent>

<dependencies>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>
</dependencies>
```



3.创建主程序

```java
// 标注主程序类，说明是Springboot应用
@SpringBootApplication
public class HelloWorld {

    public static void main(String[] args) {

        // 让Spring应用启动，需要传入主程序类，和其参数
        SpringApplication.run(HelloWorld.class,args);

    }
}
```



4.编写controller

```java
@RestController
public class HelloController {

    @RequestMapping(value = "/hello")
    public String hello(){
        return "hello world";
    }
}
```



5.测试

```java
来到主程序运行main方法
```



6.打包部署（记住名字不能有空格）

```java
<!--  将应用打包成可执行jar包的插件，package命令  -->
<build>
    <plugins>
        <plugin>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-maven-plugin</artifactId>
        </plugin>
    </plugins>
</build>
```



7.分析pom.xml

```xml
spring-boot-starter-parent：还有一个父项目spring-boot-dependencies，里面规定了依赖版本号：

<属性标签s>
<properties>
		<!-- Dependency versions -->
		<activemq.version>5.14.5</activemq.version>
		<antlr2.version>2.7.7</antlr2.version>
		<appengine-sdk.version>1.9.59</appengine-sdk.version>
		<artemis.version>1.5.5</artemis.version>
		<aspectj.version>1.8.13</aspectj.version>
		。。。。。
</properties>		
```



8.导入的依赖

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>
```

spring-boot-starter-web：springBoot的场景启动器，里面很多依赖如：spring-web、spring-webmvc、jackson、hibernate-validator、spring-boot-starter-tomcat、spring-boot-starter

场景启动器：将功能场景抽取出来，做成starters启动器，只要项目中导入对应的启动器，那么相关场景的依赖就会自动导入



9.主程序类，入口类

```java
@SpringBootApplication
public class HelloWorld {

    public static void main(String[] args) {

        // 让Spring应用启动，需要传入主程序类，和其参数
        SpringApplication.run(HelloWorld.class,args);
    }
}
```

@SpringBootApplication：说明是Springboot的主配置类，那么就会运行main方法来启动应用



10.@SpringBootApplication的内部注解

```java
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited

@SpringBootConfiguration
@EnableAutoConfiguration
@ComponentScan(
    excludeFilters = {@Filter(
    type = FilterType.CUSTOM,
    classes = {TypeExcludeFilter.class}
), @Filter(
    type = FilterType.CUSTOM,
    classes = {AutoConfigurationExcludeFilter.class}
)}
)
```

@SpringBootConfiguration：表示配置类，配置类也是容器的一个组件即@Component，（内部是用Spring的@Configuration）



11.@EnableAutoConfiguration：开启自动配置功能，其内部又有：

```
@AutoConfigurationPackage
@Import({EnableAutoConfigurationImportSelector.class})
```



12.@AutoConfigurationPackage：**将主配置类@SpringBootApplication标注的类及同级下面所有子包所有子包组件扫描**

内部是@Import({Registrar.class})，spring的导入组件注解，Registrar.class内部有个方法

```
// 注册bean的定义信息，即导组件
// metadata注解标注的原信息
public void registerBeanDefinitions(AnnotationMetadata metadata,
BeanDefinitionRegistry registry) {
            AutoConfigurationPackages.register
            (registry,
            // 这里可以获取主配置类的上级包名
            (new AutoConfigurationPackages.PackageImport(metadata)).getPackageName());
}
```



13.@Import({EnableAutoConfigurationImportSelector.class})：

导入组件的选择器EnableAutoConfigurationImportSelector.class，内容是：

```
// 内部继承父类，父类中有个方法
// 将所需导入的组件以全类名的方式返回，组件会被导入容器中
// 方法内部会导入非常多的自动配置类xxxAutoConfiguration，就是导入场景所需的全部组件，并配置好
public String[] selectImports(AnnotationMetadata annotationMetadata) {
    XXXX
}
```

有了自动配置类，就免去我们手动配置的麻烦，**SpringBoot启动时，从类路径下spring-boot-autofigure包中的META-INF/spring.factories中获取EnableAutoConfiguration的值（各种配置类的全限定类名），作为自动配置类导入容器中，那么自动配置类生效帮我们自动配置，其真正配置类也在这个包下**













# 3. 快速创建Spring应用（创建向导）

Spring  Initializer

* 主程序生成好了
* resources中包括
  * static保存所有的静态资源
  * templates保存模板页面
  * application.properties：springboot的配置文件，这里可修改默认的













# 4.  配置文件（名字固定）

* application.properties（默认使用，优先级高）
* application.yml



### 4.1 yml格式

```yml
server:
  port: 8080
	
	
	
"" : 特殊字符不会转义
'' : 特殊字符会转义


对象、Map：
friends:
  lastName: howl
  age: 20
  
friends: {lastName: howl,age: 20}


数组：
pets:
  - cat
  - dog
  
pets: [dog,cat,pig]
```



### 4.2 配置文件yml获取组件的值

```java
// 组件必须的
@Component
@ConfigurationProperties(prefix = "person") // 默认从主配置文件获取
public class Person{
    private int age;
    xxxxxx
}
```

```
person:
  age: 20
  name: Howl
```

```xml
<!--    导入配置文件处理器，配置文件yml绑定属性时会有提示    -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-configuration-processor</artifactId>
    <optional>true</optional>
</dependency>
```



properties也一样

```
乱码：idea使用utf-8,要在编译器里面设置，编译转换ascii码

person.age=20
person.name=哈哈哈
```



### 4.3 从配置文件中获取值

@Value(Spring注解)



### 4.4 读取外部文件与xml配置文件

```java
@PropertySource(value = "classpath:person.yml")
@ImportResources(locations = {"classpath:bean.xml"}) //读取是spring的<bean>标签的文件，放在主程序入口处
```



### 4.5 注解实现配置类

```java
@Configuration
public class Myconfig {

    @Bean
    public HelloWorld helloService(){
        return new HelloWorld();
    }
}
```



### 4.6 配置文件随机数与占位符

```
${random.value}
person.age=20
person.name=${person.age:默认值}_Howl
```





### 4.7 多环境支持



#### 4.7.1 多profile文件

```xml
主配置文件编写时，文件规定可以是 application-{profile}.properties/yml

eg：配置文件命名为:
application-dev.properties
application-prod.properties

主配置文件中加入：
spring.profiles.active=dev激活dev的配置文件
```



### 4.7.2 yml文档块

```yml
server:
  port: 8081
spring:
  profiles:
    active: dev
---

server:
  port: 8082
spring:
  profiles: dev
---
server:
  port: 8083
spring:
  profiles:prod
```



### 4.7.3 配置文件加载位置

```xml
application.properties
application.yml

优先级由高到低，优先采用高的，但也会互补
/config
/
classpath:/config
classpath:/
```













# 5. 自动配置原理

1）SpringBoot启动的时候加载主配置类，开启了自动配置功能：@EnableAutoConfiguration

2）@EnableAutoConfiguration 作用：（去aotuconfigure包的META-INF中获取全限定类名，再将对应的类加载进来）

- 利用@Import的 AutoConfigurationImportSelector.class选择器给容器导入一些组件
- 这个选择器中的selectImports()方法负责加载



3）每一个加载的自动配置类进行自动配置功能

4）举例一个配置类的内部的注解（根据不用条件判断配置类是否生效）

```java
@Configuration
4.1 @EnableConfigurationProperties({CacheProperties.class})  // .class是个properties映射成bean对象（但头部没有加@Component注解），要使其生效即要加入bean容器
@ConditionalOnClass({CacheManager.class}) // 内部是Spring注解@Conditional，满足条件配置类生效
其还有有参构造器，将对应的XXXproerties注入到内部变量中：
```

4.1）所有在配置i文件能配置的属性都是在XXXProperties类中封装着，配置文件能配置什么，就看对应的XXXProperties

5）所以我们能在主配置文件中配置什么，完全是是看xxxproperties的



### Debug模式

```
# 主配置文件中
# 开启SpringBoot的debug模式，会显示启用的自动配置类
debug=true
```













# 6. 日志框架

| 日志抽象层（类似于JDBC）                          | 日志实现                                       |
| ------------------------------------------------- | ---------------------------------------------- |
| SLF4j、~~jbossing-logging~~、JCL(Commons-logging) | JUL(Java.util.logging)、Log4j、Log4j2、Logback |

Spring框架默认JCL（导包common-logging）

SpringBoot选用SLF4j和logback

应该调用日志抽象层



### 使用

导入SLF4j抽象层和Logback实现类

```java
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HelloWorld {
  public static void main(String[] args) {
    Logger logger = LoggerFactory.getLogger(HelloWorld.class);
    logger.info("Hello World");
  }
}
```

```java
Logger logger = LoggerFactory.getLogger(this.getClass());

logger.trace("这时trace日志");
logger.debug("这时debug日志");
//日志级别
logger.info("这时info日志");
logger.warn("这时warn日志");
logger.error("这时error日志");
```



查看默认配置

```
去springboot第一个包中查看properties文件，
去去springboot-autoConfiguration看配置类
```



高级特性

```xml
自己写的logback.xml中可以加
<springProfile name="dev">

</springProfile name="dev">
表示只在某个环境生效
```













# 7. Web开发

eg:数据库jdbc开发：去Springboot- autoConfigtuation包下看DataSourceAutoConfiguration，头上有注解@EnableConfigurationProperties({DataSourceProperties.class})开启properties映射对象生效，DataSourceProperties是个映射properties的bean类



我们需要熟悉自动配置原理，然后才可以很好地书写配置文件



### 静态资源的映射规则

在webMvcAutoConfiguration

```java
public void addResourceHandlers(ResourceHandlerRegistry registry) {
    if (!this.resourceProperties.isAddMappings()) {
        logger.debug("Default resource handling disabled");
    } else {
        Duration cachePeriod = this.resourceProperties.getCache().getPeriod();
        CacheControl cacheControl = this.resourceProperties.getCache().getCachecontrol().toHttpCacheControl();
        if (!registry.hasMappingForPattern("/webjars/**")) {
        
        // Webjars/**下的资源请求都去 classpath:/META-INF/resources/webjars/ 找资源
        
            this.customizeResourceHandlerRegistration(registry.addResourceHandler(new String[]{"/webjars/**"}).addResourceLocations(new String[]{"classpath:/META-INF/resources/webjars/"}).setCachePeriod(this.getSeconds(cachePeriod)).setCacheControl(cacheControl));
        }

        String staticPathPattern = this.mvcProperties.getStaticPathPattern();
        if (!registry.hasMappingForPattern(staticPathPattern)) {
            this.customizeResourceHandlerRegistration(registry.addResourceHandler(new String[]{staticPathPattern}).addResourceLocations(WebMvcAutoConfiguration.getResourceLocations(this.resourceProperties.getStaticLocations())).setCachePeriod(this.getSeconds(cachePeriod)).setCacheControl(cacheControl));
        }

    }
}
```

webjars：以jar包方式引入静态资源（juery、BootStrap等打包成jar，用maven导入）

访问Webjars/**下的资源请求都去 classpath:/META-INF/resources/webjars/ 找资源，

导包后直接写名字访问就行eg: /webjars/jquery



1）addResourceHandlers



### java哪些编写代码的文件和资源文件夹下的文件编译后都放在target的classes下，classes才是类路径。根路径是个特例，不在资源文件夹下，但编译后放在classes内



2)  /** 默认去classpath找；Springboot的resourcers是默认的classpath

```xml
"classpath:/META-INF/resources/",
"classpath:/resources/",
"classpath:/static/",
"classpath:/public/"
"/": 当前项目的根路径，
```



3）欢迎页配置

欢迎页；静态资源文件夹下的所有Index

```java
@Bean
public WelcomePageHandlerMapping welcomePageHandlerMapping(ApplicationContext applicationContext, FormattingConversionService mvcConversionService, ResourceUrlProvider mvcResourceUrlProvider) {
    WelcomePageHandlerMapping welcomePageHandlerMapping = new WelcomePageHandlerMapping(new TemplateAvailabilityProviders(applicationContext), applicationContext, this.getWelcomePage(), this.mvcProperties.getStaticPathPattern());
    welcomePageHandlerMapping.setInterceptors(this.getInterceptors(mvcConversionService, mvcResourceUrlProvider));
    return welcomePageHandlerMapping;
}


// 方法引用在静态资源下找index.html，那个方法引用不用看了
return Arrays.stream(locations).map(this::getIndexHtml).filter(this::isReadable).findFirst();
```

所以： localhost:8080/  默认找资源类路径下的 index.html文件



图标也一样；静态资源下找 /favicon.ico













# 8. 模板引擎thymeleaf

1)加入依赖

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-thymeleaf</artifactId>
</dependency>
```

资源放在classpath:/templates/下，就自动渲染html



2)导入thymeleaf的名称空间、有语法提示

```html
<html lang="en" xmlns:th="http://www.thymeleaf.org">
```



3)语法

```html
th:text
th:each   这个标签每次遍历都会生成
${} 获取值
*{}
```













# 9. SpringMVC 自动配置

* 配置了ViewResolver视图解析器
* 配置了webjars解析
* support 静态资源文件路径、webjars
* support 静态首页访问 index.html
* support favicon.ico
* 自动注册了Converter、Formatter的beans
* Support th:each













# 10. 修改SpringBoot的默认配置

模式：

1）自动配置组件时（组合使用，互补），先看容器中有没有用户自己配置的@Component，@Bean，如果有就用用户配置，如果没有才自己创建自动配置

```java
@ConditionalOnMissingBean({FormContentFilter.class})
public OrderedFormContentFilter formContentFilter() {
return new OrderedFormContentFilter();
}
```



2)扩展SpringMVC（eg:Interceptors、formatters）

编写一个配置类加上注解@Configuration，实现WebMvcConfigurer接口要重写的配置即可（接口有方法体了），这里不能加@EnableWebMvc（加了就是自己的生效，自动配置失效）



**原理**

```java
在做其他自动配置时回导入 EnableWebMvcConfiguration.class,其父类是重点

public static class EnableWebMvcConfiguration extends DelegatingWebMvcConfiguration implements ResourceLoaderAware {
}



public class DelegatingWebMvcConfiguration extends WebMvcConfigurationSupport {
    private final WebMvcConfigurerComposite configurers = new WebMvcConfigurerComposite();

	// 从容器中获取所有的WebMvcConfigurer就是自己写的全托管配置类，然后把其赋值内部的configurers
    @Autowired(
        required = false
    )
    public void setConfigurers(List<WebMvcConfigurer> configurers) {
        if (!CollectionUtils.isEmpty(configurers)) {
            this.configurers.addWebMvcConfigurers(configurers);
        }
    }
    
    // 举例刚才的视图解析器，这方法添加视图映射
    protected void addViewControllers(ViewControllerRegistry registry) {
        this.configurers.addViewControllers(registry);
    }
    
    // 进去configurers.addViewControllers(registry);
    // delegates内部遍历，注入类WebMvcConfigurer
    // 把自己配的WebMvcConfigurer遍历里面的viewController全调用起作用
    // 这里遍历就包括了自己覆盖的，已经没有覆盖的
    public void addViewControllers(ViewControllerRegistry registry) {
    	Iterator var2 = this.delegates.iterator();

        while(var2.hasNext()) {
            WebMvcConfigurer delegate = (WebMvcConfigurer)var2.next();
            delegate.addViewControllers(registry);
        }
    }
}
```



3）全面接管@EnableWebMvc，所有都是我们自己配

原理就是@EnableWebMvc注解里面导入了WebMvcConfiguration.class类

```java
而我们的总的Mvc自动配置类上要先判断，否则不生效
ConditionOnMissingBean(WebMvcConfiguration.class)
```



4)SpringBoot中会有很多很多的xxxCustomizer帮助我们进行定制配置













# 11. 拦截器实现登录拦截

实现 HandlerInterceptor 接口，并在自己的配置类中注册

```java
public class LoginHandlerInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
    
        HttpSession session = request.getSession();
        Object user = session.getAttribute("user");
        if(user == null){
            request.getRequestDispatcher("/").forward(request,response);
            return false;
        }
        return true;
    }
}
```

```java
@Configuration
public class MyMvcConfig implements WebMvcConfigurer {
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 已经boot做好了静态资源放行
        registry.addInterceptor(new LoginHandlerInterceptor())
                .addPathPatterns("/**")
                .excludePathPatterns("/","/user/login");

    }

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        // 浏览器发送/howl请求，也来到success页面
        registry.addViewController("/howl").setViewName("success");
    }
}
```













# 12. Restful

|      | 普通         | Restful              |
| ---- | ------------ | -------------------- |
| 查询 | /user/get    | /user  --GET         |
| 添加 | /user/create | /user  --POST        |
| 修改 | /user/update | /user/{id}  --PUT    |
| 删除 | /user/delete | /user/{id}  --DELETE |



实际操作

URI: /资源名称/资源表示    HTTP请求方式区分对资源的CURD

|              | 请求url    | 请求方式 |
| ------------ | ---------- | -------- |
| 查询所有用户 | /users     | GET      |
| 查询某个用户 | /user/{id} | GET      |
| 添加用户     | /user      | POST     |
| 修改用户     | /user      | PUT      |
| 删除用户     | /user{id}  | DELETE   |

```java
// 查询所有
@GetMapping(value = "/users")
public String list(){
    return employeeDao.getAll().toString();
}

// 查询单个
@GetMapping(value = "/user/{id}")
public String listById(@PathVariable(value = "id") Integer id){
    return id + " 单个用户查询";
}

// 添加
@PostMapping(value = "/user")
public String addUser(){

    return "这里是添加员工";
}

@PutMapping(value = "/user")
public String updateUser(){
    return "这里是put更新用户";
}

@DeleteMapping(value = "/user/{id}")
public String deleteUser(@PathVariable(value = "id") Integer id){
    return "这里是删除用户" + id;
}
```













# 13. 错误处理



1）错误页面

错误处理的自动配置：ErrorMvcAutoConfiguration

给容器添加了

DefaultErrorAttributes、

BasicErrorController：处理默认的/error请求

ErrorPageCustomizer：系统出现错误来到error请求进行处理

步骤：一旦系统出现4xx或5xx的错误，ErrorPageCustomizer会生效（定制错误的相应规则）



2）定制错误的json数据

```java
@RestControllerAdvice
public class MyEceptionHandler {

    @ExceptionHandler(UserException.class)
    public String HandleException(Exception e){
        return e.getMessage();
    }
}
```













# 14. 嵌入式Servlet容器

SpringBoot默认使用的是嵌入式的Servlet容器（Tomcat）



1)定制和修改Servlet容器的相关配置

```properties
server.port=8081
server.servlet.context-path=/curd

server.tomcat.uri-encoding=UTF-8

# 通用的Servlet容器设置
server.xxx
server.tomcat.xxx
```



### 编写一个**WebServerFactoryCustomizer**：嵌入式的Servlet容器的定制器，来修改Servlet容器的配置

```java
@Bean
public WebServerFactoryCustomizer<ConfigurableWebServerFactory> webServerFactoryCustomizer() {
     
     // 其实就是返回本类的实现类给他
	return new WebServerFactoryCustomizer<ConfigurableWebServerFactory>() {
	
        @Override
        // 传参是一个容器
        public void customize(ConfigurableWebServerFactory factory) {
            //设置tomcat的端口号
            factory.setPort(9091);
        }
     };
}
```





## 重点

2）注册Servlet、Filter、Listener

ServletRegistrationBean

FilterRegistrationBean

SerlvetListenerRegistrationBean



Springboot默认是以jar包方式启动嵌入式的Servlet容器来启动应用，没有web.xml文件



编写Servlet

```java
public class MySerlvet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doPost(req,resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.getWriter().write("Hello World");
    }
}
```

使用官方提供的注册

```java
package com.howl.springboot.config;

@Configuration
public class MyServerConfig {

    @Bean
    public ServletRegistrationBean myServlet(){
        return new ServletRegistrationBean(new MySerlvet(),"/myServlet");
    }

    @Bean
    public FilterRegistrationBean myFilter(){
        FilterRegistrationBean filterRegistrationBean = new FilterRegistrationBean();
        filterRegistrationBean.setFilter(new MyFileter());
        filterRegistrationBean.setUrlPatterns(Arrays.asList("/myServlet"));
        return filterRegistrationBean;
    }

    @Bean
    public ServletListenerRegistrationBean myListener(){
        return  new ServletListenerRegistrationBean<MyListener>(new MyListener());
    }
}
```





## 重点：使用其他的嵌入式Servlet容器（上面11点有提及怎么配置）

Jetty（长连接），聊天

Undertow(不支持JSP)，非阻塞的，并发性能好



在pom.xml中，spring-boot-starter-web中默认加了spring-boot-starter-tomcat，所以我们要去除他，在加上spring-boot-starter-jetty

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
    <exclusions>
        <exclusion>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-tomcat</artifactId>
        </exclusion>
    </exclusions>
</dependency>

<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-jetty</artifactId>
</dependency>
```



#### 1) 嵌入式Servlet容器自动配置原理

autoConfigtuar.web.embedded下有个EmbeddedWebServerFactoryCustomizerAutoConfiguration（嵌入式容器工厂的定制器自动配置），其内部是有各种@ConditionalOnClass({Tomcat.class, UpgradeProtocol.class})条件判断，看容器中有哪个容器类类型才生效哪个配置返回一个TomcatWebServerFactoryCustomizer（Tomcat容器的定制器，传入ServerProperties.class）

注意：EmbeddedWebServerFactoryCustomizerAutoConfiguration（嵌入式容器工厂的定制器自动配置）

有@EnableConfigurationProperties({ServerProperties.class})注解，即与映射的配置文件绑定，即主配置文件中可修改



自动配置类中以tomcat为例

```java
@Configuration
@ConditionalOnClass({Tomcat.class, UpgradeProtocol.class})
public static class TomcatWebServerFactoryCustomizerConfiguration {
	
	public TomcatWebServerFactoryCustomizerConfiguration() {}

	@Bean
	public TomcatWebServerFactoryCustomizer tomcatWebServerFactoryCustomizer(Environment environment, ServerProperties serverProperties) {
	
			// 将定制器返回，下面进入定制器
            return new TomcatWebServerFactoryCustomizer(environment, serverProperties);
        }
    }
```



#### 2）嵌入式容器启动原理

```java
@SpringBootApplication
public class SpringbootWebApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringbootWebApplication.class, args);
    }

}

SpringApplication类run方法中
先： context = this.createApplicationContext();创建IOC容器（有其他类型，举例WebApplication应用就创建WebApplication容器，下面有说明）
再this.refreshContext(context)刷新容器(这里就创建嵌入式容器，包括各种bean对象)
```



**refreshContext**一直往里走有这个方法，给子容器来实现

```java
this.onRefresh();

// 而这个方法是给子容器实现的
protected void onRefresh() throws BeansException {
}
```

**子容器重写该方法：**就会创建嵌入式的Servlet容器

子容器的抽象类下面的实现类，Tomcat是使用SerlvetWebServerApplicaitonContext



总得来说：SpringBoot启动时，根据主程序入口的类类型来创建相应的容器，然后刷新容器refresh()（创建各种bean对象），此时也是创建嵌入式容器的。相关容器的子类实现类中，onRefresh方法实现了（方法中调用this.createWebServer()来创建并返回ServletWebServerFactory，再根据工厂来获取嵌入式容器）

```
ServletWebServerFactory factory = this.getWebServerFactory(); //内部其实就是从IOC容器中获取这个组件
```



IOC容器启动的时候会创建嵌入式容器



**深入看看创建容器（createApplicationContext），简化**

```java
protected ConfigurableApplicationContext createApplicationContext() {
    
    Class<?> contextClass = this.applicationContextClass;
    
    switch (this.webApplicationType) {
	case SERVLET:
			contextClass = Class.forName("org.springframework.boot.web.servlet.context.AnnotationConfigServletWebServerApplicationContext");
		break;
		
	case REACTIVE:
             contextClass = Class.forName("org.springframework.boot.web.reactive.context.AnnotationConfigReactiveWebServerApplicationContext");
         break;
                
    default:
            contextClass = Class.forName("org.springframework.context.annotation.AnnotationConfigApplicationContext");
            }
        } 
    return (ConfigurableApplicationContext) BeanUtils.instantiateClass(contextClass);
}
```













# 15. 使用外置的Servlet容器

嵌入式简单便捷、优化定制比较复杂，使用定制器或properties等来改变



外置的Serlvlet容器：外面安装Tomcat服务器--应用打包war包打包

1. 必须创建war包
2. 将嵌入式Tomcat指定为provided
3. 必须编写一个ServletInitializer，并调用configure方法
4. 启动服务器



#### 外置Servlet容器的启动原理，

jar包：执行Spring主类的main方法，启动IOC容器，过程中创建Servlet容器

war包：启动服务器，服务器启动Springboot应用（ServletInitializer），接着上面的流程



servlet3.0中有个规范：

规则：

1. 服务器启动会创建当前web应用里面每一个jar包里面的ServlerConttaininerInitializer实例
2. ServlerConttaininerInitializer的实现放在jar包的META-INF/services文件夹下，有个文件内容是指向ServlerConttaininerInitializer的实现类的全限定类名
3. 使用注解@handlesTypes，在应用启动的时候加载我们感兴趣的类



#### 流程

1）启动Tomcat

2）按照规则就会去创建jar下的实例，规则1

3）ServlerConttaininerInitializer将注解标注的类创建实例

4）每一个SpringBootServletInitializer就是多出来的ServletInitializer类的父类被创建，然后调用configure方法，其参数是主程序类，内部使用builder创建Spring应用最后run启动了













# 16. SpringBoot与数据访问

导入依赖

整合基本JDBC，在主配置文件中加入即可，默认使用class com.zaxxer.hikari.HikariDataSource数据源

```properties
spring.datasource.username=root
spring.datasource.password=
spring.datasource.url=jdbc:mysql://localhost:3306/test
spring.datasource.driver-class-name=com.mysql.jdbc.Driver
```

自动配置包下jdbc.DataSourceConfiguration中，默认支持：dbcp2、hikari、tomcat。还可以看到一个属性：spring.datasource.type













# 17. 整合druid数据源 



引入依赖

```xml
<!-- https://mvnrepository.com/artifact/com.alibaba/druid -->
<dependency>
    <groupId>com.alibaba</groupId>
    <artifactId>druid</artifactId>
    <version>1.1.21</version>
</dependency>

<!-- 德鲁伊依赖log4j我有什么办法 -->
<dependency>
    <groupId>log4j</groupId>
    <artifactId>log4j</artifactId>
    <version>1.2.17</version>
</dependency>
```



配置数据源具体数值

```yml
spring:
  datasource:
    username: root
    password:
    url: jdbc:mysql://127.0.0.1:3306/spring?useUnicode=true&characterEncoding=UTF-8&serverTimezone=Asia/Shanghai&useAffectedRows=true
    driver-class-name: com.mysql.cj.jdbc.Driver
    type: com.alibaba.druid.pool.DruidDataSource

    maxActive: 20
    initialSize: 1
    maxWait: 60000
    minIdle: 1
    timeBetweenEvictionRunsMillis: 60000
    minEvictableIdleTimeMillis: 30000
    testWhileIdle: true
    testOnBorrow: false
    testOnReturn: false
    poolPreparedStatements: true
    maxOpenPreparedStatements: 20

    filters: stat,wall,log4j
```



因为自己加的druid是第三方数据源，所以要自己配置上去

```java
@Configuration
public class DruidConfig {

    @Bean
    @ConfigurationProperties(prefix = "spring.datasource")
    public DataSource druid() {
        return new DruidDataSource();
    }

    // 配置监控
    // 1.配置管理后台的Servlet
    // 2.配置一个监控的filter
    @Bean
    public ServletRegistrationBean statViewServlet(){
        ServletRegistrationBean bean = new ServletRegistrationBean<>(new StatViewServlet(), "/druid/*");
        Map<String,String> map = new HashMap<>();
        map.put("loginUsername", "root");
        map.put("loginPassword", "root");
        map.put("allwo","");

        bean.setInitParameters(map);
        return bean;
    }

    @Bean
    public FilterRegistrationBean WebStatFilter(){
        FilterRegistrationBean<Filter> bean = new FilterRegistrationBean<>(new WebStatFilter());
        Map<String,String> map = new HashMap<>();
        map.put("exclusions","*.js,*.css,/druid/*");
        bean.setUrlPatterns(Arrays.asList("/*"));
        bean.setInitParameters(map);
        return bean;
    }
}
```













# 18. 整合Mybatis

向导开发，自动导入依赖

```xml
<dependency>
    <groupId>org.mybatis.spring.boot</groupId>
    <artifactId>mybatis-spring-boot-starter</artifactId>
    <version>2.1.2</version>
</dependency>
```



搞定数据源Druid，省略



### 注解版

```java
@Mapper
public interface DepartmentMapper {

    @Select("SELECT * FROM department WHERE id=#{id}")
    public Department getDeptById(Integer id);

    @Delete("DELETE FROM department WHERE id=#{id}")
    public int deleteById(Integer id);

    @Options(useGeneratedKeys = true,keyProperty = "id")
    @Insert("INSERT INTO department (`departmentName`) VALUES (#{departmentName})")
    public int insertDept(Department department);

    public int updateDept(Department department);
}
```

```java
@RestController
public class DeptController {

    @Autowired
    DepartmentMapper departmentMapper;

    @GetMapping("/dept/{id}")
    public Department getDeptment(@PathVariable("id") Integer id){
        return departmentMapper.getDeptById(id);
    }

    @GetMapping("/dept")
    public Department insertDept(Department department){
        departmentMapper.insertDept(department);
        return department;
    }
}
```

主程序入口

```
@MapperScan(value = "com.howl.springboot.mapper")
```



### 重点配置文件(要在主配置文件中配置)

```
mybatis:
  config-location: classpath:mybatis/mybatis-config.xml
  mapper-locations: classpath:mybatis/mapper/*
```













# 19. 最后一点SpringBoot启动原理

1. 创建SpringApplication对象

```
保存主配置类
判断是否web应用
从类路径下找applicationContextInitializer然后保存起来
从类路径下找applicationContextListener然后保存起来
从多个配置类中找到main方法的主配置类
```



1. 运行run方法

```
获取SpringapplicationRunListeners：从类路径下META-INF下spring.factories
回调所有的获取获取SpringapplicationRunListeners.starting()方法
封装命令行参数
准备环境：IOC环境，eg:profile
	创建环境后回调SpringapplicationRunListeners.environmentprepared():表示环境准备完成
创建IOC容器
准备上下文：
	environment保存到IOC中
	而且要执行applyInitalizers()方法（上面创建应用的时候就拿到了所有的Initializer），回调里面全部方法
	回调所有的Listeners的contextPrepared();
	回调所有的Listeners的contextLoaded（）方法
	刷新容器：IOC容器初始化，加载组件（配置类、@bean）,还有嵌入式容器
	从IOC获取所有的ApplicationRunner和CommandRunner
```













# 20. 定时任务

使用SpringBoot注解来开启定时任务十分简便



#### 1. 在启动类上加注解

```java
@SpringBootApplication
@EnableScheduling  // 开启定时任务
public class SpringBootApplication {
    public static void main(String[] args) {
        SpringApplication.run(SpringBootApplication.class, args);
    }
}
```



#### 2. 建立定时类

```java
@Component // 加入组件
public class TestSchedule {

    // 表示定时任务，cron表达式：每10秒执行
    @Scheduled(cron = "*/10 * * * * ?")
    public void test1(){

        System.out.println("task one");
    }

    // 表示定时任务，延迟2秒执行
    @Scheduled(fixedDelay = 2000)
    public void test2(){
        System.out.println("task two");
    }
}
```

















