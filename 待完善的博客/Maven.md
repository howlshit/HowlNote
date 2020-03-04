Maven



**Maven可以管理项目的整个声明周期，包括清除、编译，测试，报告、打包、部署等等。**



Maven拥有“约定优于配置“这么一个理念，



**Settings.xml 中默认的用户库: ${user.home}/.m2/repository[通过maven下载的jar包都会存储到指定的个人仓库中]**





上面的目录结构就是Maven所谓的”约定“，我们使用Maven来构建Java项目，都是这种目录结构的…



本地仓库



中心仓库





## maven软件构建的生命周期	



## maven坐标

1. 坐标的组成： groupId + artifactId+ version

- groupId：组id ,机构名，公司名：好比公司的id，或者是公司包名
  alibaba ——-》高德--》5.01版本
- artifactId：构建物id ，产品名或者产品的id
- version ：版本号

1. jar包组成：
   artifactId-version.jar









添加依赖

scope应用范围有test





## 重点是目录结构

```
src
  -main
      –java java源代码文件
      –resources 资源库，会自动复制到classes目录里
      –filters 资源过滤文件
      –webapp web应用的目录。WEB-INF、css、js等
  –test
      –java 单元测试java源代码文件
      –resources 测试需要用的资源库
      –filters 测试资源过滤库
  –assembly 组件的描述配置（如何打包）
  –it 集成测试(主要用于插件)
  –site Site（一些文档）
target
pom.xml  maven的pom文件
LICENSE.txt Project’s license
README.txt Project’s readme
```

