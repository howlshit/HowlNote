> 在看《阿里巴巴开发手册》里面提到浮点数之间的等值判断不要用 ==，而是指定误差范围或用BigDecimal，然后才记忆起备忘录里BigDecimal还没写呢，就这篇幅写了一星期，因为实习完全没有时间啊啊啊啊啊啊啊啊



## 1. BigDecimal介绍



### 背景

我们知道计算机都是以二进制的形式存储数据的，而我们日常则是使用十进制，那么我们的 `数字` 存进计算机则需一个进制转换的过程，这过程就会损失精度的，就导致浮点数不能用等值判断



### 原因

十进制的` 0.1 `转换 成二进制为` 0.00011001...`，是无限小数，计算机存不下只能截取。后面这截取的无限小数还原成十进制就会损失精度不准确，不能用等值判断了



### 救星

BigDecimal的横空出世是为了解决浮点数的精度问题，其全限定类名为 java.math.BigDecimal，BigDecimal是一个对象，代表着不变的，任意精度的带符号的十进制数字，我们要使用该对象的方法来进行加减乘除的操作



### 原理

既然十进制小数转成二进制会损失精度，那么把十进制小数扩大成整数再转成二进制则会保持精度了





## 2. BigDecimal基本使用



### 2.1 常见构造方法

| 方法                                                        | 描述                       |
| ----------------------------------------------------------- | -------------------------- |
| BigDecimal(int val)                                         | 将int转换成BigDecimal      |
| BigDecimal(int val, MathContext mc)                         | 根据上下设置进行舍入       |
| BigDecimal(long val)                                        | 将long转换成BigDecimal     |
| BigDecimal(double)                                          | 将double转换成BigDecimal   |
| BigDecimal(String)                                          | 将String转换成BigDecimal   |
|                                                             |                            |
| MathContext(int setPrecision, RoundingMode setRoundingMode) | 上下文取舍(精度，舍入模式) |



**阿里手册规约：禁止使用构造方法 BigDecimal(double)的方式把 double 值转化为 BigDecimal 对象**

```java
BigDecimal num1 = new BigDecimal(0.1);
BigDecimal num2 = new BigDecimal("0.1");

System.out.println(num1);	// 0.1000000000000000055511151231257827021181583404541015625
System.out.println(num2);	// 0.1
```

**因为浮点数并不是一个准确的值，而String类型的就确定**



### 2.2 常用方法

| 方法                                        | 描述                                                      |
| ------------------------------------------- | --------------------------------------------------------- |
| abs()                                       | 返回一个绝对值BigDecimal对象                              |
| scale()                                     | 小数位数，包含末尾零。返回负数表示是一个正数，且有负数位0 |
| stripTrailingZeros()                        | 除去末尾的零，包含整数                                    |
| add(BigDecimal augend)                      | 被加数                                                    |
| add(BigDecimal augend, MathContext mc)      | 根据上下文取舍                                            |
| subtract(BigDecimal subtrahend)             | 被减数                                                    |
| multiply(BigDecimal multiplicand)           | 被乘数                                                    |
| divide(BigDecima divisor, int roundingMode) | 被除数，要指定上下文取舍，否则报错                        |
| divideAndRemainder(BigDecimal divisor)      | 求余                                                      |
| compareTo(BigDecimal val)                   | 比较数值大小，equals要求scale()相同，且值相同             |
| toString()                                  | 转成字符串                                                |
| intValue()                                  | 转成整型                                                  |
| longValue()                                 | 转成长整型                                                |



```java
BigDecimal num1 = new BigDecimal("0.01234");
BigDecimal num2 = new BigDecimal("0.56789");

-------------------------------------------------------------------

System.out.println(num1.add(num2));
System.out.println(num1.subtract(num2));
// 小数保留6位，四舍五入
System.out.println(num1.multiply(num2,new MathContext(6, RoundingMode.HALF_UP)));
// 小数保留4位，直接截取
System.out.println(num1.divide(num2,new MathContext(4,RoundingMode.DOWN)));

// 0.58023
// -0.55555
// 0.00700776
// 0.02172
    
-------------------------------------------------------------------    
    
BigDecimal num1 = new BigDecimal("0.123");
BigDecimal num2 = new BigDecimal("0.123000");

System.out.println(num1.equals(num2));
System.out.println(num1.compareTo(num2));

// false
// 0
```

**除法存在除不尽的情况，所以一定要使用上下文取舍器**

**BigDecimal比较用compareTo，而equals要求scale()即小数位数相同**

**ArithmeticException常见的算数异常**









## 3. BigInteger

Java原生提供的最大整型是长整型，占8字节64位，范围是-9223372036854775808 ~ 9223372036854775807，如果超过了这个范围，那么可以用不可变的BigInteger对象，其原理是内部使用 int[] 数组来模拟大数



### 3.1 常见构造函数

| 函数                    | 描述 |
| ----------------------- | ---- |
| BigInteger(byte[] val)  |      |
| BigInteger(String) val) |      |



### 3.2 常见方法

| 方法                     | 描述 |
| ------------------------ | ---- |
| add(BigInteger) val)     | 加法 |
| subtract(BigInteger val) |      |



```java
BigInteger num1 = new BigInteger("1234567890");
System.out.println(num1.pow(5));	// 2867971860299718107233761438093672048294900000

BigInteger num2 = new BigInteger("123456");
long num3 = num2.longValue();
System.out.println(num3);	// 123456
```

