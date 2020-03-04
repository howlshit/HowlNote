# Spring入门（一）——IOC



### 1. IOC定义

Inversion of Control，减低计算机代码间的耦合度，对象的创建交给外部容器完成，不用再new了



### 2. 流程



#### 2.1 创建Bean对象

```jav
package bean;

public class User {
	
	private String name;
	private String email;
	private String password;
	
	/**
    * 省略了getters/setters
    * @author Howl
    */
	
	//构造函数
	public User(String name, String email, String password) {
		super();
		this.name = name;
		this.email = email;
		this.password = password;
	}
}
```



#### 2.2 配置**applicationContext.xml**

```xml
<beans xmlns="http://www.springframework.org/schema/beans"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xmlns:p="http://www.springframework.org/schema/p"
    xmlns:context="http://www.springframework.org/schema/context"
    xsi:schemaLocation="
        http://www.springframework.org/schema/beans
        http://www.springframework.org/schema/beans/spring-beans.xsd
        http://www.springframework.org/schema/context
        http://www.springframework.org/schema/context/spring-context.xsd">
    <!-- 上面是约束的固定写法 -->
    
    
    <!-- 配置Bean对象给Spring容器 -->
    <!-- id 唯一表示 -->
    <!-- class 对应的Bean对象 -->
    <!-- scope 作用域，单例和多例 -->
	<bean id="User" class="bean.User" scope="singleton"></bean>
    
</beans> 
```



#### 2.3 通过容器获取Bean对象

```java
//通过配置文件获取上下文
applicationContext ac = new ClassPathXmlApplicationContext("applicationContext.xml");
//通过上下文获取Bean对象，不用通过new方式了
User user = ac.getBean("User");
```







### 3. 其他细节



#### 3.1 带参构造函数创建对象

``` xml
<bean id="User" class="bean.User" scope="singleton">
    
    <!--constructor指定构造函数的参数类型、名称、第几个-->
    <constructor-arg index="0" name="name" type="String" value="Howl"></constructor-arg>
    <!--参数为对象时，value改为ref="" -->
</bean>
```



#### 3.2 装载集合

```xml
<bean id="userService" class="bb.UserService" >
	<constructor-arg >
		<list>
            //普通类型
            <value></value>
            //引用类型
            <ref></ref>
		</list>
	</constructor-arg>
</bean>
```



#### 3.3 注解

``` xml
1.先引入context名称空间
xmlns:context="http://www.springframework.org/schema/context"

2.开启注解扫描器
<context:component-scan base-package=""></context:component-scan>

3.在Bean对象中添加@Component(name ="User"),就不用在配置文件中写<Bean>标签了
```

