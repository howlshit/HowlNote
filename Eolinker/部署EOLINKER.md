## 1. 下载

下载使用Github上的 4.0开源版，这里就不贴地址了

![1587791721022](C:\Users\Howl\Desktop\plan\Blog\Eolinker\1587791721022.png)



* backend_source_code是源码，可用IDEA打开
* database存放了SQL文件
* release发布版，笔者使用就有点小问题，所以自行修改了一下









## 2. 安装MySQL

笔者使用Docker官网的5.7版本（某些版本有点不兼容），大家可以自行尝试



```shell
docker pull mysql:5.7

docker run -d -p 3306:3306 --name mysql5.7 -e MYSQL_ROOT_PASSWORD=123456 mysql:5.7
```



笔者试过的改动：

* 添加driveUrl（时区，SSL）
* 修改driver（com.mysql.cj.jdbc.Driver）
* 修改密码验证器
* 低版本不支持超时重连
* 初始化不允许远程登录root
* 修改mysql-connector-java本版兼容









## 3. 建立数据库及数据

需要自行创建数据库，然后导入SQL文件，笔者使用Navicat操作



**1. 库名：**

` eolinker_os  `

**2. 编码：**

` utf-8 `

**3. 运行SQL文件**

![1587779679612](C:\Users\Howl\Desktop\plan\Blog\Eolinker\1587779679612.png)









## 4. 修改配置

修改config里的setting.properties文件



```properties
# 配置文件
# 某些版本：serverTimezone中的Zone首字母是大写，这个坑了笔者好久
port=8080
version=
language=
dbUser=root
dbPassword=123456
dbURL=jdbc:mysql://localhost:3306/eolinker_os?characterEncoding=UTF-8&serverTimezone=Asia/Shanghai
allowRegister=true
allowUpdate=true
webSitename=eoLinker open source version
```









## 5. 打包并上传

记住连config文件夹一起上传，不能改变结构

![1587779894812](C:\Users\Howl\Desktop\plan\Blog\Eolinker\1587779894812.png)









## 6. 运行jar文件

当然运行jar得有java环境，安装jdk1.8即可



```shell
# 后台运行
nohup java -jar eolinker_os-4.0.jar > temp.text &
```



不知道为什么上面的不行，要手动来

```shell
java -jar eolinker_os-4.0.jar
^ + Z
```









## 7. 进入并初始化

打开下面的地址，然后按着提示进行部署

```
http://XX.XX.XX.XX:8080/eolinker_os/index.html
```









## 8. 技穷

还是不行？下载源码，自行修改pom.xml文件版本及其相关配置，本地能运行了才打包jar上传至服务器（笔者经历过的）
