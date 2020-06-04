> 源自笔者某次回去等通知的故事





## 1. jps

列出正在运行的虚拟机进程、及其pid，命令参数有：

-l：输出主类全限定类名

-v：虚拟机进程启动时的JVM参数



```
jps -l
```

![1591190104810](C:\Users\Howl\AppData\Roaming\Typora\typora-user-images\1591190104810.png)









## 2. jstat

监视虚拟机各种运行状态，命令参数有：

-gc：监视堆状况

-gcutil：与-gc一致，不同于显示百分比



```
jstat -gcutil pid 时间间隔 查询次数
jstat -gcutil 11564 250 20
```

![1591190303420](C:\Users\Howl\AppData\Roaming\Typora\typora-user-images\1591190303420.png)

```
Surviro from Eden Old MeteSpace CSS YGC总次数 YGC总花费时间 FGC总次数 FGC总时间 GCT垃圾回收总时间
```









## 3. jinfo

实时产看和调整虚拟机各项参数，参数有：

-flag [ +-name / name=value ] 来运行时修改参数



```
jinfo pid
jinfo 11564
```



![1591190518582](C:\Users\Howl\AppData\Roaming\Typora\typora-user-images\1591190518582.png)









## 4. jmap

生成堆转储快照（headdump），或者 设置参数 -XX：+HeadDumpOnOutOfMemoryError参数，溢出时自动生成快照文件，文件中可以获取到：

- 对象信息：类、成员变量、直接量以及引用值
- 类信息：类加载器、名称、超类、静态成员
- Garbage Collections Roots：JVM可达的对象
- 线程栈以及本地变量：获取快照时的线程栈信息，以及局部变量的详细信息



其参数有：

-dump：生成Java堆转储文件，然后用VisualVM来打开

```
jmap -dump:format=b,file=filename pid
jmap -dump:format=b,file=C:\Users\Howl\Desktop\2020-6-3-heapdump.hprof 11564
```



-histo：查看堆中对象详细信息，包括类，实例数量，合计容量

```
jmap -histo pid
jmap -histo 11564
```

![1591194899506](C:\Users\Howl\AppData\Roaming\Typora\typora-user-images\1591194899506.png)

* 可以定位哪个类溢出







## 5. jsatck

生成当前线程存储快照（Threaddump），常用于定于线程长时间停顿











## 6. 可视化工具





### 6.1 JConsole

查看各种堆、方法区、线程等信息



![1591191618971](C:\Users\Howl\AppData\Roaming\Typora\typora-user-images\1591191618971.png)



内存标签页：相当于jstat命令，可以查看堆和方法区的情况

线程标签页：相当于jstack命令，可以查看各线程停顿情况，可以检测死锁

类标签页：查看总加载类数目以及当前加载的类的数量

VM概要标签页：各种JVM参数





### 6.2 VisualVM

功能最强大的运行监控和故障处理程序之一，在JConsole的基础上可以生成查看dump文件，还有更多可安装插件的功能

![1591194467785](C:\Users\Howl\AppData\Roaming\Typora\typora-user-images\1591194467785.png)





可以生成dump，查看实例占用空间大小

![1591194998614](C:\Users\Howl\AppData\Roaming\Typora\typora-user-images\1591194998614.png)











## 7. 排查总结



### 7.1 CPU过高

* top P M X 找到占用高的进程id
  * top  -Hp pid 找到占用高的线程nid，转换16进制，（printf '%x\n' pid）
* jstack pid 来查看线程的详细信息
  * 线程状态（关注WAITING、BLOCKED），是否大量线程等待这个资源-----停顿情况
  * 死锁 （Deadlock），自动检测一下-----停顿情况
  * 然后根据打印的栈信息可定位代码位置-----查看死循环问题



### 7.2 频繁GC

问题一般是大量对象涌入撑满导致

* jstat -gcutile pid 查看是否频繁GC，根据次数和时间对比
  * 是否堆年轻代老年代需要调优



### 7.3 OOM

无非就是内存泄漏，年轻代大量涌入无法清除，进入老年代也无法清除

* 生成dump快照 或 自动设置的快照-XX：-XX：+HeadDumpOnOutOfMemoryError
* 用分析工具分析：查看哪个类和实例数过大，本来就定位线程了，只需看该线程的对象信息即可



### 7.3 死锁

* 直接JConsole排查死锁
* jstack 查看wating on Condition、等条件状态

