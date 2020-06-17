> 还是来自某次的回去等通知，当时被问是一脸懵逼的，之前是打war包后扔到Apps下，现在用了Springboot直接打成jar包运行，唯一就是没有设置过Tomcat，对了写过Tomcat结构的笔记，还没整理好发布



### 1. Tomcat的组件

先来看看Tomcat的各个组件，用于理解下面的各个配置作用

![1592271102343](C:\Users\Howl\AppData\Roaming\Typora\typora-user-images\1592271102343.png)

Server服务器监听8005端口，用于关闭Tomcat服务器





### 2. 打印日志乱码

因为logging默认使用utf-8，而我们的windows的日志输出控制台使用系统的GB2312，所以去conf中修改logging的配置编码为GB2312即可



### 3. 修改端口号

在server.xml中，8005监听关闭tomcat命令，8080监听http协议，8009负责监听集群接口，与其他Http服务器连接

```xml
<Connector port="8080" protocol="HTTP/1.1"
               connectionTimeout="20000"
               redirectPort="8443" />
```



### 4. 管理界面

在欢迎页的Server Status点开即可登录，地址为：<http://localhost:8081/manager/status>

tomcat-user.xml中添加

```xml
<role rolename="manager-gui"/>
<user username="tomcat" password="123456" roles="manager-gui"/>
```



### 5. 多域名访问

C:\Windows\System32\driver7etc\host中添加虚拟Ip

```
127.0.0.1 www.vutrial.com
```



### 6. 修改内存

内存模型：堆、栈、静态内存区

在Catalinna.bat中，添加：

```xml
JAVA_OPTS="-server -Xms1024m -Xmx4096m -Xss1024K -XX:PermSize=512m -XX:MaxPermSize=2048m"
                      堆内存     堆内存        栈         永久代             最大永久代

XX:MetaspaceSize=2048m：或将永久代改为元空间
```



### 7. 动静分离

Tomcat + Nginx：前者实现Servlet的处理，后者实现静态资源的访问

**可以在Nginx进行文件压缩处理，分担压缩的压力**



### 8. 设置连接器

连接器可以公用执行器或内部自己编写

```xml
<Connectorport="8080"protocol="org.apache.coyote.http11.Http11NioProtocol"
               maxThreads="1000"             # 最大线程数
               minSpareThreads="100"         # 最大最小空闲线程
               maxSpareThreads="200"		 # 最大最小空闲线程
               acceptCount="900"             # 等待队列阈值，满了会拒绝请求
               connectionTimeout="20000"     # 连接超时
               URIEncoding="UTF-8"
               enableLookups="false"         # 关闭dns解析，提高响应时间，用于反查域名
               redirectPort="8443"
              />
```



### 9. 开启线程池

开启线程池，并在连接器中指定线程池

```xml
<Executor name="tomcatThreadPool" 
          namePrefix="catalina-exec-"
          maxThreads="150" 
          minSpareThreads="4"
          maxIdleTime="60000"  # 空闲时间
          maxQueueSize="Integer.MAX_VALUE" # 排队数目
          />
```





### 9. 设置运行模式

正常是NIO，可以调成APR（异步请求，需要安装软件支持）