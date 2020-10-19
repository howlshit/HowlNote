## 1. 网络编程

Java中的java.net包提供了网络通信的各种实现，如果我们要使两台计算机间通过网络来交换数据，那么中间就需要有连接才可进行，而net包中Socket对象就担任连接这个的角色，net包提供了两种常见的网络协议支持：

* TCP：面向连接Socket的，基于流传递，建立Socket即尝试连接
* UDP：面向无连接Datagram的，基于数据报，在通信之前不建立连接



>Socket本质是编程接口（API），用于描述IP地址和端口（五个通信要素），是对TCP/IP的封装，当然系统提供了TCP/IP的接口，方便我们进行操作调用









## 2. Socket

Socket称为套接字，用于建立TCP连接。在传统的C/S架构中，客户端创建套接字（Socket1）来尝试连接服务器的套接字（Socket2），当二者套接字可以并连接成功时，服务器就会创建一个套接字对象（Socket3）。客户端和服务器端通过套接字对象（Socket1和Socket3）来进行数据传输。这里注意：服务器端的Socket2是一个种监听客户端连接的类



**连接流程：**

- 服务器端创建 ServerSocket 对象，表示通过服务器上的端口通信
- 服务器端调用 ServerSocket.accept() ，该方法将阻塞至有客户端连接到服务器上给定的端口
- 服务器端阻塞期间，客户端创建Socket对象，指定需要连接的服务器地址和端口号
- 客户端的Socket类的构造函数试图将客户端连接到指定的服务器和端口号，若通信被建立，则在客户端创建一个Socket对象能够与服务器进行通信（期间有三次握手）
- 服务器端，accept()方法返回服务器上一个新的socket引用，该socket连接到客户端的socket



#### ServerSocket

| 构造函数     |                                                             |                               |
| ------------ | ----------------------------------------------------------- | ----------------------------- |
|              | ServerSocket（）                                            | 未绑定的服务器套接字          |
|              | ServerSocket（int port）                                    | 绑定端口                      |
|              | ServerSocket（int port，int backlog）                       | 端口与积压数量（默认50,FIFO） |
|              | ServerSocket（int port，int backlog，InetAddress bindAddr） | 绑定端口与积压数量和IP        |
|              |                                                             |                               |
| **常见方法** | accept（）                                                  | 监听套接字，并创捷连接Socket  |
| void         | close（）                                                   | 关闭，还会关闭输入出流        |
| void         | bind（SocketAddress endpoint）                              | 绑定指定IP                    |



#### Socket

| 构造函数     |                                         |                    |
| ------------ | --------------------------------------- | ------------------ |
|              | Socket（）                              | 创建未连接的套接字 |
|              | Socket（String host, int port）         | 指定IP与端口       |
|              | Socket（InetAddress address, int port） | 指定IP与端口       |
| **常用方法** |                                         |                    |
| void         | shutdownOutput（）                      | 关闭输出流         |
| void         | shutdownInput（）                       | 关闭输入流         |
| InputStream  | getInputStream（）                      | 获取输入流         |
| OutputStream | getOutputStream（）                     | 获取输出流         |
| void         | close（）                               | 关闭               |

**创建套接字后会尝试去连接指定服务器的端口**



### 2.1 客户端实例

```java
public class MyClient {

    public static void main(String[] args) throws IOException {

        Socket socket = new Socket("127.0.0.1",8080);

        DataOutputStream out = new DataOutputStream(socket.getOutputStream());
        out.writeUTF("客户端发送消息给服务器端");

        DataInputStream in = new DataInputStream(socket.getInputStream());
        System.out.println(in.readUTF());
        
        socket.close();
    }
}
```



### 2.2. 服务器端实例

```java
public class MyServer {

    public static void main(String[] args) throws IOException {

        ServerSocket serverSocket = new ServerSocket(8080,50);
        serverSocket.setSoTimeout(10000);

        new Thread( () -> {
            try{
                while(true){
                    Socket socket = serverSocket.accept();
                    DataInputStream in = new DataInputStream(socket.getInputStream());
                    System.out.println(in.readUTF());

                    DataOutputStream out = new DataOutputStream(socket.getOutputStream());
                    out.writeUTF("服务器给客户端的响应");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }
}
```









## 3. 数据报

UDP的面向无连接通信需要数据报



### 3.1 DatagramPacket

该类是UDP传递的数据报，即打包后的数据

```java
DatagramPacket(byte[] buf,int length)
```



### 3.2 DatagramSocket

用于发送和接送数据的数据报套接字，面向无连接的UDP

```java
try{
    DatagramSocket dsocket = new DatagramSocket();
}catch(SocketException e){
    e.printStackTrace();
}
```



### 3.3 简单通信

```java
public class Server {
    public static void main(String[] args) {

        byte[] buff = new byte[1024];
        DatagramPacket dp = new DatagramPacket(buff,buff.length);

        try {
            DatagramSocket socket = new DatagramSocket(8080);
            socket.receive(dp);
            String message = new String(dp.getData(),0,dp.getLength());
            System.out.println(message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

//————————————————————————————————————————————————————----------------------------------

public class Client {
    public static void main(String[] args) {

        try {
            InetAddress address = InetAddress.getLocalHost();
            DatagramSocket socket = new DatagramSocket();
            byte[] data = "客户端发送的消息".getBytes();
            DatagramPacket dp = new DatagramPacket(data,data.length,address,8080);
            socket.send(dp);
        } catch (SocketException | UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
```









## 4. 补充



### 4.1 URL

URL（Uniform Resource Locator）中文名为统一资源定位符，也称为网页地址，表示网页的资源。而URI（Uniform Resource Identifier）统一资源标识符，注意和URL区别，他不局限于网页地址的表示，可用于电子邮件、电话号码等各种标识。简单来说URL是URI的子集

| 构造方法      |                       |                                           |
| ------------- | --------------------- | ----------------------------------------- |
|               | URL（String address） | 创建URL对象                               |
| **常用方法**  |                       |                                           |
| InputStream   | openStream（）        | 返回InputStream 对象                      |
| URLConnection | openConnection（）    | 返回一个URLConnection实例，所有连接的超类 |

```java
public static void main(String[] args) throws IOException {

    String path = "http://www.baidu.com";
    URL url = new URL(path);
    
    InputStream in = url.openStream();
    
    // 或者使用URLConnection，URL采用了HTTP协议,所以返回HttpURLConnection对象
    // URLConnection conn =  url.openConnection();
    // HttpURLConnection connection = null;
    // if(conn instanceof  HttpURLConnection){
    //        connection = (HttpURLConnection) conn;
    //  }else{
    //      System.out.println("Please enter an HTTP URL.");
    //      return;
    //  }
    // InputStream in = connection.getInputStream()
    
    BufferedReader br = new BufferedReader(new InputStreamReader(in));
    String msg = null;
    while ((msg = br.readLine()) != null) {
        System.out.println(msg);
        System.out.println("\n");
    }
}
```





### 4.2 InetAddress 

表示Internet协议（IP）地址的封装，其没有构造函数，只能通过静态方法获取实例

|                    |                          |                      |
| ------------------ | ------------------------ | -------------------- |
| static InetAddress | getByName（String host） | 确定主机名称的IP地址 |
| String             | getHostAddress（）       | 返回IP字符串         |
| String             | getHostName（）          | 返回主机名           |






