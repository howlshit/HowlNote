# Spring——DI



## 1. DI

Dependency Injection，依赖注入。当对象里有属性或对象的时候，就需要为这些属性或对象赋值



## 2. 流程

这里介绍两种方式

* set方法
* 注解方式



### 2.1 set方法

**Bean准备**

```java
package bean;

import bean.Question;

public class User {
	
	private String name;
	private String email;
	private String password;
    private Question question;
	
	/**
    * 省略了getters/setters
    * @author Howl
    */
    
    //set方法
    public void setQuestion(Question question) {
       this.question = question;
    }
	
	//构造函数
	public User(String name, String email, String password) {
		super();
		this.name = name;
		this.email = email;
		this.password = password;
	}
}
```

```java
package bean;

import java.sql.Timestamp;
import java.util.Date;

public class Question {
	
	private int id;
	private Timestamp time;
	private String content;
	
	/**
    * 省略了getters/setters
    * @author Howl
    */
	
    //构造函数
	public Question(int id, Timestamp time, String content) {
		super();
		this.id = id;
		this.time = time;
		this.content = content;
	}
}
```

**applictionContext.xml配置**

```xml
<!--创建User对象-->
<bean id="User" class="User">
    <!-- 依赖注入,setting自动注入 -->
	<property name="Question" ref="Question"/>
</bean>

 <!--创建Question对象-->
<bean id="Question" class="Question"></bean>
```

**获取对象**

```java
ApplicationContext ac = new ClassPathXmlApplicationContext("applicationContext.xml");

//直接获取
User user = ac.getBean("User");
```







### 2.2 注解方式

**注解准备**

```java
package bean;

import bean.Question;

@Component
public class User {
	
	private String name;
	private String email;
	private String password;
    private Question question;
	
	/**
    * 省略了getters/setters
    * @author Howl
    */
    
    //set方法
    @Autowired
    public void setQuestion(Question question) {
       this.question = question;
    }
	
	//构造函数
	public User(String name, String email, String password) {
		super();
		this.name = name;
		this.email = email;
		this.password = password;
	}
}
```

**获取对象**

```java
ApplicationContext ac = new ClassPathXmlApplicationContext("applicationContext.xml");

//直接获取
User user = ac.getBean("User");
```

