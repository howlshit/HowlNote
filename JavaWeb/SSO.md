> 今天学的内容太南了（主要太菜），为了找回自信去瞅了下吃灰的备忘录，还真发现些内容，下期预定第三方登录



## 1. SSO

单点登录（Single Sign On），在多个互相信任的Web站点中，只要登录过其中一个，那么其他的站点都可以直接访问而不用登录。举个栗子：淘宝和天猫是两个Web站点，登录淘宝之后就不用登录天猫而可以互相访问。



为什么需要单点登录？

在大型系统架构中，其往往有很多的子站点，各个站点部署在不同的服务器上。那么用户在访问不同站点时就需要逐一登录，用户体验不友好。而且每个站点都需要做登录模块，业务冗余，重复性太高。单点登录就是解决这些问题的，下面说明主要主要是思想，而实现是其次，因为实现方式有多种

![1587023610120](C:\Users\Howl\AppData\Roaming\Typora\typora-user-images\1587023610120.png)









## 2. 回顾单系统登录

HTTP是无状态的，我们可以用Cookie和Session来实现会话跟踪。一般登录功能的流程：

* 用户输入账号密码正确，用户信息存储在Session中（Session存储在当前Tomcat服务器上）
* Tomcat服务器根据当前Session发送含唯一JESSIONID的Cookie给浏览器自动保存
* 下次浏览器再次访问会带上该Cookie，服务器识别JESSIONID对应的Session来跟踪会话



实现单点登录要解决的是Session共享问题，以及Cookie跨域









## 3. 单点登录简单实现

* 最简单实现：[JWT](<https://www.cnblogs.com/Howlet/p/12293989.html>)（单点登录的友好使者）



* 借助Redis实现Session共享

![1587083147627](C:\Users\Howl\AppData\Roaming\Typora\typora-user-images\1587083147627.png)

补充：Session是服务器实现的一种机制，可以用Redis来模拟其功能



**登录站点业务层实现**

这个站点的功能在于给其他站点提供登录服务

```java
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    UserMapper userMapper;

    // 1. 正常登录流程
    public String login(String username, String password) {

        // 验证账号密码
        User user = userMapper.find(username, password);
        if (user == null) {
            return ResponseHelper.error("0001","账号或密码错误",null);
        } else {
            user.setPassword(null);
        }

        // 在Redis中存入Session
        String token = UUID.randomUUID().toString().replace("-", "");
        Jedis jedis = JedisUtil.getResource();
        jedis.setex("userToken:" + token, 60 * 60 * 24, JSON.toJSONString(user));
        jedis.close();
        return ResponseHelper.success("0002","登录成功",JSON.toJSONString(token));
    }
    
    // 2. Redis中获取Session
    public String getSessionByToken(String token){
        Jedis jedis = JedisUtil.getResource();
        String redisSession = jedis.get("userToken:" + token);
        jedis.close();
        return ResponseHelper.success("0003","获取成功",JSON.toJSONString(redisSession));
    }
}
```

登录成功后返回token，客户端将该token保存起来，下次访问带上即可



**其他站点**

其他站点需要登录时，利用HttpClient去登录站点登录，返回token保存到Cookie中

```java
// LOGIN_WEB_URL登录站点的请求地址
public String login(String username, String password, HttpServletResponse response) {

    // 请求参数，与HttpClient登录站点
    Map<String, String> param = new HashMap<>();
    param.put("username", username);
    param.put("password", password);
    String token = HttpClientUtil.doPost(LOGIN_WEB_URL, param);
    
    // 写入Cookie
    Cookie cookie = new Cookie("userToken", token);
    cookie.setMaxAge(60 * 60 * 24);
    response.addCookie(cookie);

    return ResponseHelper.success(0002, "登录成功", null);
}
```

登录拦截器，拦截没有token

```java
public class LoginHandlerInterceptor implements HandlerInterceptor {
    private BeanContext JedisUtil;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        // 1. 获取Token,然后去Redis查Session
        boolean auth = false;
        Cookie[] cookies = request.getCookies();
        for (Cookie value : cookies){
            if (value.getName().equals("userToken")){
                String token = value.getValue();
                Jedis jedis = JedisUtil.getResource();
                String redisSession = jedis.get("userToken:" + token);
                if(redisSession != null){
                    // request.getSession().setAttribute("user",redisSession);
                    auth = true;
                }
            }
        }

        // 2. Redis中没有Session，跳转本站登录页面
        if(!auth){
            request.getRequestDispatcher("/user/login.html").forward(request,response);
            return false;
        }
        return true;
    }
}

@Configuration
public class MyMvcConfig implements WebMvcConfigurer {
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 已经boot做好了静态资源放行
        registry.addInterceptor(new LoginHandlerInterceptor())
                .addPathPatterns("/**")
                .excludePathPatterns("/","/user/login");

    }
}
```

**注意：为了精简，部分Controller层内容放入Service层操作**



流程总结：

* 进入站点1时，没有Cookie则跳转登录站点，登录后将用户信息存入Redis（模拟Session），生成Token（模拟JESSIONID），返回给浏览器保存
* 浏览器从上面接收到Token之后写入cookie或LocalStorage或SessionStorage，刷新页面，访问时带上Token即可（写入Token操作由前端进行，前后端分离）
* 进入站点2时，发现有带上Token，查询Redis后有对应的Session放行。当想要用户信息时，带上Token去Redis查询
* 上面步骤其实在用Redis模拟Session的机制。若熟悉Session机制，完全可重写request.getSession()，用包装者模式，增加其向Redis获取用户信息的功能，实现Session共享



至此Session共享问题解决了，共享实现还有**但不建议**：Session绑定（Nginx的Hash_ip绑定服务器），Tomcat集群Session复制



Cookie由于有跨域问题，同域下可以设置domain，不同域则无法携带，但不同域可以用token存放到LocalStorage（永久）或SessionStorage（会话级别），访问时带上token，即验证token即可（跨域可用协议解决或Nginx反向代理）



>补充一个重复登录：用户每次登录然后在共享Session中更新一个随机数（singleToken），此singleToken也让客户端保存为Cookie，之后的访问对比自己Cookie中的singleToken与共享Session中的是否一致，不一致则有人登录了，踢出后者。踢出动作可以服务器端发出（长连接），或者客户端主动刷新对比。









## 4. CAS机制

Central Authentication Service，将登录功能抽取出来单独做一个认证中心，此后的所有相关功能都去认证中心操作，这里提供思路。阿里云的控制台登录，跳转登录再跳转回来的



* 用户访问需登录的站点1，重定向至认证中心（带上自己访问站点1的url）。若在认证中心也没有登录，跳转登录页面登录，登陆后客户端与认证中间建立全局会话（Cookie和Session），并生成一个ST（Service Ticket），然后带上该ST重定向至站点1的url
* 回到站点1之后，站点1拿这个ST去认证中心验证，正确则建立局部会话（Session），那么至此站点1是登录状态了。
* 用户这次访问需登录的站点2，重定向至认证中心（带上自己访问站点2的url），因为已经和认证中心建立全局会话，所以认证中心直接返回ST重定向回站点2，而站点2携带ST去认证中心验证，正确则建立局部会话

这里的局部会话关闭浏览器则会失效，下次再次访问还是需要 重定向至认证中心返回ST，带上ST去验证再建立局部会话

