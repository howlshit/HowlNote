本文只是萌新初步了解JVM，本来打算写给自己看的，由于知识有限，写得不好甚至有错，欢迎指正



2020.2.12回来填坑





# 1. 程序的运行流程

我们coding完后点击IDE的运行，程序就跑起来了，怎么回事？

首先我们写的源文件叫.java文件，然后点击IDE的运行在硬盘会生成.class字节码文件，接着Java虚拟机从硬盘加载.class字节码文件，再者内部操作和解析成电脑能识别的机械码，最后CPU执行

我们要重点关注的下面框框的部分，也就是JVM了

![1577409121078](C:\Users\Howl\AppData\Roaming\Typora\typora-user-images\1577409121078.png)





# 2. JVM

别那么着急，首先得看看JVM的体系结构：

*  类加载器（ClassLoader）用来加载.class文件
*  运行时数据区（方法区、堆、Java栈、本地方法栈、程序计数器），这么复杂先不要管他
* 执行引擎，来执行.class字节码文件或执行本地方法

这时上面框框的内容就可以稍微详细一点了

**注意这时的图并不是完整且准确的，为了简便而改名或省略了，最后面会放出完整图的**

![1577410777602](C:\Users\Howl\AppData\Roaming\Typora\typora-user-images\1577410777602.png)

那么，我们从上往下开始认识这些结构





## 2.1 类加载器

负责加载.class字节码文件到 Java 虚拟机中，只有把文件放入虚拟机才能被读取



#### 什么时候开始加载呢？

当然是动态加载的！即需要用到时才加载，这样节省了很多内存空间，防止内存溢出



#### 用什么来加载.class文件呢？

那就是类的加载器了，类加载器默认有三种，还有一个自定义类加载器：

* Bootstrap ClassLoader，负责加载rt.jar里的所有类，rt.jar就是运行时的核心jar包，包含java.*
* Extension ClassLoader，负责加载java平台中扩展功能的jar包，包含javax.*
* App ClassLoader，负责加载classpath中的jar包及目录中class，即自己编写的.class文件和开发jar包
* 自定义类加载器继承ClassLoader，并重写findClass方法，重点在于字节码文件的获取

![1577412395704](C:\Users\Howl\AppData\Roaming\Typora\typora-user-images\1577412395704.png)

这里就有一个看起来高大尚的名词**双亲委派**，不用觉得很难，所谓的双亲委派指的是：

* 类加载器收到类加载请求后，首先把请求委派给父加载器，父加载器再向上委派，如此类推
* 如果顶级父类找不到这个要加载的类，则给子类加载器去加载，还是如此类推



为什么要双亲委派不麻烦吗？

为了安全性

* 防止内存中出现多份同样的字节码
* 还有防止覆盖重要类，比如我绕开编译器，在记事本写了一个Object类，加载时首先双亲就加载根类Object了，我写的Object就不会被加载



缓存机制：还有某个类被加载后就会把类的实例放到内存中，下次直接用内存中实例，不用再次加载了



#### 类的加载

1. 通过全限定名获取字节文件
2. 将字节文件的结构转换成方法区运行时的数据结构
3. Java堆中生成类对象，作为对方法区中数据访问的入口





加载过程分类三步：

* 加载，加载.class文件到JVM，并创建对应类的实例
* 链接，又分三步:
  * 验证：字节文件的信息是否符合虚拟机的要求（文件格式、元数据、字节码、符号引用验证）
  * 准备：为类的静态变量分配内存（方法区），并将其初始化为默认值（0，null）
  * 解析：将类的二进制数据中的符号引用替换成直接引用
    * 符号引用：用符号表示引用的目标，比如java.lang.System.out.println()代表了该类，还没有被加载
    * 直接引用：直接指向目标的指针，已经加载了
* 初始化：为类的静态变量赋予给定的初始值，原本的初始值是0或null

![1577414951816](C:\Users\Howl\AppData\Roaming\Typora\typora-user-images\1577414951816.png)







## 2.2 运行时数据区

类加载完后就开始给新生对象分配内存了，先来look look 虚拟机的内存结构把

![1577416555607](C:\Users\Howl\AppData\Roaming\Typora\typora-user-images\1577416555607.png)

浅绿色为线程共享

浅橙色为线程私有

其中：

* 方法区：已被加载的类信息，常量，静态变量
* 虚拟机栈：为每一个执行方法创建栈帧（包含了局部变量，方法出口等，递归太多栈空间越大）
* 本地方法栈：执行本地方法 C / C++
* 程序计数器：当前线程所执行的字节码的行号指示器
* 堆：存放对象实例（太多会内存溢出，不需要连续空间，可动态增加内存，基本分代处理）
  * 年轻代
    * Eden
    * SurviorFrom
    * SurviorTo
  * 老年代
  * 永久代（1.8转变为直接内存中的元空间）
* 运行时常量池：1.8放入堆中了（字面量、符号引用、还可动态生成String）



**简述一下内存分配**

准备了两个类

```java
public class BeanTest {

	private int id;
	private String name;
	
	//各种Getters/Setters
}
```

```java
public class JVMTest {
	
	public static void main(String[] args) {
		
		BeanTest beanTest = new BeanTest();
		beanTest.setName("Howl");
		System.out.println(beanTest.getName());
		
	}
}
```

* JVMTest首先被加载到JVM的方法区，存储类信息，比如类名，类的方法等
* 找到JVMTest方法入口（main），为main创建栈帧压入栈，执行函数
* 执行第一条语句，`BeanTest beanTest = new BeanTest();`，方法区没有这个类的元数据，动态加载
* 加载后为BeanTest实例在堆中分配内存，然后调用构造函数初始化该实例（该实例持有指向方法区对应类的数据，后面有用）
* 执行第二条语句，`beanTest.setName("Howl");`，该实例根据指向去方法区找到对应类的数据（方法表），获取对应函数的字节码地址
* 为该函数创建栈帧，执行函数，执行完退栈，如此类推





## 2.3 执行引擎

当然是根据调配的指令顺序，依次执行程序指令拉



**这里面有个技术需要讲一下下，JIT即时编辑器**

JVM加载了.class文件后逐条读取并解析成机器码给CPU执行，我们当然不满足于此，有没有方法提高效率呢？

答案是有的，用 `JIT即时编辑器`



思路是这样的：

* 对于热点代码（经常使用的代码，多次调用的方法，循环体），我们重新编译，优化给CPU执行，这部分优化的代码会提高效率
* 因为重新编译也需要内耗，所以我们只对热点代码重编，对于其他代码就直接解析器解析给CPU执行了



我们Sun公司使用的虚拟机是HotSpot，它采用计数器的方式（方法调用计数器，回边计数器）来判断是否为热点代码，当超过一定的数值时判断为热点代码

![1577419597524](C:\Users\Howl\AppData\Roaming\Typora\typora-user-images\1577419597524.png)





# 3. 整合图

是时候放出稍微标准的图了，顺带提一下没有讲到的内容

![1577420164175](C:\Users\Howl\AppData\Roaming\Typora\typora-user-images\1577420164175.png)









### 3.1 新生代与老年代



**新生代**：存放刚创建和年轻的对象，若生存足够长（15）没有被回收会被移入老年代，这里会频繁创建也就会频繁MinorGC垃圾回收



Minor GC（复制算法）：

1. 将Eden和ServivorFrom中存活的对象复制到ServivorTo（若年龄达到15移入老年代）
2. 同时把这些对象的年龄增加1（若ServivorTo不够就移入老年区）
3. 清空Eden和ServivorFrom，最后，ServivorFrom 和 ServivorTo互换，原ServivorTo成为下一次GC的ServivorFrom区



Eden：新对象的出生地 （新对象若过大，直接分配到老年区），Eden内存不够会触发Minor GC

ServivorFrom：保留了一次MinorGC过程中的幸存者

ServivorTo：上一次GC的幸存者，作为这一次GC的被扫描者



****



**老年代**：对象比较稳定，所以Major GC不会频繁执行。进行Major GG前一般会先进行一次Minor GC，使得有新生代的对象晋入老年代，导致空间不够用才触发，当无法找到足够大的连续空间分配给新创建的较大对象时也会提前触发一次Major GC进行垃圾回收腾出空间



Major GC（标记-清除算法）：

1. 扫描所有老年代，标记存活的对象
2. 回收没有标记的对象
3. 当老年代也满了，就会抛出OOM（out of memory）异常



****



**永久代**：内存的永久保存区域，主要存放class和Meta（元数据），GC不会在主程序运行期间对永久区域进行清理，这导致永久代区域随着类加载的class增多而胀满，最终抛出OOM（Out Of Memory）异常

在1.8中，永久代被直接空间里的元空间代替，即大小受实际内存限制，不是虚拟机限制



****



**Full GC、Major GC**

Full GC：收集年轻代，老年代，永久代
Major GC：只收集老年代







### 3.2 JVM、JMM、Java的内存模型

![c9ad2bf4-5580-4018-bce4-1b9a71804d9c](C:\Users\Howl\Desktop\c9ad2bf4-5580-4018-bce4-1b9a71804d9c.png)

![java-memory-model-5](C:\Users\Howl\Desktop\java-memory-model-5.png)

**后二者连接后产生的问题**

* 可见性：修改值后放入缓存，其他线程读取主存后导致值不一致
* 竞争性：从主存读取值后，各自用引用计数器操作，写回主存导致多次操作

















### 3.3 垃圾回收（GC）

程序在运行过程中，虚拟机会自动帮我们清理程序中不再需要的垃圾，减轻内存负担。

堆是垃圾回收的主战场，里面存放了大量实例

不像C语言，申请空间后需要free()来释放空间，但也不要因为有垃圾回收就不理会内存了



**怎么判断是否为垃圾呢？**

* 引用计数法（被引用时数量+1，引用失效-1，一直为0即为垃圾，但不能解决循环引用，虚拟机不使用）
* 可达性分析算法（不会，埋坑）



**判断完就到回收垃圾算法了**

- 标记-清除算法：将存活的对象进行标记，然后清理掉未被标记的对象
- 复制算法：将内存划分为大小相等的两块，每次只使用其中一块，当这一块内存用完了就将还存活的对象复制到另一块上面，然后再把使用过的内存空间进行一次清理
- 标记-整理算法：让所有存活的对象都向一端移动，然后直接清理掉端边界以外的内存
- 分代收集算法
  - 新生代使用: 复制算法
  - 老年代使用: 标记 - 清除 / 标记 - 整理 算法











### 3.4 JVM参数与调优

```java
-XX:MetaspaceSize=128M -XX:MaxMetaspaceSize=256M -Xms256m -Xmx256m
```

具体大小设置还得看环境，回来填坑









### 3.5 补充

* 字面量：文本字符串，final常量等
* 符号引用：
  * 类和接口的全限定名(Fully Qualified Name)
  * 字段的名称和描述符号(Descriptor)
  * 方法的名称和描述符







****

参考

[JavaGuide](<https://github.com/Snailclimb/JavaGuide#jvm>)

[Java3y](<https://mp.weixin.qq.com/s?__biz=MzI4Njg5MDA5NA==&mid=2247484721&idx=2&sn=73b2762abd8c54cdc8b6bb8b5692384b&chksm=ebd74430dca0cd262c0cd72509e8e9df71287eb74d3e3e56430934df7c60db38a78824a80a4a&token=1676899695&lang=zh_CN###rd>)

[张凯](https://me.csdn.net/qq_41701956)

[Janeiro](<https://www.cnblogs.com/yuechuan/p/8984262.html>)

[redcreen](<https://www.cnblogs.com/redcreen/archive/2011/05/04/2037057.html>)

工具

[ProcessOn](<https://www.processon.com/>)