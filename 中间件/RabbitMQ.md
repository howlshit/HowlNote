> 真的想尽快学完种种框架，综合搭建起一个项目来，然后看着他出Bug、慢慢地自己去优化，重构，再完善。





## 1. RabbitMQ的作用

笔者经常能看到MQ这个词，知道其作为消息队列，但始终没有接触过，现在刚好有个机会（不知道在抢答系统中能不能用上），首先当然要知道MQ有什么作用：



* 异步处理：
  * 用户注册：注册后发送邮件、短信、验证码等可以异步处理，使注册这个过程写入数据库后就可立即返回
* 流量消峰
  * 秒杀活动：超过阈值的请求丢弃转向错误页面，然后根据消息队列的消息做业务处理
* 日志处理
  * 可以将error的日志单独给消息队列进行持久化处理
* 应用解耦
  * 购物的下单操作：订单系统与库存系统中间加消息队列，使二者解耦，若后者故障也不会导致消息丢失









## 2.安装

Docker快速安装，想不到之前学Docker为了简化环境搭建，现在这么快就能体验上了

```shell
# 安装带有标签的版本，开启了插件有web管理页面
docker pull rabbitmq:management

# 运行rabbit，默认账号密码为 guest
docker run -d --hostname my-rabbit --name rabbit -p 15672:15672 -p 5672:5672 rabbitmq:management

# 也可以改变环境变量来改变初始账号密码
docker run -d --hostname my-rabbit --name rabbit -e RABBITMQ_DEFAULT_USER=user -e RABBITMQ_DEFAULT_PASS=password -p 15672:15672 -p 5672:5672 rabbitmq:management
```



RabbitMQ的端口是：5672，其插件manage的端口为：15672



还有手动安装的小伙伴其配置文件在： /etc/RabbitMQ/rabbit.conf（有些需要手动创建）









## 3. Web管理页面

RabbitMQ提供了插件功能，上面的manager插件也就是Web管理页面给我们提供了Web页面管理MQ的途径。进去首先改密码、创建新的账户、创建新的虚拟主机（库）、将新账号分配新虚拟机等（这些名词后面会有解释）



那么就打开Web管理页面

```
// 打开浏览器输入，就会看见登录页面
// 默认账号密码都是guest

http://localhost:15672
```



进去先不要慌，点击上方的Admin标签，尝试改密码（主要为了熟悉界面，可以直接跳过）

![1587913622695](C:\Users\Howl\AppData\Roaming\Typora\typora-user-images\1587913622695.png)



* 以后我们可以在这个Web页面对MQ进行管理，当然也有命令行，不过笔者还是喜欢页面的形式









## 4. 提前剧透

这里提前解释一些后面会遇到的名词，方便大家构建对MQ的理解。



* RabbitMQ：作为消息代理，负责接受并转发消息，可理解为邮局负责收发邮件，其使用了amqp协议
* 消息队列：存储消息的数据结构，本质是消息缓冲区
* 生产者：生产消息的一方，将消息发送到队列中
* 消费者：消费消息的一方，从队列中接收消息
* 连接：用来连接MQ，是socket的抽象，为我们处理协议版本协商和身份验证等
* 通道：我们基本都是使用通道的API来完成各种操作
* 交换机：
* 虚拟机：可以理解为数据库中的库



RabbitMQ的基本模型

















## 4. 消息模型

非常强烈建议去官网看Docs，其文档内容不多，有各语言的实操代码与解释。笔者就是看相关文档，加上自己实操与理解写下的笔记，以下内容均来自官网，笔者做了部分修改来契合自己的书写习惯，下面就以发送一条语句为例说明



首先需要导包：使用普通maven工程或Springboot工程都可，笔者就按照官网的硬编码方式走一遍

```xml
<dependency>
    <groupId>com.rabbitmq</groupId>
    <artifactId>amqp-client</artifactId>
    <version>5.9.0</version>
</dependency>
```





### 4.1 Hello World

![1587826822493](C:\Users\Howl\AppData\Roaming\Typora\typora-user-images\1587826822493.png)

队列的名字就叫 Hello World ，是一对一模型，中间不需要交换机

```java
public class Send {

    // name the queue
    private final static String QUEUE_NAME = "hello";

    public static void main(String[] args) {

        // then we can create a connection to the server
        // 根据方法名就知道各种参数是什么意思了，主要用于建立连接
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("127.0.0.1");
        factory.setPort(5672);
        factory.setVirtualHost("/hello");
        factory.setUsername("guest");
        factory.setPassword("guest");

        // 使用try,resouce方式关闭连接
        try (Connection conn = factory.newConnection();
             Channel channel = conn.createChannel()){

            // 声明消息队列，各参数为：队列名字，持久化与否，连接是否独占队列，是否消费完自动删除，最后一个不管
            channel.queueDeclare(QUEUE_NAME,false,false,false,null);
            
            // 要发送的消息
            String message = "Hello World!";
            
            // 发布消息：交换机，队列名，传递消息额外设置，消息内容需要字节
            channel.basicPublish("",QUEUE_NAME,null,message.getBytes());
            
            System.out.println(" [x] Sent '" + message + "'");
        } catch (TimeoutException | IOException e) {
            e.printStackTrace();
        }
    }
}
```

```java
public class Recv {

    private final static String QUEUE_NAME = "hello";

    public static void main(String[] argv) throws Exception {

        // then we can create a connection to the server
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("127.0.0.1");
        factory.setPort(5672);
        factory.setVirtualHost("/test");
        factory.setUsername("guest");
        factory.setPassword("guest");

        Connection conn = factory.newConnection();
        Channel channel = conn.createChannel();
        channel.queueDeclare(QUEUE_NAME, false, false, false, null);

        // 消费消息：队列名，是否开启自动确认机制，回调接口
        channel.basicConsume("hello",true,new DefaultConsumer(channel){
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                // 最后一个参数，即消息队列中取出的消息
                System.out.println(" [x] Received '" + new String(body) + "'");
            }
        });

        // channel.close();
        // conn.close();
    }
}
```

*  队列可以先声明，没有则创建，有则直接使用：幂等性
*  队列可以设置持久化，即重启后队列还存在
*  独占队列意思是只有一个连接可以操作改队列
*  交换机为空则使用默认的
*  basicConsume方法负责消费消息
*  通道和连接不需要关闭，这样会一直监听相应的队列









### 4.2 封装工具类

创建工厂，获取连接，通道存在各业务中属于冗余代码，所以将其封装成一个工具类，方便后面使用，以及简化后面的逻辑，聚集在模型理解

```java
public class RabbitMQUtil {

    private static ConnectionFactory factory;
    static {
        factory = new ConnectionFactory();
        factory.setHost("47.56.143.47");
        factory.setPort(5672);
        factory.setVirtualHost("/");
        factory.setUsername("guest");
        factory.setPassword("guest");
    }

    public static Connection get() {
        try {
            return factory.newConnection();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void close(Channel channel, Connection conn) {
        try {
            if(channel != null){
                channel.close();
            }
            if(conn != null){
                conn.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
```









### 4.3 工作队列

![1587863389689](C:\Users\Howl\AppData\Roaming\Typora\typora-user-images\1587863389689.png)

与Hello World相比，这种队列是增加了消费者，应该容易理解

```java
public class NewTask {

    private static final String TASK_QUEUE_NAME = "task_queue";

    public static void main(String[] args) throws IOException {
        Connection conn = RabbitMQUtil.get();
        Channel channel = conn.createChannel();
        channel.queueDeclare(TASK_QUEUE_NAME, true, false, false, null);

        String message = "Word Queue!";

        for (int i = 0; i < 100; i++) {
            channel.basicPublish("",TASK_QUEUE_NAME,null,(message + ": " + i).getBytes());
        }

        System.out.println(" [x] Sent '" + message + "'");
        RabbitMQUtil.close(channel,conn);
    }
}
```

```java
// Worker2代码一样，不重复写了
public class Worker1 {

    private static final String TASK_QUEUE_NAME = "task_queue";

    public static void main(String[] args) throws IOException {
        Connection conn = RabbitMQUtil.get();
        Channel channel = conn.createChannel();
        channel.queueDeclare(TASK_QUEUE_NAME, true, false, false, null);

        channel.basicConsume(TASK_QUEUE_NAME, true, new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                System.out.println(" [x] Received '" + new String(body) + "'");
            }
        });
    }
}
```

* 默认是平均分配：一次性将分配的任务交给消费者，谁先消费完分配的任务就闲置，不管其余消费者是否还在消费
* 确认机制
  * 默认消费者自动向MQ确认：MQ收到确认后将自身存储的消息删除
  * 修改确认机制：MQ每次发送一个消息给消费者，确认完后谁先消费完就发送给谁，即能者多劳
  * 最后记得手动确认，不然MQ还会保存消息，手动确认使用标签

```java
// 仅接收一次未确认的消息
channel.basicQos(1);

// 队列持久化
channel.queueDeclare(TASK_QUEUE_NAME, true, false, false, null);

// 消息消费完后手动确认
channel.basicAck(envelope.getDeliveryTag(),false);
```









### 4.4 发布/订阅

![1587872332908](C:\Users\Howl\AppData\Roaming\Typora\typora-user-images\1587872332908.png)

该模型中添加了交换机X，与以往不同。RabbitMQ消息传递模型中的核心思想是生产者从不将任何消息直接发送到队列。实际上，生产者经常甚至根本不知道是否将消息传递到任何队列



相反，生产者只能将消息发送到交换机。交流是一件非常简单的事情。一方面，它接收来自生产者的消息，另一方面，将它们推入队列。交易所必须确切知道如何处理收到的消息。是否应将其附加到特定队列？是否应该将其附加到许多队列中？还是应该丢弃它。规则由交换类型定义 。



> We'll focus on the last one -- the fanout，下面我们将主要讨论扇出这个模型



扇出类似于广播

```java
public class EmitLog {

    private static final String EXCHANGE_NAME = "logs";

    public static void main(String[] args) throws IOException {
        Connection conn = RabbitMQUtil.get();
        Channel channel = conn.createChannel();
        
        // 声明交换机
        channel.exchangeDeclare(EXCHANGE_NAME,"fanout");

        String message = "Fanout!";

        channel.basicPublish(EXCHANGE_NAME,"",null,message.getBytes());
        System.out.println(" [x] Sent '" + message + "'");
        RabbitMQUtil.close(channel,conn);
    }
}
```

```java
public class ReceiveLogs1 {

    private static final String EXCHANGE_NAME = "logs";

    public static void main(String[] args) throws IOException {
        Connection conn = RabbitMQUtil.get();
        Channel channel = conn.createChannel();
        channel.exchangeDeclare(EXCHANGE_NAME,"fanout");

        // 获取随机队列
        String queueName = channel.queueDeclare().getQueue();
        // 第三个参数是路由key，广播中无意义
        channel.queueBind(queueName,EXCHANGE_NAME,"");

        channel.basicConsume(queueName,true,new DefaultConsumer(channel){
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                System.out.println(new String(body));
            }
        });
    }
}
```

* 交换类型有：direct，topic，headers、fanout
* 临时队列：声明队列是不加参数则是非持久，自动删除的队列









### 4.5 路由

![1587907558251](C:\Users\Howl\AppData\Roaming\Typora\typora-user-images\1587907558251.png)



笔者理解为是通过设置路由关键字使消息定向到不同的队列

```java
public class EmitLogDirect {

    // 定义路由键
    private static final String EXCHANGE_NAME = "direct_logs";
    private static final String ROUTING_KEY = "info";

    public static void main(String[] args) throws IOException {
        Connection conn = RabbitMQUtil.get();
        Channel channel = conn.createChannel();
        channel.exchangeDeclare(EXCHANGE_NAME,"direct");

        String message = "Routing!";

        channel.basicPublish(EXCHANGE_NAME,ROUTING_KEY,null,message.getBytes());
        System.out.println(" [x] Sent '" + ROUTING_KEY + "':'" + message + "'");
        RabbitMQUtil.close(channel,conn);
    }
}
```

```java
public class ReceiveLogsDirect1 {

    private static final String EXCHANGE_NAME = "direct_logs";
    private static final String ROUTING_KEY = "info";

    public static void main(String[] args) throws IOException {
        Connection conn = RabbitMQUtil.get();
        Channel channel = conn.createChannel();
        channel.exchangeDeclare(EXCHANGE_NAME, "direct");

        String queueName = channel.queueDeclare().getQueue();
        channel.queueBind(queueName,EXCHANGE_NAME,ROUTING_KEY);

        channel.basicConsume(queueName,true,new DefaultConsumer(channel){
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                System.out.println(new String(body));
            }
        });
    }
}
```

* 其他消费者的代码只需改ROUTING_KEY的内容即可
* 若没有对应的路由键时，消息被丢弃
* 路由的交换机类型为direct









### 4.6 主题

![1587948832774](C:\Users\Howl\AppData\Roaming\Typora\typora-user-images\1587948832774.png)



主题的路由键它必须是单词列表，以点分隔。功能类似于动态路由，其中 * 匹配一个单词， # 匹配0或多个，eg：quick.orange.rabbit

```java
public class EmitLogTopic {

    private static final String EXCHANGE_NAME = "topic_logs";
    private static final String ROUNTING_KEY = "lazy.origin";

    public static void main(String[] args) throws IOException {
        Connection conn = RabbitMQUtil.get();
        Channel channel = conn.createChannel();
        channel.exchangeDeclare(EXCHANGE_NAME, "topic");

        String message = "Topic!";

        channel.basicPublish(EXCHANGE_NAME,ROUNTING_KEY,null,message.getBytes());
        System.out.println(" [x] Sent '" + ROUNTING_KEY + "':'" + message + "'");
        RabbitMQUtil.close(channel,conn);
    }
}
```

```java
public class ReceiveLogsTopic1 {

    private static final String EXCHANGE_NAME = "topic_logs";
    private static final String ROUNTING_KEY = "lazy.*";

    public static void main(String[] args) throws IOException {
        Connection conn = RabbitMQUtil.get();
        Channel channel = conn.createChannel();
        channel.exchangeDeclare(EXCHANGE_NAME, "topic");

        String queueName = channel.queueDeclare().getQueue();
        channel.queueBind(queueName, EXCHANGE_NAME,ROUNTING_KEY);
        channel.basicConsume(queueName,true,new DefaultConsumer(channel){
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                System.out.println(new String(body));
            }
        });
    }
}
```

* 这个主题Topics类似于`订阅`，对自己感兴趣的事接收消息，而这个`订阅`功能用路由键来实现









### 4.7 其余

后面还有RPC以及新出的Publisher Confirms模型，这里简单给出RPC模型，因为笔者暂时使用不到这些模型，后期需要用到再来补坑

![1587949016163](C:\Users\Howl\AppData\Roaming\Typora\typora-user-images\1587949016163.png)







****

参考

<https://www.rabbitmq.com/getstarted.html>