> 学了Lambda表达式之后感觉没什么用处对吧，其实不然，在流操作里面可以体验其简便性，因为平时用得少，很容易忘记





## 1. Stream

其在java.util.Stream包下， 提供对数据进行各种简便操作，效率当然比我们手写要好，并且支持并行操作，其使用过程分为三步：

* 创建Stream流
* 中间操作
* 最终操作



是不是有点奇怪？ 没关系，刚开始的时候笔者也这样觉得，后面就知道其必要性













## 2. 创建Stream流

还有个平行流这里不解释了（parallelStream），创建的创建流的方式：



#### Arrays.stream()

数组工具类有个静态方法，传入数组创建流

```java
int[] arr = {9,5,2,7};
Arrays.stream(arr);
```



#### Stream.of()

流里也有一个静态方法，传入一个泛型数组，或多个值创建流，记得导包

```java
Stream.of(1,2,3);
```



#### 集合中创建

我们最常用集合来创建Stream流，**注意不支持Map集合**

```java
new ArrayList( Arrays.asList("11111","AAAA","5") );
new HashSet().stream();
```













## 3. 中间操作

所谓的中间操作笔者是这样理解的，其方法返回值是this，没错就是返回Stream流本身，中间操作会返回一个新的流，然后再执行后面的中间操作，可以理解为过滤数据之后再进行过滤。而且这些操作是延迟执行的，即调用最终操作时才全部执行中间操作。



常见的中间操作，看名字就知道有什么作用了


- filter()
- map()
- distinct()     // 去重基于equals 和 hashCode的，具体为什么 [请看这里](<https://www.cnblogs.com/Howlet/p/12259639.html>)
- sorted()
- peek()
- limit()
- skip()



**实际操作**

```java
ArrayList<String> arrayList = new ArrayList( Arrays.asList("11111","AAAA","5","6","7222","CC","211","3","FFF","zzz") );


Stream stream = arrayList.stream()
                .sorted()
                .distinct()
                .limit(10)
                .skip(1)
                .filter(s-> s.length() > 1)
                .map( s -> s + " index")
                .map( s -> s.toLowerCase())
```



filter其方法的参数是一个Predicate函数式接口，其接口为`boolean test(T t);`

map方法的参数是Function函数式接口，方法为`R apply(T t)`，@return the function result













## 4. 最终操作



常见的最终操作

- forEach()

- toArray()

- min()

- max()

- count()

- anyMatch

- allMatch

- reduce



forEach方法的参数是一个Consumer函数式接口，接口为`void accept(T t)`，最给定的参数T进行操作，熟悉Lambda的可能会将方法引用放进去，下面二者是相等的

```java
stream.forEach( System.out::println );

stream.forEach( s -> System.out.println(s) );
```













## 5. 简便方法

笔者最近遇到的，立个flag，要成为Stream的忠实粉丝



#### 数组去重

```java
int[] arr = {1,2,3,4,5,1,1,3,10};
arr = Arrays.stream(arr).distinct().toArray();
		
for(int value : arr){
	System.out.println(value);
}
```

```
1
2
3
4
5
10
```



#### 求最值

```java
int a = Arrays.stream(arr).max().getAsInt();	// 返回Optinal类型	
System.out.println(a);
```

```
10
```



#### 匹配

```java
boolean rs = Arrays.stream(arr).anyMatch( t -> t == 10);  // 传入一个predicate
System.out.println(rs);
```



#### 聚合

```java
int sum = Arrays.stream(arr).reduce( (l,r) -> l+r ).getAsInt();
System.out.println(sum);  // 25，聚合实现总计功能，即用上一个值计算下一个值 
```



