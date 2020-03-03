### 1. 来源

Object类中定义了`equal`和`hashCode`方法，又因为Object是基类，所以继承了Object的类都有这两个方法



先来看看Object类中的equal方法

```java
* @param   obj   the reference object with which to compare.
* @return  {@code true} if this object is the same as the obj
*          argument; {@code false} otherwise.
* @see     #hashCode()
* @see     java.util.HashMap
*/
public boolean equals(Object obj) {
	return (this == obj);
}
```

从中可以看出，Object类中的equal方法是用 `==`来比较的，即二者地址是否相同，这样比较即判断二者是否同一对象



### 2. 需求

如果比较对象的内部是否相等，而不是比较是否同一对象，该怎么办？有没有想法？其实我们日常也经常使用这种比较，只是没有注意到而已，没错那就是字符串，`String.equals( )`，虽然不是同一对象，但只要内容相同，就返回true，即：`"123".equals("123")  == true`，那么来看看String内部是如何实现这种功能的



String内部的equals方法

```java
public boolean equals(Object anObject) {
    if (this == anObject) {		// 首先比较地址，如果地址相同，那肯定是同一对象同内容
        return true;
    }
    if (anObject instanceof String) {	// 再判断类型，只有类型相同才能比较内部的值
        String anotherString = (String)anObject;	// 向下转型
        int n = value.length;
        if (n == anotherString.value.length) {	// 比较二者字符串长度，长度不同，内容肯定不同
            char v1[] = value;		// 这里也可以看出字符串底层是数组实现
            char v2[] = anotherString.value;
            int i = 0;
            while (n-- != 0) {		// 逐一比较二者底层数组的每一个字符
                if (v1[i] != v2[i])
                    return false;
                i++;
            }
            return true;
        }
    }
    return false;
}
```





### 3. 重写equals方法

自定义的类该怎么实现equal方法呢？这里得遵循如下规则

* 两对象若equals相同，则hashCode方法返回值也得相同
* 两个对象的hashCode返回值相同二者equals不一定相同

从该规则可以知道，重写equals必须重写hashCode方法，因为hashCode是对堆内存的对象产生的特殊值，如果没有重写，不同对象产生的哈希值基本是不同的（哈希碰撞），集合中判断对象是否相同也是先判断哈希值再判断equals，Object的hashCode是native方法，所以不放出源码了，下面直接挂出重写equal的代码（仿照String）



重写自定义类的equals方法

```java
public class User {
	
	private String name;

	public User(String name) {
		super();
		this.name = name;
	}
	
	// 重写equal方法
	@Override
	public boolean equals(Object obj) {
        if(this == obj){
        	return true;
        }
        if(obj == null || obj.getClass() != this.getClass()){
        	return false;
        }
        User user = (User)obj;
    	return this.name.equals(user.name);
    }
	
    // 重写hashCode方法
	@Override
	public int hashCode(){
		return this.name.hashCode();
	}
	
	public static void main(String[] args) {
		
		User user1 = new User("Howl");
		User user2 = new User("Howl");
		
		System.out.println(user1.equals(user2));
	}
}
```

```
true
```





### 4. 验证集合是否先判断hashCode

当然要使用唯一的集合，这里举例hashSet，还是使用上面的代码



```java
public static void main(String[] args) {
		
    User user1 = new User("Howl");
    User user2 = new User("Howl");

    HashSet<User> hashSet = new HashSet<User>();

    hashSet.add(user1);
    hashSet.add(user2);

    Iterator iterator = hashSet.iterator();
    while(iterator.hasNext()){
        System.out.println(iterator.next());
    }

    System.out.println(user1.hashCode());
    System.out.println(user2.hashCode());
}
```

```
User [name=Howl]
2255420
2255420
```

**发现集合只有一个对象，即判断user1，user2为同一对象了**





举个反例，即注释掉User类重写的hashCode，再重新运行上面的代码，输出如下

```java
User [name=Howl]
User [name=Howl]
366712642
1829164700
```

**这样就可以验证集合确实是对hashCode来判断二者是否相等的，所以这里得十分十分十分注意，以后使用集合存储对象，如果要判断是否相等，考虑重写equal和hashCode方法**