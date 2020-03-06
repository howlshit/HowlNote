String是最常操作的引用类型了，但也是我最怕的地方（因为不熟悉），最怕还是String和Array同时出现，所以现在先写下一篇博客熟悉熟悉字符串







### 0. 字符串非空

```java
if( str == null || str.length() == 0){
    System.out.println("11");
}
```




### 1.字符串的不可变性

我们常说String是不可变的，但的对应的变量为什么还是能"改"为不同的字符串呢？

来看一下String的部分源码

![1577583581894](C:\Users\Howl\AppData\Roaming\Typora\typora-user-images\1577583581894.png)

* 在114行可以看出，String内部使用数组来存储，使用了private与final修饰，且内部没有修改value数组的方法，所以一旦定义就不能修改，即String的不可变性

**不可变性的好处**

* 不可变才有字符串常量池，优化空间
* 存储hashCode的，因为经常使用
* 线程安全，因为不可变





### 2. 但为什么我们的变量还是能改为不同的字符串呢？

```java
String a = new String("String不可变性");
a = new String("String确定不可变吗？");
System.out.println(a);

//输出
//String确定不可变吗？
```

* 其实字符串都没有变，变的是a的引用地址，这样看起来貌似字符串改变了罢了





### 3. String Pool

创建字符串会放到字符串常量池中，下次创建相同的字符串会从常量池中拿取引用，所以相同字符串引用相同

```java
String a = "String不可变性";	//字面量
String b = "String不可变性";
System.out.println(a == b);

//输出
//true

//但new关键字是在堆中复制一个副本，引用地址给了变量，所以指向的对象的地址不同
String a1 = new String("String不可变性");	//对象
String b1 = new String("String不可变性");
System.out.println(a1 == b1);

//输出
//false
```



### 4. 连接符 “+”

```java
//字符串常量，JVM会优化，在字符串常量池直接存放“123”
String a = "1" + "2" + "3";

//字符串变量，会在底层创建StringBuilder,然后append，最后toString返回
String a = new String("1") + new String("2");
```



### 5. 构造方法及常用方法

| 构造函数                                 | 解释                                                       |
| ---------------------------------------- | ---------------------------------------------------------- |
| String(byte[] bytes, String charsetName) | 构造一个新的String用指定的字节数组和解码                   |
| String(String original)                  | 初始化新创建的String对象，新创建的字符串是参数字符串的副本 |
| String(StringBuffer buffer)              | 其中包含当前包含在字符串缓冲区参数中的字符序列             |
| String(StringBuilder builder)            | 其中包含当前包含在字符串构建器参数中的字符序列             |



| 返回值    | 方法名                                    | 解释                                                   |
| --------- | ----------------------------------------- | ------------------------------------------------------ |
| char      | charAt(int index)                         | 返回指定索引处的字符                                   |
| int       | compareTo(String anotherString)           | 按字典顺序比较两个字符串                               |
| int       | compareToIgnoreCase(String str)           | 按字典顺序比较两个字符串，忽略大小写                   |
| String    | concat(String str)                        | 将指定的字符串连接到该字符串的末尾                     |
| boolean   | contains(Strin str)                       | 判断字符串中是否有指定的子字符串                       |
| boolean   | equals(Object anObject)                   | 判断值是否相同                                         |
| byte[]    | getBytes(String charsetName)              | 使用命名的字符集将此String编码为字节序列               |
| int       | length()                                  | 返回此字符串的长度                                     |
| int       | indexOf(int ch,int fromIndex)             | 返回指定字符第一次出现的字符串内的索引，从指定索引开始 |
| int       | lastIndexOf(int ch)                       | 返回指定字符最后一次出现的字符串内的索引               |
| String [] | split(String regex)                       | 将此字符串分割为给定的字符串                           |
| String    | substring(int beginIndex,int endIndex)    | 返回一个字符串，该字符串是此字符串的子字符串。         |
| char[]    | toCharArray()                             | 将此字符串转换为新的字符数组                           |
| String    | trim()                                    | 返回一个字符串，删除任何前导和尾随空格                 |
| String    | replace(old char/String, new char/String) | 返回一个新字符串                                       |
| boolean   | matches(String regex)                     | 是否匹配正则                                           |



**计算字串出现的次数**

```java
public int StrTimes(String a,String b){
    
    int count = 0
    int fromIndex = 0;
    
    while( (fromIndex = a.indexOf(b,fromIndex)) != -1 ){
        fromIndex += b.length();
        count++;
    }
    
    return count;
}
```





### 6. StringBuilder和StringBuffer

* StringBuilder线程不安全，速度稍快
* StringBuffer  线程安全，速度稍慢

他俩的实现方式是创建一个可变的底层数组，且提供各种改变数组序列的方法



这里来看StringBuffer  ，StringBuilder类似，就不讲了

* 二者都继承AbstractStringBuilder

![1577590451659](C:\Users\Howl\AppData\Roaming\Typora\typora-user-images\1577590451659.png)

![1577590484557](C:\Users\Howl\AppData\Roaming\Typora\typora-user-images\1577590484557.png)

* 66行super()默认构造函数使用父类的，默认大小为16，底层也是字符数组
* 会自动扩容，扩为原数组的2倍加2，这时是创建一个新的数组，并将原数组复制到新数组（与集合扩容类似）

![1577590653484](C:\Users\Howl\AppData\Roaming\Typora\typora-user-images\1577590653484.png)

* 线程安全来源于synchronized





**常见方法**

构造函数

| 构造函数                   | 解释                                                         |
| -------------------------- | ------------------------------------------------------------ |
| StringBuffer()             | 构造一个没有字符的字符串缓冲区，初始容量为16个字符           |
| StringBuffer(String str)   | 构造一个初始化为指定字符串内容的字符串缓冲区，大小为str.length()+16 |
| StringBuffer(int capacity) | 构造一个没有字符的字符串缓冲区和指定的初始容量               |

常用方法

| 返回值       | 方法名                                  | 解释                                       |
| ------------ | --------------------------------------- | ------------------------------------------ |
| StringBuffer | append(String str)                      | 将指定的字符串附加到此字符序列             |
| int          | capacity()                              | 返回当前容量                               |
| StringBuffer | delete(int start, int end)              | 删除此序列的子字符串中的字符               |
| StringBuffer | insert(int offset, String str)          | 将字符串插入到此字符序列中                 |
| int          | length()                                | 返回长度（字符数）                         |
| StringBuffer | replace(int start, int end, String str) | 用指定的String中的字符替换此序列的子字符串 |
| StringBuffer | reverse()                               | 导致该字符序列被序列的相反代替             |
| String       | toString()                              | 返回表示此顺序中的数据的字符串             |
|              | **并且有String的方法**                  |                                            |





**简单操作**

可以链式操作，因为返回的是this本对象



```java
StringBuffer stringBuffer = new StringBuffer();
stringBuffer.append("123")
    .append("abc")
    .append("-----")
    .delete(0, 3)
    .replace(3, 5, "dddd")
    .insert(7, "e");

System.out.println(stringBuffer.length());
System.out.println(stringBuffer.capacity());
System.out.println(stringBuffer.toString());
System.out.println(stringBuffer.reverse());
```

```xml
11
16
abcdddde---
---eddddcba
```





### 7. 总结

String适合操作少量数据

StringBuffer适合线程安全操作大量数据

StringBuilder适合单线程操作大量数据







## 8 补充

java是按值传递，不是引用传递，下面举例



```java
public class Pass {
	
	public static void test(int a, String b, User user){
		
		a = 2;
		b = "bb";
		user.name = "Change";
		
		System.out.println(a);
		System.out.println(b);
		System.out.println(user.name);
		
	}

	public static void main(String[] args) {
		
		int a = 1;
		String b = "b";
		User user = new User("Howl");
		
		test(a, b, user);
		System.out.println("--------------上面是传递改变，下面是未传递前----------------");
		
		System.out.println(a);
		System.out.println(b);
		System.out.println(user.name);
		
	}
}
```

```
2
bb
Change
--------------上面是传递改变，下面是未传递前----------------
1
b
Change
```



**这里有个冲突分歧怪异点：为什么String和user对象只有user才能被改变？难道只有自定义对象才是按值传递？**

非也，基本类型是按值传递这里大家没有意见把，主要在于引用类型是按值传递还是引用传递？其实这里应该在上面的test方法里加多一个语句才好理解



```java
public static void test(int a, String b, User user){
		
		a = 2;
		b = "bb";
		user = new User("Change");	// 加的语句在这里
		// user.name = "Change";
		
		System.out.println(a);
		System.out.println(b);
		System.out.println(user.name);
}
```

```
2
bb
Change?
--------------上面是传递改变，下面是未传递前----------------
1
b
Howl
```



**改变语句后，引用传递的效果就失去了，下面来分析一下：**

```java
public static void test(int a, String b, User user){
		
		a = 2;		// 参数a传递过来的是副本，即按值传递
		b = "bb";	// 这里String类型有点特殊，b = “bb” 相当于 b = new String(“bb”),新建了一个对象
		user = new User("Change");	// 把user的指向改为new User("XXX"),这里就明示出不是引用传递
		// user.name = "Change";	// test方法里和main方法里的user都指向堆内存同一个对象，当然可以改变
		
		System.out.println(a);
		System.out.println(b);
		System.out.println(user.name);
}
```



**总结一下：基本类型按值传递，引用类型的传参传的是地址的副本，在给参数中的引用类型赋值时，改变的是参数的地址，即不属于引用传递，下面画图更好理解**



**没加入语句前**

![1580789412087](C:\Users\Howl\AppData\Roaming\Typora\typora-user-images\1580789412087.png)



**加入语句前**

![1580789554648](C:\Users\Howl\AppData\Roaming\Typora\typora-user-images\1580789554648.png)