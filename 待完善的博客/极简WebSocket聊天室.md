> 最近看到了WebSocket，不免想做些什么小功能，然后就选择了聊天室，首先当然先介绍什么是WebSocket



## 1. WebSocket







为什么需要WebSokcet？解决HTTP协议的缺陷：通信只能由客户端发起。

而WebSokcet可以主动向客户端推送消息，是全双工通讯，只需要建立一次连接，就可以一直保持连接状态。

其建立在TCP上，服务器端实现比较容易

协议标识符为ws：`ws://www.baidu.com`

实现了浏览器与服务器全双工的通信协议

java中的socket是长连接，不通信就浪费资源





















## 2. 实现

这个代码是极简的，适合入门理解。WebSocket是一套已经规范好的标准的API，Tomcat、Spring等都实现了这套API，下面笔者用Springboot来操作



#### 2.1 导入依赖

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-websocket</artifactId>
</dependency>
```



#### 2.2 目录结构

![1587275272979](C:\Users\Howl\AppData\Roaming\Typora\typora-user-images\1587275272979.png)



#### 2.3 ServerConfig

```java
@Configuration  // 配置类，用来注册服务
public class serverConfig {
    @Bean  // 返回的bean会自动注册有@ServerEndpoint注解声明的endpoint
    public ServerEndpointExporter serverEndpointExporter() {
        return new ServerEndpointExporter();
    }
}
```



#### 2.4 MyServer

重点就在这里，先说明一下：

* Endpoint为端点，可理解为服务器接收端，WebSocket是端对端的通信
* Session为会话，表示两个端点间的交互，要和cookie和session这个区分开来
* 方法上的注解：@OnOpen表示成功建立连接后调用的方法，其余类推

```java
@Component // 注解虽然单例，但还是会创建多例
@ServerEndpoint(value = "/wechat/{username}")  // 声明为服务器端点
public class MyServer {

    // 成员变量
    private Session session;
    private String username;

    // 类变量
    // 类变量涉及同步问题，用线程安全类
    // 可以用<String room,<String username,MyServer> >来形成房间
    private static AtomicInteger onlineCount = new AtomicInteger(0);
    private static ConcurrentHashMap<String, MyServer> map = new ConcurrentHashMap<>();

    // 连接
    @OnOpen
    public void onOpen(@PathParam("username") String username, Session session) throws IOException {
        this.session = session;
        this.username = username;
        map.put(username, this);
        addOnlineCount();
        sendMessageAll(username + "加入了房间，当前在线人数：" + getOnlineCount());
    }

    // 关闭
    @OnClose
    public void onClose() throws IOException {
        subOnlineCount();
        map.remove(username);
        sendMessageAll(username + "退出了房间，当前在线人数：" + getOnlineCount());
    }

    // 发送错误
    @OnError
    public void onError(Session session, Throwable error) {
        error.printStackTrace();
    }

    // 默认群发
    @OnMessage
    public void onMessage(String message) throws IOException {
        sendMessageAll(username + "：" + message);
    }

    // 群发
    private void sendMessageAll(String message) throws IOException {
        for (MyServer value : map.values()) {
            value.session.getBasicRemote().sendText(message);    // 阻塞式
            // this.session.getAsyncRemote().sendText(message);  // 非阻塞式
        }
    }

    // 私发
    private void sendMessageTo(String message, String to) throws IOException {
        MyServer toUser = map.get(to);
        toUser.session.getAsyncRemote().sendText(message);
    }

    public static synchronized int getOnlineCount() {
        return onlineCount.get();
    }

    public static synchronized void addOnlineCount() {
        MyServer.onlineCount.getAndIncrement();
    }

    public static synchronized void subOnlineCount() {
        MyServer.onlineCount.getAndDecrement();
    }
}
```



#### 2.5 index.html

笔者写的前端不太靠谱，知道什么意思即可~

```html
<!doctype html>
<html lang="en">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport"
            content="width=device-width, user-scalable=no, initial-scale=1.0, maximum-scale=1.0, minimum-scale=1.0">
        <meta http-equiv="X-UA-Compatible" content="ie=edge">
        <title>登录页</title>
    </head>

    // 输入名字，url传参省事
    <body>
        <label for="username">Username:</label>
        <input id="username" type="text" placeholder="请输入昵称">
        <button id="submit" >ENTER</button>
    </body>

    <script>
        var submit = document.getElementById('submit');
        submit.addEventListener('click',function(){
            window.location.href = 'homepage.html?username=' + document.getElementById('username').value;
        })
    </script>
</html>
```



#### 2.6 homepage.html

```html
<!doctype html>
<html lang="en">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, user-scalable=no, initial-scale=1.0, maximum-scale=1.0, minimum-scale=1.0">
        <meta http-equiv="X-UA-Compatible" content="ie=edge">
        <title>房间</title>
    </head>

    <body>
        <button onclick="wsClose()">退出房间</button>
        <br/><br/>
        <div id="showMessage"></div>
        <br/><br/>
        <input id="sendMessage" type="text"/>
        <button onclick="sendMessage()">发送消息</button>
    </body>

    <script>
        // 获取url参数的昵称
        function getQueryVariable(variable) {
            var query = window.location.search.substring(1);
            var vars = query.split("&");
            for (var i=0;i<vars.length;i++) {
                var pair = vars[i].split("=");
                if(pair[0] == variable){return pair[1];}
            }
            return(false);
        }
        var conn = "ws://localhost:8080/wechat/" + getQueryVariable("username");

        // webSocket连接
        var ws = new WebSocket(conn);

        // 连接错误要做什么呢？
        ws.onerror = function () {
            showMessageInnerHTML("发生未知错误错误");
        }
        // 客户端连接需要干什么呢？
        ws.onopen = function () {
            showMessageInnerHTML("--------------------------");
        }

        // 客户端关闭需要干什么呢？
        ws.onclose = function () {
            showMessageInnerHTML("退出了当前房间");
        }

        // 收到消息
        ws.onmessage = function (even) {
            showMessageInnerHTML(even.data);
        }

        // 关闭浏览器时
        window.onbeforeunload = function () {
            ws.wsClose();
        }

        // 网页上显示消息
        function showMessageInnerHTML(msg) {
            document.getElementById('showMessage').innerHTML += msg + '<br/>';
        }

        // 发送消息
        function sendMessage() {
            var msg = document.getElementById('sendMessage').value;
            ws.send(msg);
            document.getElementById('sendMessage').value = '';
        }

        // 关闭连接
        function wsClose() {
            ws.close();
        }
    </script>
</html>
```



#### 2.7 截图

不想弄前端，凑合着看吧



![1587275412821](C:\Users\Howl\AppData\Roaming\Typora\typora-user-images\1587275412821.png)

![1587275458990](C:\Users\Howl\AppData\Roaming\Typora\typora-user-images\1587275458990.png)













------

参考

tomcat、Spring官网均有简介及API的详细介绍。推荐使用后者，后者符合spring规范而且更加优雅

<http://tomcat.apache.org/tomcat-9.0-doc/websocketapi/index.html>

<https://spring.io/guides/gs/messaging-stomp-websocket/>