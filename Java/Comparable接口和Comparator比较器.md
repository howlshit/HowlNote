### 1. Comparable接口

* 在java.lang包下，实现了Comparable函数式接口的对象可以自然排序，而数组和集合实现了该接口，所以我们会用Arrays.sort()或Collections.sort()来排序
* Comparable比较大于就返回1，小于返回-1，等于返回0

* 如果自定义的对象也要排序，就需要实现该接口并且手动重写里面的compareTo()方法

| 返回值 | 函数名         | 解释                                   |
| ------ | -------------- | -------------------------------------- |
| int    | compareTo(T o) | 将此对象与指定的对象进行比较以进行排序 |



**需要排序的自定义对象**

```java
public class User implements Comparable<User>{

	private int age;
	private String name;
	
	//省略各种Getters、Setters、toString、Constructor
    
	@Override
    //重写方法
	public int compareTo(User o) {
		//根据成绩年龄来排序
		if (this.age > o.age) return 1;
		if (this.age < o.age) return -1;
		return 0;
	}
}
```

**测试**

```java
public static void main(String[] args) {
	
	//创建泛型集合
	ArrayList<User> arrayList = new ArrayList<User>();
	
	//集合添加了四个奇怪名字的User
	arrayList.add(new User(100,"Howl"));
	arrayList.add(new User(1,"Howlet"));
	arrayList.add(new User(50,"晚上没宵夜"));
	arrayList.add(new User(7,"云吞面"));
	
	//compareTo方法测试
	System.out.println( "compareTo方法测试: " + new User(100,"Howl").compareTo(new User(1,"Howlet")) + "\n");
	
	//集合类排序
	Collections.sort(arrayList);
	
	//输出排序后集合
	Iterator iterator = arrayList.iterator();
	while(iterator.hasNext()){
		System.out.println(iterator.next());
	}
}
```

**输出**

```xml
compareTo方法测试: 1

User [age=1, name=Howlet]
User [age=7, name=云吞面]
User [age=50, name=晚上没宵夜]
User [age=100, name=Howl]
```









## 2. Comparator

* 在java.util包下，实现该接口的对象可以精确控制排序的顺序，还可以将该比较器传递给Collections.sort或Arrays.sort以实现控制顺序
* 实现该接口需要重写里面的compare()方法

| 返回值 | 函数名              | 解释                 |
| ------ | ------------------- | -------------------- |
| int    | compare(T o1, T o2) | 比较其两个参数的顺序 |



**需要排序的自定义对象**

```java
public class UserComparator implements Comparator<User>{

	@Override
	public int compare(User o1, User o2) {
		
		//逆序
		if (o1.getAge() > o2.getAge()) return -1;
		if (o1.getAge() < o2.getAge()) return 1;
		return 0;
	}
}
```

**测试（和上面的一样，只是下面的排序要添加比较器）**

```java
//集合类排序
Collections.sort(arrayList,new UserComparator());
```

**输出**

```xml
compareTo方法测试: -1

User [age=100, name=Howl]
User [age=50, name=晚上没宵夜]
User [age=7, name=云吞面]
User [age=1, name=Howlet]
```







### 3. 比较二者

* Comparable实现的是自然排序，是对象内部自己实现的
* Comparator实现定制排序，是对象之外实现的，借助了外力来推动比较
* 二者同时存在则使用Comparator排序















