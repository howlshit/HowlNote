需要导入的包

* c3p0-0.9.5.2.jar
* mchange-commons-0.2.15.jar
* mysql-connector.jar



#### 1. 配置xml

**创建c3p0-config.xml文件，名字不能改动，并且放到src下，c3p0包会自动到src下查找c3p0-config.xml，名字错了，地方不对都不能配置成功**



配置

```xml
<c3p0-config>
    <!-- 默认配置，如果没有指定使用则使用这个配置 -->
    <default-config>
        <property name="driverClass">com.mysql.jdbc.Driver</property>
        <property name="jdbcUrl">jdbc:mysql://localhost:3306/test</property>
        <property name="user">root</property>
        <property name="password"></property>
        <property name="acquireIncrement">5</property>
        <property name="initialPoolSize">10</property>
        <property name="minPoolSize">10</property>
        <property name="maxPoolSize">50</property>
        <property name="maxStatements">5</property>
        <property name="maxStatementsPerConnection">5</property>
    </default-config>
</c3p0-config>
```

或者到官网查看标准配置 [C3P0](<https://www.mchange.com/projects/c3p0/>)

![1576672609038](C:\Users\Howl\AppData\Roaming\Typora\typora-user-images\1576672609038.png)







#### 2. C3P0Util工具类

```java
public class C3P0Util {
	
	private static ComboPooledDataSource comboPooledDataSource = new ComboPooledDataSource();
	
	public static Connection getConnection(){
		try {
			return comboPooledDataSource.getConnection();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
}
```





#### 3. 测试

```java
public class Test {
	
	public static void main(String[] args) {
		
        //这里的关闭函数被c3p0动态代理了，被改写为放入连接池
		try(Connection conn = C3P0Util.getConnection()) {
			String sql = "INSERT INTO  account (`money`) VALUES (100)";
			PreparedStatement ps =  conn.prepareStatement(sql,Statement.RETURN_GENERATED_KEYS);
			if (ps.executeUpdate() > 0){
				ResultSet rs1 = ps.getGeneratedKeys();
				while(rs1.next()){
					System.out.println(rs1.getInt(1));
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
```

输出结果

```xml
9  
s#返回的主键
#测试成功
```

