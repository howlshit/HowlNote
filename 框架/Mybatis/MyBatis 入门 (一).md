## MyBatis 入门



#### 1.必要的库包

* mybatis.jar
* mysql-connector-java-bin.jar
****

补充 : 可以访问maven官网去下载这些包



#### 2.开发流程



##### 2.1 配置db.properties和Mybatis-config.xml文件

* 这里使用xml文件配置，现在推荐使用接口类型

  

db.properties----配置连接数据库信息

``` properties
driver   = com.mysql.jdbc.Driver
url      = jdbc:mysql://127.0.0.1:3306/zero_demo
username = root
password =
```

Mybatis-config.xml----配置Mybatis文件

``` xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE configuration PUBLIC "-//mybatis.org//DTD Config 3.0//EN" 
"http://mybatis.org/dtd/mybatis-3-config.dtd">

<!-- 根标签 -->
<configuration>

	<!-- 加载properties文件 -->
	<properties resource="db.properties">	

	<!-- 环境，可以配置多个，default：指定采用哪个环境，和spring整合后environments将废除 -->
	<environments default="default">
		<!-- id：唯一标识 -->
	    <environment id="default">
	    	<!-- 事务管理器，JDBC类型的事务管理器 -->
	        <transactionManager type="JDBC" />
		    <!-- 数据源，池类型的数据源 -->
		    <dataSource type="POOLED">
		        <property name="driver" value="${driver}" />
		        <property name="url" value="${url}" />
		        <property name="username" value="${username}" />
		        <property name="password" value="${password}" />
	      	</dataSource>
	    </environment>
	</environments>
	
	<!-- 引入mapper文件的位置 -->
	<mappers>
		<mapper resource="mapper/user.xml"/>
	</mappers>
	
</configuration>
```



##### 2.2 创建sql表单和JavaBean对象

 创建sql表单

``` sql
CREATE TABLE `user` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `email` varchar(20) NOT NULL,
  `password` text NOT NULL,
  `name` text NOT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `email` (`email`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
```

创建JavaBean对象

``` java
package com.howl;

public class user {
	
	private int id;
	private String email;
	private String password;
	private String name;
    
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	@Override
	public String toString() {
		return "user [id=" + id + ", email=" + email + ", password=" + password + ", name=" + name + "]";
	}
}
```





##### 2.3 配置mapper文件

``` xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE mapper
  PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN"
  "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
  
<!-- 命名空间 ,方便和其他JavaBean类的语句名称区分-->
<mapper namespace="user">

	<!-- id唯一表示语句 -->
    <!-- parameterType参数类型 -->
    <!-- resultType返回结果类型，这里的结果类型为上面创建的JavaBean-->
    <!-- #{}解析传进的参数占位符，预编译 -->
    <!-- ${}原样拼接，易mysql注入 -->
    <!-- <insert>/<select>/<update>/<delete>标签互用，但建议各用各 -->
    
	<select id="findUserById" parameterType="Integer" resultType="com.howl.user">
		select * from user where id = #{id};
	</select>
	
	<select id="findUser" resultType="com.howl.user">
		select * from user;
	</select>
	
	<insert id="addUser" parameterType="com.howl.user">
		insert into user (`email`,`password`,`name`) values (#{email},#{password},#{name})
	</insert>
	
</mapper>
```





##### 2.4 获取sqlSession的流程

``` java
//1.mybatis的配置文件地址	
String resource = "mybatis-config.xml";

//2.获取配置文件流
InputStream inputStream = Resources.getResourceAsStream(resource);

//3.根据配置文件流创建工厂----工厂模式
SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);

//4.根据工厂创建sqlsession
SqlSession sqlSession = sqlSessionFactory.openSession();
```





##### 2.4 sqlSession获取对应的Mapper文件，从而读取对应的sql语句，最后执行语句

``` java
user user = sqlSession.select("user.findUserById",1);
List<user> users = sqlSession.selectList("user.findUser");
int ifSuccess = sqlSession.insert("user.addUser");

//mybatis默认关闭自动提交
//除了select语句，其他语句都需要commit提交，
sqlSession.commit();
```