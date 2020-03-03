# Spring入门（四）——整合Mybatis



## 1. 准备jar包及目录结构

![捕获](C:\Users\Howl\Desktop\捕获.PNG)

![捕获1](C:\Users\Howl\Desktop\捕获1.PNG)

## 2. 配置db.properties

```xml
driver = com.mysql.jdbc.Driver
url = jdbc:mysql://127.0.0.1:3306/Howl
name = root
password =
```



## 3. 配置applicationContext.xml

```xml
<!-- 加载配置文件 -->
<context:property-placeholder location="classpath:db.properties" />
	
	
<!--spring自带数据源,没有连接池功能 -->
<!-- <bean id="DataSource" class="org.springframework.jdbc.datasource.DriverManagerDataSource">
    <property name="driverClassName" value="${driver}"></property>
    <property name="url" value="${url}"></property>
    <property name="username" value="${name}"></property>
    <property name="password" value="${password}"></property>
</bean> -->
    
    
<!-- c3p0数据源，推荐 -->
<bean id="DataSource" class="com.mchange.v2.c3p0.ComboPooledDataSource" destroy-method="close">       
    <property name="driverClass" value="${driver}"/>       
    <property name="jdbcUrl" value="${url}"/>       
    <property name="user" value="${name}"/>       
    <property name="password" value="${password}"/>       
</bean>
    
    
<!-- 配置SqlSessionFactory,加载mybaits配置文件和映射文件 -->
<bean id="sqlSessionFactory" class="org.mybatis.spring.SqlSessionFactoryBean">
	<property name="configLocation" value="classpath:mybatis.xml"/>
	<property name="dataSource" ref="DataSource"/>
</bean>
```



## 4. 创建Bean对象

```java
package bean;

public class User {
	
	private int id;
	private String name;
	private String password;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	@Override
	public String toString() {
		return "User [id=" + id + ", name=" + name + ", password=" + password + "]";
	}
}
```





## 5. 配置Mapper.xml文件（Mapper代理方式）

```xml
<mapper namespace="com.howl.UserMapper">

	<!-- 通过id查询用户 -->
    <select id="findUserById" parameterType="Integer" resultType="bean.User">
    	select * from user where id = #{id}
    </select>

</mapper>
```



## 6. 代理接口

```xml
package com.howl;

public interface UserMapper {

	public User findUserById(int id);
	
}
```



## 7. 配置Mybatis.xml

```xml
<!-- 根标签 -->
<configuration>
	
	<!-- 引入mapper文件的位置 -->
	<mappers>
		<mapper resource="Mapper/UserMapper.xml"/>
	</mappers>
	
</configuration>
```



## 8. 测试

```java
ApplicationContext ac = new ClassPathXmlApplicationContext("applicationContext.xml");
		SqlSessionFactory sqlSessionFactory = (SqlSessionFactory ) ac.getBean("sqlSessionFactory");
		SqlSession sqlSession = sqlSessionFactory.openSession();
		
		UserMapper userMapper = sqlSession.getMapper(com.howl.UserMapper.class);
		User user = userMapper.findUserById(46);
		
		System.out.println(user);
```



## 9. 打印

```xml
<!-- 整合成功 -->
<!-- 十二月 04, 2019 8:30:35 下午 com.mchange.v2.c3p0.impl.AbstractPoolBackedDataSource -->

User [id=46, name=123123, password=123123]
```

