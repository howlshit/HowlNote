>之前看过Mybatis，但因为时间长远没有用，大部分已经忘记了，这里贴下coding练习过程





# 1. 导包

* log4j.jar
* mybatis-3.4.6.jar

* mysql-connector-java-5.1.39-bin.jar









# 2. 准备配置



### 2.1 log4j.xml

```java
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE log4j:configuration SYSTEM "log4j.dtd">
 
<log4j:configuration xmlns:log4j="http://jakarta.apache.org/log4j/">
 
 <appender name="STDOUT" class="org.apache.log4j.ConsoleAppender">
   <param name="Encoding" value="UTF-8" />
   <layout class="org.apache.log4j.PatternLayout">
    <param name="ConversionPattern" value="%-5p %d{MM-dd HH:mm:ss,SSS} %m  (%F:%L) \n" />
   </layout>
 </appender>
 <logger name="java.sql">
   <level value="debug" />
 </logger>
 <logger name="org.apache.ibatis">
   <level value="info" />
 </logger>
 <root>
   <level value="debug" />
   <appender-ref ref="STDOUT" />
 </root>
</log4j:configuration>
```



### 2.2 db.properties

```properties
jdbc.driver = com.mysql.jdbc.Driver
jdbc.url = jdbc\:mysql\://127.0.0.1\:3306/mybatis?useUnicode\=true&characterEncoding\=UTF-8&serverTimezone\=Asia/Shanghai&useAffectedRows\=true
jdbc.username = root
jdbc.password =
```



### 2.3 mybatis-config.xml

```xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE configuration PUBLIC "-//mybatis.org//DTD Config 3.0//EN" 
"http://mybatis.org/dtd/mybatis-3-config.dtd">

<!-- 根标签 -->
<configuration>

	<!-- 引入外部java资源文件properties,mybatis自带 -->
	<properties resource="db.properties"></properties>

	<!-- 最好显示写上，防止版本更新默认值不同  -->
	<settings>
		<setting name="mapUnderscoreToCamelCase" value="true"/>
		<setting name="useActualParamName" value="true"/>
		<setting name="lazyLoadingEnabled" value="true"/>
		<setting name="aggressiveLazyLoading" value="false"/>
	</settings>
	
	<!-- 别名，存在的意义仅在于用来减少类完全限定名的冗余 -->
	<typeAliases>
		<!--  alias可选，默认小写  -->
		<typeAlias alias="User" type="com.howl.entity.User"/>
		<!--  自动扫描包，包下的类自动使用Bean的首字母小写  -->
		<package name="com.howl.entity"/>
	</typeAliases>
	
	<!-- 环境，可以配置多个，default：缺省，和spring整合后environments将废除 -->
	<environments default="development">
	
		<!-- id：唯一标识，下面两个标签必须有 -->
	    <environment id="development">
	    	<!-- 事务管理器，JDBC类型的事务管理器 -->
	        <transactionManager type="JDBC" />
		    <!-- 数据源，池类型的数据源 -->
		    <dataSource type="POOLED">
		        <property name="driver" value="${jdbc.driver}" />
		        <property name="url" value="${jdbc.url}" />
		        <property name="username" value="${jdbc.username}" />
		        <property name="password" value="${jdbc.password}" />
	      	</dataSource>
	    </environment>
	    
	    <environment id="product">
	    	<transactionManager type="JDBC"></transactionManager>
	    	<dataSource type="POOLED">
	    		<property name="driver" value="${jdbc.driver}"/>
	    		<property name="url" value="${jdbc.url}"/>
	    		<property name="username" value="${jdbc.username}"/>
	    		<property name="password" value="${jdbc.password}"/>
	    	</dataSource>
	    </environment>
	    
	</environments>
	
	<!-- 引入映射文件或接口 ，可以使用类名，包名(针对接口)、相对路径‘/’（针对xml文件）  -->
	<mappers>
		<mapper resource="mybatis/mappers/UserMapper.xml"/>
		<mapper resource="mybatis/mappers/CategoryMapper.xml"/>
		 
		<!--  引入接口,这样的话找不到映射文件，只能把映射文件和接口放在同一包下  -->
		<!-- <mapper class="com.howl.mapper.dao.UserMapper" /> -->
		<!--  还可以在接口上使用注解开发，这样就省去了映射文件，但只适用于简单查询，且耦合度高  -->
		
		<!--  批量注册,映射文件要和接口在同一包下  -->
		<!-- <package name="com.howl.dao"/> -->
	</mappers>
	
</configuration>
```







# 3. 创建表和实体



### 3.1 表

```mysql
CREATE TABLE `user` (
  `user_id` int(11) NOT NULL AUTO_INCREMENT,
  `user_name` varchar(255) DEFAULT NULL,
  `user_email` varchar(255) DEFAULT NULL,
  `user_category_id` int(11) DEFAULT NULL,
  PRIMARY KEY (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=31 DEFAULT CHARSET=utf8;
```

```mysql
CREATE TABLE `category` (
  `category_id` int(11) NOT NULL,
  `category_name` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`category_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
```





### 3.2 entity

```java
package com.howl.entity;

public class User {
	
	private int id;
	private String name;
	private String email;
	private int category_id;
	private Category category;
	
	
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
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public int getCategory_id() {
		return category_id;
	}
	public void setCategory_id(int category_id) {
		this.category_id = category_id;
	}
	public Category getCategory() {
		return category;
	}
	public void setCategory(Category category) {
		this.category = category;
	}
	
	
	
	
	public User() {
		super();
	}
	public User(String name, String email) {
		super();
		this.name = name;
		this.email = email;
	}
	
	public User(int id, String name) {
		super();
		this.id = id;
		this.name = name;
	}
	public User(int id, String name, String email) {
		super();
		this.id = id;
		this.name = name;
		this.email = email;
	}
	
	
	
	
	@Override
	public String toString() {
		return "User [id=" + id + ", name=" + name + ", email=" + email + ", category_id=" + category_id + ", category="
				+ category + "]";
	}
}
```

```java
package com.howl.entity;

import java.util.List;

public class Category {
	
	private int id;
	private String name;
	private List<User> users;
	
	
	
	
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
	public List<User> getUsers() {
		return users;
	}
	public void setUsers(List<User> users) {
		this.users = users;
	}
	
	
	
	
	@Override
	public String toString() {
		return "Category [id=" + id + ", name=" + name + ", users=" + users + "]";
	}
}
```









# 4. 创建表与实体的映射文件及与关联全局配置文件

```xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE mapper
  PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN"
  "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
  
  
<mapper namespace="com.howl.dao.UserMapper">

	<!--  结果映射，用于字段与属性不一致  -->
	<!--  可选方法@Param、resultMap、语句查询时用as  -->
	<resultMap type="com.howl.entity.User" id="UserMap">
		<id property="id" column="user_id"/>
		<result property="name" column="user_name"/>
		<result property="email" column="user_email"/>
		
		<!--  一对一，联合查询（嵌套）,这里懒不在全局配置里写别名了,这种查询需要在写sql语句时全部查出  -->
		<association property="category" javaType="com.howl.entity.Category">
			<id property="id" column="category_id"/>
			<result property="name" column="category_name"/>
		</association>
		
		<!--  分步关联查询,用写好的语句来查询，传入外层已经查到的数据作为参数  -->
		<!--  column是外层插好的值的名字  -->
		<!--  厉害于可以延迟加载，所以分布加载存在的必要性，不然分两次查询网络传输都损耗性能  -->
		<!--  懒加载在需要用到的时候，比如syso输出时再发请求  -->
		<!--  多值传参进写好的语句时：column="{key1=column1,key2=column2}"  -->
		<association property="category" select="com.howl.dao.CategoryMapper.selectCategoryById" column="user_category_id" fetchType="eager/lazy">
		</association>
	</resultMap>
	
	
	
	
	<select id="selectUserById" resultMap="UserMap">
		SELECT * FROM user WHERE user_id = #{id};
	</select>
	
	<!--  单参用封装User类，框架通过getter来获取  -->
	<select id="selectUserByConditions" resultMap="UserMap">
		SELECT * FROM user WHERE user_id = #{id} AND user_name = #{name};
	</select>
	
	<!--  返回集合，结果类型也要写集合中元素的类型，模糊查询  -->
	<select id="selectUserByLikeName" resultMap="UserMap">
		SELECT * FROM user WHERE user_name LIKE #{name}
	</select>
	
    <!--  一对一关联查询  -->
	<select id="selectUserByIdWithCategory" resultMap="UserMap">
		SELECT * FROM user u, category c WHERE u.user_category_id = c.category_id AND u.user_id = #{id}
	</select>
	
	
	
	
	<!--  动态语句  -->
	<select id="selectUserDynamic" resultMap="UserMap">
		<!--  绑定处理，不推荐  -->
		<!-- <bind name="nameLike" value=" '%' + name + '%' "/> -->
		
		SELECT * FROM user WHERE 1 = 1
		<if test="name != null and name != '' ">
			AND user_name LIKE #{name}
		</if>
		<if test="email != null and email != '' ">
			AND user_email LIKE #{email}
		</if>
		<if test="category_id != null and category_id != '' ">
			AND user_category_id = #{category_id}
		</if>
	</select>
	<!--  动态语句  -->
	
	
	
	
	<!--  批处理  -->
	<select id="selectUserByIdForeach" resultMap="UserMap">
		SELECT * FROM user WHERE user_id IN 
		<foreach collection="list" item="user_id" separator="," open="(" close=")">
			#{user_id}
		</foreach>
	</select>
	<!--  批处理  -->
	
	
	
	
	<!--  增删改  -->
	<!--  参数类型可选，类型转换器会帮你判断  -->
    <!--  获取自增主键  -->
	<insert id="insertUser" parameterType="User" useGeneratedKeys="true" keyProperty="id">
		INSERT INTO user(`user_name`,`user_email`) VALUES (#{name},#{email});
	</insert>
	
	<update id="updateUser">
		UPDATE user SET user_name = #{name},user_email = #{email} WHERE user_id = #{id}
	</update>
	
	<delete id="deleteUser">
		DELETE FROM user WHERE user_id = #{id}
	</delete>
	<!--  增删改  -->
	
	
</mapper>
```

```java
public interface UserMapper {
	
	public User selectUserById(int id);
	
	public User selectUserByConditions(User user);
	// 多参传入方法
	public User selectUserByConditions(Map map);
	
	public User selectUserByConditions(@Param("id")int id, @Param("name")String name);
	
	public User selectUserByIdWithCategory(@Param("id")int id);
	
	public List<User> selectUserByLikeName(String name);
	
	// 动态查询
	public List<User> selectUserDynamic(User user);
	
	// 批处理
	public List<User> selectUserByIdForeach(List list);
	
	public int insertUser(User user);
	
	public int updateUser(User user);
	
	public int deleteUser(int id);

}
```





```xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE mapper
  PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN"
  "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
  
  
<mapper namespace="com.howl.dao.CategoryMapper">

	<resultMap type="com.howl.entity.Category" id="CategoryMap">
		<id property="id" column="category_id"/>
		<result property="name" column="category_name"/>
		<!--  一对多，联合查询,ofType指定集合元素的类型  -->
		<collection property="users" ofType="com.howl.entity.User">
			<id property="id" column="user_id"/>
			<result property="name" column="user_name"/>
			<result property="email" column="user_email"/>
		</collection>
	</resultMap>
	
	
	<select id="selectCategoryById" resultMap="CategoryMap">
		SELECT * FROM category WHERE category_id = #{id};
	</select>
	
	<select id="selectCategoryByIdWithUser" resultMap="CategoryMap">
		SELECT * FROM category c LEFT JOIN user u on c.category_id = u.user_category_id WHERE category_id = #{id}
	</select>
	
	
	
</mapper>
```

```java
public interface CategoryMapper {
	
	public Category selectCategoryById(int id);
	
	public Category selectCategoryByIdWithUser(int id);

}
```









# 5. 工具类

```java
public class MybatisUtil {

	private static  InputStream in;
	private static SqlSessionFactory sqlSessionFactory;
	
	static {
		try {
			String resource = "mybatis-config.xml";
			in = Resources.getResourceAsStream(resource);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private MybatisUtil(){}
	
    // 可选数据源
	public static SqlSession getSqlSession (String environment){
		sqlSessionFactory = new SqlSessionFactoryBuilder().build(in,environment);
		return sqlSessionFactory.openSession();
	}
	
	public static void closeSqlSession(SqlSession sqlSession){
		if(sqlSession != null){
			sqlSession.close();
		}
	}
	
    // 测试连接
	public static void main(String[] args) {
		Connection conn = MybatisUtil.getSqlSession("product").getConnection();
		if(conn != null){
			System.out.println("连接成功");
		}
	}
}
```









# 6. 测试

```java
public class main {

	
	public static void main(String[] args) {
		
		// 工具类获取SqlSession连接会话
		SqlSession sqlSession = MybatisUtil.getSqlSession("product");
		// 根据会话获取代理对象
		UserMapper userMapper = sqlSession.getMapper(com.howl.dao.UserMapper.class);
		
		
		
		
        // 增
        User user1 = new User("Howl","1210911104@qq.com");
        userMapper.insertUser(user1);
        System.out.println("自增主键为:" + user1.getId());
        sqlSession.commit();

        // 删
        int deleteNum = userMapper.deleteUser(24);
        System.out.println("删除影响条数为：" + deleteNum);
        sqlSession.commit();

        // 改(多次同样修改也返回1，应为返回的是matched的数，需要在连接url上加参数useAffectedRows=true)
        int updateNum = userMapper.updateUser(new User(26,"Howlet","111111"));
        System.out.println("修改的影响条数为：" + updateNum);
        sqlSession.commit();
		
		
		
		
		// 单参查询
		User user2 = userMapper.selectUserById(25);
		System.out.println("查询结果为：" + user2.getName());
		
		// 单参数查询，封装成User了
		User user3 = new User(26,"Howl");
		user3 = userMapper.selectUserByConditions(user3);
		System.out.println(user3);
		
		// 模糊查询记住要在传参时拼接，语句中不支持，除非用绑定
		List<User> users1 = userMapper.selectUserByLikeName("%How%");
		for(User value : users1){
			System.out.println(value);
		}
		
		// 一对一联合查询
		User user4 = userMapper.selectUserByIdWithCategory(25);
		System.out.println(user4);
		
		// 动态模糊查询，建议把模糊字段在sql语句外拼接
		User user5 = new User();
		user5.setName("%ho%");
		// user5.setCategory_id(1);
		List<User> users = userMapper.selectUserDynamic(user5);
		for(User value : users){
			System.out.println(value);
		}
		
		
		
		
		// 批处理,更新也是如此返回影响条数 ，一般前端都是传数组的
		List<Integer> list1 = new ArrayList();
		list1.add(25);
		list1.add(26);
		list1.add(27);
		List<User> list2 = userMapper.selectUserByIdForeach(list1);
		for(User value : list2){
			System.out.println(value);
		}
				
		
		
		
		// 懒查询,开启association的select
		// user查询语句和正常查询user一样，不同于之处在于调用关联对象后，框架调用select片段
		User user6 = userMapper.selectUserById(25);
		System.out.println("查询结果为：" + user6.getName());
		
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		System.out.println(user6.getCategory());
		
		
	
//--------------------------  category  -----------------------------
		
        
        
        
        // 根据会话获取
		CategoryMapper categoryMapper = sqlSession.getMapper(com.howl.dao.CategoryMapper.class);

		Category category1 = categoryMapper.selectCategoryById(1);
		System.out.println(category1);
		
		Category category2 = categoryMapper.selectCategoryByIdWithUser(1);
		System.out.println(category2.getName());
		

	}
	
}
```







****



观看了B站视频 [这里](<https://www.bilibili.com/video/av34875242?p=70>)

