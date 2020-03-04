**JDBC**

是Java的API，各数据库厂商负责实现，我们只要用数据库驱动程序就好，这样就避免了使用不同数据库就要学习不同数据库的方法



**JDBC对象及其方法**





### 1. Connection：与数据库连接的对象

| 类型              | 方法名                       | 解释         |
| ----------------- | ---------------------------- | ------------ |
| void              | close                        | 关闭连接     |
| void              | commit                       | 提交任务     |
| void              | rollback                     | 回滚         |
| void              | setAutoCommmit(boolean bool) | 设置提交方式 |
| PreparedStatement | preparedStatement            | 创建该对象   |
| Statement         | createStatement              | 创建该对象   |
| CallableStatement | prepareCall(String sql)      | 创建该对象   |





### 2. Statement：向数据库发送Sql语句的对象

| 类型      | 方法名                    | 解释                                    |
| --------- | ------------------------- | --------------------------------------- |
| void      | addBatch(String sql)      | 多条的sql放进同一个批处理（增删改语句） |
| int[]     | executeBatch              | 执行批处理，返回数组                    |
| void      | clearBatch                | 清空批处理内容                          |
| void      | close                     | 关闭                                    |
| boolean   | execute(String sql)       | 执行查询语句返回true，增删改返回false   |
| ResultSet | getResultSet              | 返回上面执行查询后的结果集              |
| int       | getUpdateCount            | 返回上面执行增删改后的影响行数          |
| ResultSet | executeQuery(String sql)  | 返回结果集（执行查询）                  |
| int       | executeUpdate(String sql) | 返回影响条数（执行增删改）              |

```java
// 其中execute(String sql)能执行查询和增删改，查询返回true，增删改返回false
// execute.getResultSet()获取结果集，getUpdateCount()获取影响条数
```





### 3.PreparedStatement：继承Statement，预编译Sql语句存储在本对象中，防止SQL注入，之后的参数不再编译

| 类型 | 方法名                                  | 解释                 |
| ---- | --------------------------------------- | -------------------- |
| void | setString(int parameterIndex, String x) | 给定下标的占位符赋值 |
| void | setInt(int parameterIndex, Int x)       | 给定下标的占位符赋值 |

```java
// 插入语句后获取到自增主键列的值
// 获取执行对象时需多加个参数，版本不兼容问题 `Statement.RETURN_GENERATED_KEYS`


String sql = "INSERT INTO things (`name`) VALUES (?)";
PreparedStatement ps =  conn.prepareStatement(sql,Statement.RETURN_GENERATED_KEYS);
ps.setString(1, "手机");
int num = ps.executeUpdate();
resultSet rs = ps.getGeneratedKeys();
if (rs.next()) {
    int id = rs.getInt(1);
    System.out.println(id);
}
```





### 4. ResultSet：Sql语句的执行结果，当生成ResultSet的Statement对象要关闭或者重新执行或是获取下一个ResultSet的时候，ResultSet对象也会自动关闭

| 类型    | 方法名                        | 解释                       |
| ------- | ----------------------------- | -------------------------- |
| boolean | absolute(int row)             | 将光标移到给定位置         |
| void    | afterLast                     | 光标移到最后一个之后       |
| void    | beforeFirst                   | 光标移到第一个之前         |
| void    | close                         | 关闭                       |
| boolean | first                         | 光标移到第一行             |
| boolean | last                          | 光标移到最后一行           |
| boolean | next                          | 光标往下移                 |
| boolean | previous                      | 光标往上移                 |
| int     | getRow                        | 返回当前行号               |
| Object  | getObject(String columnLabel) | 返回结果集中给定字段的对象 |
| String  | getString(String columnLabel) | 返回结果集中给定字段的值   |

```java
// 查看总行数的方法
rs.last()
rs.getRow()
// 结果集指针一开始是在第一个之前的
```















**步骤**

1. 导入驱动包
2. 加载驱动程序
3. 获取连接
4. 获取执行SQL语句的对象
5. 执行SQL语句
6. 关闭连接

```java
public class DBUtil {
	
	private static String driver = "com.mysql.jdbc.Driver";
	private static String url = "jdbc:mysql://localhost:3306/test";
	private static String username = "root";
	private static String password = "";
	
	static{
		 try {
             //注册驱动
			Class.forName(driver);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	
	public static Connection getConnection() throws SQLException {
        //获取数据库连接
	    return DriverManager.getConnection(url,username,password);
	}
	
	public static void closeConnection(Connection connection, Statement statement,PreparedStatement preparedstatement ,ResultSet resultSet){
		if (resultSet != null) {
	        try {
	            resultSet.close();
	        } catch (SQLException e) {
	            e.printStackTrace();
	        }
	    }
	    if (statement != null) {
	        try {
	            statement.close();
	        } catch (SQLException e) {
	            e.printStackTrace();
	        }
	    }
	    if (preparedstatement != null) {
	        try {
	        	preparedstatement.close();
	        } catch (SQLException e) {
	            e.printStackTrace();
	        }
	    }
	    if (connection != null) {
	        try {
	            connection.close();
	        } catch (SQLException e) {
	            e.printStackTrace();
	        }
	    }
	}
}
```



**批处理**

Statement，返回一个int[]数组，该数组代表各句SQL的返回值

```java
//可以处理不同语句
String sql1 = "UPDATE <表名> SET name='Howl' WHERE id='1'";
String sql2 = "INSERT INTO <表名> (id, name) VALUES ('1','Howl')";

//将sql添加到批处理
statement.addBatch(sql1);
statement.addBatch(sql2);

//执行批处理
statement.executeBatch();

//清空批处理的sql
statement.clearBatch();
```

PreparedStatement

```java
//只能处理同类型语句
```



**事务**

```java
try{
	//关闭自动提交
	conn.setAutoCommit(false);
	
	//账号1减去50
	String sql1 = "UPDATE account SET money = money-50 WHERE id = 1";
	PreparedStatement ps1 = conn.prepareStatement(sql1);
    ps1.executeUpdate();
	
	//报错
	int a = 1 / 0;
	
	//账号2加50
	String sql2 = "UPDATE account SET money = money+50 WHERE id = 2";
	PreparedStatement ps2 = conn.prepareStatement(sql2);
    ps2.executeUpdate();
	
    //提交
    conn.commit();
    //关闭事务，防止后面使用不提交
    conn.setAutoCommit(true);
} catch (SQLException e) {
	try {
		//回滚
		conn.rollback();
		conn.setAutoCommit(true);
	} catch (SQLException e1) {
		e1.printStackTrace();
	}
}
```



**存储过程**

调用存储过程的语法：

```
{call <procedure-name>[(<arg1>,<arg2>, ...)]}
```

调用函数的语法：

```
{?= call <procedure-name>[(<arg1>,<arg2>, ...)]}
```

```java
public class Test {

    public static void main(String[] args) {
        Connection connection = null;
        CallableStatement callableStatement = null;

        try {
            connection = JdbcUtils.getConnection();

            callableStatement = connection.prepareCall("{call demoSp(?,?)}");

            callableStatement.setString(1, "nihaoa");

            //注册第2个参数,类型是VARCHAR
            callableStatement.registerOutParameter(2, Types.VARCHAR);
            callableStatement.execute();

            //获取传出参数[获取存储过程里的值]
            String result = callableStatement.getString(2);
            System.out.println(result);

        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            try {
                callableStatement.close();
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}    
```

