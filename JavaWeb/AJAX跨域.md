> 本文是一篇简单介绍跨域的博文，其入门是十分简单的，虽然我们是后端攻城狮，但也要了解前端的部分知识，这样前后端分离才更有把握





## 1. 什么是跨域

发起请求的url中**协议、域名、端口号**三者任意一个与当前页面url中的不同就是跨域，跨域针对的是AJAX，即跨域问题限制了AJAX访问不同网站时的操作







## 2. 是谁限制了AJAX

为了用户的安全性，是浏览器的同源策略限制了AJAX操作，同源策略分为：



**DOM 同源策略：**不同域之间不能操作DOM对象

**XMLHttpRequest 同源策略：**不同域之间不能操作Cookie





假如没有浏览器的同源策略：

一个钓鱼网站用了<frame>标签把登录QQ邮箱的网页嵌套进来，那么当用户来到此钓鱼网站时输入密码账号登录时是真的能登录成功并操作的，但此时钓鱼网站也能获取到用户密码（DOM不同源）



用户登录了QQ邮箱其Cookie存在了本地，当其访问钓鱼网站并带上该Cookie时，钓鱼网站就能获取到Cookie，利用Cookie来伪造登录（XMLHttpRequest 不同源），而从实现CSRF攻击









## 3. 如何解决跨域问题

使用CORS方法来解决跨域问题，也可以使用Nginx（这里不做说明），CORS是一个W3C标准，全称是"跨域资源共享"（Cross-origin resource sharing），它允许浏览器向跨源服务器，发出XMLHttpRequest请求，从而克服了AJAX只能同源使用的限制，**注意：**需要浏览器和服务器的支持，现在市面上的浏览器都支持CORS，并且是后台自动使用，是感觉不到的。跨域请求可以分为简单请求和非简单请求





### 3.1. 简单请求

简单请求需要在下面的条件内

```xml
请求方法：HEAD、GET、POST

请求头信息：
Accept
Accept-Language
Content-Language
Last-Event-ID
Content-Type：只限于三个值application/x-www-form-urlencoded、multipart/form-data、text/plain
```



**基本流程**

* 发起简单跨域请求后，浏览器会在头部增加一个字段：`Origin: http://www.howl.com`
* 服务器接收到请求后，判断是否在许可范围内。若在，则服务器的回应会添加多几个字段`Access-Control-Allow-Origin`、`Access-Control-Allow-Credentials`、`Access-Control-Expose-Headers`，分别表示服务可接收的跨域地址，是否允许发送Cookie，是否允许添加额外请求头。若不在，服务器正常响应，但是没有上述的响应头，从而报错





### 3.2 非简单请求

非简单请求是对服务器有特殊要求的请求，比如请求方法是PUT或DELETE。非简单请求会在请求之前发送预检请求（OPTIONS，询问是否支持本次操作），只有服务器支持本次请求AJAX才会才会继续请求，否则报错。一旦服务器通过了"预检"请求，以后每次浏览器正常的CORS请求，就都跟简单请求一样，会有一个`Origin`头信息字段。服务器的回应，也都会有一个`Access-Control-Allow-Origin`头信息字段。















### 3.3 后端服务器

只需配置个全局Filter即可



```java
@WebFilter(filterName="OriginFilter",urlPatterns="/*")
public class FilterTest implements Filter {

	@Override
	public void destroy() {
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		
		// 存放许可的地址
		TreeSet originSet = new TreeSet();
		originSet.add("www.howl.com");
		
		// 存放许可的请求头
		TreeSet headersSet = new TreeSet();
		headersSet.add("X-howl-Header,X-howlet-Header");
		
		// 强转增强功能
		HttpServletRequest req = (HttpServletRequest) request;
		HttpServletResponse res = (HttpServletResponse) response;
        
		// 获取跨域请求地址
		String origin = req.getHeader("Origin");
		
		// 若在许可范围内
        if(originSet.contains(origin)){
        	res.addHeader("Access-Control-Allow-Origin", origin);
        	res.addHeader("Access-Control-Allow-Credentials", "true");
        }
        
        // 获取非简单请求的头
        String headers = req.getHeader("Access-Control-Request-Headers");
        
        // 若在许可范围内
        String[] arr = headers.split(",");
        for(String value : arr){
        	if(!headersSet.contains(value.toUpperCase())){
        		break;
        	}
        	res.addHeader("Access-Control-Allow-Headers", headers); 
        }

        // 预检许可的方法
        res.addHeader("Access-Control-Allow-Methods", "*");
        
		//表示放行
		chain.doFilter(request, response);
	}

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {

	}
}
```









