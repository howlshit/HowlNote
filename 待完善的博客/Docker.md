## 核心概念



docker主机：安装了Docker程序的主机

客户端：连接docker主机进行操作（与守护进程通信）

仓库：保存各种打包好的软件镜像

镜像：软件打包好的镜像，放在仓库中

容器：镜像启动后的实例成为容器







使用步骤

安装docker

去仓库下载镜像

使用docker运行这个镜像，生成容器

对容器的启动停止，就是对软件的停止



Linux

```
systemctl：进程操作命令
start  开启
stop  停止
enable 开机自启

eg:systemctl start docker
```







安装docker

```
yum install docker
```

启动

```
systemctl start docker
```

开启启动

```
systemctl enable docker
```





docker命令

```
docker search
docker pull: docker pull + 镜像名+标签（版本号）
docker images: 查看所有镜像
docker rmi + imageId:删除
```



软件镜像--运行镜像--产生容器（正在运行的软件）



容器操作

```
docker run --name mytomcat -d tomcat：起别名，后台运行
docker ps：查看后台运行
```









​		