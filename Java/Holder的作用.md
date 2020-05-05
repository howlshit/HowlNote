> 笔者初看框架源码是真的头大，一大堆不懂的问题。然后一个个查资料弄懂，但这也是技能广度快速拓宽的原因，说起来也是庆幸





## 1. Holder

这个类在 javax.xml.ws.Holder 中，笔者在很多地方都遇到过XXXHoler类，只知道包装了我们需要的值，但不知道为什么要包装多一层。其实Holder这个类属于JAX-WS 2.0规范中的一个类，其作用是为不可变的对象引用提供一个可变的包装。



这就涉及到Java的按值传递与引用传递之争了。引用传递中在栈上copy一个引用的副本，其指向同一堆对象。但不可变类（比如String）是新建一个堆对象或指向常量池，这样在传递的时候如果需要两者可像引用传递那样改变就需要借助Holder。



> 补充 [按值传递还是引用传递](<https://www.cnblogs.com/Howlet/p/12803736.html>)









## 2.没有Holder的情况

我们在main方法中定义了不可变类String，然后传递给changeName方法改变其值



```java
public class test  {

    public static void main(String[] args) {

        String name = "my old name is Howl";
        changeName(name);
        System.out.println(name);	// my old name is Howl
    }

    private static void changeName(String name) {
        name = "my new name is Howllll";
    }
}
```



输出的肯定是 my old name is Howl，changeName()不能改变main方法里面的name值









## 3. 出现了Holder

此时Holder出现了，解决了不可变类的特殊引用传递问题



先来看看Holder类，简简单单。一个value属性及其构造方法

```java
public final class Holder<T> implements Serializable {

    private static final long serialVersionUID = 2623699057546497185L;

    /**
     * The value contained in the holder.
     */
    public T value;

    /**
     * Creates a new holder with a <code>null</code> value.
     */
    public Holder() {
    }

    /**
     * Create a new holder with the specified value.
     *
     * @param value The value to be stored in the holder.
     */
    public Holder(T value) {
        this.value = value;
    }
}
```



简单使用

```java
public class test  {

    public static void main(String[] args) {

        String name = "my old name is Howl";
        Holder holder = new Holder(name);
        changeName(holder);
        System.out.println(holder.value);  // my new name is Howllll
    }

    private static void changeName(Holder holder) {
        holder.value = "my new name is Howllll";
    }
}
```



* 此时输出就是 my new name is Howlllll，那么main方法通过调用Holder的方法来实现二者同步改变





#### 总结

Holder其实是利用了普通的引用传递（Holder）来实现不可变类的  `引用传递效果`











