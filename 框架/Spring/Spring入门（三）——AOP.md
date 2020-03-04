# Spring入门（三）——AOP



## 1. AOP

aspect object programming ，简单来说就是把重复的代码抽取出来，然后再需要用到的地方进行切入，这里讲解基于接口的注解实现





## 2. 了解

* 关注点：即重复的代码
* 切面：关注点形成的类
* 切入点：被切入的方法
* 切入点表达式：通过表达式来指定需要切入的方法
* @Component()：IOC的注解实现
* @Aspect：指定为切面类的注解
* @Pointcut：切入点表达式注解的实现
* @Before：前置通知





## 3. 准备

applicationContext.xml：开启注解

```xml
<!-- 开启IOC注解的包 -->
<context:component-scan base-package="main"></context:component-scan> 

<!-- 开启AOP注解方式 -->
<aop:aspectj-autoproxy></aop:aspectj-autoproxy>
```

接口 ：基于接口实现，一定要写接口

```java
public interface UserDao {

    //这里写被切入的方法
	void add();
		
}
```

接口实现类

```java
@Component("UserDaoImp")
public class UserDaoImp implements UserDao {

	@Override
	public void add() {
		System.out.println("被切入的方法执行了");
	}

}
```

切面类：重复的代码的集合类

```java
package main;

@Component
@Aspect
public class Logger {

	@Before("execution(* main..*(..))")
	public void beforelogger(){
		System.out.println("前置通知");
	}
	
	@After("execution(* main..*(..))")
	public void afterlogger(){
		System.out.println("后置通知");
	}
	
}

```





## 4. 运行

``` java
public static void main(String[] args) {
    
	ApplicationContext ac = new ClassPathXmlApplicationContext("applicationContext.xml");

    //获得Bean对象要转为接口类型
    UserDao imp = (UserDao) ac.getBean("UserDaoImp");
    //执行方法
    imp.add();

}
```

**结果**

```xml
前置通知
被切入的方法执行了
后置通知
```

