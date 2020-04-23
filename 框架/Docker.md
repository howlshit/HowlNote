## 1. 为什么出现Docker

以前我们开发项目有专门的开发环境，做测试时有测试环境，而产品上线就会有生产环境，这个过程经常要迁移项目，不同的环境配置可能导致不可预估的错误，要经常性的改动

世界陷入了错误，于是上帝说，让Docker来吧，于是一切光明。Dokcer把原始的环境一模一样地复制过来，那么就消除了协作编码时，我的机器能运行，而其他机器不能运行的困境









## 2. Docker的术语

Docker主机：安装了Docker程序的主机

客户端：连接docker主机进行操作（与守护进程通信）

仓库：保存各种打包好的软件镜像（笔者理解为软件管家可以下载很多软件包）

镜像：软件打包好的镜像，放在仓库中（笔者理解为安装包）

容器：镜像启动后的实例成为容器（笔者理解安装好的在运行的软件）



#### 特点

- 直接使用系统的硬件资源，而不需要虚拟化硬件资源

- 使用宿主机的内核而不需要GuestOS，所以新建时无需重新加载内核，因此是秒级

- 是Client-Server结构的系统，其守护进程运行在主机上，然后通过Socket连接访问，守护进程从客户端接收命令并管理运行在主机上的容器。

流程：下载镜像--->运行镜像--->产生容器（正在运行的软件）









## 3. 安装并启动



安装yun工具包，里面包含yum-config-manager，用来设置yun配置

```shell
yum install -y yum-utils
yum-config-manager --add-repo https://download.docker.com/linux/centos/docker-ce.repo
```



安装docker-ce免费版

```shell
yum install -y docker-ce
```



设置阿里云镜像加速（梯子原因）

```shell
vim /etc/docker/daemon.json

# 去阿里云开发平台查看自己的仓库地址
{
  "registry-mirrors": ["https://XXXXXXXX.mirror.aliyuncs.com"]
}

systemctl daemon-reload
systemctl start docker
systemctl enable docker  # 开机自启
```









## 4. hello-world

```shell
# 输入命令并执行
docker run hello-world
```

![1587571852513](C:\Users\Howl\AppData\Roaming\Typora\typora-user-images\1587571852513.png)

阅读运行之后的说明，里面有步骤与相关信息，对理解其原理有很大的帮助









## 5. 启动容器

以前我们要运行Tomcat：得先安装并设置jdk环境，以及安装tomcat，期间需要用到weget、tar等命令

现在使用了Docker：只需几个命令即可



**下面以tomcat为例**，体验docker的快捷便利。跟着下面命令执行（正常情况运行1和2步骤即可，但特殊原因多了几步）



1 拉取镜像

```shell
# 不加后面的标签默认拉取最新版
docker pull tomcat
```



2 启动容器

```shell
docker run -it -p 8080:8080 --name mytomcat tomcat
ctrl + P + Q
```



3 打开网页访问

```shell
# 访问结果是404，当然不是我们弄错了，而是这个最新版镜像webapps是空的，意思是没有html页面给我们访问
# 其实最新的镜像将wepapps改名为webapps.dist，那么我们只需改名回来即可
# 不怕有解决方法，还是按着下面步骤输入：

docker exec -it  mytomcat  /bin/bash  # 进入容器
mv webapps webappsEmpty
mv webapps.dist webapps
exit
```



4 再次访问

```shell
# 是不是熟悉的tomcat欢迎页回来了
```



补充

```shell
# 这个笔者没理清为什么
# 我们安装的centos是精简版，很多命令都没有所以需要自己下载包括yum
# 我们用apt-get下载软件，apt-get是ubuntu的？？？？
# 不求甚解,将就用把，然后我们熟悉的vim回来了
apt-get update
apt-get install vim -y
```









## 6. 启动MySQL

去Docker官网搜索MySQL，跟着其步骤走 [MySQL的Docker地址](<https://hub.docker.com/_/mysql>)，下面的密码设置官网也都有详细介绍



因为使用Navicat连接时会发生身份验证器错误，所以我们得进去容器修改验证器插件

```shell
# 启动并设置密码
docker run -d -p 3306:3306 --name howlmysql -e MYSQL_ROOT_PASSWORD=123456 mysql

# 进入容器内部
docker exec -it containerId /bin/bash

# 登录容器内的MySQL
mysql -uroot -p 123456

# 修改身份验证插件
ALTER USER 'root'@'%' IDENTIFIED WITH mysql_native_password BY '123456';

# 刷新权限
flush privileges;
```









## 7. 常用命令

**忘记了命令怎么办？  docker  help**



镜像命令

```shell
docker images  列出本地镜像
docker search  查找镜像
docker pull name [tag]  拉镜像
docker commit -m -a Id newName 提交容器使之成为一个新的镜像,在本地image中
docker rmi -f name  删镜像
```



容器命令

```shell
docker run image  新建并启动容器
--name 重命名
-d 守护容器后台
-i 交互模式
-t 重新分配伪终端
-P 端口映射 -p 8080:8080 / 前者为docker端口，后者为实际端口

docker ps  列出正在运行的容器
-a 显示包括未运行的
-l 显示最近创建的容器
-n 显示最近创建的n个容器

docker start Id/Name 启动容器
docker restart
docker stop
docker kill
docker rm -f Id  删除容器

docker exit 交互中关闭并退出
ctrl+P+Q 交互中容器不停止退出
docker attach Id 重新交互
docker exec 在外面拿 容器里的执行命令的结果
docker exec -it  id /bin/bash 在外面获取容器交互终端，exit后不会停止容器运行

docker cp 拷贝容器文件到宿主机
docker logs
docker top  查看容器里运行的进程


docker container prune  删除所有容器
docker rmi $(docker images -q) 删除所有镜像
docker rm $(docker ps -a -q)
```









## 8. 镜像

镜像是用来打包软件运行环境和基于运行环境开发的技术，他包含运行某个软件所需的所有内容，包括代码、运行时库、环境变量和配置文件



UnionFS：底层使用了联合文件系统对文件系统的修改作为一次提交来一层层的叠加 

镜像加载原理：其用了UnionFS分层，最底层加载内核，之上是rootfs即标准目录和文件，一个精简OS的rootfs可以很小只需最基本命令和工具以及程序库

分层的镜像：比如tomcat，底层kervel，接着centos、Jdk8、tomcat，对外才暴露tomcat，所以对外看起来是一个整体

共享资源：多个镜像从多个base镜像构建而来，那么宿主机只需在磁盘上保存一份即可，内存也只需加载一一份

镜像都是只读的，当容器启动时，一个新的可写层被加载到镜像的顶部（容器层，之下叫镜像层）









## 9. 容器数据卷

卷是目录或文件，存在于一个或多个容器中，由docker挂载到容器但不属于联合文件系统，因此能绕过UFS一些用于持续存储或共享数据的特性，卷的出现是为了数据的持久化，完全独立于容器的生存周期，因为Docker不会在容器删除时删除其挂载的数据卷。**简单来说：使容器与宿主机之间共享数据**

**特点：**

- 数据卷可以容器之间共享或重用
- 卷中的更改直接生效
- 卷中的更改不会包含在镜像的更新中
- 数据卷的生命周期持续到没有容器使用为止



命令行实现

```shell
docker run -it -v /宿主机绝对路径:/容器内目录 镜像名
```



Dockerfile实现

```shell
# 后续再说
```









## 10. Dockerfile

Dockerfile是用来构建Docker镜像文件的，由一系列的命令和参数构成的脚本。（笔者理解为构建自己的软件包）

构建步骤：

1. 编写Dockerfile文件   
2. docker build   
3. docker run



centos为例

```shell
FROM scratch
ADD centos-7-x86_64-docker.tar.xz /

LABEL org.label-schema.schema-version="1.0" \
    org.label-schema.name="CentOS Base Image" \
    org.label-schema.vendor="CentOS" \
    org.label-schema.license="GPLv2" \
    org.label-schema.build-date="20191001"

CMD ["/bin/bash"]
```

- 每条指令都会创建一个新的镜像层，并对镜像进行提交
- 从基础镜像运行一个容器
- 执行一条容器并对容器作出修改
- 执行类型commit的操作提交一个新的镜像层
- docker再基于刚提交的镜像运行一个新容器
- 执行dockerfile中的下一条指令直到所有指令都执行完成docker



**这里只是简单说一下：我们可以通过编写Dockerfile文件来自定义自己需要的镜像**









## 11. 发布镜像

进入阿里云镜像管理可以创建镜像仓库，然后跟着里面的提示走



#### 11.1 创建镜像仓库

![1587633666486](C:\Users\Howl\AppData\Roaming\Typora\typora-user-images\1587633666486.png)

跟着下一步即可，选择公开与本地仓库



#### 11.2 推送

```shell
# 登录
docker login --username=XXX registry.cn-hongkong.aliyuncs.com

# 选择分支对应关系
docker tag [ImageId] registry.cn-hongkong.aliyuncs.com/howlet/mytomcat:[镜像版本号]

# 推送上去
docker push registry.cn-hongkong.aliyuncs.com/howlet/mytomcat:[镜像版本号]

# 拉取
docker pull registry.cn-hongkong.aliyuncs.com/howlet/mytomcat:[镜像版本号]
```









最后补充一句：在服务器上做测试用Dokcer真的很爽，一旦配错了，删掉重来。

------

参考

<https://docs.docker.com/engine/install/centos/#install-using-the-repository>

<https://cr.console.aliyun.com/cn-hangzhou/instances/mirrors>

<https://www.bilibili.com/video/BV1Vs411E7AR?p=18>