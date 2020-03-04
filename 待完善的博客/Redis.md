## 为什么要用redis

redis是基于内存来储存数据的非关系型数据库，我们知道内存速度远高于硬盘，所以性能比基于硬盘的数据库要好很多，这也是redis作为缓存中间件的原因，并且配合基于硬盘的数据库可以分流，提高并发量



## 安装

redis官网只有Linux版本，目前最新稳定版到5.0，而Windows版已经没有更新（最近发布在16年），所以这里安装Linux版本，但讲解会基于图形化界面     [redis官网下载传送门](<https://redis.io/download>)



官网有安装编译介绍，对着命令行打就对了，这里做简单提示

```
//下载redis压缩包
$ wget http://download.redis.io/releases/redis-5.0.7.tar.gz

//解压下载的包
$ tar xzf redis-5.0.7.tar.gz

//进入解压的包
$ cd redis-5.0.7

//编译
$ make

//运行redis服务
$ ./redis-server

//打开客户端
$ ./redis-cli
```



## 配置

修改守护进程

解除本机绑定使用（外网可以访问）

设置密码 （使用图形化工具必须设置密码）

开启端口防火墙（如果使用阿里云服务器要在阿里云控制台添加安全组端口）







## 内存淘汰策略

RDB

AOF





## 命令

keys *

set name key //重复设置是覆盖，不管类型

setnx name key //键不存在赋值，存在不覆盖返回0

get key

getrange start end //截取，不包括尾

getbit key offset //偏移量

mset /mget 	//批量读写

strlen key //键长

incr key //自增

decr key //自减

incrby key num //自增num

命名空间

hset key filed value

hget key filed





exists key

ttl key //生存时间

expire key secend  // -1,-2

shutdown

del key

select //选择数据库

rename key1 key2

dbsize //查看数据库数量

flushdb //清空当前数据库

type key //返回类型



## Redis Manager

有个坑，不知道大家有没有遇到，输入命令行键盘上的两个回车作用不一样，大回车键是执行命令，右下角的回车是换行功能。。







## 命名

user:1:zhangsan 123

区分mysql t_name下划线





## 应用

计数器









## 知识点

持久化 AOF

集群

事务 

哨兵

穿透 设置一个空字符串加过期时间 ，布隆过滤器

雪崩 解决办法

发布和订阅  通道 	

数据库双写一致







## 主从复制 

自带，主写，从读





## 哨兵模式Sentinel（主备切换）

