## 1. SPI

SPI （ Service Provider Interface），是JDK提供的一种服务发现机制。可发现并自动加载在ClassPath下的jar包中META-INF/services文件下以服务接口命名的文件内的全限定类名映射的类。当服务的提供者，提供了服务接口的一种实现之后，只需在jar包的META-INF/services/目录里同时创建一个以服务接口命名的文件即可被程序加载并使用。



在Coding中，模块间使用接口编程可实现松耦合，而不进行硬编码。那么SPI的出现可用于动态地启用框架扩展和替换组件，其常见应用：

- 数据库驱动加载接口实现类的加载
- 日志门面接口实现类加载
- Spring中servlet3.0规范对ServletContainerInitializer的实现



简单来说：客户端提供了自己所需服务的接口，而服务端有很多各自有各自的不同实现。那么客户端可根据自己选择的不同服务端而动态的加载并使用这些服务





## 使用栗子

- 服务端提供了具体实现类后，要在jar包的META-INF/services目录下创建一个以“接口全限定名”为命名的文件，内容为实现类的全限定名
- 服务实现类所在的jar包要在classpath中
- 使用java.util.ServiceLoder动态装载实现模块，它通过扫描META-INF/services目录下的配置文件找到实现类的全限定名，把类加载到JVM
- SPI的实现类必须携带一个不带参数的构造方法





#### 1.1 客户端提供接口

```java
public interface SPInterface {

    public abstract void eat();

}
```





#### 1.2 不同服务端提供各自的实现类

```java
public class Dog implements SPInterface {
    @Override
    public void eat() {
        System.out.println("狗吃肉");
    }
}
```



```java
public class Cat implements SPInterface {
    @Override
    public void eat() {
        System.out.println("猫吃鱼");
    }
}
```





#### 1.3 服务端建立Services文件

类路径下建 /META-INF/services 目录， 并创建用接口命名的文件 ，其内容为服务实现类的全限定类名（每行一个类名）

```
com.howl.spi.impl.Cat
com.howl.spi.impl.Dog
```





#### 1.4 使用SPI机制

```java
public class main {


    public static void main(String[] args) {
        ServiceLoader<SPInterface> spInterfaces = ServiceLoader.load(SPInterface.class);
        Iterator<SPInterface> iterator = spInterfaces.iterator();
        while (iterator.hasNext()){
            iterator.next().eat();
        }
    }
}
```

```
猫吃鱼
狗吃肉
```













## 2. JDBC的SPI解析

下面从源码的角度解析SPI机制在JDBC中的使用



#### 2.1 以前我们使用jdbc的流程

```java
public class Demo {

    public static void main(String[] args) {

        String driver = "com.mysql.cj.jdbc.Driver";
        String url = "jdbc:mysql://localhost:3306/test";
        String user = "root";
        String password = "123456";

        try {
            Class.forName(driver);    // 在内存中加载driver类
            Connection conn = DriverManager.getConnection(url,user,password);  // 获取连接
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }
}
```

* 较新的jar包driver改名成了 com.mysql.cj.jdbc.Driver
* Class.forName(driver)   ，使用了桥接模式，抽象与实现分离
* 使用 Class.forName，不用new因为不同实现子类可以通过改变配置文件而实现，若new则要修改代码编译





#### 2.2 进入driver类

```java
public class Driver extends NonRegisteringDriver implements java.sql.Driver {

    static {
        try {
            java.sql.DriverManager.registerDriver(new Driver());  // 在静态代码块注册了driver
        } catch (SQLException E) {
            throw new RuntimeException("Can't register driver!");
        }
    }
```

* 这里有个更新：以前的dvier继承了 jc 的新类了，而 jc 实现了java.sql.Driver接口
* JDBC规范中要求Driver类在使用前必须向DriverManager注册自己（静态代码块中实现了，可不用手动注册）





#### 2.3 SPI的使用

新的版本中，Class.forName(driver)也已经自动实现了，可不用手动加载。**原因：**在DriverManager中使用了SPI机制

```java
public class DriverManager {

    /**
     * Load the initial JDBC drivers by checking the System property
     * jdbc.properties and then use the {@code ServiceLoader} mechanism
     *
     * 通过 检查系统变量 jdbc.properties 然后使用 ServiceLoader 机制
     * 来加载初始化的JDBC dirvers
     */
    static {
        loadInitialDrivers();   // 这里加载并实例化了Drivers
        println("JDBC DriverManager initialized");
    }
}
```



```java
private static void loadInitialDrivers() {
    String drivers;

    AccessController.doPrivileged(new PrivilegedAction<Void>() {
        public Void run() {

            // SPI机制
            // 加载Driver接口的服务类，Driver接口的包是java.sql.Driver
            // 即从找 META-INF/services/java.sql.Driver中的全限定类名
            ServiceLoader<Driver> loadedDrivers = ServiceLoader.load(Driver.class);
            Iterator<Driver> driversIterator = loadedDrivers.iterator();

			// 遍历每一个Driver类，即加载每一个驱动，以便实例化
            try{
                while(driversIterator.hasNext()) {
                    driversIterator.next();  // 这里实例化
                }
            } catch(Throwable t) {
            // Do nothing
            }
            return null;
        }
    });
```

* ServiceLoader.load(Driver.class)  使用了SPI机制加载了Driver类
* 迭代器的next()方法就是创建实例，从而向Manager注册了自己
* 在mysql-connection-java包下的META-INF/services/java.sql.Driver中有 全限定类名 com.mysql.cj.jdbc.Driver





#### 2.4 新版的JDBC使用

```java
public class Demo {

    public static void main(String[] args) {

        String driver = "com.mysql.cj.jdbc.Driver";
        String url = "jdbc:mysql://47.56.143.47:3306/test";
        String user = "root";
        String password = "123456";

        try {
            Connection conn = DriverManager.getConnection(url,user,password);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
```

