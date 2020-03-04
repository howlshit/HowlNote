学习Lambda表达式之前需要一些铺垫，下面直接开始把



### 1. 接口的默认方法

接口之前定义为只有常量和抽象方法，JDK1.8之后增加了默认方法



```java
public interface Test {
    
	int num = 10;
	
	abstract void say();

	default void sayHello(){
		System.out.println("sayHello");
	}
}
```

```java
public static void main(String[] args) {
	
	Test test = new Test(){
		@Override
		public void say() {
			System.out.println("say");
		}
	};
	test.say();
	test.sayHello();
}
```

```
say
sayHello
```









### 2. 函数式接口

函数式接口(Functional Interface)就是一个有且仅有一个抽象方法，但是可以有多个非抽象方法的接口，下面举例多线程的Runnable接口



```java
@FunctionalInterface
public interface Runnable {
    
    public abstract void run();
}
```

从中可以看出该接口只有一个抽象方法，并且方法上标有@FunctionalInterface注解，不了解注解的同学可以康康 [注解传送门](<https://www.cnblogs.com/Howlet/p/12252541.html>)









### 3. Lambda表达式

这个不知道怎么开头，所以看代码先



```java
public static void main(String[] args) {

    Thread thread = new Thread(
        ()-> { System.out.println("Lambda表达式"); }	// Lambda表达式
    );
    thread.start();
}
```

```java
Lambda表达式
```



* 其中 `() - > {}`，圆括号表示抽象方法的参数列表括号，大括号表示抽象方法的方法体
* return 可以省略，形参类型也是，因为会自动对应
* Lambda表达式表示的是接口的实例对象，记住不是方法喔









### 4.  `::` 引用



创建一个类

```java
class Test {
	
	// 静态方法
    public static void testStatic(String str) {
        System.out.println(str);
    }

    // 实例方法
    public void testInstance(String name) {
        System.out.println(name);
    }

    // 无参构造方法
    public Test() {
    }
}
```

`::`引用

```java
public static void main(String[] args) {
    
    // 静态方法引用
    Consumer<String> Consumer1 = Test::testStatic;
    Consumer1.accept("Howl");

    // 实例方法引用
    Test test = new Test();
    Consumer<String> consumer2 = test::testInstance;
    consumer2.accept("Howl");

    // 构造方法方法引用
    Supplier<Test> supplier = Test::new;
    System.out.println(supplier.get());
}
```

输出

```
Howl
Howl
Test@e9e54c2
```



