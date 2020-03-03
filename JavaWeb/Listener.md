## Listener



* Servlet的监听对象有三个：ServletContext，HttpSession，ServletRequest
* 根据监听不同的事件分为：监听对象的创建和销毁，监听对象属性变化，监听Session内的对象
* Listener也是一个java接口，根据事件分类有如下：

****

* 监听对象的创建和销毁

HttpSessionListener、ServletContextListener、ServletRequestListener，**里面方法有：Destroyed()，Initialized()**

* 监听对象属性变化

ServletContextAttributeListener、HttpSessionAttributeListener、ServletRequestAttributeListener，里面方法有：**attributeAdded()，attributeRemoved()，attributeReplaced()**

* 监听Session内的对象

HttpSessionActivationListener、HttpSessionBindingListener，**实现这两个接口并不需要在web.xml文件中注册**，因为是让里面的对象自己监听自己，并且对象需要实现序列化接口







* Web.xml配置

```xml
<listener>
  	<listener-class>listener.ListenerTest</listener-class>
</listener>
```





## 测试



* 监听对象的创建和销毁

```java
public class ListenerTest implements HttpSessionListener, ServletContextListener, ServletRequestListener {

	public void requestDestroyed(ServletRequestEvent arg0) {
		System.out.println("请求销毁了");
	}

	public void requestInitialized(ServletRequestEvent arg0) {
		System.out.println("请求初始化了");
	}

	public void contextDestroyed(ServletContextEvent arg0) {
		System.out.println("容器销毁了");
	}

	public void contextInitialized(ServletContextEvent arg0) {
		System.out.println("容器初始化了");
	}

	public void sessionCreated(HttpSessionEvent arg0) {
		System.out.println("Session创建了");
	}

	public void sessionDestroyed(HttpSessionEvent arg0) {
		System.out.println("Session销毁了");
	}
```

```xml
容器初始化了
请求初始化了
Session创建了
# Session是在内存中的，所以看不到销毁
请求销毁了
容器销毁了
```





* 监听对象属性变化

属性变化

```java
//增加
httpSession.setAttribute("Session", "Howl");
	
//替换
httpSession.setAttribute("Session", "Howlet");
	
//移除
httpSession.removeAttribute("Session");
```

```java
public class ListenerTest2 implements HttpSessionAttributeListener {

	public void attributeAdded(HttpSessionBindingEvent arg0) {
		System.out.println("Session属性增加了");
	}

	public void attributeRemoved(HttpSessionBindingEvent arg0) {
		System.out.println("Session属性移除了");
	}

	public void attributeReplaced(HttpSessionBindingEvent arg0) {
		System.out.println("Session属性替换了");
	}
}    
```

```xml
Session属性增加了
Session属性替换了
Session属性移除了
```





* 监听Session内的对象

```java
public class UserBean implements Serializable, HttpSessionActivationListener, HttpSessionBindingListener {

	private String name;
	
	public UserBean(String name) {
		this.name = name;
	}

	public void valueBound(HttpSessionBindingEvent arg0) {
		System.out.println("绑定了对象");
	}

	public void valueUnbound(HttpSessionBindingEvent arg0) {
		System.out.println("移除了对象");
	}

	public void sessionDidActivate(HttpSessionEvent arg0) {
		HttpSession httpSession = arg0.getSession();
        System.out.println("活化了");
	}

	public void sessionWillPassivate(HttpSessionEvent arg0) {
		HttpSession httpSession = arg0.getSession();
        System.out.println("钝化了");
	}
}    
```

```java
UserBean user = new UserBean("Howl");
	
request.getSession().setAttribute("user", user);
request.getSession().removeAttribute("user");
```

```web
绑定了对象
<!-- 关闭服务器 -->
钝化了
<!-- 开启服务器 -->
活化了
移除了对象
```





## 作用

统计用户人数

监听用户上线与退出