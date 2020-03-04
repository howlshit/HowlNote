>笔者之前仅看过RPC这个单词，完全没有了解过，不想终于还是碰上了。起因：这边想提高并发量而去看kafka（最后折中使用了redis），其中kafka需要安装ZooKeeper，而ZooKeeper又与分布式相关，再继续就发现分布式的基础是RPC









## 1. RPC

RPC（Remote Procedure Call）远程过程调用，即通过网络通信来调用远程计算机程序上的服务，而这个调用过程就像调用本地方法一样简单透明，并且不需要了解底层的网络技术协议。RPC采用C/S架构，发出请求的程序是Client，提供服务的则是Server，类似于Http请求与响应。简单总结就是：调用的方法实际在远程，而要像调用本地方法一样简单。

1）对于客户端的我：调用本地的一个方法（存根）就能获得服务。 这个存根是远程服务的一个代理，其底层如何实现，对于我来说是透明的。  

2）对于远程服务器：监听是否有连接过来，来了就调用对应的方法并返回（服务器端较易理解）



其结构图如下：





![1582688313034](C:\Users\Howl\AppData\Roaming\Typora\typora-user-images\1582688313034.png)

1. 用户调用一个 “本地” 函数，该函数调用客户句柄（远程服务在本地的存根）
2. 客户句柄调用网络通信来访问远程程序
3. 远程程序收到网络通信及相关信息就调用服务句柄
4. 服务句柄就调用服务函数，函数结束逆序返回结果完成一次远程调用











## 2. 为什么需要RPC

当我们的业务量越来越庞大，垂直增加服务器的数量对提高性能的作用愈加微乎，此时难免会采用分布式的架构以便更好地提高性能。分布式架构的每个服务都是独立的部分，当需要完成某项业务且依赖不同的服务式时，这些服务就需要互相调用，此时服务之间的调用就需要一种高效的应用程序之间的通讯手段了，这就是PRC出现的原因











## 3. RPC实现要求



#### 3.1 服务提供方

提供服务：实现所提供的服务

服务暴漏：仅仅实现了服务是不够的，还需要将提供的服务暴漏给外界，让外界知道有何，如何使用服务



#### 3.2 服务调用方

远程代理对象：在调用本地方法时实际调用的是远程的方法，那么势必本地需要一个远程代理对象



**总结：**为了实现RPC需要有：通信模型（BIO、NIO），服务定位（IP、PORT），远程代理对象（远程服务的本地代理），序列化（网络传输转换成二进制）











## 4. 简单实现

其主要的对象有：服务端接口、服务端接口实现、服务暴漏、客户端接口（与服务端共享同个接口）、服务的引用





#### 4.1 服务端接口

```java
public interface Service {

	// 提供两个服务，说hello和整数相加
	public String hello();
	public int sum(int a, int b);
	
}
```





#### 4.2 服务端接口实现

```java
public class ServiceImpl implements Service {

	@Override
	public String hello() {
		return "Hello World";
	}

	@Override
	public int sum(int a, int b) {
		return  a + b;
	}
}
```





#### 4.3 服务暴漏

```java
public static void export(Object service, int port) {
	
	if (service == null || port <= 0 || port > 65535) {
        throw new RuntimeException("Arguments error");
    }
	
	System.out.println(service.getClass().getName() + ": " + port + "服务暴露");
	
	new Thread( () -> {
		
		try (ServerSocket server = new ServerSocket(port);) {
			
			while(true){
				try (
						Socket socket = server.accept();
						ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
						ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
					) {
					
					// 读取方法名
					String methodName = in.readUTF();
					
					// 读取参数类型
					Class<?>[] parameterTypes = (Class<?>[])in.readObject();
					
					// 读取参数值
					Object[] arguments = (Object[])in.readObject();
					
					// 获取方法
					Method method = service.getClass().getMethod(methodName, parameterTypes);
					
					// 处理结果
					Object result = method.invoke(service, arguments);
					
					// 写入结果
					out.writeObject(result);
					
				} catch (Exception e) {
					e.printStackTrace();
				} 
			}
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}).start();
}
```

这个暴露的逻辑是服务端监听特定端口，等客户端发起请求后连接，然后通过Java的IO流获取方法名，参数等相关信息，最后通过反射实现方法的调用并将结果响应给客户端





#### 4.4 客户端接口

```java
public interface ClientService {

	// 提供两个服务，说hello和整数相加
	public String hello();
	public int sum(int a, int b);
		
}
```





#### 4.5 服务引用

```java
public static <T>T refer(Class<T> interfaceClass, String host, int port){
	
	if(interfaceClass == null || !interfaceClass.isInterface() || host == null || port <= 0 || port > 65535){
		throw new RuntimeException("Arguments error");
	}
	
	System.out.println("正在调用远程服务");
	
	@SuppressWarnings("unchecked")
	T proxy = (T)Proxy.newProxyInstance(interfaceClass.getClassLoader(), new Class<?>[] {interfaceClass}, new InvocationHandler() {
				
		@Override
		public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
			
			Object result = null;
			try (
					Socket socket = new Socket(host, port);
					ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
					ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
				) {
				
				out.writeUTF(method.getName());
				
				out.writeObject(method.getParameterTypes());
				
				out.writeObject(args);
				
				result = in.readObject();
				
			} catch (Exception e) {
				e.printStackTrace();
			}
			return result;
		}
	});
	return proxy;
}
```

而引用服务的逻辑是：创建Socket套接字连接，序列化相关请求信息发送给服务端，然后等待响应结果。其中透明调用是使用了动态代理





#### 4.6 测试

```java
public class Test {
	
	public static void main(String[] args) {
		
		// 暴露服务
		ServiceImpl service = new ServiceImpl();
		RPCFramework.export(service, 8080);
		
		// 调用服务
		Client client = RPCFramework.refer(Client.class, "127.0.0.1", 8080);
		int sum = client.sum(1, 2);
		String rs = client.hello();
		System.out.println("远程响应：" + sum);
		System.out.println("远程响应：" + rs);
	}
}
```

```java
RPC.ServiceImpl:8080----- 服务暴露

正在调用远程服务
远程响应：3
远程响应：Hello World
```







## 5. 思考



#### 5.1 为什么不用Http

RPC与具体协议无关，可基于Http、TCP，但因为TCP性能相对较好。Http属于应用层协议，TCP属于传输层协议，相对在底层少了一层封装，而且为了可靠传输而选择TCP不选择UDP





#### 5.2 常用的RPC框架

Dubbo（阿里巴巴）、SpringCloud、RMI（JDK内置）





#### 5.3 为什么要使用动态代理

因为要像本地调用一样，对于使用者来说是透明的。

```java
Object result = XXX(String method, String host, int port)
```

上面这样其实也行，但并不能感觉到是调用本地方法一样，而且如果一个接口有多个方法的话，每调用一次方法就需要发送一次host / port

```java
// 动态代理可以这样使用
ProxyObject.方法1
ProxyObject.方法2

// 没有使用动态代理则不人性化
XXX(String method1, String host, int port)
XXX(String method2, String host, int port)
```





#### 5.4 为什么参数与参数类型需要分开传

为了方便分辨方法的重载，下面获取方法需要方法名和参数类型

```java
service.getClass().getMethod(methodName, parameterTypes)
```







## 6. 优化



#### 6.1 网络通信

上面事例中采用BIO形式，阻塞访问而导致并发量不高，可以用NIO代替





#### 6.2 序列化

这里用了JDK原生方法只能序列化实现了Serializable接口的类，可以使用第三方的类库来提高性能





#### 6.3 服务负载

服务的自动发现，客户端能动态感知服务端的变化，从实现热部署，可用定时轮询的方法，eg:ZooKeeper





#### 6.4 Cluster

集群化，这样便可以提供负载均衡





#### 6.5 请求与响应

请求与响应可以进行编码封装，而不是这样单独一个一个发送



