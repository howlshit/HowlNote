## 1. Dubbo是什么

Dubbo是一个RPC框架，简单来说就是实现不同主机间的功能调用的框架，其中需要建立网络连接以及参数传递需要的序列化操作，这二者影响了RPC框架的速度，[RPC介绍](https://www.cnblogs.com/Howlet/p/12367745.html)





![dubbo-architecture-roadmap](C:\Users\Howl\Desktop\dubbo-architecture-roadmap.jpg)

<center>From Dubbo文档</center>



我们开发的架构发展：

* 单一应用架构：站点流量小，只需一个所有功能都部署在一起的应用，此时关键用ORM框架可简化开发
* 垂直应用架构：访问量增大，单一应用的集群部署带来的加速度减缓，此时关键将应用拆分成互不相干的几个应用独立部署到不同机器上，此时应用上了MVC框架
* 分布式服务架构：垂直应用增多，应用之间存在交互，那么应将交互的核心业务抽取出来作为独立的服务形成服务中心，此时关键用提高业务复用及整合的分布式服务框架
* 流动计算架构（SOA）：服务越来越多，不同服务的访问压力和占有资源容易出现浪费，此时关键增加一个调度中心基于访问压力实时管理集群容量，提高集群利用率（哪个服务压力大就对应就增加几台机器）













## 2. 架构流程

![architecture](C:\Users\Howl\Desktop\architecture.png)



0. RPC框架容器启动，然后初始化服务

1. 提供者把服务注册到注册中心
2. 消费者向注册中心订阅所需服务
3. 若服务功能有所改变，那么会通过长连接推送给消费者
4. 远程调用（同步的）
5. 调用信息，调用时间会定时异步发给监控中心













## 3. 环境搭建



### 3.1 注册中心

官方推荐使用 [ZooKeeper](https://www.cnblogs.com/Howlet/p/13837546.html) 来作为注册中心



### 3.2 监控中心

Dubbo的监控中心是前后端分离的，前端Vue，后端SpringBoot

具体可参考 [官方文档部署](http://dubbo.apache.org/zh-cn/docs/admin/introduction.html)

![1603362429167](C:\Users\Howl\AppData\Roaming\Typora\typora-user-images\1603362429167.png)



**笔者在本地搭建全部环境，然后端口弄了一团糟，这里需要注意：**

* ZooKeeper启动占用2181，8080端口
* 监控中心的后端SpringBoot默认也是8080，需要自行修改，比如：server.port=8088
* 监控中心的前端Vue默认代理8080，自行修改vue.config.js，要和后端端口一致：target: ''http://localhost:8088/''











## 4. SpringBoot搭建Dubbo

使用注解和配置文件方式来配置



### 4.1 添加依赖、开启Dubbo注解

```xml
<dependency>
    <groupId>org.apache.dubbo</groupId>
    <artifactId>dubbo-spring-boot-starter</artifactId>
    <version>2.7.3</version>
</dependency>

<!--  操作Zookeeper的，starter里面没有自带，因为注册中心自己选择  -->
<dependency>
    <groupId>org.apache.curator</groupId>
    <artifactId>curator-recipes</artifactId>
    <version>4.2.0</version>
</dependency>
```

```java
// 主应用函数上添加注解
@EnableDubbo
@SpringBootApplication
public class XXXXApplication {

    public static void main(String[] args) {
        SpringApplication.run(XXXXApplication.class, args);
    }
}
```



### 4.2 Provider

* 编写接口及其实现类（举例一个两数之和）

```java
@Service    // 向注册中心暴露服务，是Dubbo的注解
@Component  // IOC加入容器，别和@Service的业务层搞混
public class SumServiceImpl implements SumService {
    @Override
    public int sum(int a, int b) {
        return a+b;
    }
}
```

* 配置文件properties

```properties
# provider的应用名字，用于计算依赖关系
dubbo.application.name=com.howl.dubbo.provider

# 注册中心地址、通信协议
dubbo.registry.address=zookeeper://127.0.0.1:2181
dubbo.registry.protocol=zookeeper

# 与消费者通信的协议、端口号
dubbo.protocol.name=dubbo
dubbo.protocol.port=20880
```



### 4.3 Consumer

* 在需要远程调用的属性上加入@Reference注解，则会进行动态代理

```java
@RestController
public class SumController {

    @Reference
    SumService sumService;

    @GetMapping("/")
    public String sum(Integer a,Integer b){
        return String.valueOf( sumService.sum(a,b) );
    }
}
```

* 配置文件properties

```properties
# consumer的应用信息，用于计算依赖关系
dubbo.application.name=com.howl.dubbo.consumer

# 注册中心地址、协议
dubbo.registry.address=zookeeper://127.0.0.1:2181
```



> 注意：
>
> Consumer的接口全限定类名一定要和Provider的一致，否则Zookeeper里面路径不同，找不到提供者
>
> Dubbo推荐将服务接口、服务模型、服务异常等均放在 API 包中，这样二者就实现共享




