## 1. 流

1. 不同设备之间的数据传输就是流，可以抽象理解供水厂到家庭之间的水管，水（数据）在管道传输，就成了流



2. 根据流向分为输出流和输入流

* 数据从文件流向程序称为输入流

* 数据从程序流向文件称为输出流



3. 根据数据类型分为字节流和字符流

* 字节流处理字节数据
* 字符流处理字符数据



4. 按处理数据分为节点流和处理流

* 节点流
  * 文件流
  * 数组流
  * 管道流
* 处理流
  * 缓冲流
  * 转换流
  * 基本数据流
  * 对象序列化流
  * 打印控制流



5. IO流属于阻塞操作，一般项目中应放到子线程中，避免阻塞主线程





**其中几个常见流之间的继承关系如下**

![5763525-b9823af16e7843da](C:\Users\Howl\Desktop\5763525-b9823af16e7843da.png)





## 2. 各种流介绍



### 2.1 File

在了解各种流之前，先来看看File类



**其静态字段有：**

| Modifier and Type | Field             | Description      |
| ----------------- | ----------------- | ---------------- |
| static String     | pathSeparator     | 系统的路径分隔符 |
| static char       | pathSeparatorChar | 系统的路径分隔符 |
| static String     | separator         | 系统的名称分隔符 |
| static char       | separatorChar     | 系统的名称分隔符 |



以windows系统为例上面的字段值为：

```java
public static void main(String[] args) {

	System.out.println(File.pathSeparator);
	System.out.println(File.pathSeparatorChar);
	
	System.out.println(File.separator);
	System.out.println(File.separatorChar);
}
```

```
;
;
\
\
```







**其构造函数**，是对文件系统的映射，并不是硬盘上真实的文件，可以不存在，但真正被当参数应用的时候不存在就报错，所以得处理异常

| Modifier and Type | Constructor           | Description                  |
| ----------------- | --------------------- | ---------------------------- |
| File              | File(String pathname) | 从字符串路径参数创建文件实例 |



```java
File file = new File("C:\\Users\\Howl\\Desktop\\FileInputStream.txt");
```





**其方法**

| Modifier and Type | Method          | Description                    |
| ----------------- | --------------- | ------------------------------ |
| boolean           | delete          | 删除文件或目录                 |
| boolean           | exists          | 文件或目录是否存在             |
| File              | getAbsolutePath | 返回此实例的绝对路径           |
| String            | getName         | 返回此实例的目录或文件名       |
| long              | length          | 此实例的长度                   |
| boolean           | mkdirs          | 创建目录，包括父目录，不是文件 |
| boolean           | createNewFile   | 创建文件                       |
| File              | getParentFile   | 返回父类目录文件类             |
| boolean           | isFile          | 判断是否文件                   |



举个创建/删除目录的例子，还挺好玩的，注意目录有内容是不能被删除的，要先把里面东西删完，这里不介绍了

```java
public static void main(String[] args) throws IOException {
	
	File file = new File("C:/Users/Howl/Desktop/test/Howl.txt");
	File dir = file.getParentFile();
	
	if(!dir.exists()){
		dir.mkdirs();
		file.createNewFile();
	}else{
		file.delete();
		dir.delete();
	}
}
```





### 2.1 字节流

能处理各类数据，比如图片视频，这种流解释为原始的二进制数据，二进制不需要编码解码，比文本效率高，可移植，缺点是人们看不懂二进制内容，当读入数据到内存时，用一个字节或字节数组来存储，写出时同理，并且无论使用什么流，底层传输的都是二进制，所以字节流是一切流的基础



#### 2.1.1 FileInputStream和FileOutputStream

从本地文件读写字节流，先看二者构造函数，还有各自的方法

```java
FileInputStream(File file)						//参数为一个File类型		
FileOutputStream(File file, boolean append)		//第二个参数表示覆盖还是追加
```

FileInputStream

```java
int read()								//返回下一个字节的值，到了末尾返回-1
int read(byte[] b)						//把字节放入数组b,返回读取的字节个数
int read(byte[] b,int off,int len)		//把字节放入数组b，后两参数代表始末位置,返回读取的字节个数
void close()							//关闭流
```

FileOutputStream

```java
void write(int b)						//只读取低8位
void write(byte[] b)  					//和上面同理
void write(byte b,int off,int len)
void close()							//关闭流
```

例子

```java
File file = new File("C:/Users/Howl/Desktop/test/FileOutputStream.txt");
byte[] bytes = {127,10,10,20,0,13,13,20,30,10,40,127,126,15,124};	// 数据字节数组

FileOutputStream fs = new FileOutputStream(file,true);
fs.write(bytes);

FileInputStream fi = new FileInputStream(file);
fi.read(bytes);

for(byte a : bytes){
	System.out.println(a);
}

fi.close();
fs.close();
```





#### 2.1.2 ObjectInputStream和ObjectOutputStream

对象的序列化和反序列化，前提是该对象实现了序列化接口Serializable



其构造方法

```java
ObjectInputStream (InputStream in)
ObjectOutputStream (OutputStream out)   //参数类型为字节流
```

其方法

| 返回值 | 函数 | 说明 |
| :--- | :-- | --:|
| void | writeObject(Object obj) | 往流中写入一个对象 |
| Object | readObject() | 从流中读取一个对象 |

例子

```java
public static void main(String[] args) {
	
	//准备
	User user = new User(20,"1210911104@qq.com","Howl");
	File file = new File("C:\\Users\\Howl\\Desktop\\ObjectOutputStream.txt");
	
	//自动关闭资源
	try( FileOutputStream fos = new FileOutputStream(file);
		 ObjectOutputStream obs = new ObjectOutputStream(fos);){
		
		if(!file.exists()){
			file.createNewFile();
		}
	
		//先写入对象
		obs.writeObject(user);
		System.out.println("写入成功\n");
		
		//读取对象
		FileInputStream fis = new FileInputStream(file);
		ObjectInputStream ois = new ObjectInputStream(fis);
		User user2 = (User) ois.readObject();
		System.out.println("读入成功\n" + user2.getId() + "--" + user2.getEmail() + "--" + user2.getPassword());
		
		
	} catch (IOException e) {
		e.printStackTrace();
	} catch (ClassNotFoundException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
}
```

打印，桌面多了一个ObjectOutputStream.txt文件，存放序列化的对象，这些是字节流文件，所以是会乱码的

```xml
写入成功

读入成功
20--1210911104@qq.com--Howl
```



#### 2.1.3 BufferedInputStream和BufferedOutStream

缓冲流主要作用是为其他流提供缓冲功能，先把数据放入缓冲流中，等程序把处理完再往缓存中放入数据



构造函数

```java
BufferedInputStream(InputStream in)
BufferedOutputStream(OutputStream out)
```

方法

```java
read()
read(byte[] b, int off, int len)
    
write(byte[] b, int off, int len)
```

例子（传输图片）

```java
public static void main(String[] args) throws IOException {
	
	File file1 = new File("C:/Users/Howl/Desktop/test/1.png");
	File file2 = new File("C:/Users/Howl/Desktop/test/2.png");
	
	if(!file2.exists()){
		file2.createNewFile();
	}
	
	FileInputStream fis = new FileInputStream(file1);
	BufferedInputStream bis = new BufferedInputStream(fis);
	
	FileOutputStream fos = new FileOutputStream(file2);
	BufferedOutputStream bos = new BufferedOutputStream(fos);
	
	int num;
	byte[] bytes = new byte[1024];
	while( (num = bis.read(bytes)) != -1){
		bos.write(bytes, 0, num);
	}
	
	// 关闭各种流
}
```



#### 2.1.4 DataInputStream和DataOutputStream

主要传输基本类型的数据，接收的参数是InputStream





#### 2.1.5 PrintStream

打印流提供了非常方便的打印功能，可以打印任何的数据类型，接收的参数是OutputStream



来分析一下日常见到的System.out.println()，其中out在System类中定义 ，可以看出out属于PrintStream字节打印流

```java
public final class System {
    
    // 省略各种定义的变量
    public final static PrintStream out = null;
}
```

再进去看看PrintStream类，可以发现print方法重载了一堆，这里只写几个，难怪能打印那么多类型

```java
public void print(boolean b) {}
public void print(int i) {}
public void print(String s) {}
```



print和println什么区别？

```java
public void println(int x) {
    synchronized (this) {
        print(x);
        newLine();
    }
}

public void print(String s) {
    if (s == null) {
        s = "null";
    }
    write(s);
}


private void write(String s) {
    try {
        synchronized (this) {
            ensureOpen();		// 确保流打开着
            textOut.write(s);	// textOut属于BufferedWriter
            textOut.flushBuffer();	//
            charOut.flushBuffer();	// charOut属于OutputStreamWriter
            if (autoFlush && (s.indexOf('\n') >= 0))
                out.flush();
        }
    }
    catch (InterruptedIOException x) {
        Thread.currentThread().interrupt();
    }
    catch (IOException x) {
        trouble = true;
    }
}
```

可以看出后者调用了前者，不过在同步中增多了一个newLine()方法来换行，而print调用write()方法，write内部又调用各种方法









### 2.2 字符流

只能处理字符数据



#### 2.2.1 InputStreamReader和OutputStreamWriter

字节流转换成字符流

其构造函数，第一个参数是字节流，第二参数指定是字符集一般写utf-8

```java
InputStreamReader(InputStream in, Charset cs)
OutputStreamWriter(OutputStream out, Charset cs)
```

转换流的方法和2.1.1方法很类似但区别在于参数是byte还是int或String类型

InputStreamReader

```java
int read()
int	read(char[] cbuf, int offset, int length)
```

OutputStreamWriter

```java
void write(int c)
void write(char[] cbuf, int off, int len)
void write(String str, int off, int len)
```

例子

```java
public static void main(String[] args) throws IOException {
		
		File file1 = new File("C:/Users/Howl/Desktop/test/1.txt");
		File file2 = new File("C:/Users/Howl/Desktop/test/2.txt");
		
		if(!file2.exists()){
			file2.createNewFile();
		}
		
		FileInputStream fis = new FileInputStream(file1);
		FileOutputStream fos = new FileOutputStream(file2,true);
		
		InputStreamReader isr = new InputStreamReader(fis,"UTF-8");
		OutputStreamWriter osr = new OutputStreamWriter(fos,"UTF-8");
		
		int num;
		char[] arr = new char[10];
		while( (num = isr.read(arr)) != -1 ){
			osr.write(arr, 0, num);
		}
		
		osr.close();  // close里面有flush方法刷新，各种关闭
	}
```





#### 2.2.2 FileReader和FileWriter

从本地文件读写字符流，和上面的主要区别是上面读取字节流，能指定编码，而这里读取的是字符流，只能使用系统默认编码

构造函数，并且方法和父类一致，这里不介绍了

```java
FileReader(File file)
FileWriter(File file, boolean append)
```



#### 2.2.3 BufferedReader和BufferedWriter

带缓冲区的流，减少访问磁盘次数，提高性能，其构造函数

```java
BufferedReader(Reader in)		//以字符流为参数，例如FileReader
BufferedWriter(Writer out)		//以字符流为参数，例如FileWriter
```

BufferedReader有一个特有方法

```java
String readLine()			//读取一行字符，返回字符串
```

BufferedWriter也有一个特殊方法

```java
void newLine()				//写入行分隔符
```



