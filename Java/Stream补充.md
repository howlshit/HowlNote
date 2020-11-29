## 1. 常见的接口

我们常用Lambda来表达这些函数式接口，所以看着比较陌生，其实日常都有使用到。下面说明时会先给出源码，然后再给出使用事例



### 1.1 Consumer

传入参数，内部进行操作，没有返回值

```java
@FunctionalInterface
public interface Consumer<T> {

    /**
     * Performs this operation on the given argument.
     *
     * @param t the input argument
     */
    void accept(T t);
}
```

```java
list.stream().forEach(s -> System.out.println(s));	// forEach(Consumer<? super T> action)
```



### 1.2 Function

传入参数，内部进行转换，有返回值

```java
@FunctionalInterface
public interface Function<T, R> {

    /**
     * Applies this function to the given argument.
     *
     * @param t the function argument
     * @return the function result
     */
    R apply(T t);
}
```

```java
list.stream().map(s -> s + "-");	// Stream<R> map(Function<? super T, ? extends R> mapper);
```



### 1.3 Supplier

创建一个容器并且返回出去

```java
@FunctionalInterface
public interface Supplier<T> {

    /**
     * Gets a result.
     *
     * @return a result
     */
    T get();
}
```

```java
Collector toMap(Function keyMapper
			   ,Function valueMapper
			   ,BinaryOperator mergeFunction
			   ,Supplier<M> mapSupplier)
```



### 1.4 Predicate 

传入参数，进行判断，返回boolean

```java
@FunctionalInterface
public interface Predicate<T> {

    /**
     * Evaluates this predicate on the given argument.
     *
     * @param t the input argument
     * @return {@code true} if the input argument matches the predicate,
     * otherwise {@code false}
     */
    boolean test(T t);
}
```

```java
list.stream().anyMatch(s -> s >= 10);	// anyMatch(Predicate<? super T> predicate)
```









## 2. 集合的操作

后期才知道流可以转变成的神奇之处，前来学习



先来看流的收集方法： collect(Collector<? super T, A, R> collector) ，其主要将流中元素收集成另外一个数据结构（如：集合，String，整数等），而参数是一个Collector实例（后面会说明）





### 2.1 Collectors

Collectors是一个工具类，其常用的方法有：



- toList()，返回一个Collector实例，这就是上面所说的Collector实例
- toSet()，返回一个Collector实例
- toMap()，返回一个Collector实例
- joining()
- collectingAndThen()
- counting()





### 2.2 转成集合

流转成集合十分的简单，往 collect() 方法里面传入Collector实例即可（Collectors工具类生成的Collector实例）



```java
List list = Arrays.stream(array).collect(Collectors.toList());
Set  set  = Arrays.stream(array).collect(Collectors.toSet());
Map  map  = Arrays.stream(array).collect(Collectors.toMap());
```



#### 2.2.1 toMap()规约

阿里巴巴Java开发手册规约提到：

```
【强制】在使用 java.util.stream.Collectors 类的 toMap() 方法转为 Map 集合时，一定要使用含有参数类型为BinaryOperator，参数名为 mergeFunction 的方法，否则当出现相同 key 值时会抛出 IllegalStateException 异常
```



**使用toMap()方法转换成集合时，一般会遇到两个问题：**

* Key重复问题
* Value空指针异常





**toMap的参数：**

```java
public static Collector toMap(Function keyMapper
							 ,Function valueMapper
							 ,BinaryOperator mergeFunctio
							 ,Supplier mapSupplier) {
							 
	BiConsumer accumulator = 
	(map, element) -> map.merge(keyMapper.apply(element)
							   ,valueMapper.apply(element)
							   ,mergeFunction);
							   
    return new CollectorImpl (mapSupplier, accumulator, mapMerger(mergeFunction), CH_ID);
}
```

* 前两个是Function，传参内部操作并返回的，正常都是getKey()，getValue()

* 第三个是BiFunction实现类（类似于Function），但接收两参数返回一个值，进行合并操作的

* 第四个是Supplier，是提供的容器，默认是HashMap



为了避免上述的两个问题，我们可以进行如下操作：

* Key重复一般会使用后者覆盖策略

```java
ArrayList<User> list = new ArrayList();

list.add(new User("张三", 30));
list.add(new User("张三", 80));
list.add(new User("李四", 40));
list.add(new User("王五", 50));

Map map = list.stream().collect(Collectors.toMap(User::getName,User::getAge,(v1, v2) -> v2));

map.forEach((k,v) -> System.out.println(k + "---" + v));

----------------------------------------------------------------------

李四---40
张三---80
王五---50
```



* 而Value的NPE问题是因为底层使用了 java.util.HashMap，其 merge 方法里会进行如下的判断：

```java
if (value == null || remappingFunction == null)	throw new NullPointerException();
```









## 3. Reduce约简操作

以前约简不会用，现在接触才发现这就是迭代的形式啊，这次的输出值作为下次的输入值



```java
int[] nums = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10};

// 传入前两个参数累加，返回值作为下次的第一个参数，下次往后移动一格
System.out.println(
        Arrays.stream(nums).reduce((left, right) -> left += right).getAsInt()
);

// 有个初始值，结果不为空就不用Optional类包装了，不会NPE
System.out.println(
        Arrays.stream(nums).reduce(100,(left, right) -> left += right)
);
```

