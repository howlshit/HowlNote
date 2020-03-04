### Mybatis 入门 (二)



#### 1. Mapper配置文件处理特殊字符

``` xml
用 &gt; 和 &It; 代替 > 和 <
```



#### 2. 延迟加载

单表查询性能比多表关联查询要高得多，即先查询单表，如果需要关联多表时再进行查询

``` xml
<!-- 全局配置参数 -->
<settings>
    <!-- 延迟加载总开关 -->
    <setting name="lazyLoadingEnabled" value="true" />  
    <!-- 设置按需加载 -->
    <setting name="aggressiveLazyLoading" value="false" />
</settings
```



#### 3. resultType和resultMap

resultType:当查询字段名和Bean对象属性名一致时，查询结果自动映射

resultMap:当查询字段名与Bean对象属性名不一致时，需要配置resultMap与Bean属性的对应关系，才能映射

**association和collection完成一对一和一对多高级映射。**

``` xml
<resultMap id="ResultMap" type="user" >
    <!-- 
         字段名:id_,username_,birthday_
         id：主键
         column：结果集的列名
         property：type指定Bean的对应属性
    -->
    <id column="id_" property="id"/>
    <!-- result就是普通列的映射配置 -->
    <result column="username_" property="username"/>
    <result column="birthday_" property="birthday"/>
</resultMap>
```



#### 4. 返回主键

```xml
<insert id="insertUser" parameterType="User">
    <selectKey keyProperty="id" order="AFTER" resultType="int">
        select LAST_INSERT_ID()
    </selectKey>
    INSERT INTO USER(name,sex,age) VALUES(#{name},#{sex},#{age})
</insert>
```





#### 5. 动态sql

```xml
<select id="findByCondition" resultType="User" parameterType="map">
	select * from User
	<where>
		<if test="name!=null">
			and name = #{name}
        </if>
		<if test="id!=null">
            and sal = #{id}
		</if>
	</where>
</select>
```





#### 6. 缓存

将查询数据放到缓存中，而不用再去数据库从而提高查询效率

* 一级缓存:每个SqlSession有自己的缓存，只能访问自己的（默认一级缓存）
* 二级缓存:每个Mapper有自己缓存，Mapper内的SqlSession可以互相访问
* 二级缓存将数据从内存写入磁盘，序列化和反序列化，所以映射的Bean对象需要实现serializable接口
* 两级缓存查询都将数据放入内存中，下次查询直接从内存读取，但执行其他语句会清空内从数据，下次查询需要再次从数据库拿出数据

```xml
<!-- Mybatis-config.xml全局配置参数 -->
<settings>
    <!-- 开启二级缓存 -->
    <setting name="cacheEnabled" value="true"/>
</settings>

<!-- 再需要开启二级缓存的Mapper文件的<mapper namespace>下方添加 -->
<cache />
```





#### 7. Mapper代理（推荐）

只需要写Bean接口，Bean接口的实现对象由mybatis自动生成，Mapper文件与Bean接口需要遵从下面原则

* mapper文件中namespace为Bean接口的全限定名
* mapper文件中sql语句id为Bean接口中的方法名
* mapper文件中parameterType为Bean接口中方法的输入参数类型
* mapper文件中resultType为Bean接口中方法的返回值类



Bean接口

``` java
package com.howl;

public interface userMapper {
    
	public user findUserById(int id);
    
}
```



Mapper文件

```java
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE mapper
  PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN"
  "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
  
  
<mapper namespace="com.howl.userMapper">

	<!-- 通过id查询用户 -->
	<select id="findUserById" parameterType="Integer" resultType="com.howl.user">
		select * from user where id = #{id}
	</select>
	
</mapper>
```



运行

```java
//通过Bean接口获得Mapper代理对象
userMapper userMapper = sqlSession.getMapper(com.howl.userMapper.class);
//调用代理对象里的方法
user user = userMapper.findUserById(1);
```





```
高级映射
```

