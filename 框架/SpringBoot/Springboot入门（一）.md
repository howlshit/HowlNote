## 1 简介

Spring Boot是快速搭建Spring工程的脚手架，简化配置与依赖关系（约定大于配置），让我们把精力集中在业务部分





## 2  简单入门事例

创建一个Hello World的Web工程



### 2.1  创建Maven工程

直接Next -> 填写工程坐标 -> 创建

![1578834531197](C:\Users\Howl\AppData\Roaming\Typora\typora-user-images\1578834531197.png)



### 2.2 添加pom.xml依赖

父工程管理jar包，没有业务代码，子工程需要jar包时不用写版本号

```xml
<!--  父工程  -->
<parent>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-parent</artifactId>
    <version>2.1.5.RELEASE</version>
</parent>

<!--  设置JDK版本  -->
<properties>
    <java.version>1.8</java.version>
</properties>

<!--  添加依赖  -->
<dependencies>
    <!--  场景启动器  -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>
</dependencies>
```



### 2.3 创建主程序类Application

注意Application要放在根目录下，因为会去Application的同级目录去扫描，注意左边有层级结构

![1578835510327](C:\Users\Howl\AppData\Roaming\Typora\typora-user-images\1578835510327.png)

```java
@SpringBootApplication
public class Application {

    public static void main(String[] args) {

        //SpringBoot启动
        SpringApplication.run(Application.class,args);
    }
}
```




### 2.4 创建Controller

![1578835857855](C:\Users\Howl\AppData\Roaming\Typora\typora-user-images\1578835857855.png)

```java
@RestController
public class HelloController {

    //请求映射
    @RequestMapping(value = "hello",method = RequestMethod.GET)
    public String hello(){
        return "Hello Worlddddd";
    }
}
```




### 2.5 启动并测试 

启动SpringBoot应用

![1578836035885](C:\Users\Howl\AppData\Roaming\Typora\typora-user-images\1578836035885.png)

打开浏览器测试成功

![1578836100515](C:\Users\Howl\AppData\Roaming\Typora\typora-user-images\1578836100515.png)

**总结**

* 父工程管理各种依赖，Spring已经帮我们测试好各版本之间的关系了，所以添加依赖不用版本号
* 启动器帮我们导入各种场景所需要组件
* @SpringBootApplication标注说明这个类是主配置类，运行该类的main方法来启动SpringBoot应用







## 3. 自动配置

我们来看看@SpringBootApplication这个组合注释，底层还包括两个注解



* @SpringBootConfiguration：SpringBoot的配置类，标注在类上表示该类是个配置类

  * 它底层又有@Configuration：Spring的注解，放在配置类上

    * 配置类--配置文件：配置类也是容器中的一个组件：@Component

    

* @EnableAutoConfiguration：开启自动配置，以前需要配置的东西，SpringBoot帮我们自动配置

  * 它底层有@AutoConfigurationPackage：自动配置包

    * Import(AutoConfigurationPackages.Registrar.class)：Spring底层注解，给容器导入组件，即将主程序同级及下面子包所有组件扫描到Spring容器中
  * @Import(.AutoConfigurationImportSelector.class)：导入组件的选择器，会给容器导入非常多的自动配置类（xxxAutoConfiguration）,并配置好对应的组件



* 有了自动配置类就免去了手动编写配置注入组件了，其实现由SpringFactoriesLoader.loadFactoryNames(EnableAutoConfiguration.class,classLoader)完成



**总结：SpringBoot启动时从类路径下META_INF/spring.factories获取EnableAutoConfiguration指定的值，将这些值作为自动配置类导入到容器中，自动配置类就生效，帮我们进行自动配置工作**









### 4. 配置文件

* 名字是固定以application开头的

* 有两种方式application.properties和application.yml
* 用来修改SpringBoot默认配置



### 4.1 读取配置文件（这里以yml为事例）

* 首先要在resources下创建application.yml配置文件，然后在里面输入需要被读取的数据

```yml
jdbc:
  driverclassName: com.mysql.jdbc.Driver
  url: jdbc:mysql://localhost:3306/test?serverTimezone=UTC
  username: root
  password: howl
```

* 创建bean类，把配置的属性值写入bean对象中
* 需要用到@ConfigurationProperties()注解，该注解需要导入依赖
* 只有这个对象是容器中的组件，才能使用容器的@ConfigurationProperties功能

```java
/*
 * @ConfigurationProperties把配置文件的属性注入该类中
 */
@Component
@ConfigurationProperties(prefix = "jdbc")
public class JDBC {
    private String driverclassName;
    private String url;
    private String username;
    private String password;

    //各种Getters和Setters
}
```

* 依赖

```xml
<!--  注解解析器  -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-configuration-processor</artifactId>
    <optional>true</optional>
</dependency>
```

* 结构图

![1578843407694](C:\Users\Howl\AppData\Roaming\Typora\typora-user-images\1578843407694.png)


* 最后
再次启动Web应用，application.yml中以jdbc为前缀的属性会自动注入到JDBC这个对象组件中去



**常用注解**

```java
//将配置文件的属性注入到该对象中，只有这个注解是生效的，需要配合@EnableConfigurationProperties注解，或加入@Component注解
@ConfigurationProperties(prefix = "jdbc")

//属性注入
@value("${name}")

//使对应的配置文件生效加入到Bean容器
@EnableConfigurationProperties(JDBCProperties.class)

//声明一个类为配置类，代替xml文件，一般配合@Bean注解
@Configuration
//声明在方法上，将返回值加入Bean容器，代替<bean>标签
@Bean

//------------推荐使用上面来指定配置文件,在主目录下创一个config包-------------

//指定外部属性文件,这时还没生效,需要配合下面的注解
@PropertySource(value = {"classpath:other.properties"})
//加载多个配置文件,放在主程序类上
@ImportResourcec(locations = {"classpath:other.yml"})
```

**@ConfigurationProperties和@value区别**

|                | @ConfigurationProperties | @value   |
| -------------- | ------------------------ | -------- |
| 功能           | 批量注入配置             | 单个指定 |
| 松散绑定       | 支持                     | 不支持   |
| SpEL           | 不支持                   | 支持     |
| JSR303数据校验 | 支持                     | 不支持   |
| 复杂数据类型   | 支持                     | 不支持   |





### 4.2 Profile

* 在不同环境下需要使用不同的配置文件，这里就可以用Profile

* 名称规范 application-{profile}.yml / properties
* 默认是application.yml /properties里的配置



**我们在resources下建立两个配置文件**

* application.yml（用于部署配置）
* application-dev.yml（用于生产环境）

二者用上面的JDBC配置文件，不同于username，一个为root，一个为Howl



**激活测试**

```yml
# 在application.yml主配置文件中添加下面配置
spring:
  profiles:
    active: dev
```

Controller代码

```java
//请求映射
@RequestMapping(value = "hello",method = RequestMethod.GET)
public String hello(){
    return jdbc.getUsername();
}
```

未激活dev前，username为root

![1578922228570](C:\Users\Howl\AppData\Roaming\Typora\typora-user-images\1578922228570.png)

激活dev后为，username为Howl

![1578922204210](C:\Users\Howl\AppData\Roaming\Typora\typora-user-images\1578922204210.png)









