### 1. 预备知识

* Tomcat是java语言开发的，所以要先安装JDK才能运行

* Servlet接口和Servlet容器这一整套规范叫做Servlet规范，Tomcat按照Servlet规范的要求实现了Serlvet容器，同时也具有HTTP服务器的功能，我们只需实现一个Servlet，并把它注册到Tomcat（Servlet中），那么具体流程Tomcat会帮我们实现
* 使用到的模式：适配器模式、门面模式
* TCP/IP协议用Socket请求
* 为了区分打包的是一个jar包还是一个应用，改了个名字war
* 部署应用的方式：war直接方法，servlet.xml配置节点`<Context path docBase>`（指明应用）
* Engine--只有一个，里面可以有很多个Host
* Host--虚拟主机（下面可以有多个Context）
* Context--应用
* Wrapper--（某类Servlet，类类型）





### Tomcat架构

让Http服务器与业务类（Servlet）解耦

![1585899550381](C:\Users\Howl\AppData\Roaming\Typora\typora-user-images\1585899550381.png)





### Servlet容器工作流程

当HTTP服务器接收到请求后，将请求信息封装成ServletRequest对象并交给Servlet容器，Servlet容器拿到请求后根据请求的URL和Servlet的映射关系找到对应的Servlet，如果对应的Servlet还没有加载，就用反射创建该实例，并且调用Servlet的init()方法初始化，接着调用Servlet的service处理请求，最后把处理结果封装成ServletResponse对象返回给HTTP服务器，HTTP服务器把响应发送给客服端







### Tomcat核心功能

1. 处理Socket连接，负责网络字节流与Request和Response对象的转化
2. 加载和管理Servlet，以及具体处理Request请求

实现核心功能的组件：Connector连接器和Container容器





![1585900591053](C:\Users\Howl\AppData\Roaming\Typora\typora-user-images\1585900591053.png)

一个服务器有多个Service（即多个站点）





### 连接器Coyote

Coyote是Tomcat的连接器框架的名字，负责协议的解析，是Tomcat服务器提供的外部访问接口，其封装了底层的网络通信（Socket），为Servlet容器提供了统一的接口，使容器与具体的请求协议及IO操作解耦，专注各自的工作。这里注意：Coyote将请求响应转成Request和Response对象，而Servlet容器负责将Request和Response封装成ServletRequest，ServletResponse才能使用





支持的IO模型和协议

![1585901432537](C:\Users\Howl\AppData\Roaming\Typora\typora-user-images\1585901432537.png)

默认使用HTTP1.1协议与NIO模型，BIO之后废除了







### 连接器组件

![1585901971149](C:\Users\Howl\AppData\Roaming\Typora\typora-user-images\1585901971149.png)



EndPoint：负责接收或发送Socket请求或响应，并将请求发送给Processor（TCP/IP）

Processor：将上面发过来的`字节流解析成HTTP协议请求`，并封装成Request对象，发送给Adapter（HTTP/AJP）

Adapter：适配器经典使用，Request变成ServletRequest对象，并发送给容器来处理。因为协议的不同，发送过来的请求信息也不同，所以需要用到适配器，类似于SpringMVC的处理器适配器？？？

ProtocoHandler：Coyote协议接口，通过EndPoint和Processor实现具体协议的处理能力，Tomcat提供了按照协议和IO的实现类：Http11NioProtocol、AjpNioProtocol等，其实就是各种组合，在配置server.xml的时候可以选择具体Coyote协议接口











## 容器Catalina

Tomcat是一个由一系列可配置的组件构成的Web容器，而Catalina是Tomcat的Servlet容器，负责逻辑的处理

![1585903396300](C:\Users\Howl\AppData\Roaming\Typora\typora-user-images\1585903396300.png)





使用组合模式组合那么多组件





Tomcat设计了4种容器分别是Engine引擎、Host虚拟主机、Context站点、Wrapper（Servlet类型）

```xml
<Server>
    <Service>
    	<Connector/>
        <Connector/>
        <Engine>
        	<Host>
            	<Context></Context>
            </Host>
        </Engine>
    </Service>
</Server>
```

管理这些父子容器使用了组合模式，他们统一实现了Container接口，有addChild,setParent等方法。Container接口扩展了LifrCycle接口，LifrCycle用来统一管理各组件生命周期











### Tomcat启动流程

其实startup.bat其实是启用主类BootStrap.class，里面有个main方法









### 请求处理流程

Mapper组件，通过URL映射到对应的Servlet