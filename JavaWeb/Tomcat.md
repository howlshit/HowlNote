## Tomcat

Tomcat是一个开源的Web 应用服务器。是Servlet容器，能运行.class文件，也是Jsp容器，能处理动态资源，还是Web服务器也就是说能处理Hmlt,Css等，Tomcat启动时读取web.xml文件里的信息，加载对应类，然后反射的实例化他们





## 目录结构

![1577841131423](C:\Users\Howl\AppData\Roaming\Typora\typora-user-images\1577841131423.png)



#### 1. bin

存放tomcat的二进制可执行命令，比如启动关闭服务器

![1577841506859](C:\Users\Howl\AppData\Roaming\Typora\typora-user-images\1577841506859.png)



#### 2. conf

配置文件的目录

![1577841599615](C:\Users\Howl\AppData\Roaming\Typora\typora-user-images\1577841599615.png)

其中



**server.xml**是服务器配置文件，如端口号，虚拟目录

```xml
<!-- 默认8080 -->
<Connector port="8080" protocol="HTTP/1.1"
               connectionTimeout="20000"
               redirectPort="8443" />
```

```xml
<!-- Host标签下添加如下代码 -->
<!-- path是映射的url,docBase是对应盘符地址 -->
<Context path="/webURL" docBase="D:\web"/>
```



**web.xml**是默认站点配置

```xml
<!-- The mapping for the default servlet -->
<servlet-mapping>
    <servlet-name>default</servlet-name>
    <url-pattern>/</url-pattern>
</servlet-mapping>

<!-- The mappings for the JSP servlet -->
<servlet-mapping>
    <servlet-name>jsp</servlet-name>
    <url-pattern>*.jsp</url-pattern>
    <url-pattern>*.jspx</url-pattern>
</servlet-mapping>

<!-- 过期时间/min -->
<session-config>
    <session-timeout>30</session-timeout>
</session-config>

<!-- 各种支持的文件类型 -->
```



**context.xml**全部站点的统一配置，一般不用，都是用各个站点自己的配置信息

```xml
<WatchedResource>WEB-INF/web.xml</WatchedResource>
<WatchedResource>WEB-INF/tomcat-web.xml</WatchedResource>
<WatchedResource>${catalina.base}/conf/web.xml</WatchedResource>
```



#### 3. lib

各种加载所需的jar包

![1577842438376](C:\Users\Howl\AppData\Roaming\Typora\typora-user-images\1577842438376.png)





#### 4. logs

日志文件，记录Tomcat启动和关闭的信息，及异常信息

![1577842531586](C:\Users\Howl\AppData\Roaming\Typora\typora-user-images\1577842531586.png)





#### 5. temp

存放临时文件





#### 6. webapps

存放站点，就是你的网站

![1577842871410](C:\Users\Howl\AppData\Roaming\Typora\typora-user-images\1577842871410.png)





#### 7.work

工作目录，即运行时生成的文件，最终运行的文件都在这里，比如放jsp被访问后生成对应的server文件和.class文件（现在我没用过JSP了）

