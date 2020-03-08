> 有需求就要想办法解决，最近参与的项目其涉及的三个数据表分别在三台不同的服务器上，这就有点突兀了，第一次遇到这种情况，可这难不倒笔者，资料一查，代码一打，回头看看源码，万事大吉







## 1. 预备知识

这里默认大家都会SSM框架了，使用时我们要往sqlSessionFactory里注入数据源。那么猜测：1、可以往sqlSessionFactory里注入多数据源来实现切换；2、将多个数据源封装成一个`总源`，再把这个`总源`注入到sqlSessionFactory里实现切换。答案是使用后者，即封装成`总源`的形式。Spring提供了动态切换数据源的功能，那么我们来看看其实现原理









## 2. 实现原理

笔者是根据源码讲解的，这些步骤讲完会贴出源码内容



### 一、

Spring提供了AbstractRoutingDataSource抽象类，其继承了AbstractDataSource。而AbstractDataSource又实现了DataSource。因此我们可以将AbstractRoutingDataSource的实现类注入到sqlSessionFactory中来实现切换数据源



### 二、

刚才我们将多个数据源封装成`总源`的想法在AbstractRoutingDataSource中有体现，其内部用一个Map集合封装多个数据源，即 `private Map<Object, DataSource> resolvedDataSources;` ，那么要使用时从该Map集合中获取即可



### 三、

AbstractRoutingDataSource中有个determineTargetDataSource()方法，其作用是决定使用哪个数据源。我们通过determineTargetDataSource()方法从Map集合中获取数据源，那么必须有个key值指定才行。所以determineTargetDataSource()方法内部通过调用determineCurrentLookupKey()方法来获取key值，Spring将determineCurrentLookupKey()方法抽象出来给用户实现，从而让用户决定使用哪个数据源



### 四、

既然知道我们需要重写determineCurrentLookupKey()方法，那么就开始把。实现时发现该方法没有参数，我们无法传参来决定返回的key值，又不能改动方法（因为是重写），所以方法内部调用我们自定义类的静态方法即可解决问题

```java
public class DynamicDataSource extends AbstractRoutingDataSource {
    @Override
    protected Object determineCurrentLookupKey() {
        return DynamicDataSourceHolder.getDataSourceKey();
    }
}
```



### 五、

自定义类，作用是让我们传入key值来决定使用哪个key

```java
public class DynamicDataSourceHolder {

    // ThreadLocal没什么好说的，绑定当前线程
    private static final ThreadLocal<String> dataSourceKey = new ThreadLocal<String>();

    public static String getDataSourceKey(){
        return dataSourceKey.get();
    }

    public static void setDataSourceKey(String key){
        dataSourceKey.set(key);
    }

    public static void clearDataSourceKey(){
        dataSourceKey.remove();
    }
}
```



### 六、

AbstractRoutingDataSource抽象类源码（**不喜可跳**）

```java
public abstract class AbstractRoutingDataSource extends AbstractDataSource implements InitializingBean {
    @Nullable
    private Map<Object, Object> targetDataSources;
    @Nullable
    private Object defaultTargetDataSource;
    private boolean lenientFallback = true;
    private DataSourceLookup dataSourceLookup = new JndiDataSourceLookup();
    @Nullable
    private Map<Object, DataSource> resolvedDataSources;
    @Nullable
    private DataSource resolvedDefaultDataSource;

    public AbstractRoutingDataSource() {
    }

    public void setTargetDataSources(Map<Object, Object> targetDataSources) {
        this.targetDataSources = targetDataSources;
    }

    public void setDefaultTargetDataSource(Object defaultTargetDataSource) {
        this.defaultTargetDataSource = defaultTargetDataSource;
    }

    public void setLenientFallback(boolean lenientFallback) {
        this.lenientFallback = lenientFallback;
    }

    public void setDataSourceLookup(@Nullable DataSourceLookup dataSourceLookup) {
        this.dataSourceLookup = (DataSourceLookup)(dataSourceLookup != null ? dataSourceLookup : new JndiDataSourceLookup());
    }

    public void afterPropertiesSet() {
        if (this.targetDataSources == null) {
            throw new IllegalArgumentException("Property 'targetDataSources' is required");
        } else {
            this.resolvedDataSources = new HashMap(this.targetDataSources.size());
            this.targetDataSources.forEach((key, value) -> {
                Object lookupKey = this.resolveSpecifiedLookupKey(key);
                DataSource dataSource = this.resolveSpecifiedDataSource(value);
                this.resolvedDataSources.put(lookupKey, dataSource);
            });
            if (this.defaultTargetDataSource != null) {
                this.resolvedDefaultDataSource = this.resolveSpecifiedDataSource(this.defaultTargetDataSource);
            }

        }
    }

    protected Object resolveSpecifiedLookupKey(Object lookupKey) {
        return lookupKey;
    }

    protected DataSource resolveSpecifiedDataSource(Object dataSource) throws IllegalArgumentException {
        if (dataSource instanceof DataSource) {
            return (DataSource)dataSource;
        } else if (dataSource instanceof String) {
            return this.dataSourceLookup.getDataSource((String)dataSource);
        } else {
            throw new IllegalArgumentException("Illegal data source value - only [javax.sql.DataSource] and String supported: " + dataSource);
        }
    }

    public Connection getConnection() throws SQLException {
        return this.determineTargetDataSource().getConnection();
    }

    public Connection getConnection(String username, String password) throws SQLException {
        return this.determineTargetDataSource().getConnection(username, password);
    }

    public <T> T unwrap(Class<T> iface) throws SQLException {
        return iface.isInstance(this) ? this : this.determineTargetDataSource().unwrap(iface);
    }

    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return iface.isInstance(this) || this.determineTargetDataSource().isWrapperFor(iface);
    }

    protected DataSource determineTargetDataSource() {
        Assert.notNull(this.resolvedDataSources, "DataSource router not initialized");
        Object lookupKey = this.determineCurrentLookupKey();
        DataSource dataSource = (DataSource)this.resolvedDataSources.get(lookupKey);
        if (dataSource == null && (this.lenientFallback || lookupKey == null)) {
            dataSource = this.resolvedDefaultDataSource;
        }

        if (dataSource == null) {
            throw new IllegalStateException("Cannot determine target DataSource for lookup key [" + lookupKey + "]");
        } else {
            return dataSource;
        }
    }

    @Nullable
    protected abstract Object determineCurrentLookupKey();
}
```









## 3. 配置



### 3.1 配置db.properties

这里配置两个数据库，一个评论库，一个用户库

```properties
# 问题库
howl.comments.driverClassName = com.mysql.jdbc.Driver
howl.comments.url = jdbc:mysql://127.0.0.1:3306/comment
howl.comments.username = root
howl.comments.password =

# 用户库
howl.users.driverClassName = com.mysql.jdbc.Driver
howl.users.url = jdbc:mysql://127.0.0.1:3306/user
howl.users.username = root
howl.users.password =
```





### 3.2 配置applicationContext.xml

```xml
<!--  加载properties文件  -->
<context:property-placeholder location="classpath:db.properties"></context:property-placeholder>


<!--  问题的数据源  -->
<bean id="commentsDataSource" class="org.springframework.jdbc.datasource.DriverManagerDataSource">
	<property name="driverClassName" value="${howl.comments.driverClassName}"></property>
	<property name="url" value="${howl.comments.url}"></property>
    <property name="username" value="${howl.comments.username}"></property>
    <property name="password" value="${howl.comments.password}"></property>
</bean>


<!--  用户的数据源  -->
<bean id="usersDataSource" class="org.springframework.jdbc.datasource.DriverManagerDataSource">
	<property name="driverClassName" value="${howl.users.driverClassName}"></property>
    <property name="url" value="${howl.users.url}"></property>
    <property name="username" value="${howl.users.username}"></property>
    <property name="password" value="${howl.users.password}"></property>
</bean>


<!--  通过setter方法，往DynamicDataSource的Map集合中注入数据  -->
<!--  具体参数，看名字可以明白  -->
<bean id="dynamicDataSource" class="com.howl.util.DynamicDataSource">
    <property name="targetDataSources">
        <map key-type="java.lang.String">
            <entry key="cds" value-ref="commentsDataSource"/>
            <entry key="uds" value-ref="usersDataSource"/>
        </map>
    </property>
    <property name="defaultTargetDataSource" ref="commentsDataSource"></property>
</bean>


<!--  将`总源`注入SqlSessionFactory工厂  -->
<bean id="sqlSessionFactory" class="org.mybatis.spring.SqlSessionFactoryBean">
    <property name="configLocation" value="classpath:mybatis-config.xml"></property>
    <property name="dataSource" ref="dynamicDataSource"></property>
</bean>
```

因为dynamicDataSource是继承AbstractRoutingDataSource，所以setter注入方法得去父类里面去找，开始笔者也是懵了一下





### 3.3 切换数据源

数据源是在Service层切换的



**UserService**

```java
@Service
public class UserService {

    @Autowired
    private UserDao userDao;

    public User selectUserById(int id) {

        // 表明使用usersDataSource库
        DynamicDataSourceHolder.setDataSourceKey("uds");
        return userDao.selectUserById(id);
    }
}
```



**CommentService**

```java
@Service
public class CommentService {

    @Autowired
    CommentDao commentDao;

    public List<Comment> selectCommentById(int blogId) {

        // 表明使用评论库
        DynamicDataSourceHolder.setDataSourceKey("cds");
        return commentDao.selectCommentById(blogId, -1);
    }
}
```







### 3.4 自动切换

手动切换容易忘记，我们学了AOP可以使用AOP来切换，这里使用注解实现



```xml
<!-- 开启AOP注解支持 -->
<aop:aspectj-autoproxy></aop:aspectj-autoproxy>
```



**切面类**

```java
@Component
@Aspect
public class DataSourceAspect {

    @Pointcut("execution(* com.howl.service.impl.*(..))")
    private void pt1() {
    }

    @Around("pt1()")
    public Object around(ProceedingJoinPoint pjp) {

        Object rtValue = null;
        try {
            String name = pjp.getTarget().getClass().getName();
            if (name.equals("com.howl.service.UserService")) {
                DynamicDataSourceHolder.setDataSourceKey("uds");
            }
            if (name.equals("com.howl.service.CommentService")){
                DynamicDataSourceHolder.setDataSourceKey("cds");
            }
            // 调用业务层方法
            rtValue = pjp.proceed();

            System.out.println("后置通知");
        } catch (Throwable t) {
            System.out.println("异常通知");
            t.printStackTrace();
        } finally {
            System.out.println("最终通知");
        }
        return rtValue;
    }
}
```

使用环绕通知实现切入com.howl.service.impl里的所有方法，在遇到UserService、CommentService时，前置通知动态切换对应的数据源











## 4. 总结

1. 以前笔者认为Service层多了impl包和接口是多余的，现在要用到AOP的时候后悔莫及，所以默认结构如此肯定有道理的
2. 出bug的时候，才知道分步测试哪里出问题了，如果TDD推动那么能快速定位报错地方，日志也很重要







****

参考

<https://www.jianshu.com/p/d97cd60e404f>