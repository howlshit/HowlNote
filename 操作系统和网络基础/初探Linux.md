> 这是一个小小新手根据自己对Linux的理解而写下的笔记，记录的是大体的学习内容。记录的笔记不全面，甚至没有整体的概念，但也希望能够给部分人一些入门的帮助，实机基于CentOS 7。





#### 导语：学习一件新事物先从大体把握，了解其组成部分然后再分模块去学习，笔者是依据这样写下笔记的





# 1. 系统的组成

Linux的组成可大致分为：**内核**、**shell**、**文件系统**、**应用程序**

内核（Kernal）：Linux最主要的部分，操作整个计算机的资源，提供内存管理，进程管理，驱动管理等基本功能

Shell：提供用户与内核进行交互的接口，Shell是一个解析器负责解析命令，将用户传过来的命令解析过后通知内核执行相应的服务、Shell也可以解析Shell脚本，即Shell脚本可以像用户传命令过来一样有被执行的能力

文件系统：提供文件存储、管理等功能。Linux抽象出虚拟文件系统以便适应不同类型的文件系统

应用程序：计算器，编辑器，GIMP等

![u=4135787709,4096059424&fm=27&gp=0](C:\Users\Howl\Desktop\u=4135787709,4096059424&fm=27&gp=0.jpg)











# 2.文件系统

从一切皆对象的Java变成一切皆文件的Linux了，因为Linux的系统资源都是以文件形式存在。首先来了解文件系统，其实就是认识各种目录结构及其作用，这里的内容有个印象即可，真要用到回来再看看



### 2.1 文件系统结构

-   /   ：根目录
-   /bin：二进制可执行文件，即常用的命令（cd、ls、mkdir）
-   /sbin：二进制可执行文件，root专属命令
-   /boot：系统引导文件
-   /dev：设备文件
-   /etc：系统配置文件
-   /home：各用户的目录
-   /root：管理专属目录，不放在home下
-   /var：运行时需要改变数据的文件
-   /lib ：库
-   /usr ：系统应用程序
-   /tmp：各种临时文件
-   /opt：第三方应用程序
-   /proc：系统内存映射，虚拟文件系统
-   /mnt：临时文件系统挂载点
-   /media：移动设备挂载点





### 2.2 文件类型、权限、组

```
# 文件类型
–：普通文件
d：目录文件
b：块文件
c：字符文件
l：符号链接文件
p：管道文件pipe
s：套字节文件socket


# 权限
r：可读，表示文件可读，目录可看列表
w：可改，表示文件可改，目录可在内部新建和删除文件
x：可运行，表示文件可运行，目录可进入
可用数字表示：4=r，2=w，1=x
467分别表示：可读，可读可写，可读可写可运行
```



输入命令`cd /`，`ls -l`显示根目录文件，首字母就表示文件类型，剩余表示权限，之后的数字表示连接数，跟着两个是所有者和所属组

![1583848556315](C:\Users\Howl\AppData\Roaming\Typora\typora-user-images\1583848556315.png)





### 2.3 关于文件目录的命令

```
cd：进入目录
ls：列出目录
mkdir：创建目录
touch：创建文件
mv：移动文件
rm：删除
cp：复制
```





### 2.4 文件打包与压缩

归档：也称打包，将多个文件或目录一起建立归档，一般是形成 .tar 文件

压缩：利用算法对文件进行处理，达到压缩大小的目的

注意：是不能直接压缩目录的，将多个文件或目录打包成一个 .tar 文件然后才能进行压缩，所以打包和压缩一般是同时进行的。压缩格式有：bz2，xz，zip，gz（最常见）

```
tar -zcvf：选项从左到右表示gzip压缩，打包文件，显示过程，指定文件名
tar -zxvf：其中z表示解压缩（同一选项不同功能），x表示解包
tar -zcvf file.tar.gz  file：表示将file打包与压缩并命名为file.tar.gz，其后缀是为了方便辨认
```





### 2.5 文本编辑器Vim

![1583855218726](C:\Users\Howl\AppData\Roaming\Typora\typora-user-images\1583855218726.png)

在一切皆文件下，学会使用文本编辑器很关键，这里介绍Vim

在没有学Vim之前，笔者进入文本编辑器看着命令行不知所措，乱按一通没有效果，只好关机重启，居然被小小的文本编辑器打败了，不好意思说是计算机专业的，这和windows的记事本完全不一样。在进入Vim之前，请先看清楚使用方法，不然进去就和笔者一样要重启解决



**一、Vim的三种模式：**

Normal：默认进入时的模式，输入会被当成命令

```
i：进入Insert模式
gg：跳转文头
G：跳转文末
/：查找
n：下一个
y：复制
p：粘贴
x：删除
u：撤回
```

Insert：输入的内容会插入到文件中（按 i 进入）

```
ESC键：退出Insert，进入Normal模式
```

Command ：在Normal下输入`：`会进入，在最后一行会有提示

```
:q   不保存退出
:q!  不保存强制退出
:w   保存但不退出
:w!  强制保存
:wq! 强制保存退出
:w [文件名] 另存为
```



**二、Vim的进入：**

```
vim 文件名：直接进入
```













# 3. 应用系统



### 3.1 应用程序安装

Linux应用程序的安装可分为源码安装和二进制安装



**源码安装：**是编写出的源代码，需计算机编译成二进制文件后才可进行安装，其编译时间较长，安装步骤繁杂，若安装过程中报错，新手是很难解决的，但也有优势：可自由选择需要的功能甚至可以改写源代码，因为是编译安装所以更适合自己的系统，效率和稳定性也有提升（笔者还未接触到，不做说明）



**二进制安装（举例rpm包管理器）：**是源码经过编译后的安装包，安装速度较快，不需要编译所以出错概率很小

rpm安装是用默认安装路径的，不建议修改

```
i：安装
v：详情
h：进度
rpm -ivh 包全名：表示安装某一程序，注意要出现两次100%才是安装完成，第一次表示完成安装的准备
```

rpm安装也有其缺点，因为程序间依赖关系复杂，若rpm安装过程中缺失依赖会报错，而且卸载时要从后安装的依赖开始卸载

```
a：查询所有套件
q：查询是否安装某软件
|：管道符，将前面命令的输出交给后面的命令
grep：搜索

# 查询某程序是否安装
rpm -qa | grep 程序包名
```



**查询 yum程序 是否安装**

![1583890980136](C:\Users\Howl\AppData\Roaming\Typora\typora-user-images\1583890980136.png)





这些程序安装都过于繁杂，有没有适合新手的？ 当然有那就是yum了，yum是基于rpm的，所以也是二进制安装，安装速度较快，重要的是**自动解决依赖问题，不需要手动下载**



**yum安装**

```
y：安装过程中的选项都选择yes
install：安装rpm软件包
search：查看特定安装包
update：更新rpm软件包
check-update：检查是否有可用的更新rpm软件包
remove：删除指定的rpm软件包
```



这里举例 gcc程序 的安装，gcc是C语言编译器，这学期上Linux程序设计需要用到

```
yum -y install gcc
```



![1583892663292](C:\Users\Howl\AppData\Roaming\Typora\typora-user-images\1583892663292.png)

从安装过程可以看到，yum会自动解决依赖关系，一键安装的感觉









### 3.2 程序的打开

常规的程序在命令行输入程序名即可打开，其安装在 $PATH下，即 /bin，/usr/bin，/usr/local/bin中，系统会去 $PATH 下查找，类似于windows下的环境变量



其他程序则要主动去到程序的安装目录下输入启动文件来打开，假如要打开lampp

```
/opt/lampp/./lampp start
其中./表示当前目录，不然系统会去环境变量找
```



或者将这个程序的安装目录加入到 $PATH中，这样就可以在命令行直接输入来启动程序了（类似windows下添加jdk环境变量）

```
export PATH = $PATH:/opt/lampp
```



















# 4. Shell



### 4.1 Shell编程

Shell编程是对Linux命令的逻辑化处理，笔者笔记的自动上传也是用了Shell编程，不要觉得很深奥，其实有了编程语言的基础，几分钟就可以入门。**笔者挑出部分基础说明，达到让大家认识Shell编程的目的即可**



**简单入门**

```
touch helloWorld.sh		（新建一个helloWorld.sh文件，`后缀`只是为了方便辨认，没有实质作用）
vim helloworld.sh       （文件中加入 echo "helloworld" ，echo表示输出，保存并退出vim）
chmod +x helloworld.sh  （增加可运行的权限）
./helloworld.sh			（运行Shell脚本）
```

![1583902354360](C:\Users\Howl\AppData\Roaming\Typora\typora-user-images\1583902354360.png)

是不是很简单？



### 4.2 与Java的差异

| 布尔运算 | 解释 |
| -------- | ---- |
| !        | 非   |
| -o       | 或   |
| -a       | 与   |



| 关系运算符 | 解释         |
| ---------- | ------------ |
| -eq        | 数字是否相等 |
| -gt        | 大于         |
| -lt        | 小于         |



| 字符串运算符 | 解释                     |
| ------------ | ------------------------ |
| -z           | 长度是否为0，为0返回true |
| str          | 是否为空                 |



**if：（简单事例）**

```
a=1;
b=1;

if [ $a -eq $b ]
then
   echo "equal"
elif [ $a -gt $b ]
then
   echo "大于"
else
   echo "小于"
fi
```



**for：（简单事例）**

```
#!/bin/bash

for value in 1 3 5 7 9
do
    echo $value
done
exit 0
```













# 5. 内核

内核提供进程管理，网络管理等功能，所以笔者将这些内容归到内核模块下



### 5.1 进程--ps

```
e：显示所有程序
f：显示UID,PPIP,C与STIME栏位
```

![1583905173391](C:\Users\Howl\AppData\Roaming\Typora\typora-user-images\1583905173391.png)

```
UID：用户ID
PID：进程ID
PPID：父进程ID
C：优先级
STIME：进程启动的时间
TTY：使用终端
TIME：CPU时间
CMD：显示完整的启动进程所用的命令和参数
```





### 5.2 进程--kill

关闭进程，那么首先需要知道进程ID。假如关闭运行的vim

```
ps -ef | grep vim
kill [进程号]
```

![1583905571356](C:\Users\Howl\AppData\Roaming\Typora\typora-user-images\1583905571356.png)

左边为开着的vim进程





### 5.3 进程--前后台

```
jobs：查看后台运行的进程
fg：将后台进程转至前台
bg：将前台进程转至后台
在命令行最后加入 &：也是将前台进程转至后台
```



这里将下载redis的任务加入后台（快捷键 Ctrl + z），用jobs查看后台进程，再用fg将其调至前台

![1583906610465](C:\Users\Howl\AppData\Roaming\Typora\typora-user-images\1583906610465.png)





### 5.4 网络--端口



**netstat**

```
n：不DNS轮询，显示IP
t：显示TCP端口
u：显示UDP端口
l：仅显示套接字
p：显示进程标识符和程序名称

# 查看80端口
netstat -ntp | grep 80
```



**防火墙端口 firewall-cmd**

```
--query-port=<端口号>/<protocol>：查询指定端口
--add-port=<端口号>/<protocol>：开放端口
--remove-port=<端口号>/<protocol>：关闭端口

--zone=public：作用域
--permanent：永久生效
--reload：重启防火墙


# 查询是否开启
firewall-cmd --zone=public --query-port=80/tcp

# 开启防火墙端口
firewall-cmd --zone=public --add-port=80/tcp --permanent

# 重载防火墙
firewall-cmd --reload
```

![1583913318847](C:\Users\Howl\AppData\Roaming\Typora\typora-user-images\1583913318847.png)













这是笔者目前所浅显理解的Linux，Linux很优秀，需要在不断地使用中去挖掘





****

参考：

   CentOS 7

​    菜鸟教程

《鸟哥的私房菜》







