## 1. 测试方法

* 添加启动器依赖

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-test</artifactId>
</dependency>
```

* 添加测试环境

```java
@RunWith(SpringRunner.class)
@SpringBootTest
```







## 2. 日志框架

默认使用slf4门面和logback实现，但也支持log4j和jul，从下面的依赖图可以看出

![1578926268129](C:\Users\Howl\AppData\Roaming\Typora\typora-user-images\1578926268129.png)



修改配置（各种配置去依赖包下找）

```yml
logging:
  path: C:\Users\Howl\Desktop
  level:
    root: info
  pattern:
    console: -%d{yyyy/MM/dd-HH:mm:ss} [%thread] %-5level %logger- %msg%n
  file:
    max-size: 10MB
```





## 3. 静态资源映射规则

* WebJars：以jar包方式引入静态资源，所有/webjars/** 都去classpath：/META_INF/resources/webjar/下找资源（主要是官网包）

```xml
去WebJars查找对应的依赖
```



* /** 只要没人处理，就访问当前项目的任何资源,

```java
classpath：/META_INF/resources/
classpath：/resources/
classpath：/static/
classpath：/public/
//上面三个在工程目录下是/resources/resources
```











拦截器和过滤器，其中有个排除选项







### 整合SpringMVC拦截器，不是过滤器

编写拦截器

编写配置类实现WebConfigurer，在该类中添加各种组件



blog里面有

























## 4. 错误处理机制

blog里面有

















### 整合数据库连接池Druid

添加依赖com.alibaba，druid-spring-boot-starter测试1.1.10才成功

```yml
# 数据库连接池，自带hikari
spring:
  datasource:
    type: com.alibaba.druid.pool.DruidDataSource
    driver-class-name: com.mysql.jdbc.Driver
    url: jdbc:mysql://localhost:3306/test?serverTimezone=UTC
    username: root
    password:
    druid:
      initial-size: 15
      min-idle: 10
      max-active: 30
      pool-prepared-statements: true
```





### 整合mybatis（添加依赖去maven找对应的不然一直报错）

添加启动器依赖，连接依赖需要5.1.28以上

配置mybatis：实体类别名包，映射文件等

编写Dao下的mapper文件，采用注解方式

在启动器上配置MapperScan

测试





