> 怒写是因为使用过程中遇到了小插曲，愣是没找到原因十分无奈，在放弃之际看到了SpringMVC的类型转换器才豁然开朗







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



Spring也提供了参数校验的方式，即实现validator接口来进行参数校验，其内部有两个方法







​       JSR-303的校验是基于注解的，它内部已经定义好了一系列的限制注解，我们只需要把这些注解标记在需要验证的实体类的属性上或是其对应的get方法上。来看以下一个需要验证的实体类User的代码：





我们可以看到我们在username、password和age对应的get方法上都加上了一个注解，这些注解就是JSR-303里面定义的限制，其中@NotBlank是Hibernate Validator的扩展。不难发现，使用JSR-303来进行校验比使用Spring提供的Validator接口要简单的多。我们知道注解只是起到一个标记性的作用，它是不会直接影响到代码的运行的，它需要被某些类识别到才能起到限制作用。使用SpringMVC的时候我们只需要把JSR-303的实现者对应的jar包放到classpath中，然后在SpringMVC的配置文件中引入MVC Namespace，并加上<mvn:annotation-driven/>就可以非常方便的使用JSR-303来进行实体对象的验证。加上了<mvn:annotation-driven/>之后Spring会自动检测classpath下的JSR-303提供者并自动启用对JSR-303的支持，把对应的校验错误信息放到Spring的Errors对象中。这时候SpringMVC的配置文件如下所示：





上面代码可以看到@Validated User user, BindingResult br这两个参数,@Validated表明参数user是要校验的类,BindingResult是存储错误信息的类,两者必须一一对应,并且位置挨着,不能中间有其他参数。**而且这个参数是必须紧挨着@Valid参数的，即必须紧挨着需要校验的参数，这就意味着我们有多少个@Valid参数就需要有多少个对应的Errors参数，它们是一一对应的**。



*****若使用了@Validated/@Valid注解开启校验，但DTO后面没有紧跟BindingResult对象，那么当参数不符合时，将直接返回400 Bad Request状态码。 







限制

说明

@Null

限制只能为null

@NotNull

限制必须不为null

@AssertFalse

限制必须为false

@AssertTrue

限制必须为true

@DecimalMax(value)

限制必须为一个不大于指定值的数字

@DecimalMin(value)

限制必须为一个不小于指定值的数字

@Digits(integer,fraction)

限制必须为一个小数，且整数部分的位数不能超过integer，小数部分的位数不能超过fraction

@Future

限制必须是一个将来的日期

@Max(value)

限制必须为一个不大于指定值的数字

@Min(value)

限制必须为一个不小于指定值的数字

@Past

限制必须是一个过去的日期

@Pattern(value)

限制必须符合指定的正则表达式

@Size(max,min)

限制字符长度必须在min到max之间

@Past

验证注解的元素值（日期类型）比当前时间早

@NotEmpty

验证注解的元素值不为null且不为空（字符串长度不为0、集合大小不为0）

@NotBlank

验证注解的元素值不为空（不为null、去除首位空格后长度为0），不同于@NotEmpty，@NotBlank只应用于字符串且在比较时会去除字符串的空格

@Email

验证注解的元素值是Email，也可以通过正则表达式和flag指定自定义的email格式
————————————————
版权声明：本文为CSDN博主「小辉同学」的原创文章，遵循 CC 4.0 BY-SA 版权协议，转载请附上原文出处链接及本声明。
原文链接：https://blog.csdn.net/yangqh521/article/details/81906944







```
BindingResult 是package org.springframework.validation;的错误参数对象
```