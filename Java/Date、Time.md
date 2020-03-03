## 1. 时间API

我们的时间在java里是long类型的整数，这个整数称之为时间戳（也叫格林威治时间），即从1970-01-01到现在为止所经过的毫秒数，单有这个时间戳是不能准确表达世界各地的时间，还需加上时区。比如现在输出笔者本地的时间`Mon Feb 10 09:48:43 GMT+08:00 2020`，其中 GMT+08:00 表示格林威治时间的东8区，也就是北京时间。









## 2. 旧时间API

java有两套与时间相关的API，分别位于java.util和java.time下，现在更推荐使用time包下的API，由于历史原因，我们还需兼容以前版本的时间函数，所以util也要来学习一下





### 2.1 Date



**Date类内部原理**

```java
private transient long fastTime;

public Date() {
    this(System.currentTimeMillis());
}

public Date(long date) {
    fastTime = date;
}
```

* 从变量可以看出java时间戳使用long类型存储
* 默认构造函数中的`System.currentTimeMillis()`获取的是当前的时间戳
* 有参构造说明Date类是依赖时间戳的





**从API和JDK可以知道Date内部很多函数都弃用了，打上了@Deprecated标签，旧API中也不推荐使用了，相对推荐使用后面的Calendar类**

![1581300334791](C:\Users\Howl\AppData\Roaming\Typora\typora-user-images\1581300334791.png)

```java
@Deprecated
public Date(int year, int month, int date) {}

@Deprecated
public Date(String s) {}
```





**下面列出常用的方法**

| 类型 | 函数名     | 解释                          |
| ---- | ---------- | ----------------------------- |
| long | getTime    | 返回Date对象表示的时间戳      |
| int  | getYear    | 返回Date对象表示的年，需+1900 |
| int  | getMonth   | 返回Date对象表示的月，需+1    |
| int  | getDate    | 返回Date对象表示的日          |
| int  | getHours   | 返回Date对象表示的小时        |
| int  | getMinutes | 返回Date对象表示的分钟        |
| int  | getSeconds | 返回Date对象表示的秒          |
| int  | getDay     | 返回Date对象表示的周几        |





**例子**

```java
Date date = new Date();
System.out.println(date.getTime());
System.out.println(date.getYear() + 1900);
System.out.println(date.getMonth() + 1);
System.out.println(date.getDate());
```

```
1581301070240
2020
2
10
```





**简单使用时间格式化类**

```java
SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		
System.out.println("简单使用时间格式化类： " + sdf.format(date)); 
System.out.println("原本的输出：" + date);
```

```
简单使用时间格式化类： 2020-02-10 10:24:13
原本的输出：Mon Feb 10 10:24:13 GMT+08:00 2020
```













### 2.2 Calendar

Calendar类比Date多了日期计算功能，还有时区。并且Calendar是抽象类，构造方法为protected即外部包没有继承关系不能访问的，不过他提供了getInstance来获取实例



**获取实例**

```java
protected Calendar(){
    // 省略内部如何实现
}

public static Calendar getInstance(){
    // 省略内部如何实现
}
```





**常用方法**

| 类型 | 函数名                      | 解释                           |
| ---- | --------------------------- | ------------------------------ |
| int  | get(int field)              | 返回日历给定字段的值           |
| Date | getTime                     | 返回当前Calendar表示的Date     |
| void | setTimeZone(TimeZone value) | 设置时区                       |
| void | add(int field, int amount)  | 当前日历在给定字段上增加给定值 |
| void | setTime(Date date)          | 将给定Date设置为日历           |





**栗子**

```java
Calendar calendar = Calendar.getInstance();
		
System.out.println(calendar.get(Calendar.YEAR));
System.out.println(calendar.get(Calendar.MONTH) + 1);
System.out.println(calendar.get(Calendar.DATE));

calendar.add(Calendar.YEAR,200);
System.out.println(calendar.get(Calendar.YEAR));

Date date = calendar.getTime();
```

```
2020
2
10
2220
```









### 2.3 TimeZone

TimeZone类就是可以设置时区咯



**常用方法**

| 类型     | 函数名                          | 解释                     |
| -------- | ------------------------------- | ------------------------ |
| TimeZone | getDefault                      | 返回默认时区             |
| TimeZone | getTimeZone(String ID / zoneId) | 返回给定字符串对应的时区 |





**板栗**

```java
Calendar calendar = Calendar.getInstance();
System.out.println(calendar.get(Calendar.HOUR));	// 笔者当前时间11：09
calendar.setTimeZone(TimeZone.getTimeZone("GMT+10:00"));
System.out.println(calendar.get(Calendar.HOUR));	// 东10区时间为01：09
```

```
11
1
```













## 3. java1.8的新时间API

推出新时间API是因为旧API饱受诟病，因为：

* Date为可变（多线程访问时间会变化）
* 输出不人性化（Mon Feb 10 10:24:13 GMT+08:00 2020，且从1900开始计算）
* 格式化类线程不安全（多线程访问格式化不同）



**所以新API，java.time包所有类都是不可变和线程安全，且区分日期与时间**

```java
LocalDate date = LocalDate.now();
LocalTime time = LocalTime.now();
LocalDateTime dateTime = LocalDateTime.now();

System.out.println(date);
System.out.println(time);
System.out.println(dateTime);
```

```
2020-02-10
11:39:57.147
2020-02-10T11:39:57.147
```



**输出看起来是不是很舒服，可是LocalDateTime的输出有点懵?，特意找了LocalDateTime的toString方法**

```java
@Override
public String toString() {
    return date.toString() + 'T' + time.toString();
}
```

从中可以看出内部是维护了LocalDate和LocalTime了，中间使用了字符 `T`隔开，为什么不用空格？？？？







### 3.1 LocalDate



**常用方法**

| 类型      | 函数名                                  | 解释         |
| --------- | --------------------------------------- | ------------ |
| LocalDate | now                                     | 返回当前日期 |
| int       | getYear                                 | 返回年       |
| int       | getMonthValue                           | 返回月       |
| int       | getDayOfMonth                           | 返回日       |
| LocalDate | of(int year, int month, int dayOfMonth) | 创建实例     |



**西瓜**

```java
LocalDate localDate = LocalDate.now();
System.out.println(localDate.getYear());
System.out.println(localDate.getMonthValue());
System.out.println(localDate.getDayOfMonth());
```

```
2020
2
10
```





### 3.2 LocalTime

这些其实都差不多就不一一说明了



**桃子**

```java
LocalTime localTime = LocalTime.now();
System.out.println(localTime.getHour());
System.out.println(localTime.getMinute());
System.out.println(localTime.getSecond());
```

```
12
1
18
```





### 3.3 LocalDateTime 

这里新增了plus和minus方法，对应加减，因为是不可变性，所以和String类一样，返回一个新副本



**苹果**

```java
LocalDateTime localDateTime = LocalDateTime.now();
System.out.println(localDateTime);

LocalDateTime localTime1 = localDateTime.plusYears(1).plusMonths(1).plusDays(1);
System.out.println(localTime1);
        
LocalDateTime localTime2 = localDateTime.minusYears(1).minusMonths(1).minusDays(1);
System.out.println(localTime2);
```

```
2020-02-10T12:07:44.403
2021-03-11T12:07:44.403
2019-01-09T12:07:44.403
```





### 3.4 获取时间戳

```java
Instant timestamp = Instant.now();
```





### 3.5 获取时间差

```java
LocalDateTime startDateTime = LocalDateTime.of(2020, 2, 10, 10, 10, 10);
LocalDateTime endDateTime = LocalDateTime.of(2019, 1, 9, 9, 9, 9);

Duration duration = Duration.between(startDateTime, endDateTime);
System.out.println(duration);
```

```
PT-9529H-1M-1S		// 相隔9529小时1分1秒
```

