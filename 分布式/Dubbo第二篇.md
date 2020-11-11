## 1. 启动时检查

Dubbo 缺省会在启动时检查依赖的服务是否可用，不可用时会抛出异常，阻止 Spring 初始化完成，以便上线时，能及早发现问题



我们可以关闭检查，比如测试时，有些服务不关心，或者出现了循环依赖，必须有一方先启动

```properties
dubbo.consumer.check=false
dubbo.reference.check=false
dubbo.registry.check=false
```









## 2. 超时、配置覆盖关系、属性优先级



### 2.1 超时

provider因为网络等原因很长时间才返回，此时造成消费者阻塞，所以可以设置超时来解决，默认1000毫秒

可在注解内设置timeout

```java
@Reference(timeout = 5000)
```





### 2.2 配置覆盖关系

* 方法级优先，接口次之，全局配置再次之
* 级别一样，消费者优先，提供者次之



### 2.3 属性优先级

优先级从高到低：

* JVM设置的属性
* XML文件的属性
* Properties内的属性









## 3. 重试次数

* 调用失败后的重试次数，不包含第一次失败的调用，默认2次，在注解内可用retries= ‘2’ 设置

* 有些调用是幂等的，所以可以重试、有些非幂等设置为0不重试

```java
@Reference(retries = 0)
```









## 4. 多版本

当一个接口实现有不兼容升级时，可以用版本号过渡，版本号不同的服务相互间不引用，让一部分调用旧版本，一部分调用新版本



可在暴露服务接口上添加属性

```java
@Service(version = "1.0.0")
@Service(version = "2.0.0")

@Reference(version = "1.0.0")
```









## 5. 本地存根

在进行远程过程调用的时候，我们可能需要先进行参数校验或者本地缓存，那么此时可以利用本地存根了



### 5.1 消费者方

在消费者这边要创建本地存根，而该存根需要一个有参构造

```java
public class SumServiceStub implements SumService {

    // 自己手动添加一个有参构造
    private final SumService sumService;

    // 框架会传入远程代理对象
    public SumServiceStub(SumService sumService) {
        this.sumService = sumService;
    }

    // 这里可以进行各种调用前置操作
    public int sum(int a, int b) {
        if(a != 0  && b != 0){
            return sumService.sum(a,b);
        }
        return -1;
    }
}
```



### 5.2 提供者方

提供者只是需要指明消费者存根的全限定类名即可

```java
@Service(stub = "com.howl.consumer.service.impl.SumserviceStub")	// 指明消费者存根
@Component
public class SumServiceImpl implements SumService {
    @Override
    public int sum(int a, int b) {
        return a+b;
    }
}
```









## 6. 高可用

这里本来都不想写了，因为官方写得很清楚，还详细有实例



* 注册中心宕机

  * 若之前调用过，则会有本地缓存不影响使用，但不能更新服务信息
  * 或者在@Service(url=“127.0.0.1:20882”)跳过注册中心直连提供者

* 负载均衡：可在注解，控制台进行配置

  * Random 随机
  * RoundRobin 轮询
  * LeastActive  最少活跃
  * ConsistentHash 一致性哈希

* 服务降级：服务器有压力时，对某些服务或页面进行处理或不处理，保证核心业务正常

  * 消费者不调用而直接返回空
  * 消费者调用失败而返回空

  



  

  

