## Filter 过滤器



过滤器是实现了Filter接口的一个java类，可以处理request和response，它有下面三种方法

```java
public void destroy() {
}

public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
}

public void init(FilterConfig filterConfig) throws ServletException {
}
```



**工作区间**

![1576370968514](C:\Users\Howl\AppData\Roaming\Typora\typora-user-images\1576370968514.png)







## 操作流程



实现Filter接口

```java
//注解配置（@WebFilter(filterName="FilterTest",urlPatterns="/*"))
//但我这里用xml配置
public class FilterTest implements Filter {

public void destroy() {
}

public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
		throws IOException, ServletException {
	
	//需要执行的逻辑代码在这里
	System.out.println("我是首个Filter");
	
	//表示放行
	chain.doFilter(request, response);
}

public void init(FilterConfig filterConfig) throws ServletException {
}
```

web.xml配置

```xml
<filter>
	<filter-name>FilterTest</filter-name>
	<filter-class>filter.FilterTest</filter-class>
</filter>
<filter-mapping>
	<filter-name>FilterTest</filter-name>
	<url-pattern>/*</url-pattern>
</filter-mapping>
```

* **xml配置的顺序就是Filter的执行顺序，而注解方式是通过匹配地址的自然顺序执行的，这是注解的一个遗憾**



**其中chain.doFilter表示放行，配置多个过滤器时会把这些过滤器放到FilterChain里，调用它就会带上request,和response，自动执行下一个过滤器**



**这样的调用属于链式调用，类似于递归，调用完后还会回到该函数继续执行下面的代码**



**chain.doFilter如果没有下一个过滤器就访问资源**









## 作用 



过滤POST乱码

```java
@Override
public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
		throws IOException, ServletException {
	
	//ServletRequest功能不够强大，需要强转成HttpSerlvet
    HttpServletRequest httpServletRequest = (HttpServletRequest) request;
    HttpServletResponse httpServletResponse = (HttpServletResponse) response;
	
    //设置编码
    httpServletRequest.setCharacterEncoding("UTF-8");
    httpServletResponse.setContentType("text/html;charset=UTF-8");
	
	//表示放行
	chain.doFilter(request, response);
}
```



过滤Get乱码（使用到了 [装饰者模式](https://www.cnblogs.com/Howlet/p/12020604.html))

装饰类

```java
class MyRequest extends HttpServletRequestWrapper {

    private HttpServletRequest request;

    public MyRequest(HttpServletRequest request) {
        this.request = request;
    }

    public String getParameter(String name) {
        String value = this.request.getParameter(name);

        if (value == null) {
            return null;
        }

        //如果不是get方法的，直接返回就行了
        if (!this.request.getMethod().equalsIgnoreCase("get")) {
            return null;
        }

        try {

            //进来了就说明是get方法，把乱码的数据
            value = new String(value.getBytes("ISO8859-1"), this.request.getCharacterEncoding());
            return value ;

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();

            throw new RuntimeException("不支持该编码");
        }
    }
}
```

实现

```java
MyRequest myRequest = new MyRequest(httpServletRequest);
//装饰增加新功能
```

**参考[Java3y](https://mp.weixin.qq.com/s?__biz=MzI4Njg5MDA5NA==&mid=2247484862&idx=3&sn=f53c22e52dae6a0b315d84fdc4553c05&chksm=ebd744bfdca0cda9c29a60d052b71edcaf0b7b5d0e23630af045a36f8927776504cc85338737###rd)**










