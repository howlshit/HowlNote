> 整合SSM的过程可以很好的理解三者关系，顺便复习如何使用他们，加深印象





# 1. 整合理论

我们整合SSM框架，其实就是用Spring去整理其余二者，主要以Spring为主。其工作流程是Web层调用Service层，而Service层调用Dao层，那么我们就从底层的Dao层开始整合，以TDD推动开发，那么下面给出整理好后的结构图，方便后面参考（使用的是IEDA）



![1583397530103](C:\Users\Howl\AppData\Roaming\Typora\typora-user-images\1583397530103.png)













# 2. 搭建基础环境

创建一个maven的Web工程，pom.xml如下（内容中已注释其对应的应用范围）：



### 2.1 pom.xml

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <groupId>com.howl</groupId>
    <artifactId>SpringPractice</artifactId>
    <version>1.0-SNAPSHOT</version>
    <packaging>war</packaging>

    <dependencies>

        <!--    SpringMVC相关    -->
        <dependency>
            <groupId>org.springframework</groupId>
            <artifactId>spring-web</artifactId>
            <version>5.0.2.RELEASE</version>
        </dependency>
        <dependency>
            <groupId>org.springframework</groupId>
            <artifactId>spring-webmvc</artifactId>
            <version>5.0.2.RELEASE</version>
        </dependency>
        <!--    SpringMVC相关    -->


        <!--    Spring相关    -->
        <dependency>
            <groupId>org.springframework</groupId>
            <artifactId>spring-tx</artifactId>
            <version>5.0.2.RELEASE</version>
        </dependency>
        <dependency>
            <groupId>org.springframework</groupId>
            <artifactId>spring-context</artifactId>
            <version>5.0.2.RELEASE</version>
        </dependency>
        <dependency>
            <groupId>org.springframework</groupId>
            <artifactId>spring-aop</artifactId>
            <version>5.0.2.RELEASE</version>
        </dependency>
        <dependency>
            <groupId>org.aspectj</groupId>
            <artifactId>aspectjweaver</artifactId>
            <version>1.8.7</version>
        </dependency>
        <!--    Spring相关    -->


        <!--    单元测试    -->
        <dependency>
            <groupId>org.springframework</groupId>
            <artifactId>spring-test</artifactId>
            <version>5.0.2.RELEASE</version>
        </dependency>
        <dependency>
            <groupId>junit</groupId>
            <artifactId>junit</artifactId>
            <version>4.12</version>
        </dependency>
        <!--    单元测试    -->


        <!--    数据库相关，差个连接池    -->
        <dependency>
            <groupId>org.springframework</groupId>
            <artifactId>spring-jdbc</artifactId>
            <version>5.0.2.RELEASE</version>
        </dependency>
        <dependency>
            <groupId>mysql</groupId>
            <artifactId>mysql-connector-java</artifactId>
            <version>5.1.6</version>
        </dependency>
        <dependency>
            <groupId>org.mybatis</groupId>
            <artifactId>mybatis</artifactId>
            <version>3.5.1</version>
        </dependency>
        <dependency>
            <groupId>org.mybatis</groupId>
            <artifactId>mybatis-spring</artifactId>
            <version>2.0.1</version>
        </dependency>
        <!--    数据库相关，差个连接池    -->


        <!--    bean转json    -->
        <dependency>
            <groupId>com.alibaba</groupId>
            <artifactId>fastjson</artifactId>
            <version>1.2.62</version>
        </dependency>
        <!--    bean转json    -->

    </dependencies>

</project>
```



### 2.2 建表

```mysql
CREATE TABLE `user` (
  `user_id` int(11) NOT NULL AUTO_INCREMENT,
  `user_name` varchar(255) DEFAULT NULL,
  `user_email` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`user_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=16 DEFAULT CHARSET=utf8;
```













# 3. Dao层

Dao对应的是Mybatis，简单使用可以阅读笔者写的 [Mybatis入门](<https://www.cnblogs.com/Howlet/p/11973432.html>)，那么就开始把



### 3.1 entity

建立与数据库表对应的实体类，之后需要使用FastJson转成字符串，需要用到getter/setter



```java
public class User {

    private int id;
    private String name;
    private String email;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public User() {
    }

    public User(int id, String name, String email) {
        this.id = id;
        this.name = name;
        this.email = email;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}
```



### 3.2 Dao层接口

使用Mybatis的动态代理，这里就只演示两个方法

```java
public interface UserDao {

    // 查
    public User selectUserById(int id);

    // 增
    public int createUser(User user);
}
```



### 3.3 接口对应的映射文件

```xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE mapper
        PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN"
        "http://mybatis.org/dtd/mybatis-3-mapper.dtd">


<mapper namespace="com.howl.dao.UserDao">

    <resultMap id="userMap" type="com.howl.entity.User">
        <id property="id" column="user_id"></id>
        <result property="name" column="user_name"></result>
        <result property="email" column="user_email"></result>
    </resultMap>


    <select id="selectUserById" resultMap="userMap">
        SELECT * FROM user WHERE user_id = #{id}
    </select>

    <insert id="createUser" parameterType="com.howl.entity.User" useGeneratedKeys="true" keyProperty="id">
        INSERT INTO user (`user_name`,`user_email`) VALUES (#{name},#{email})
    </insert>

</mapper>
```



### 3.4 测试推动

测试成功，因为笔者已经试过很多次了

下面第一个是Mybatis测试环境特有的，放在测试的resources中即可，而第二个测试环境也需要，也将其放进resources中



**测试环境**

```xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE configuration PUBLIC "-//mybatis.org//DTD Config 3.0//EN"
        "http://mybatis.org/dtd/mybatis-3-config.dtd">

<!-- 根标签 -->
<configuration>

    <environments default="default">
        <environment id="default">
            <transactionManager type="JDBC" />
            <dataSource type="POOLED">
                <property name="driver" value="com.mysql.jdbc.Driver" />
                <property name="url" value="jdbc:mysql://127.0.0.1:3306/spring" />
                <property name="username" value="root" />
                <property name="password" value="" />
            </dataSource>
        </environment>
    </environments>


    <mappers>
        <mapper resource="UserMapper.xml"/>
    </mappers>

</configuration>
```



**具体测试**

```java
public class UserDaoTest {

    private UserDao userDao;
    private SqlSession sqlSession;

    @Before
    public void before() throws IOException {
        String resource = "mybatis-config.xml";
        InputStream inputStream = Resources.getResourceAsStream(resource);
        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);
        sqlSession = sqlSessionFactory.openSession();
        userDao = sqlSession.getMapper(com.howl.dao.UserDao.class);
    }

    @After
    public void after() {
        sqlSession.commit();
        sqlSession.close();
    }

    @Test
    public void selectUserById() {
        User user = userDao.selectUserById(1);
        System.out.println(user);
    }

    @Test
    public void createUser() {
        User user = new User(0, "howlet", "9527@qq.com");
        userDao.createUser(user);
        System.out.println(user.getId());
    }
}
```













# 4. Service层

Service是关于Spring的，有容器管理，那么将方便很多



### 4.1 配置文件

当将Dao层交给Service管理时，Mybatis中的环境将交给Spring容器管理，那么则需要配置SqlSessionFactory工厂（mybatis-spring包中），而SqlSessionFactory又需要Mybaits的配置文件，则将配置文件当参数注入到SqlSessionFactory中，其余内容在注释中已经详细说明了，顺便将数据库信息提出成properties



**db.properties**

```properties
jdbc.driverClassName = com.mysql.jdbc.Driver
jdbc.url = jdbc:mysql://127.0.0.1:3306/spring?useUnicode\=true&characterEncoding\=UTF-8&serverTimezone\=Asia/Shanghai&useAffectedRows\=true
jdbc.username = root
jdbc.password =
```



**mybatis-config.xml**

```xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE configuration PUBLIC "-//mybatis.org//DTD Config 3.0//EN"
        "http://mybatis.org/dtd/mybatis-3-config.dtd">

<!-- 根标签 -->
<configuration>

    <!--    <settings>-->
    <!--        <setting name="" value=""/>-->
    <!--    </settings>-->

    <mappers>
        <mapper resource="/mapper/UserMapper.xml"/>
    </mappers>

</configuration>
```



**applicationContext.xml**

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xmlns:context="http://www.springframework.org/schema/context"
       xmlns:aop="http://www.springframework.org/schema/aop"
       xmlns:tx="http://www.springframework.org/schema/tx"
       xsi:schemaLocation="http://www.springframework.org/schema/beans
       http://www.springframework.org/schema/beans/spring-beans.xsd
       http://www.springframework.org/schema/context
       http://www.springframework.org/schema/context/spring-context.xsd
       http://www.springframework.org/schema/aop
       http://www.springframework.org/schema/aop/spring-aop.xsd
       http://www.springframework.org/schema/tx
       http://www.springframework.org/schema/tx/spring-tx.xsd">

    <!--  加载properties文件  -->
    <context:property-placeholder location="classpath:db.properties"></context:property-placeholder>

    <!--  开启注解扫描，只处理service和dao  -->
    <context:component-scan base-package="com.howl">
        <context:exclude-filter type="annotation" expression="org.springframework.stereotype.Controller"/>
    </context:component-scan>

    <!--  配置数据源,spring自带的没有连接池功能  -->
    <bean id="dataSource" class="org.springframework.jdbc.datasource.DriverManagerDataSource">
        <property name="driverClassName" value="${jdbc.driverClassName}"></property>
        <property name="url" value="${jdbc.url}"></property>
        <property name="username" value="${jdbc.username}"></property>
        <property name="password" value=""></property>
    </bean>

    <!--  配置SqlSessionFactory工厂,内部再配置mybatis文件  -->
    <bean id="sqlSessionFactory" class="org.mybatis.spring.SqlSessionFactoryBean">
        <property name="configLocation" value="classpath:mybatis-config.xml"></property>
        <property name="dataSource" ref="dataSource"></property>
    </bean>

    <bean id="mapperScannerConfigurer" class="org.mybatis.spring.mapper.MapperScannerConfigurer">
        <property name="basePackage" value="com.howl.dao"></property>
    </bean>

    <!--  声明式事务管理  -->
    <bean id="dataSourceTransactionManager" class="org.springframework.jdbc.datasource.DataSourceTransactionManager">
        <property name="dataSource" ref="dataSource"></property>
    </bean>

    <!--  配置事务通知  -->
    <tx:advice id="txAdvice" transaction-manager="dataSourceTransactionManager">
        <tx:attributes>
            <tx:method name="select*" read-only="true"/>
            <tx:method name="*" isolation="DEFAULT"/>
        </tx:attributes>
    </tx:advice>

    <!--  事务切面  -->
    <aop:config>
        <aop:pointcut id="pt1" expression="execution(* com.howl.service.*.*(..))"/>
        <aop:advisor advice-ref="txAdvice" pointcut-ref="pt1"></aop:advisor>
    </aop:config>


</beans>
```





### 4.1 Service实现类

一般来说需要先定义接口，然后把其实现类统一放在Impl包中管理，这样才方便业务逻辑的处理，但这里仅是讲解整理不涉及其他内容，所以省略这一步骤。（上面这句话可以不用看）

```java
@Service
public class UserService{

    @Autowired
    private UserDao userDao;

    public User selectUserById(int id) {
        User user = userDao.selectUserById(id);
        return user;
    }

    public int createUser(User user) {
        int num = userDao.createUser(user);
        return num;
    }
}
```

发现没有，类上和依赖都加了注解，用注解的方法交给容器管理





### 4.2 测试推动

由于JUnit不理会我们使用什么框架，所以没有加载容器也就没有依赖注入功能。不用怕，Spring整合了test，内部讲Runner实现了，所以头部两个注解是为了加载Spring的配置环境

```java
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:applicationContext.xml")
public class UserServiceTest {

    @Autowired
    public UserService userService;

    @Test
    public void selectUserById() {
        User user = userService.selectUserById(1);
        System.out.println(user);
    }

    @Test
    public void createUser() {
        User user = new User();
        int rs = userService.createUser(user);
        if(rs > 0){
            System.out.println(user.getId());
        }
    }
}
```













# 5. Web层

Web层是SpringMVC，因为使用的是前后端分离，所以需要额外配置乱码问题



### 5.1 springmvc.xml

```java
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xmlns:context="http://www.springframework.org/schema/context"
       xmlns:aop="http://www.springframework.org/schema/aop"
       xmlns:tx="http://www.springframework.org/schema/tx"
       xmlns:mvc="http://www.springframework.org/schema/mvc"
       xsi:schemaLocation="http://www.springframework.org/schema/beans
       http://www.springframework.org/schema/beans/spring-beans.xsd
       http://www.springframework.org/schema/context
       http://www.springframework.org/schema/context/spring-context.xsd
       http://www.springframework.org/schema/aop
       http://www.springframework.org/schema/aop/spring-aop.xsd
       http://www.springframework.org/schema/tx
       http://www.springframework.org/schema/tx/spring-tx.xsd
       http://www.springframework.org/schema/mvc
       http://www.springframework.org/schema/mvc/spring-mvc.xsd">

    <!--  开启mvc的注解支持,并且在Responsebody上使用UFT-8  -->
    <mvc:annotation-driven>
        <mvc:message-converters register-defaults="true">
            <bean class="org.springframework.http.converter.StringHttpMessageConverter">
                <property name="supportedMediaTypes" value="text/html;charset=UTF-8"></property>
            </bean>
        </mvc:message-converters>
    </mvc:annotation-driven>

    <!--  注解扫描，mvc只扫描Controller  -->
    <context:component-scan base-package="com.howl">
        <context:include-filter type="annotation" expression="org.springframework.stereotype.Controller"/>
    </context:component-scan>

    <!--  静态资源过滤  -->
    <mvc:resources mapping="/pages/**" location="/pages/"/>
    <mvc:resources mapping="/css/**" location="/css/"/>
    <mvc:resources mapping="/js/**" location="/js/"/>
    <mvc:resources mapping="*.html" location="/"/>

</beans>
```





### 5.2 UserController

```java
@RestController
@RequestMapping(value = "/user", produces = "application/json;charset=utf-8")
public class UserController {

    @Autowired
    private UserService userService;

    @RequestMapping(value = "/select", method = RequestMethod.GET)
    public String selectUserById(int id) {
        User user = userService.selectUserById(id);
        return JSON.toJSONString(user);
    }

    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public String createUser(User user) {
        int num = userService.createUser(user);
        if (num > 0) {
            return "增加用用户成功";
        }
        return "增加用户失败";
    }
}
```





### 5.3 测试

```java
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:applicationContext.xml")
public class UserControllerTest {

    @Autowired
    UserService userService;

    @Test
    public void selectUserById() {
        User user = userService.selectUserById(1);
        System.out.println(user);
    }

    @Test
    public void createUser() {
        User user = new User();
        int rs = userService.createUser(user);
        if(rs > 0){
            System.out.println(user.getId());
        }
    }
}
```







# 6. 最终整合

使用SpringMVC需要在Web.xml中配置前端控制器的，但Spring配置谁来加载呢？这是可以用Spring内置的Listener来完成



### Web.xml

```xml
<!DOCTYPE web-app PUBLIC
        "-//Sun Microsystems, Inc.//DTD Web Application 2.3//EN"
        "http://java.sun.com/dtd/web-app_2_3.dtd" >

<web-app>
    <display-name>Archetype Created Web Application</display-name>


    <listener>
        <!--    默认加载WEB-INF下的applicationContext.xml文件    -->
        <listener-class>org.springframework.web.context.ContextLoaderListener</listener-class>
    </listener>
    <!--  上面的解决方法直接配置说明加载类路径下就可以了  -->
    <context-param>
        <param-name>contextConfigLocation</param-name>
        <param-value>classpath:applicationContext.xml</param-value>
    </context-param>


    <!--  编码问题  -->
    <filter>
        <filter-name>characterEncodingFilter</filter-name>
        <filter-class>org.springframework.web.filter.CharacterEncodingFilter</filter-class>
        <init-param>
            <param-name>encoding</param-name>
            <param-value>UTF-8</param-value>
        </init-param>
        <init-param>
            <param-name>forceEncoding</param-name>
            <param-value>true</param-value>
        </init-param>
    </filter>
    <filter-mapping>
        <filter-name>characterEncodingFilter</filter-name>
        <url-pattern>/*</url-pattern>
    </filter-mapping>


    <!--  前端控制器  -->
    <servlet>
        <servlet-name>dispatcherServlet</servlet-name>
        <servlet-class>org.springframework.web.servlet.DispatcherServlet</servlet-class>
        <init-param>
            <param-name>contextConfigLocation</param-name>
            <param-value>classpath:springmvc.xml</param-value>
        </init-param>
        <load-on-startup>1</load-on-startup>
    </servlet>
    <servlet-mapping>
        <servlet-name>dispatcherServlet</servlet-name>
        <url-pattern>/</url-pattern>
    </servlet-mapping>


</web-app>

```













# 7. 整体测试

编写前端页面，部署项目，测试运行



```html
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Title</title>
</head>
<body>

    <form action="user/select" method="get">
        id：<input type="text" name="id">
        <input type="submit" value="提交查询">
    </form>

    <form action="user/create" method="post">
        id：<input type="text" name="id">
        name：<input type="text" name="name">
        email：<input type="text" name="email">
        <input type="submit" value="提交增加">
    </form>

</body>
</html>
```



![Title-Google-Chrome-2020-03-05-17-17-28](C:\Users\Howl\Desktop\Title-Google-Chrome-2020-03-05-17-17-28.gif)