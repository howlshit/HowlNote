> 辛辛苦苦搬了一天砖，结果发现绝大部分时间是在写Bug，改起来真是痛不欲生，然后忽然想起来还有个JUnit测试，方便我们debug调试





## 1. Assertion

断言是一种调试程序的方式，可以理解为高级的异常，其常与测试类并用，使用`assert`关键字来实现断言。在JVM中默认是关闭断言的（这样在线上环境就不会启用，而在生产环境自己手动开启方便调试）



### 1.1 开启断言

点击菜单栏的Windows -> preference，在弹窗中选择Installed JRES编辑它，在默认参数中添加 -ea，确定完成

![1582898905049](C:\Users\Howl\AppData\Roaming\Typora\typora-user-images\1582898905049.png)







### 1.2 格式与使用

assert <布尔表达式>

assert <布尔表达式> ：消息



```java
public static void main(String[] args) {
    
    int num = 1;
    assert (num == 1) : "num不等于1";
	
}
```

```java
// 表达式为true,则没有断言发生
```



```java
public static void main(String[] args) {
    
    String str = "Hello World";
    assert (str.equals("Hello")) : "str不等于Hello";
		
}
```

```java
// 表达式为false，发生断言并由消息提示

Exception in thread "main" java.lang.AssertionError: str不等于Hello
	at Assert.main(Assert.java:10)
```









## 2. JUnit

基于TDD开发，经常要测试代码功能是否可使用，平常我们写完一个功能都是在类里的main()方法中来测试的，eg:



**完成的功能**

```java
public class Foo {
	
	public void add(int a, int b){
		System.out.println(a + b);
	}
	
	public void sub(int a, int b){
		System.out.println(a - b);
	}
    
    // 用来测试的main()方法
    public static void main(String[] args) {
    
        Foo f = new Foo();
        f.add(1, 1);
        f.sub(3, 1);
	}
}
```



**但是类中只能有一个main方法且不能把测试代码分离，又没有打印出测试结果和期望结果，而且难于编写一组通用的测试代码，所以我们需要一个完成的测试框架，这时JUnit出现了，几乎所有的IDE都集成了JUnit，这样我们就可以直接在IDE中编写并运行JUnit测试**







### 2.1 编写测试类

自动生成的目录结构，默认测试类以Test结尾，且在测试的方法上加上注解@Test，这时断言就出现作用了，使用断言能知道报错的具体内容。单元测试可以确保单个方法按照正确预期运行，如果修改了某个方法的代码，只需确保其对应的单元测试通过，即可认为改动正确。此外，测试代码本身就可以作为示例代码，用来演示如何调用该方法，对着要测试方法右键 -> Run AS -> JUnit Test

![1582943398290](C:\Users\Howl\AppData\Roaming\Typora\typora-user-images\1582943398290.png)







**没有出错**

![1582943806395](C:\Users\Howl\AppData\Roaming\Typora\typora-user-images\1582943806395.png)





**出错**

![1582943844877](C:\Users\Howl\AppData\Roaming\Typora\typora-user-images\1582943844877.png)

可以在左边看到红色的提示框，代表报错，提示框上放Runs表示运行的测试方法/通过的方法，报错的数量，失败次数











### 2.2 Fixture

进行测试时我们经常需要准备测试环境，比如需要创建对象，测试之后还需还原环境，若在每个测试方法内都写上环境内容就重复太多了，这时Fixture就出现了，其注解有：@Before，@After表示在每个测试方法前后运行

我们测试环境可用上面两个注解来实现



![1582944639229](C:\Users\Howl\AppData\Roaming\Typora\typora-user-images\1582944639229.png)







### 2.3 @Ignore

如果给测试方法加上@Ignore，则测试中暂时不允许该方法。不建议去掉@Test注解，这样就不知道该方法是个测试方法了





### 2.4 补充



**Timeout**

```java
@Test(timeout = 1000)	//毫秒为单位
public void TimeOut(){
    while(true){
        System.out.println("循环");
    }
}
```





**@Runwith(XXX.class)**

JUnit的测试方法都是在Runner（运行器）中执行的。使用@Runwith可以为这个测试类指定一个运行器。JUnit中有一个默认的Runner，所以我们平时没有使用这个注解，但在Spring中我们就可以看到@Runwith的身影，这时因为spring整合了JUnit，为其提供了一个包括Spring容器的运行器

```java
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:applicationContext.xml" })
public class SpringTest {
    
　　@Autowired
　　UserDao userDao;
 
　　@Test
　　public void testUserDao() {
　　}
}
```

