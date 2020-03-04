# 1. BIO和NIO

我们平常使用的IO是BIO（Blocking-IO），即阻塞IO、而NIO（No-blocking-IO）则是非阻塞IO，二者有什么区别呢？





#### 预先知识准备

* 同步：发起调用后，调用者一直处理任务至结束后才返回结果，期间不能执行其他任务
* 异步：发起调用后，调用者立即返回结果的标记（当结果出来后用回调等机制通知），期间可以执行其他任务
* 阻塞：发起请求后，发起者一直等待结果返回，期间不能执行其他任务
* 非阻塞：发起请求后，发起者不用一直等待结果，期间可以执行其他任务
* IO模式有五种（同步、异步、阻塞、非阻塞、多路复用）这里介绍同步阻塞和同步非阻塞IO，而剩下的后面回来填坑





**NIO主要体现在网络IO中，所以下面就围绕网络IO来说明，这里会涉及到传统的BIO、网络编程、反应器设计模式，如果不了解的童鞋这里有各自的传送门 [BIO](<https://www.cnblogs.com/Howlet/p/12033337.html>) ，[未完善]**







#### 二者区别

|      | BIO      | NIO        |
| ---- | -------- | ---------- |
| 类型 | 同步阻塞 | 同步非阻塞 |
| 面向 | 面向流   | 面向缓冲区 |
| 组件 | 无       | 选择器     |

若没有了解过NIO，那么列出的区别只需有个印象即可，后面会逐步说明













# 2.BIO



### 2.1 传统BIO

传统的IO其读写操作都阻塞在同一个线程之中，即在读写期间不能再接收其他请求



那么我们就来看看传统BIO是怎么实现的，后面都以网络编程的Socket为例，因其与后面的NIO有关

```java
public class BIO {
	
	public static void main(String[] args) throws IOException {
		
		// 开个线程运行服务器端套接字
		new Thread( () -> {
			try {
                // 建立服务器端套接字
                ServerSocket serverSocket = new ServerSocket(8080);
                // 该方法阻塞至有请求过来
                Socket socket = serverSocket.accept();

                // 获取输入流
                int length = 0;
                byte[] bytes = new byte[1024];
                InputStream in = socket.getInputStream();

                // 客户端关闭输出流这里才会读取到-1,shutdownOutput或者close
                while( (length = in.read(bytes)) != -1){
                    System.out.println(new String(bytes,0,length));
                }

                System.out.println("这里服务器端处理任务花费了10秒");
                Thread.sleep(10000);

                // 获取输出流
                OutputStream out = socket.getOutputStream();
                out.write( ("这里是服务器端发送给客户端的消息: " + new Date()).getBytes() );

                // 关闭资源
                in.close();
                out.close();
                socket.close();
                serverSocket.close();
			} catch (Exception e) {
			}
		}).start();
		
		
		
		
		// 开个线程运行客户端套接字
		new Thread( () -> {
			try {
				// 建立客户端套接字
				Socket socket = new Socket("127.0.0.1",8080);
				
				// 获取输出流
				OutputStream out = socket.getOutputStream();
				out.write( ("这里是客户端发送给服务器端的消息：" + new Date()).getBytes() );
				// 关闭输出流,让服务器知道数据已经发送完毕，剩下接收数据了
				socket.shutdownOutput();
				
				// 获取输入流
				int length = 0;
				byte[] bytes = new byte[1024];
				InputStream in = socket.getInputStream();
				while( (length = in.read(bytes)) != -1){
					System.out.println(new String(bytes,0,length));
				}
				
				// 关闭资源，若没有关闭则会保持连接至超时，单线程服务器端就不能接收后来的连接请求
				out.close();
				in.close();
				socket.close();
			} catch (Exception e) {
			}
		}).start();

	}
}
```

```
这里是客户端发送给服务器端的消息：Sat Feb 08 15:14:55 GMT+08:00 2020
这里服务器端处理任务花费了10秒
这里是服务器端发送给客户端的消息: Sat Feb 08 15:15:05 GMT+08:00 2020
```

* 从输出可以看出，客户端会一直等待阻塞直至服务器端返回内容


* 服务器端的accept()方法会阻塞当前线程，直至有请求发送过来才会继续accept()方法下面的代码
* 服务器端接收到一个请求后且该请求还没处理完，后又再有一个请求过来，则后来的请求会被阻塞需排队等待
* 客户端打开输出流若没关闭，则服务器端是不知道客户端数据已经发送完，会一直等待至超时 ，关闭方法：
  * 客户端socket.close()，整个连接也关闭了
  * 客户端socket.shutdownOutput()，单方面关闭输出流，不关闭连接
  * 客户端的outputStream.close()，会造成socket被关闭









### 2.2 伪异步BIO

传统的BIO是单线程的，一次只能处理一个请求，而我们可以改进为多线程，即服务器端每接收到一个请求就为该请求单独创建一个线程，而主线程还是继续监听是否有请求过来，伪异步是因为accept方法到底还是同步的



```java
public class SocketTest {
	
	// 定义线程接口
	class MyRunnable implements Runnable{
		
		@Override
		public void run(){
			try {
				Socket socket = new Socket("127.0.0.1",8080);
				
				// 获取输出流
				OutputStream out = socket.getOutputStream();
				out.write( ("这里是客户端发送给服务器端的消息：" + new Date()).getBytes() );
				// 关闭输出流,让服务器知道数据已经发送完毕，剩下接收数据了
				socket.shutdownOutput();
				
				// 获取输入流
				int length = 0;
				byte[] bytes = new byte[1024];
				InputStream in = socket.getInputStream();
				while( (length = in.read(bytes)) != -1){
					System.out.println(new String(bytes,0,length));
				}
				
				// 关闭资源
				out.close();
				in.close();
				socket.close();
			} catch (Exception e) {
			}
		}
	}
	
	
	
	public static void main(String[] args) throws IOException, InterruptedException {
		
		// 开个线程运行服务器端套接字
		new Thread( () -> {
			try {
				// 建立服务器端套接字
				ServerSocket serverSocket = new ServerSocket(8080);
				
				// 循环接收请求
				while(true){
					Socket socket = serverSocket.accept();
					
					// 为每个请求单独开线程，这里就不那么复杂使用线程池了
					new Thread( () -> {
						try {
							// 获取输入流
							int length = 0;
							byte[] bytes = new byte[1024];
							InputStream in = socket.getInputStream();
							while( (length = in.read(bytes)) != -1){
								System.out.println(new String(bytes,0,length));
							}
							
							// 获取输出流
							OutputStream out = socket.getOutputStream();
							out.write( ("这里是服务器端发送给客户端的消息: " + new Date()).getBytes() );
							
							// 关闭资源
							in.close();
							out.close();
							socket.close();
						} catch (Exception e) {
							// TODO: handle exception
						}
					}).start();
				}
			} catch (Exception e) {
			}
		}).start();
		
        
		// 创建多线程，调用类中接口（为了偷懒写成这样了。。。）
        // 关注点在于伪异步，像线程计数器，，线程池，Lambda表达式也尽量少用，使代码易懂
		SocketTest socketTest = new SocketTest();
		MyRunnable myRunnable = socketTest.new MyRunnable();
		new Thread(myRunnable).start();
		new Thread(myRunnable).start();
		new Thread(myRunnable).start();

	}
}
```

```
这里是客户端发送给服务器端的消息：Sat Feb 08 15:52:00 GMT+08:00 2020
这里是服务器端发送给客户端的消息: Sat Feb 08 15:52:00 GMT+08:00 2020
这里是客户端发送给服务器端的消息：Sat Feb 08 15:52:00 GMT+08:00 2020
这里是客户端发送给服务器端的消息：Sat Feb 08 15:52:00 GMT+08:00 2020
这里是服务器端发送给客户端的消息: Sat Feb 08 15:52:00 GMT+08:00 2020
这里是服务器端发送给客户端的消息: Sat Feb 08 15:52:00 GMT+08:00 2020
```

* 服务器端每来一个请求就为之单独创建线程来处理任务，使主线程可以继续循环接收请求
* 客户端的请求之间就互不干扰了，不用等待上一个请求处理完才处理下一个
* 其本质还是同步，使用了多线程才实现异步功能
* 使用多线程，若在多高并发情况下，会大量创建线程而导致内存溢出（可以使用线程池优化，但有界限池终究不是办法）













# 3. NIO

* 看了上面那么多铺垫，终于到我们的正题了。NIO主要使用在网络IO中，当然文件IO也有使用，NIO在高并发的网络IO中有极大的优势，其在JDK1.4中引入，以我们传统再传统的开发环境--1.7中可以使用了
* 在单线程中，NIO在写读数据的时候可以同时执行其他任务，不必等数据完全读写而导致阻塞（后面有地方说明）



**NIO的组成**

* Buffer（缓冲区）
* Channel（通道）
* Selector（选择器）

那么我们就来看看NIO的三个组成把





### 3.1 Buffer

NIO是面向缓冲区的，一次处理一个区的数据，在NIO中我们都是使用缓冲区来处理数据，即数据的读入或写出都要经过缓冲区



缓冲区的类型有：
* ByteBuffer、

* ShortBuffer、

* IntBuffer、

* LongBuffer、

* FloatBuffer、

* DoubleBuffer、

* CharBuffer

最常用是ByteBuffer，记住后面要用到，可使用静态方法获取缓冲区：`ByteBuffer.allocate(1024)`





Buffer类中主要的方法：

| 返回类型   | 函数                   | 解释                 |
| ---------- | ---------------------- | -------------------- |
| XXXBuffer  | allocate(int capacity) | 返回指定容量的缓冲区 |
| ByteBuffer | put(byte[] src)        | 向缓冲区添加字节数组 |
| ByteBuffer | get(byte[] dst)        | 向缓冲区获取字节数组 |
| XXXBuffer  | flip()                 | 切换成读模式         |
| XXXBuffer  | clear()                | 清除此缓冲区         |



其内部维护了几个变量

```java
// Invariants: mark <= position <= limit <= capacity
private int mark = -1;	// 标记这里不讲解
private int position = 0;	//位置
private int limit;		// 限制
private int capacity;	// 容量大小
```



变量的变化：

初始化时：position为0，limit和capacity为容量大小，且capacity不变化，后面省略

put数据时：position为put进去数据大小（如放进5字节数据，则position=5），其余不变，正常默认为`写模式`

切换读模式：limit赋值为position的当前值，而position赋值为0

get数据时：读取多少个数据，position就前进几个位置

清空：调用clear()，变量变为初始化状态，即position为0，limit为容量大小

![1581171604021](C:\Users\Howl\AppData\Roaming\Typora\typora-user-images\1581171604021.png)







### 3.2 Channel

通道主要是传输数据的，不进行数据操作，并且与流不同可以前后移动，而且通道是双向的读写的，最重要的是Channel只能与Buffer交互，所以要使用NIO就要用Channel和Buffer来配合



其类型包括：

- FileChannel
- DatagramChannel
- SocketChannel:
- ServerSocketChannel

可以看出NIO主要支持网络IO及文件IO，可通过静态方法获取：`ServerSocketChannel.open()，然后通过ServerSocketChannel.socket()获取对应的套接字，套接字的获取通道方法前提是已经绑定了通道才行，不然空指针`



通道的主要方法：

| 类型                | 函数名                          | 解释                                   |
| ------------------- | ------------------------------- | -------------------------------------- |
| ServerSocketChannel | open                            | 返回对应的通道                         |
| int                 | read(ByteBuffer dst)            | 从该通道读取到给定缓冲区的字节序列     |
| int                 | write(ByteBuffer src)           | 从给定的缓冲区向该通道写入一个字节序列 |
| ServerSocketChannel | bind(SocketAddress local)       | 将通道的套接字绑定到本地，设为监听连接 |
| SelectableChannel   | configureBlocking(Boolean bool) | 设置通道的阻塞模式                     |
| SelectionKey        | register(Selector sel, int ops) | 将通道注册到选择器                     |



配合Channel和Buffer来简单实现数据流通

```java
int length = 0;
while( (length = inChannel.read()) != -1 ){
    buffer.flip();	//切换读模式
    outChannel.write(buffer);	// 数据写入通道
    buffer.clear();		// 清空缓冲区，实现可再写入
}
```







### 3.3 Selector

NIO特有的组件（选择器容器），注意只有在网络IO中才具有非阻塞性，网络IO中的套接字的通道才有非阻塞的配置。使用单线程通过Selector来轮询监听多个Channel，在IO事件还没到达时不会陷入阻塞态等待。**划重点：**传统BIO在事件还没到达时该线程会被阻塞而等待，一次只能处理一个请求（可以使用多线程来提高处理能力）。而NIO在事件还没到达是非阻塞轮询监听的，一次可以处理多个事件。使用一个线程来处理多个事件明显比一个线程处理一个事件更优秀，获取选择器：`Selector.open()`



![1581174444472](C:\Users\Howl\AppData\Roaming\Typora\typora-user-images\1581174444472.png)





选择器主要方法：

| 类型              | 方法名       | 解释                 |
| ----------------- | ------------ | -------------------- |
| void              | close        | 关闭此选择器         |
| Selector          | open         | 打开选择器           |
| int               | select       | 选择一组准备好的IO键 |
| Set<selectedKeys> | selectedKeys | 返回选择器的键集     |





**这里补充一下注册通道时返回的键的方法**

|            |              |                              |
| ---------- | ------------ | ---------------------------- |
| XXXChannel | channel      | 返回键对应的通道，类似于句柄 |
| boolean    | isAcceptable | 键对应的通道是否准备好了     |
| boolean    | isReadable   | 键对应的通道是否可读         |
| boolean    | isWritable   | 键对应的通道是否可写         |













### 3.4 使用事例

综合上面BIO的 2.1和 2.2的代码，客户端基本不用改动，使用多线程来模拟多次请求，而重点改造在于服务器端



这里的服务器端用单线程来处理请求，即一对多使用了多路复用。若是BIO单线程则会阻塞，即一请求一应答

```java
public class NIOTest {0.
	
	// 定义线程接口
		class MyRunnable implements Runnable{
			
			@Override
			public void run(){
				try {
					Socket socket = new Socket("127.0.0.1",8080);
					
					// 获取输出流
					OutputStream out = socket.getOutputStream();
					out.write( ("这里是客户端发送给服务器端的消息：" + new Date()).getBytes() );
					// 关闭输出流,让服务器知道数据已经发送完毕，剩下接收数据了
					socket.shutdownOutput();
					
					// 获取输入流
					int length = 0;
					byte[] bytes = new byte[1024];
					InputStream in = socket.getInputStream();
					while( (length = in.read(bytes)) != -1){
						System.out.println(new String(bytes,0,length));
					}
					
					// 这里故意不关闭资源，保持连接
					// 如果是BIO单线程，没有断开连接，则会阻塞后面的请求
					// 而NIO则不会阻塞，因为是多路复用
					
				} catch (Exception e) {
				}
			}
		}
	
	

	public static void main(String[] args) throws UnknownHostException, IOException {
		
		// 开个线程运行服务器端套接字
		new Thread( () -> {
			
			try {
				// 静态方法获取选择器
				// 开启选择器的线程会被选择器阻塞，所以要另开一个线程执行
				Selector selector = Selector.open();
				
				// 获取服务器端通道并配置非阻塞
				ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
				serverSocketChannel.configureBlocking(false);
				InetSocketAddress inetSocketAddress = new InetSocketAddress("127.0.0.1", 8080);
				serverSocketChannel.bind(inetSocketAddress);
				
				// Selector管理Channel，则需将对应的channel注册上去，且指定类型
				// 将服务器通道注册到选择器上,注册为accept
				// 可频道为：一看能看出来不解释了
				/**
				 * SelectionKey.OP_CONNECT
				 * SelectionKey.OP_ACCEPT
				 * SelectionKey.OP_READ
				 * SelectionKey.OP_WRITE
				 */
				serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
				
				while(true){
					// 轮询监听是否有准备好的连接
					if(selector.select() > 0){
						// 获取键集
						Set<SelectionKey> set = selector.selectedKeys();
						Iterator iterator = set.iterator();
						// 迭代器迭代
						while(iterator.hasNext()){
							SelectionKey selectionKey = (SelectionKey) iterator.next();
							
							// 接收连接事件
							if(selectionKey.isAcceptable()){
								SocketChannel socketChannel = serverSocketChannel.accept();
								
								// 设置客户端通道为非阻塞，不然选择器会被阻塞，其存在没有意义了
								socketChannel.configureBlocking(false);
								
								// 将客户端通道注册到选择器上，使选择器可以统一管理
								socketChannel.register(selector, SelectionKey.OP_READ);
								
							// 处理可读事件
							}else if(selectionKey.isReadable()){
								// 通过key来获取通道
								SocketChannel socketChannel = (SocketChannel) selectionKey.channel();
								
								// 配合缓冲区
								ByteBuffer bytebuffer = ByteBuffer.allocate(1024);
								
								int length = 0;
								byte[] bytes = new byte[1024];
								while( (length = socketChannel.read(bytebuffer)) != -1){
									bytebuffer.flip();
									
									// 将缓冲区数据放入字节数组，并输出
									bytebuffer.get(bytes, 0, length);
									System.out.println(new String(bytes,0,length));
									
									bytebuffer.clear();
								}
							}
							// 取消选择键，因为有些已经处理了
							iterator.remove();
						}
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}).start();
		
		
		// 调用类中接口，创建多线程（为了偷懒写成这样了。。。）
		NIOTest NIOTest = new NIOTest();
		MyRunnable myRunnable = NIOTest.new MyRunnable();
		new Thread(myRunnable).start();
		new Thread(myRunnable).start();
		new Thread(myRunnable).start();

	}
}
```

* 上面客户端故意不关闭连接，未超时情况下也能处理多请求，则说明NIO是非阻塞的，最大好处就在于这里









### 总结

挖坑：AIO异步的IO，基于网络编程的Netty框架，越来越多的坑要填了 —_—!









