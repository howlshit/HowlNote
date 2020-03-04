**日志系统**

日志是维护项目的有利工具，代替System.out.println()来定位BUG

JDK有自带的 JUL(java util logging) 日志系统，并不需要引用别的类库，但这并不满足我们的需求，所以我们用log4j代替，注意的是Apache为了让众多的日志工具有一个相同操作方式，实现了一个通用日志工具包：commons-logging，所要使用log4j就先得有commons-logging支持





**日志的三大组件**

Logger：日志记录器

Appender：日志输出目的地

 Layout：控制输出流的格式



Logger官方建议使用四个级别，由低到高分别是：

| 级别  | 解释                                       |
| ----- | ------------------------------------------ |
| ERROR | 发生错误事件，但不影响系统继续运行         |
| WARN  | 警告潜在错误的情形                         |
| INFO  | 打印你感兴趣的或者重要的信息，用于生产环境 |
| DEBUG | 主要用于开发过程中打印一些运行信息         |



布局格式化日志

| 类别          | 解释                         |
| ------------- | ---------------------------- |
| HTMLLayout    | HTML形式                     |
| PatternLayout | 指定布局模式                 |
| SimpleLayout  | 日志级别和信息字符串         |
| TTCCLayout    | 日志产生的时间、线程、类别等 |



附加器输出地方

| 分类                     | 解释                                       |
| ------------------------ | ------------------------------------------ |
| ConsoleAppender          | 输出到控制台                               |
| FileAppender             | 输出到文件                                 |
| DailyRollingFileAppender | 输出到每天生成的新文件                     |
| RollingFileAppender      | 文件大小到达指定尺寸的时候产生一个新的文件 |
| JDBCAppender             | 保存到数据库中                             |
| WriterAppender           | 以流格式发送到任意指定的地方               |



**基本使用**



1. 导包

* log4j-1.2.17.jar
* commons-logging-1.2.jar



2. src下新建log4j.properties

```properties
# 配置根logger，预定义附加器
log4j.rootLogger = WARN, console, file

# 配置console附加器
log4j.appender.console = org.apache.log4j.ConsoleAppender
log4j.appender.console.layout = org.apache.log4j.PatternLayout
log4j.appender.console.layout.ConversionPattern = %d{yyyy/MM/dd HH:mm:ss} %p [%c -Row:%L] --> %m%n

# 配置file附加器
log4j.appender.file = org.apache.log4j.DailyRollingFileAppender
log4j.appender.file.File = C\:\\Users\\Howl\\Desktop\\log4j.txt
log4j.appender.file.layout = org.apache.log4j.PatternLayout
log4j.appender.file.layout.ConversionPattern = %d{yyyy/MM/dd HH:mm:ss} %p [%c -Row:%L] --> %m%n
log4j.appender.file.DatePattern ='.'yyyy-MM
log4j.appender.file.Threshold = ERROR
```



3. 测试	

	public static void main(String[] args) {
		
		//获取Logger对象的实例 
		Logger logger = Logger.getLogger(Log4jTest.class);
			
		//日志报错
		logger.debug("这是debug");
	    logger.info("这是info");
	    logger.warn("这是warn");
	    logger.error("这是error");
	    logger.fatal("这是fatal");
	}



4.  打印

```xml
2019/12/19 12:16:05 WARN [logging.Log4jTest -Row:22] --> 这是warn
2019/12/19 12:16:05 ERROR [logging.Log4jTest -Row:23] --> 这是error
2019/12/19 12:16:05 FATAL [logging.Log4jTest -Row:24] --> 这是fatal
```













****

参考[Log4J日志整合及配置详解](https://www.cnblogs.com/wangzhuxing/p/7753420.html)

