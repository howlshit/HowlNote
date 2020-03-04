> 继上一篇跨域博客，这次来介绍Nginx，使用Nginx也可以解决跨域问题，现在流行的VUE前端框架就常搭配Nginx食用









## 1. 简介

Nginx (engine x) 是一个高性能的HTTP和反向代理web服务器，同时也提供了IMAP/POP3/SMTP服务。其特点是占用内存少，支持热部署，并发能力强，专为高并发而优化，事实上Nginx的并发能力在同类型的网页服务器中表现最好（来自百度百科）



看了那么多官方介绍，总结一句话就是：Nginx是一个高性能的服务器（和Tomcat类似）



在学习一项新技术之前，我们要先知道为什么去学他？他有什么作用？    笔者学习Nginx是为了解决跨域问题，然后在学习的过程中扩展认识其他的功能，这样才有积极的意义。那么Nginx到底有什么作用呢？



其作用有：**反向代理**、**负载均衡**、**动静分离**、**高可用**

这些专业名词在后面会一一说明，现在让心里有个底，看完之后就会豁然开朗的








## 2. 下载和安装

使用Nginx当然免不了下载和安装，其分为Windows和Linux版，笔者在本地测试没有用到服务器，所以就以Windows版为例



#### 第一步下载：
进入 [Nginx官网](http://nginx.org/)，然后点击右下角的 [Download](http://nginx.org/en/download.html)，下载目前（2020-2-20）最新的稳定Windows版

![1582212640021](C:\Users\Howl\AppData\Roaming\Typora\typora-user-images\1582212640021.png)





#### 第二步安装：

解压下载的文件到想要安装的目录即可，没错解压即可用（绿色免装版），而且大小只有1.62MB（轻量型服务器），但不能因为其大小而小觑性能，业界如百度、京东、腾讯、网易、淘宝等都有使用Nginx

![1582213556293](C:\Users\Howl\AppData\Roaming\Typora\typora-user-images\1582213556293.png)









## 3. 目录与启动

学习之前来了解一下Nginx的目录结构与启动方式，不然学完之后连软件都不知道怎么打开



#### 目录结构

![1582213486544](C:\Users\Howl\AppData\Roaming\Typora\typora-user-images\1582213486544.png)

打开Nginx安装目录之后就会有以上的文件夹及文件，其分别是：配置文件、核心模块、说明文档、默认页面、日志文件、临时目录以及启动图标





#### 启动

双击上图的nginx.exe，然后有控制台一闪而过就表示启动了，打开浏览器输入localhost回车就会有以下熟悉的欢迎页

![1582214199144](C:\Users\Howl\AppData\Roaming\Typora\typora-user-images\1582214199144.png)





#### 守护进程

即是一类在后台运行的特殊进程，用于执行特定的系统任务

![1582214353039](C:\Users\Howl\AppData\Roaming\Typora\typora-user-images\1582214353039.png)









## 4. 常见命令

和普通软件不一样，Nginx在windows也是需要使用命令行的



```
start nginx			启动nginx
nginx -s stop		快速关闭，不保存相关信息
nginx -s quit		平稳关闭，保存相关信息
nginx -s reload		重新加载配置文件
nginx -c filename	指定配置文件
```

windows下需要加上前缀 eg：`./nginx.exe -s stop `







## 5. 配置文件

config文件夹中nginx.conf配置文件分为三块：



第一块：从开头到event块之间，主要设置影响nginx服务器整体运行的配置



第二块：设置服务器与用户的网络连接



第三块：代理、缓存、日志和第三方模块都在这里（里面又有个server块）



```xml
# 全局块
worker_processes  1;

# event块
events {
   XXXXX
}

# http块
http {
	XXXXX
	
	# server块
    server {
		XXX
    }
}
```











## 6. 反向代理

要说明反向代理就少不了正向代理，正向代理隐藏真实客户端，反向代理隐藏真实服务端。举个例子：



**正向代理：**学校食堂有意见箱，我们（客户端）可以往里面投递自己的意见，食堂的工作人员就可以在意见箱里面获取意见，意见箱作为代理，让食堂工作人员（服务器）不知道是哪位同学投递的请求（隐藏真实客户端）



**反向代理：**我们（客户端）闲下来了去吃云吞面，只需跟服务员说要一份云吞面即可，我们作为客户端是不知道哪位厨师（服务器）为我们准备的，而是通过请求服务员（代理）实现沟通厨师（服务器）的功能（隐藏了真实后台）



****



我们要实现客户端访问nginx（127.0.0.1:80）就被代理到Tomcat（127.0.0.1:8080）的功能，即访问127.0.0.1:80就是访问127.0.0.1:8080，这里实现了跨域功能。



**第一步：**在nginx.conf的http块里的server块中的localtion添加一个配置

```xml
location / {
	proxy_pass   http://127.0.0.1:8080;  #代理的意思
}
```



**第二步：**开启Tomcat，并写下一些html页面，使其访问的时候显示访问的端口号



**第三步：**测试访问（地址栏若是80端口即会省略），实现了转发

![1582253620917](C:\Users\Howl\AppData\Roaming\Typora\typora-user-images\1582253620917.png)













## 7. 负载均衡

在服务器集群中（即多台服务器），将原先集中请求单台服务器转化为将请求分发到多台服务器上，从而实现均衡服务器的负荷（负载均衡）的功能

![1582250573513](C:\Users\Howl\AppData\Roaming\Typora\typora-user-images\1582250573513.png)





****



在浏览器输入127.0.0.1:80实现把请求均衡到服务器集群上，这里开三台Tomcat服务器，只是端口号不同（8080，8081，8082）



**第一步：**

在nginx.conf的http块中添加一个配置

```xml
#集群操作，在http节点中间,不要用localhost-慢
upstream tomcatCluster {
	server 127.0.0.1:8080 weight=1;
	server 127.0.0.1:8081 weight=1;
	server 127.0.0.1:8082 weight=1;
}
```

在在nginx.conf的server块中添加一个配置

```xml
location / {
	proxy_pass   http://tomcatCluster;
}
```





**第二步：**开启Tomcat，并写下一些html页面，使其访问的时候显示各自访问的端口号



**第三步：**测试访问（首先要重载配置`nginx.exe -s reload`）

<video src="C:\Users\Howl\Videos\Captures\localhost_index.html - Google Chrome 2020-02-21 11-03-48.mp4"></video>



> 负载均衡默认是轮询（某个服务器down掉会自动剔除），可以设置权重、ip_hash、第三方











## 8. 动静分离

为了加快网站的解析速度，可以把动态页面和静态页面交由不同的服务器来解析。专业的功能让专业的软件来实现，Nginx处理静态文件极佳，可以让静态文件交由Nginx来处理，不用Tomcat服务器来处理从而不损耗其性能又提高了网站的解析速度，也就是CDN处理





![1582256357674](C:\Users\Howl\AppData\Roaming\Typora\typora-user-images\1582256357674.png)





**第一步：**

在nginx.conf的http块中添加配置

```xml
#8081、8082处理动态资源
upstream tomcatCluster {
	server 127.0.0.1:8081 weight=1;
	server 127.0.0.1:8082 weight=1;
}
```

在server中添加配置

```xml
# 静态资源代理到8080端口
location ~ \.(html|js|css|images|png|gif)$ {
	proxy_pass   http://127.0.0.1:8080;
}
	
# 动资源代理到集群服务器上（8081、8082）
location / {
	proxy_pass   http://tomcatCluster;
}
```



**第二步：**启动各个服务器





**第三步：**测试

<video src="C:\Users\Howl\Videos\Captures\localhost_index.html - Google Chrome 2020-02-21 11-36-56.mp4"></video>









## 9. Session

集群中Session可以在不同服务器上，解决常见的解决方法是Session共享，即用redis存储Session，集群的服务器向redis获取Session