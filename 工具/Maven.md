## 1. Maven

Maven是一个可对项目进行构建，依赖管理的自动化构建工具，其也是Apache下的一个纯 Java 开发的开源项目，所以需要JDK支持



**其主要解决的问题是：**

* 一个项目就是一个工程

```
冗余庞大的项目不适合用package划分模块，应一个模块对应一个工程，利于分工协作
maven可以做到项目拆分工程
```

* 项目中的 jar 包必须手动复制到WEB-INF/lib下，并添加资源路径，而且需要自行下载

```
同样的jar包重复出现不同项目工程中，浪费空间
maven可以做到本地保存一份，工程使用做一个`引用`
maven可以做到统一下载管理
```

* 一个jar包的依赖需要自行手动加入到项目中

```
maven可以做到自动将依赖的jar包导入进来
```









## 2. 目录结构

maven采用`约定大于配置`的目录结构来构建项目，所以我们先看下其`常见的结构`如何



```
工程名
|----src				源码
|    |----main				存放主程序
|	 |    |--–-java				源代码文件
|	 |	  |--–-resources		资源库，会自动复制到classes目录里
|    |--–test				存放测试程序
|	 |    |--–java				单元测试源代码文件
|	 |    |--–resources			测试需要用的资源库
|    |
|----target				编译后的文件
|----pom.xml  			项目对象模型
|----README.txt 		README
```

> 约定  >  配置  >  编码









## 3. 生命周期

Maven构建定义了一个项目构建和发布的过程，其生命周期由下面的阶段组成



| 阶段     | 描述                                           |
| -------- | ---------------------------------------------- |
| clean    | 删除之前编译的字节码文件，为下次编译做准备     |
| validate | 验证项目是否正确且所有必须信息是可用的         |
| compile  | 源代码编译成字节码文件                         |
| Test     | 自己写好测试代码，构建过程自动调用             |
| package  | 将工程打包成 JAR / WAR                         |
| verify   | 对集成测试的结果进行检查，即对上一步进行检测   |
| install  | 安装打包的项目到本地仓库，以供其他项目使用     |
| site     | 项目站点文档创建的处理                         |
| deploy   | 部署最终的Web工程包到Servlet容器中，使其可运行 |



### Maven的三套相互独立的标准生命周期

* Clean：项目清理的处理
* Site：项目站点文档创建的处理
* Default：项目部署的处理（有23个阶段）









## 4. POM文件

pom.xml ( Project Object Model--项目对象模型 ) 包含了项目的基本信息，用于描述项目如何构建，声明项目依赖等

执行任务或目标时，Maven会在当前目录中查找pom.xml来获取所需的配置信息，然后执行目标

Maven核心程序仅仅定义了抽象的生命周期，具体工作必须由特定的插件完成，所以构建过程需要下载插件



#### 常见的结构

```xml
<!--  Schema约束  -->
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0
                             https://maven.apache.org/xsd/maven-4.0.0.xsd">
    
    
    <!-- 对象模型版本 -->
    <modelVersion>4.0.0</modelVersion>
    
    
    <!-- 所有pom都继承一个父pom(隐式superpom，),父pom包含可被继承的默认设置 -->
    <!-- 父pom可自己编写，例如springboot -->
    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>2.3.4.RELEASE</version>
        <relativePath/> <!-- 以当前文件为基准的父工程pom文件的相对路径，不然依赖加不上去 -->
    </parent>
    
    
    <!-- Maven坐标，组成：groupId + artifactId + version -->
    <!-- groupId是组织的唯一标识+项目名，也是工程的保存路径 -->
    <!-- artifactId是项目的唯一标志（也可是模块），一个组织下面可有多个项目/模块 -->
    <!-- version是版本号 -->
    <!-- packaging是打包方式 -->
    <!-- name是项目名称 -->
    <!-- 项目描述 -->
    <groupId>com.howl.maven</groupId>
    <artifactId>maven-test</artifactId>
    <version>0.0.1-SNAPSHOT</version>
    <packaging>war</packaging>
    <name>test</name>
    <description>Demo project for Spring Boot</description>
    
    <!--  项目开发属性 -->
    <properties>
        <java.version>1.8</java.version>
    </properties>
    
    
    <!--  依赖列表  -->
    <dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter</artifactId>
        </dependency>
    </dependencies>
    
    
    <!--  构建项目需要的信息  -->
    <build>
        
        <!--  使用的插件列表 -->
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
            </plugin>
        </plugins>
    </build>
</project>
```









## 5. 仓库

仓库是存放maven的各种构件的地方

settings.xml 中默认的用户库: ${user.home}/.m2/repository，通过maven下载的jar包都会存储到指定的个人仓库中

settings.xml 中可以设置远程仓库地址，笔者使用阿里的镜像



* 本地仓库：Maven直接从本地仓库获取构件，若本地没有，则从中央仓库下载至本地，后再用本地仓库的构件
* 中央仓库：由 Maven 社区提供管理的仓库，其中包含了大量常用的库（可用镜像解决网速问题）
* 远程仓库：远程仓库是开发人员定制的仓库（私服）









## 6. 依赖

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter</artifactId>
    <version>0.0.1-SNAPSHOT</version>
    <scope>test</scope>
    <exclusions>
        <exclusion>
            <artifactId></artifactId>
            <groupId></groupId>
        </exclusion>
    </exclusions>
</dependency>
```



> 若自己写的某个项目依赖另一个项目，那么首先要将另一个项目安装到本地仓库中（install），然后用`<dependency>`才能正确引用，否则编译失败



>  依赖的传递性：假设jar包A被工程B依赖，若工程B被工程C依赖，那么C会自动依赖A，并且只需在最下层工程B中添加一次依赖即可



>依赖的原则：就近原则，先声明优先



### 依赖范围

|          范围           |  主程序  | 测试程序 | 参与打包 |
| :---------------------: | :------: | :------: | :------: |
|         compile         |    √     |    √     |    √     |
|          test           |    ×     |    √     |    ×     |
|                         |          |          |          |
|        **范围**         | **开发** | **部署** | **运行** |
| provided（eg：Servlet） |    √     |    ×     |    √     |



### 依赖版本统一管理

```xml
<!--  首先设置项目开发属性  -->
<properties>
    <com.howl.maven.version>1.0.0.RELEASE</com.howl.maven.version>
</properties>

<!--  然后在依赖里用 ${}  -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter</artifactId>
    <version>${com.howl.maven.version}</version>
</dependency>
```









## 7. 继承

因为各模块test范围的jar包不能传递，所以易致版本不一致，因此我们要统一管理各个模块工程中对某个 jar 包的版本

解决方法就是将jar包依赖提取到父工程中，在子工程声明依赖中不指定版本，最常见的就是SpringBoot的依赖版本控制



### 步骤

* 创建一个父maven工程，**注意打包成pom工程**

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <groupId>com.howl</groupId>
    <artifactId>parent</artifactId>
    <version>1.0-SNAPSHOT</version>
    <packaging>pom</packaging>
</project>
```

* 在子工程中声明对父工程的引用

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <parent>
        <groupId>com.howl</groupId>
        <artifactId>parent</artifactId>
        <version>1.0-SNAPSHOT</version>
        <!--  默认工程目录去找父工程pom文件  -->
        <relativePath>../parent/pom.xml</relativePath>
    </parent>
    
    <groupId>com.howl</groupId>
    <artifactId>child</artifactId>
    <version>1.0-SNAPSHOT</version>
</project>
```

* 父工程中统一jar的依赖

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <groupId>com.howl</groupId>
    <artifactId>parent</artifactId>
    <version>1.0-SNAPSHOT</version>
    <packaging>pom</packaging>

     <!--  配置依赖管理  -->
    <dependencyManagement>
        <dependencies>
            <dependency>
                <groupId>junit</groupId>
                <artifactId>junit</artifactId>
                <version>4.11</version>
                <scope>test</scope>
            </dependency>
        </dependencies>
    </dependencyManagement>
</project>
```

* 在子工程中删除jar的依赖本版号

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <parent>
        <groupId>com.howl</groupId>
        <artifactId>parent</artifactId>
        <version>1.0-SNAPSHOT</version>
        <relativePath>../parent/pom.xml</relativePath>
    </parent>

    <groupId>com.howl</groupId>
    <artifactId>child</artifactId>
    <version>1.0-SNAPSHOT</version>

    <dependencies>
        <dependency>
            <groupId>junit</groupId>
            <artifactId>junit</artifactId>
            <!--  去掉了版本号  -->
            <scope>test</scope>
        </dependency>
    </dependencies>
</project>
```









## 8. 聚合

当我们的项目分成好几个模块时，可将多个模块聚合起来，一次构建。此时需要一个聚合的载体，即一个普通的maven项目，内部加上`<module>`

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <groupId>com.howl</groupId>
    <artifactId>parent</artifactId>
    <version>1.0-SNAPSHOT</version>
    <packaging>pom</packaging>

    <!--  依赖自动管理，一次性构建  -->
    <modules>
        <module>../child</module>
    </modules>
</project>
```














