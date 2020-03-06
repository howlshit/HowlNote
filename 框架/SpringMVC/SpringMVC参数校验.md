> SpringMVC是根据参数的名字，然后用setter方法来对数据进行绑定的，若类型没有匹配上则会出现400的错误，同时还要注意空值问题





## 1. 参数校验

我们在做Web层的时候，接收了各种参数，尽管前端已经做了验证，但难免恶意传参，所以要对传过来的数据保持不信任的态度来进行参数校验



笔者日常进行验证的方式如下：

```java
@RequestMapping(value = "/create", method = RequestMethod.POST)
public String createUser(String name, String email) {
	 
	if(name == null || name.isEmpty()){
		return "名字不能为空";
	}
	if(email == null || email.isEmpty()){
        
        // 这里还要加上邮箱格式的验证，省略省略
        
		return "邮箱不能为空";
	}  
}
```

乍一看好像没什么问题，能够应付需求，但是一旦参数多了起来就会像下面那样



```java
@RequestMapping(value = "/create", method = RequestMethod.POST)
public String createUser(String name, String email, String sex, String password, String nickName, String address) {
	 
	if(name == null || name.isEmpty()){
		return "名字不能为空";
	}
	if(email == null || email.isEmpty()){
		return "邮箱不能为空";
	}  
    if(sex == null || sex.isEmpty()){
		return "性别不能为空";
	}
	if(password == null || password.isEmpty()){
		return "密码不能为空";
	}
    if(address == null || address.isEmpty()){
		return "地址不能为空";
	}  
}
```

这里看还挺整齐的，一目了然，其实除了非空判断还需各种格式验证没有列出了，**如果再添加参数**就成了累赘，一个类中参数校验的代码就占了大部分，得不偿失



**这时候就该考虑简便的参数校验方式了——JSR-303（基于注解）**













## 2. JSR-303

JSR-303是一个被提出来的数据验证**规范**，所以这仅仅是个接口，没有具体实现的功能，**容易被误解为JSR-303就是用于数据验证的的工具**。我们要用到JSR-303的规范，那么就需要导入实现类的jar包，比如Hibernate Validator也是我们后面使用的jar包。



Spring也提供了参数校验的方式，即实现其内部的validator接口来进行参数校验，接口有两个方法：

```java
public class UserValidator implements Validator {

    // 判断是否支持验证该类
    public boolean supports(Class clazz) {
        return User.class.equals(clazz);
    }

    // 校验数据，将报错信息放入Error对象中
    public void validate(Object obj, Errors e) {
        // ValidationUtils的静态方法rejectIfEmpty()，对属性进行非空判断
        ValidationUtils.rejectIfEmpty(e,"name","name.empty");
        User user = (User)obj;
        if(user.getAge() < 0){
            e.rejectValue("age", "年龄不能为负数");
        }
    }
}
```



我们当然不满足那么麻烦的方法，所以JSR-303出场



 JSR-303是基于注解校验的，注解已经实现了各种限制，我们可以将注解标记在需要校验的类的属性上，或是对应的setter方法上（笔者习惯标记在属性上）



导入Hibernate Validator依赖jar包，笔者使用maven工程

```xml
<!--    参数校验    -->
<dependency>
    <groupId>org.hibernate.validator</groupId>
    <artifactId>hibernate-validator</artifactId>
    <version>6.1.2.Final</version>
</dependency>
```



hibernate-validator实现了JSR-303的所有功能，额外还提供了一些实用的注解。我们可以将其分成两部分，一个是JSR-303规范中包含的，另一部分是hibernate额外提供的。下面的注解看解释就能明白是什么功能了



JSR-303规范

| **Annotation**                | **Description**                                          |
| :---------------------------- | :------------------------------------------------------- |
| `@Null`                       | 被注释的元素必须为 null                                  |
| `@NotNull`                    | 被注释的元素必须不为 null                                |
| `@AssertTrue`                 | 被注释的元素必须为 true                                  |
| `@AssertFalse`                | 被注释的元素必须为 false                                 |
| `@Min(value)`                 | 被注释的元素必须是一个数字，其值必须大于等于指定的最小值 |
| `@Max(value)`                 | 被注释的元素必须是一个数字，其值必须小于等于指定的最大值 |
| `@DecimalMin(value)`          | 被注释的元素必须是一个数字，其值必须大于等于指定的最小值 |
| `@DecimalMax(value)`          | 被注释的元素必须是一个数字，其值必须小于等于指定的最大值 |
| `@Size(max, min)`             | 被注释的元素的大小必须在指定的范围内                     |
| `@Digits (integer, fraction)` | 被注释的元素必须是一个数字，其值必须在可接受的范围内     |
| `@Past`                       | 被注释的元素必须是一个过去的日期                         |
| `@Future`                     | 被注释的元素必须是一个将来的日期                         |
| `@Pattern(value)`             | 被注释的元素必须符合指定的正则表达式                     |



hibernate额外提供的

| **Constraint** | **详细信息**                           |
| :------------- | :------------------------------------- |
| `@Email`       | 被注释的元素必须是电子邮箱地址         |
| `@Length`      | 被注释的字符串的大小必须在指定的范围内 |
| `@NotEmpty`    | 被注释的字符串的必须非空               |
| `@Range`       | 被注释的元素必须在合适的范围内         |

​      











## 3. JSR-303的简单使用



### 3.1 在需要校验的属性上标记注解

注解有个属性message存放自定义的错误信息



```java
public class User {

    @NotNull(message = "名字不能为空")
    private String name;

    @Email(message = "邮箱格式错误")
    private String email;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    // 各种getter / setter / 构造器
}
```





### 3.2 开启校验

在Controller方法入参中需要校验的参数前加入@Validated()表明需要校验，后方要加@BindingResult接收错误信息，若没加即接收不了错误信息会报错（若使用了全局异常处理则可以不加）。@Validated()和@BindingResult二者一前一后紧密相连的，中间不能有任何数值相隔。



```java
@RequestMapping(value = "/create", method = RequestMethod.POST)
public String createUser(@Validated() User user, BindingResult bindingResult) {

    // 判断是否有错
    if (bindingResult.hasErrors()) {
        // 获取字段上的错误
        FieldError errors = bindingResult.getFieldError();
        // 输出message信息
        return (errors.getDefaultMessage() + "\n");
    }
    // dosomething
}
```





### 3.3 补充

按上面的方法日常使用应该没什么问题了，数据校验中还有**分组**与**自定义校验**的知识点，这里笔者就不做 (tou) 说明 (lan) 了













## 4. 笔者遇到的小插曲

我们知道前端传参过来都是字符串，经过Spring的类型转换器转换成为我们需要的类型才能正常使用，之前笔者没有使用JSR-303规范来校验参数的时候莫得发觉问题，但这也为现在埋下了坑





如果传个整型呢？

```java
public class User {

    @Min(value = 0, message = "不能为负数")
    private int id;
    
    // 各种getter / setter / 构造器
}
```

```java
@RequestMapping(value = "/list", method = RequestMethod.GET)
public String listByPage(@Validated() User user, BindingResult bindingResult) {

    if (bindingResult.hasErrors()) {
        FieldError errors = bindingResult.getFieldError();
        return （errors.getDefaultMessage() + "\n");
     }
    
    // dosomething
}
```



乍一看没有什么问题，普通使用能过去。**但是但是但是 int id 传了空值就会报错：**

```
Failed to convert property value of type 'java.lang.String' to required type 'int' for property 'id'; nested exception is java.lang.NumberFormatException: For input string: ""

// 翻译:转换String到int id失败，报错原因是数字格式化异常，因为输入了字符串 “”
```

这里就是那个小小小的插曲，开始真是不知如何解决







### 解决方法

使用包装类Integer，类型对不上就不匹配了，包装类还会自动装箱和拆箱，所以很方便解决空值问题

```java
// Integer id

// 替换成包装类之后传的参数为，空值不接收即为null
User{id=null, name='jiafu liu', email='1210911104@qq.com'}
```



### 教训是：对于可能会传空值的属性一般会用包装类型













 
