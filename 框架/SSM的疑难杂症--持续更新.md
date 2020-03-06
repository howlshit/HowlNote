# 1. Spring



### 单例Bean的线程安全问题

单例bean存在线程安全问题，当多线程操作成员变量时会有冲突发生，如果定义在方法入参处那样在栈中则不会。避免使用成员变量，推荐使用ThreadLocal成员变量拉





### Bean的生命周期



**预先知识**

其实在学习Spring时就有接触，没有使用就很容易忘记，见名知意

1、实现InitializingBean接口中的afterPropertiesSet方法

2、实现DisposableBean接口中的destroy方法

3、bean标签中init-method方法

4、bean标签中destroy-method方法

5、实现BeanPostProcessor接口的postProcessBeforeInitialization和postProcessAfterInitialization：分别在容器初始化bean对象之前之后执行

6、实现xxxAware接口，获取Spring框架的对象：

- ApplicationContextAware：获得ApplicationContext对象，可以用来获取所有Bean definition的名字
- BeanFactoryAware：获得BeanFactory对象，可以用来检测Bean的作用域
- BeanNameAware：获得Bean在配置文件中定义的名字
- ResourceLoaderAware：获得ResourceLoader对象，可以获得classpath中某个文件
- ServletContextAware：在一个MVC应用中可以获取ServletContext对象，可以读取context中的参数
- ServletConfigAware：在一个MVC应用中可以获取ServletConfig对象，可以读取config中的参数







**总结：**

- Bean容器读取配置文件并注册类信息
- Bean容器反射创建Bean的实例
- Bean容器根据注册信息进行依赖注入
- 若实现BeanNameAware接口，则调用setBeanName方法，传入Bean的名字
- 若实现BeanClassLoaderAware接口，则调用setBeanClassLoader方法，传入ClassLoader对象的实例
- 若实现BeanFactoryAware接口，调用setBeanClassLoader()方法，传入ClassLoader对象的实例
- 与上面的类似，如果实现了其他*Aware接口，就调用相应的方法
- 若实现BeanPostProcessor接口，执行postProcessBeforeInitialization方法
- 若实现InitializingBean接口，执行afterPropertiesSet方法
- 若有配置init-method属性，执行指定的方法
- 若实现BeanPostProcessor接口，执行postProcessAfterInitialization方法
- 当销毁Bean时，若实现DisposableBean接口，执行destroy方法
- 当销毁Bean时，若配置destroy-method属性，执行指定的方法





### Spring用到的设计模式

工厂模式：BeanFactory和ApplicationContext创建bean对象

代理模式：AOP的动态代理

单例模式：默认bean时单例的

模板模式：JDBCTemplate对数据库操作的类

观察者模式：Spring的事件





### Bean的加载和使用过程

Spring从配置文件中读取类的信息将其放入容器的BeanDefinition注册表中，然后根据注册表实例化bean对象将其放入缓存池中，当需要使用时，如果使用@Autowired注解，则根据类类型匹配key值，（map存放bean对象），匹配不到报错、匹配多个，则按名字再匹配，没有或多个也报错。可以用@Qualifier指定名字匹配





### AOP的实现方式

Spring的AOP是面向切面编程，其有JDK的基于接口和cglib基于子类的实现，Spring都集成了二者，所以是开发者是透明的，单例使用JDK，多例使用cglib效率会更好













# 2. SpringMVC



### 流程图

![1737887-20200304102758029-1245290565](C:\Users\Howl\Desktop\1737887-20200304102758029-1245290565.jpg)

一句话总结：请求发送到前端控制器，然后转移给映射器返回处理器和拦截器链，前端控制器通过控制器获取适配器，然后调用里面的hanlder方法执行控制器方法，控制器将json返回值返回到客户端，具体请看 [SpringMVC知识梳理](<https://www.cnblogs.com/Howlet/p/12399045.html>)











### 乱码

post：使用CharacterEncodingFilter

get：参数重编译，更改tomcat配置文件的编码、

使用json的话映射注解中加入@RequestMapping(value = "/user", produces = "application/json;charset=utf-8")

或者springmvc.xml全局配置

```xml
<!--  开启mvc的注解支持,并且在Responsebody上使用UFT-8  -->
<mvc:annotation-driven>
    <mvc:message-converters register-defaults="true">
        <bean class="org.springframework.http.converter.StringHttpMessageConverter">
            <property name="supportedMediaTypes" value="text/html;charset=UTF-8"></property>
        </bean>
    </mvc:message-converters>
</mvc:annotation-driven>
```





### 转发和重定向

```java
//     项目名 ssm
//     @RequestMapping(value = "/user")
//     /index2.html表示项目根地址，没有带项目名
//     index2.html被mapping带乱变成 /ssm/user/index2.html
//     所以全写上

response.sendRedirect("/ssm/index2.html");
request.getRequestDispatcher("/ssm/index2.html").forward(request,response);
```













# 3. Mybatis



### Dao接口中参数不同可重载吗？

不行，因为是根据全限定类名+方法名作为key值唯一定位一个MappedStatmented的





### 分页原理

* 物理分页：使用RowBounds在结果集中进行分页
* 逻辑分页：在语句查询中进行limit分页





### 分页插件原理

使用Mybatis提供的接口，拦截语句后再改写语句





### 延迟加载原理

使用cglib动态代理，拦截方法，当发现调用的属性为空时，会单独发送事先保存的关联对象的sql语句





### 动态代理的要求

* 代理接口的全限定类名与映射文件命名空间相同
* 代理接口的方法名与映射文件语句的id相同
* 代理接口的参数类型与映射文件语句的参数类型相同
* 代理接口的返回值类型与映射文件语句的返回值类型相同















****

终于阶段性完成一部分内容了，好想出去啊   2020/03/06