> 笔者一直错在传递的理解，现在重复记下笔记，加深印象
>
> 首篇相关传递的笔记 <https://www.cnblogs.com/Howlet/p/12114605.html>





#### 1. Java是按值传递，即将栈中的值进行按值传递（创建副本）

#### 2. 当一个对象实例作为参数时，参数的值是该对象的引用的一个副本，该副本指向同一个堆实例





## 1. 基本类型

```java
public static void main(String[] args) throws IOException {
    int num = 0;
    change(num);  // num = 0
}

public static void change(int n){
    n = 1;
}
```

![1588153651798](C:\Users\Howl\AppData\Roaming\Typora\typora-user-images\1588153651798.png)









## 2. String类型

```java
public static void main(String[] args) throws IOException {
    
    String str = "default";
    str = "change";  // str = "change"
    
}
```

![1588152822055](C:\Users\Howl\AppData\Roaming\Typora\typora-user-images\1588152822055.png)









## 3. String类型变形

```java
public static void main(String[] args) throws IOException {
    String str = "default";
    change(str);  // str = "default"
}

public static void change(String s){
    s = "change";
}
```

![1588153102080](C:\Users\Howl\AppData\Roaming\Typora\typora-user-images\1588153102080.png)









## 4. 引用类型

改变u的指向不会影响user，但如果改变u指向实例的内容name，那么就会影响到user了

```java
public static void main(String[] args) throws IOException {
    User user = new User("default");
    change(user);  // user.name = "change"
}

public static void change(User u){
    u.name =  "change";
}
```

![1588153367160](C:\Users\Howl\AppData\Roaming\Typora\typora-user-images\1588153367160.png)