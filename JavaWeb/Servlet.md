## Servlet

Servlet是一个java接口，为我们封装好了处理HTTP请求的各种方法，而从达到浏览器和服务器的交互的目的





## Tomcat

Tomcat是一个Servlet容器，能运行.class文件，也是Jsp容器能处理动态资源，还是Web服务器也就是说能处理Hmlt,Css等，Tomcat启动时读取webapps下web.xml文件里的信息，加载对应类，然后反射的实例化他们



常用的xml元素

```xml
<web-app> 
    <display-name></display-name>定义了WEB应用的名字 
    <description></description> 声明WEB应用的描述信息 
    
    <context-param></context-param> context-param元素声明应用范围内的初始化参数。 
    <filter></filter> 过滤器元素将一个名字与一个实现javax.servlet.Filter接口的类相关联。 
    <filter-mapping></filter-mapping> 一旦命名了一个过滤器，就要利用filter-mapping元素把它与一个或多个servlet或JSP页面相关联。 
    
    <listener></listener>servlet API的版本2.3增加了对事件监听程序的支持，事件监听程序在建立、修改和删除会话或servlet环境时得到通知。Listener元素指出事件监听程序类。 
    
    <servlet></servlet> 在向servlet或JSP页面制定初始化参数或定制URL时，必须首先命名servlet或JSP页面。Servlet元素就是用来完成此项任务的。 
    
    <servlet-mapping></servlet-mapping> 服务器一般为servlet提供一个缺省的URL：http://host/webAppPrefix/servlet/ServletName.
    
    但是，常常会更改这个URL，以便servlet可以访问初始化参数或更容易地处理相对URL。在更改缺省URL时，使用servlet-mapping元素。 
 
    <welcome-file-list></welcome-file-list> 指示服务器在收到引用一个目录名而不是文件名的URL时，使用哪个文件。 
    
    <error-page></error-page> 在返回特定HTTP状态代码时，或者特定类型的异常被抛出时，能够制定将要显示的页面
</web-app>
```

元素的配置

```xml
<listener> 
    <listerner-class>listener.SessionListener</listener-class> 
</listener>


<context-param> 
    <param-name>ContextParameter</para-name> 
    <param-value>test</param-value> 
    <description>It is a test parameter.</description> 
</context-param> 


<servlet>
    <servlet-name>ServletConfigTest</servlet-name>
    <servlet-class>pratices.ServletConfigTest</servlet-class>
    <init-param>
    	<param-name>name</param-name>
    	<param-value>Howl</param-value>
    </init-param>
    <load-on-startup>1</load-on-startup>
</servlet>
<servlet-mapping>
    <servlet-name>ServletConfigTest</servlet-name>
    <url-pattern>/ServletConfigTest</url-pattern>
</servlet-mapping>


<welcome-file-list>
    <welcome-file>index.html</welcome-file>
</welcome-file-list>


<error-page> 
	<error-code>404</error-code> 
	<location>/404.html</location> 
</error-page> 


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
```





## 使用流程



* 创建测试类实现Servlet接口，其中有五个方法

```java
public class ServletTest implements Servlet {

	public void destroy() {

	}

	public ServletConfig getServletConfig() {
		return null;
	}

	public String getServletInfo() {
		return null;
	}

	public void init(ServletConfig arg0) throws ServletException {

	}

	public void service(ServletRequest arg0, ServletResponse arg1) throws ServletException, IOException {

	}
}
```

* 显然service() 是我们处理请求的地方，所以我们在Service()方法里面写入

```java
arg1.getWriter().write("Hello World");  //字符流
```

* 写完Service()是不够的，我们还需要让Tomcat知道该类在什么条件下会被调用

配置web.xml

```xml
<servlet>
    <!-- 配置一个名字 -->
	<servlet-name>ServletTest</servlet-name>
    <!-- 对应的Servlet类 -->
	<servlet-class>com.howl.controller.ServletTest</servlet-class>
</servlet>
<servlet-mapping>
    <!-- 需要映射的Servlet名字 -->
	<servlet-name>ServletTest</servlet-name>
    <!-- 映射地址 -->
	<url-pattern>/HelloWorld</url-pattern>
</servlet-mapping>
```

* 输入对应的映射地址访问

![1576229217679](C:\Users\Howl\AppData\Roaming\Typora\typora-user-images\1576229217679.png)





## 生命周期

* 加载实例化：第一次访问该Servlet时加载对应的Class并且创建该实例，**属于单例**，但多线程

* init：实例化后调用该函数初始化

* service：浏览器访问该类时调用该函数

* destory：Tomcat关闭或者主动调用destory该类会被销毁





## HttpServlet

一般我们开发时继承HttpServlet类的，该类实现了Servlet的所有方法，并且添加了HTTP协议的处理方法，比Servlet更有优势，我么只需要重写doGet()和doPost()就可以

```java
public class ServletTest extends HttpServlet {

    //处理Get请求
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

	}

    //处理Post请求
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

	}
}
```





### ServletConfig

每个servlet都有这个对象，获取web.xml里面的配置初始化参数

### ServletContext

代表着当前web站点，所有servlet共享该资源

```java
ServletConfig sc = this.getServletConfig();
sc.getInitParameter("name");

ServletContext sct = this.getServletContext();
sct.getInitParameter("name");
```



### HttpServletResponse

返回中文

```java
//设置内部编码及浏览器显示编码
response.setContentType("text/html;charset=UTF-8");
response.getWriter().write("我爱中国");
```

下载

```java
File file = new File("/download/test.png");

FileInputStream fileInputStream = new FileInputStream(file);
ServletOutputStream servletOutputStream = response.getOutputStream();

//设置响应头为下载
response.setHeader("Content-Disposition", "attachment; filename=" + URLEncoder.encode(file.getName(), "UTF-8"));

int length = 0;
byte[] bytes = new byte[1024];
while( (length = fileInputStream.read(bytes)) != -1 ){
	servletOutputStream.write(bytes, 0, length);
}
servletOutputStream.close();
fileInputStream.close();
```

重定向

```java
response.sendRedirect("/404.html");//本地发生的，地址栏发生了变化
```

设置Cookie，Cookie的详细方法后面会介绍

```java
void	addCookie(Cookie cookie)
```





### HttpServletRequest

常用方法

```java
request.setCharacterEncoding("UTF-8"); //设置POST获取参数的编码,get不行
request.getRemoteAddr		//发送请求的IP地址
request.getParameter("userId");		//获取参数
request.getAttribute()   //在request上存放数据，可用于转发
request.getRequestDispatcher.forward(request,response) //实现转发,服务器发生的，本地地址栏没变化
```

获取Cookie

```java
Request.getCookies();  //获得一个数组，里面包含所有对象
Request.getSession();	//获取当前浏览器的Session
```







## Cookie

Http是无状态的，但Cookie会话技术就可以解决这个问题，当浏览器访问服务器时，服务器给浏览器颁发一个Cookie里面记录了相关信息，当浏览器再次访问该服务器时就会带上对应的Cookie，这样服务器就会认识你拉



构造函数

```java
Cookie(java.lang.String name, java.lang.String value)
```

常见方法

```java
String	getName()
String  getValue()
void    setValue(java.lang.String newValue)
int	    getMaxAge()
void    setMaxAge(int expiry)
String  getPath()
void	setPath()
String	getDomain()
void    setDomain(java.lang.String pattern)
```

常见操作

```java
//设置response的编码
response.setContentType("text/html;charset=UTF-8");

//实例化一个Cookie，注意导包是导入javax.servlet.http.Cookie，这个包在tomcat下
//URLEncoder在java.net包下，单参构造函数已废弃
Cookie cookie = new Cookie("name", URLEncoder.encode("我爱中国", "UTF-8"));

//设置过期时间，单位为秒，-1有效到浏览器关闭
cookie.setMaxAge(1000);

//设置额外二级域
cookie.setDomain("a.com");

//请求头添加set-cookie
response.addCookie(cookie);

//设置路径，一般cookie整个站点都可以用，但也可以只限制该地址可用
//cookie.setPath("/ServletConfigTest");

/*************************************/


//返回包含所有对象的数组
Cookie[] cookies = request.getCookies();

for(Cookie cookieLoop : cookies){
	String name = cookieLoop.getName();
	String value = URLDecoder.decode(cookieLoop.getValue(), "UTF-8");
	System.out.println(name + "----" + value);
}
```





## HttpSession

Session也是解决http无状态的一种方式，Session能存放对象，并且Session是存在服务器端的，所以Servlet能共享属于某个浏览器的Session



只有当浏览器访问服务器的**Servlet，并且使用了response.getSession()**才会自动给该浏览器颁发一个带JESSIONID的Cookie，JESSIONID就是唯一标识浏览器Session的id,该cookie默认生命周期为当前浏览器，所以关闭了浏览器Session就会失效



Session的有效期是访问一次就重置，而cookie的是累计，Session存放于服务器内存，超时会自动删除，默认超时为30min



当服务器正常关闭时,还存活着的session(在设置时间内没有销毁) 会随着服务器的关闭被以文件(“SESSIONS.ser”)的形式存储在tomcat 的work 目录下,这个过程叫做Session 的钝化。



**Session的活化和钝化**（[下篇Listener有提及](https://www.cnblogs.com/Howlet/p/12047343.html)）
钝化：服务器关闭时还有正常的Session存在并未超时，就会以文件的形式存储起来

活化：服务器再次开启时，恢复存储起来的Session对象

实现该功能的对象需要实现Serializable接口







方法

```java
long getCreationTime()		//获取Session被创建时间
String getId() 				//获取Session的id
long getLastAccessedTime() 			//返回Session最后活跃的时间
ServletContext getServletContext() 			//获取ServletContext对象
void setMaxInactiveInterval(int var1) 	//设置Session超时时间
int getMaxInactiveInterval() 	//获取Session超时时间
Object getAttribute(String var1) 	//获取Session属性,代替了getValue
Enumeration getAttributeNames() 		//获取Session所有的属性名
void setAttribute(String var1, Object var2) 	//设置Session属性
void removeAttribute(String var1) 		//移除Session属性
void invalidate() 		//销毁该Session
boolean isNew() 		//该Session是否为新的
```



常见操作

```java
HttpSession httpSession = request.getSession();
httpSession.setAttribute("Session", "Howl");
		
System.out.println(request.getSession().getAttribute("Session"));

//当浏览器禁止了Cookie的时候，encodeURL的另一个功能，带上JESSIONID访问即URL地址重写
//通过在其中包含会话ID对指定的URL进行编码，或者，如果不需要编码，则返回不变的URL
//之后服务器端会自动获取该ID
Response.encodeURL(java.lang.String url)
```

验证码

```java
//getCode()获取验证码存入Session
request.getSession.setAttribute("code", getCode());

//然后和requset获取的对比
request.getAttribute(code) == request.getSession.getAttribute("code")
```















****

API参考 [oracle官网文档](https://docs.oracle.com/cd/E17802_01/products/products/servlet/2.5/docs/servlet-2_5-mr2/overview-summary.html "可谷歌浏览器翻译")

Web.xml参考 [思否](https://segmentfault.com/a/1190000011404088?utm_source=tag-newest "思否")

