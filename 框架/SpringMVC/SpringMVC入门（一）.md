# SpringMVC入门（一）





## 1. 工作流程

![捕获](C:\Users\Howl\Desktop\捕获.PNG)

* 用户请求服务器，然后核心控制器捕获请求
* 核心控制器交由映射器把请求url和控制器进行映射
* 核心控制器交由适配器调用映射的控制器，中间还进行数据转换
* 对应的控制器的逻辑操作完成后返回ModelAndView或String
* 视图解析器解析ModelAndView
* 返回一个View
* 用户看到请求的数据



* **笔者目前的操作都是返回String，然后前端Ajax异步获取数据，所以下面演示也用String类型的返回值**
* **并且都是基于注解**







## 2. 步骤



### 2.1 工程目录及jar包

![捕获1](C:\Users\Howl\Desktop\捕获1.PNG)





### 2.2 配置Web.xml

```xml
<!-- 配置spring的核心控制器 -->
<servlet>
    <servlet-name>dispatcherServlet</servlet-name>
    <servlet-class>org.springframework.web.servlet.DispatcherServlet</servlet-class>
	<!-- 配置servlet初始化参数，告知applicationContext位置 -->
    <init-param>
        <param-name>contextConfigLocation</param-name>
        <param-value>classpath:applicationContext.xml</param-value>
	</init-param>
    <!-- 启动顺序 -->
    <load-on-startup>1</load-on-startup>
</servlet>
<!-- 映射地址 -->
<servlet-mapping>
    <servlet-name>dispatcherServlet</servlet-name>
    <url-pattern>*.do</url-pattern>
</servlet-mapping>
	

<!-- 编码过滤器，解决传送过来的参数乱码问题 -->
<filter>
    <filter-name>CharacterEncodingFilter</filter-name>
    <filter-class>
    	org.springframework.web.filter.CharacterEncodingFilter
    </filter-class>
	<init-param>
        <param-name>encoding</param-name>
        <param-value>UTF-8</param-value>
	</init-param>
</filter>
<filter-mapping>
    <filter-name>CharacterEncodingFilter</filter-name>
    <url-pattern>/*</url-pattern>
</filter-mapping>
	
	
<!-- 欢迎页 -->
<welcome-file-list>
	<welcome-file>/WEB-INF/pages/index.html</welcome-file>
</welcome-file-list>
```





## 2.3 配置applicationContext.xml

```xml
<!-- 告知sptring创建容器时要扫描的包 -->
<context:component-scan base-package="com.howl.controller"/>


<!-- 配置springmvc的视图解析器，这里不使用 -->
<!-- <bean id="viewResolver" class="org.springframework.web.servlet.view.InternalResourceViewResolver">
	<property name="prefix" value="/WEB-INF/pages/"></property>
	<property name="suffix" value=".html"></property>
</bean> -->
	

<!-- 注册HandlerMapping，HandlerAdapter -->
<mvc:annotation-driven >
	<!-- response设置utf-8，解决返给前端中文乱码问题 -->
	<mvc:message-converters register-defaults="true">
		<bean class="org.springframework.http.converter.StringHttpMessageConverter">
			<property name="supportedMediaTypes" value="text/html;charset=UTF-8"/>
		</bean>
	</mvc:message-converters>
</mvc:annotation-driven>
```



## 2.4 创建Controller

```java
package com.howl.controller;

@Controller("UserController") //注解控制器
@RequestMapping("/User") //分模块地址映射
@ResponseBody  //返回字符串，不是ModelandView
public class UserController {
	
	@RequestMapping(value="/add",method=RequestMethod.GET) //指定访问方法
	public String add(){
		System.out.println("执行了add方法");
		return "{'code' : '0000','msg' : '执行了add方法'}"; //返回值json
	}
	
//模块测试,先忽略后面讲解------------------[开始]    
	@RequestMapping("/update")
	public String update(){
		System.out.println("执行了update方法");
		return "执行了update方法";
	}
//模块测试--------------------------------[完]
	
	@RequestMapping("/insert")
	public String insert(int id){
		System.out.println("执行了insert方法，id="+ id);
		return "执行了insert方法，id="+ id;
	}

}
```





## 2.5 前端页面

```html
<body>
    <a href="User/add.do">add</a>
    <a href="update.do">update</a>  <!-- 这里测试分模块，后面会讲解，先略过 -->
    <a href="User/insert.do?id=1">insert</a>
</body>
```



* 前端点击add与insert链接的时候，分别返回

![图片1](C:\Users\Howl\Desktop\图片1.png)

* 这里注意，前端发送参数的属性name要和对应方法的参数名字类型一致





## 2.6 模块测试

* 笔者在UserController加了@RequestMapping("/User")，设置了模块

* 而且UserController里确实写了@RequestMapping("/update")

* 但是访问前端页面的update链接的时候  -_- !! 找不到对象 !!

  ![1575524669176](C:\Users\Howl\Desktop\1575524669176.png)





**究其原因：是分模块的锅，仔细看二者的url区别**

* add——http://localhost:8080/Springmvc/User/add.do
* update——http://localhost:8080/Springmvc/update.do
* 可以看到二者地址栏区别在于/User/，没错就是在类上配置的@RequestMapping("/User")
* 这个注解可以用在模块化管理，十分便利，比如访问用户/User/，访问问题/Question/，一眼就看出来







