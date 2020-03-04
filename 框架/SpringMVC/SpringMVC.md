> 同上一篇博客，复习梳理SpringMVC知识点，这次的梳理比较快，很多细节没有顾虑到，后期可能会回来补充





# 1. 整体架构



### 1.1 在学习了SSM框架后我们来理清三者的应用层面

![1583130271826](C:\Users\Howl\AppData\Roaming\Typora\typora-user-images\1583130271826.png)





浏览器发送请求，请求到达SpringMVC处理，然后调用业务层逻辑实现，跟着持久层操作获取数据，最后逆序响应到浏览器。前面我们复习了Mybaits和Spring框架，我们当然不陌生了，现在就来了解下SpringMVC到底有什么作用





### 1.2 MVC

MVC模型中，M是把浏览器传的参数封装成的pojo类型，V则代表视图，C就是控制器也是重点。SpringMVC是以**组件**的形式来形成整体的，下面也是画图来解释



![1583155251763](C:\Users\Howl\AppData\Roaming\Typora\typora-user-images\1583155251763.png)

- 核心控制器被Tomcat初始化并主动加载applicationContext配置文件
- 用户发送请求
- 请求到达核心控制器
- 核心控制器交由映射器处理映射地址
- 核心控制器找到适配器来适配处理器（适配器模式）
- 将请求过来的数据进行转换
- 讲转好的数据给处理器处理并沿路返回
- 最后通过视图解析器解析
- 响应对应的页面





从上面可以看出 DispatcherServlet 是核心指挥中心，MVC框架围绕其来设计的，处理所有的http请求和响应



DispatcherServlet 收到请求后根据HandlerMappering来选择并且调用映射的控制器



控制器接收到请求后基于GET、POST调用适当的Servce方法后将数据返回到DispatcherServlet中



上面所说的HandlerMapping、Controller是WebApplicationContext的一部分，其是ApplicationContext的扩展，也是BeanFactory的扩展



启动Tomcat，初始化web.xml中的 DispatcherServlet ，而DispatcherServlet 框架则尝试加载applicationContext.xml配置文件内容













# 2. 映射关系

MVC作用在表现层用来处理请求，所以地址映射也在这里，即在Controller中，请求是在方法上处理的，不是类上（这也是单例的原因，类上使用映射即为分模块作用），方法的返回值默认为返回的网页地址（现在前后端分离使用得比较少了，下面讲解都是用前后端分离模式），其映射关系使用注解的过程为：



```java
@Controller
@RequestMapping("/user")
public class HelloController {

    @RequestMapping(value = "/hello",method = RequestMethod.GET)
    public String sayHello(){
        System.out.println("Hello World");
        return "success";
    }
}
```













# 3. 参数绑定

这里是重点，因为请求一般都带数据的，然后在这里绑定成Model，方便我们使用，不用再像JavaWeb程序中`request.getParameter()`了，支持基本类型、String类型，bean类型以及集合类型



**这里一个小插曲，如果要获取request、response，则在方法参数上自己添加即可**



### 3.0 这里先给出需要用到的Bean

```java
public class User {

    private int id;
    private String name;
    private String email;
    private InnerBean innerBean;
    private List<InnerBean> innerBeanList;
    private String[] array;

 	// 省略各种getter / setter
}
```

```java
public class InnerBean {

    private String inner;

    public void setInner(String inner) {
        this.inner = inner;
    }
}
```







### 3.1 简单参数绑定

MVC框架会在方法参数中绑定请求中名字相同的变量（使用了反射），简单参数为基本类型和String，参数名若不同则使用@RequestParam注解绑定

```java
<form action="param/param1" method="get">
	id：<input type="text" name="id">
    name：<input type="text" name="name">
    email：<input type="text" name="email">
    <input type="submit" value="submit1">
</form>

// 表单中name和形参名字相同

// 简单参数
@RequestMapping(value = "/param1")
public String getParameter1(String id, String name, String email) {
    System.out.println(id + name + email) ;
    return "success";
}
```





### 3.2 Bean封装

bean类型封装是用过里面的setter实现的，而且还有bean中有bean的情况

```java
<form action="param/param2" method="get">
	id：<input type="text" name="id">
    name：<input type="text" name="name">
    email：<input type="text" name="email">
    inner：<input type="text" name="innerBean.inner">
    <input type="submit" value="submit2">
</form>


// 封装Bean对象，依靠setter方法
@RequestMapping(value = "/param2")
public String getParameter2(User user) {
    System.out.println(user) ;
    return "success";
}
```





### 3.3 集合封装

```java
<form action="param/param3" method="get">
	id：<input type="text" name="id">
    name：<input type="text" name="name">
    email：<input type="text" name="email">
    inner：<input type="text" name="innerBeanList[0].inner">
    inner：<input type="text" name="innerBeanList[1].inner">
    inner：<input type="text" name="array[0]">
    inner：<input type="text" name="array[1]">
    <input type="submit" value="submit2">
</form>



// 封装集合
@RequestMapping(value = "/param3")
public String getParameter3(User user) {
    System.out.println(user) ;
    return "success";
}
```













# 4. 类型转换器

请求传过来的数据都是字符串，那么我们使用的时候为什么可以获取其他类型呢？这里是使用了框架内部的默认转换器所以才可以取得其他类型数据，但如果默认转换器识别不了，那么我们就要自己配置类型转换器来实现功能



这里有个场景：前端传2020/3/2过来让Date类型接收是没问题的，但是如果前端传了2020-3-2呢？这样就会报错，因为2020-3-2框架没有这个转换器来转成Date类型，那么就需要我们手动来设置



```java
@Controller
@RequestMapping(value = "/converter")
public class ConverterController {

    @RequestMapping(value = "/converter")
    public String converterMethod(Date date){
        System.out.println(date);
        return "success";
    }
}
```





### 4.1 创建转换器类

这个类实现了Converter<S,T>接口，这个泛型要自己添加，返回类型为转换好的类型

```java
public class StringToDateConverter implements Converter<String, Date> {

    /**
     * 需要自己手动添加泛型，s指传进来的字符串
     */
    public Date convert(String s) {
        s = s.replace('/','-');
        DateFormat sdf = new SimpleDateFormat("yyyy-MM-DD");
        try {
            return sdf.parse(s);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }
}
```





### 4.2 将自定义转换器注册在转换器服务工厂中，并给容器管理

```java
<!--  自定义类型转换器  -->
<bean id="conversionServiceFactoryBean" class="org.springframework.context.support.ConversionServiceFactoryBean">
    <property name="converters">
    	<!--  set标签  -->
        <set>
            <bean class="com.howl.util.StringToDateConverter"></bean>
        </set>
    </property>
</bean>
```





### 4.3 注册组件

因为MVC是基于组件的，所以使用了组件就要在配置文件中注册

```xml
<!--  开启mvc注解支持,并且组件生效，默认使用适配器和映射器  -->
<mvc:annotation-driven conversion-service="conversionServiceFactoryBean"></mvc:annotation-driven>
```













# 5. 文件上传

要求：

1、表单要是enctype="multipart/form-data"

2、方法要是POST

3、输入框要是`<input type="file">`



当上传表单为多个文件时，根据hppt请求体来分割很复杂，所以要借助第三方jar，也就是传统的上传方法，该方法依赖 `commons-fileupload`（当然下面的MVC的简化文件上传也要该依赖），传统的文件上传笔者已经写过一篇博文了，[请点击这里](<https://www.cnblogs.com/Howlet/p/12057441.html>)





至于MVC的上传呢，更加简便。MVC提供了MultipartFiled对象，需要表单的name与之对应

```java
<p>文件上传</p>
<form action="upload/upload" method="post" enctype="multipart/form-data">
    选择文件：<input type="file" name="uploads">
    选择文件：<input type="file" name="uploads">
    <input type="submit" value="submit">
</form>





@RestController
@RequestMapping(value = "/upload")
public class FileUploadController {

    @RequestMapping(value = "/upload")
    public String fileUploadMethod(HttpServletRequest httpServletRequest, MultipartFile[] uploads) throws IOException {

        // 创建目录
        String path = httpServletRequest.getSession().getServletContext().getRealPath("/uploads/");
        File file = new File(path);
        if(!file.exists()){
            file.mkdirs();
        }

        for(MultipartFile value : uploads){

            // 原名
            String originalFilename = value.getOriginalFilename();
            // 生成随机名
            String fileName = UUID.randomUUID().toString().replace("-","") + "_" + originalFilename;
            // 和传统文件上传不同，参数为File，差别看博客
            value.transferTo(new File(path,fileName));
        }

        // 笔者这里返回了地址，一般返回成功消息的
        return path;
    }
}
```



#### MVC是基于组件的，所以文件解析器也是一个组件需要配置



```xml
<!--  文件解析器，名字必须是multipartResolver  -->
<bean id="multipartResolver" class="org.springframework.web.multipart.commons.CommonsMultipartResolver">
    <property name="defaultEncoding" value="UTF-8"></property>
    <property name="maxUploadSize" value="1024102410"></property>
</bean>
```













# 6. 异常处理

![1583160033357](C:\Users\Howl\AppData\Roaming\Typora\typora-user-images\1583160033357.png)



正常操作是底层发生异常会一直向上抛，直到发给浏览器用户看到，我们要避免这种事情发生，就需要异常处理，所以我们要把流程改成下面这样



![1583160182802](C:\Users\Howl\AppData\Roaming\Typora\typora-user-images\1583160182802.png)





### 6.1 编写自定义异常类（做提示信息）

```java
public class MyException extends Exception {

    private String msg;

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public MyException(String msg) {
        this.msg = msg;
    }
}
```







### 6.2 编写全局异常处理器（注解版）

```java
// 获取所有异常
@RestControllerAdvice()
public class GlobalExceptionHandler {

    @ExceptionHandler(value = Exception.class)
    public String handleException(Exception e){

        // 如果是自定义异常，则发送自己的消息
        if(e instanceof MyException){
            return ResponseHelper.error(((MyException) e).getMsg(),new User());
        }

        // 否则发送系统错误
        return ResponseHelper.error("系统错误");
    }
}
```













# 7. 拦截器

类似于Filter，但拦截器是对处理器Controller进行预处理和后处理，不同于Filter拦截Servlet。拦截器是MVC内部的，使用MVC框架才有拦截器，而过滤器是javaWeb内部的。范围不同，Filter中配置 `/*`会过滤所有请求，拦截器应用场景有：权限检查和日志处理





### 7.1 实现HandlerInterceptor

这个接口要自己手动输入重写，因为1.8之后接口变了，笔者在这里配置权限检查



```java
public class MyInterceptor implements HandlerInterceptor {

    /**
     * @return true表示放行，执行下一个拦截器，false表示拦截
     * @throws Exception
     */
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        Object user = request.getSession().getAttribute("user");
        if(user == null){
            // 没登录，重定向
            response.sendRedirect("/admin/login.html");
        }
        // 否则放行
        return  true;
    }

    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

    }

    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {

    }
}
```







### 7.2 applicationContext配置拦截器

万物皆组件



```xml
<!--  配置拦截器，注意path是写url地址  -->
<mvc:interceptors>
    <mvc:interceptor>
        <mvc:mapping path="/interceptor/*"/>
        <bean class="com.howl.interceptor.MyInterceptor"></bean>
    </mvc:interceptor>
    
    <!--  第二个拦截器应该这里配  -->
    <mvc:interceptor>
        <mvc:mapping path="/interceptor/*"/>
        <bean class="com.howl.interceptor.MyInterceptor"></bean>
    </mvc:interceptor>

</mvc:interceptors>
```













# 8. 注解总览

```java
@Controller
@RequestMapping(value = "/hello",method = RequestMethod.GET)
@RequestParam(value = "name") // 用于匹配名字不一致
@PathVariable(value = "sid")  // 绑定url中的占位符，主要用于Restful风格，下面有这里

@ResponseBody // 主要用于响应json数据，即Controller方法的返回值通过适当转换器后，写入Response不走视图解析器，笔者用fastjson将bean转换成json即String类型返给前端，即前后端分离

@RequestBody  // 若异步请求，则发送给后端的是json数据无法绑定参数，用了这个注解，将获取请求体中全部参数，以key=value的形式，get方法不在请求体中，无法使用，当以键值对出现时，则是换成普通请求的数据格式，使用setter将绑定参数

@@RestController // @Controller和@ResponseBody的结合，用于前后分离，不走视图解析器，可放于类上，则类中的全部方法适用，而@RequestBody则不行
```





**@PathVariable**

```java
@RequestMapping("/anno2/{sid}")
public String annoMethod2(@PathVariable(value = "sid") int id) {
    System.out.println(id);
    return "success";
}
```













# 9. 补充



#### DispatcherServlet在配置映射关系中用/，则会拦截所有请求，包括静态资源而导致无法访问，所以要在applicationContext中配置不拦截

```xml
<!--  配置静态资源不拦截  -->
<mvc:resources mapping="/pages/*" location="/pages/"></mvc:resources>
```



#### MVC 三大组件：适配器，映射器，解析器



#### 在Spring的基础上需要的额外jar包：spring-web、spring-mvc





#### 约束

```xml
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

</beans>
```





#### pom.xml

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
        <dependency>
            <groupId>org.springframework</groupId>
            <artifactId>spring-context</artifactId>
            <version>5.0.2.RELEASE</version>
        </dependency>
        <dependency>
            <groupId>org.springframework</groupId>
            <artifactId>spring-test</artifactId>
            <version>5.0.2.RELEASE</version>
        </dependency>
        <dependency>
            <groupId>junit</groupId>
            <artifactId>junit</artifactId>
            <version>4.12</version>
            <scope>test</scope>
        </dependency>
        <dependency>
            <groupId>org.junit.jupiter</groupId>
            <artifactId>junit-jupiter</artifactId>
            <version>RELEASE</version>
            <scope>test</scope>
        </dependency>
        <dependency>
            <groupId>org.aspectj</groupId>
            <artifactId>aspectjweaver</artifactId>
            <version>1.8.7</version>
        </dependency>
        <dependency>
            <groupId>org.springframework</groupId>
            <artifactId>spring-tx</artifactId>
            <version>5.0.2.RELEASE</version>
        </dependency>
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
        
        <dependency>
            <groupId>commons-fileupload</groupId>
            <artifactId>commons-fileupload</artifactId>
            <version>1.4</version>
        </dependency>
        
        <dependency>
            <groupId>com.alibaba</groupId>
            <artifactId>fastjson</artifactId>
            <version>1.2.62</version>
        </dependency>
        
        <!--  HttpServletRequest要用，一般Tomcat自带  -->
        <dependency>
            <groupId>javax.servlet</groupId>
            <artifactId>servlet-api</artifactId>
            <version>2.5</version>
            <scope>provided</scope>
        </dependency>

    </dependencies>
</project>
```

