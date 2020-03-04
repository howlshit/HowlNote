# 简单JSON



## JSON是什么



JavaScript Object Notation（JavaScript 对象表示），是一种存储和交换文本信息的语法，它独立程序语言，是轻量级的文本数据交换格式，比XML更小、更快，更易解析，JS原生支持JSON解析





## 语法



包括了**{对象}**和**[数组]**，二者可以互相嵌套

* 字符串用 “ ” 包裹，其余就不用（数字，布尔值，null）

* 对象类似于键值对,键不可以重复，对象用 { } 包裹，键值之间用 : 间隔，并且各值之间用 , 隔开

* 数组废话类似于数组了，值之间用 ，间隔

```json
{
	"name": "Howl",
	"nickname": "Howlet"
}

["name", "Howl", "Howlshit"]

{
	"name": "Howl",
	"nickname": "Howlet",
	"array": ["firstname", "lastname"],
	"complex": [{
		"java": "good"
	}, {
		"php": "bad"
	}]
}
```





## 客户端方法



* JSON.parse(String str)  把服务器传过来的JSON字符串转化成JavaScript对象
* JSON.stringify(Object oj)  把JavaScript对象转换成JSON字符串







## JavaBean转化JSON



Java中并没有内置JSON的解析类所以要借助第三方类库，这里使用阿里巴巴的**FastJson**

FastJson方法

```java
JSON.toJSONString(Object)						//JavaBean转化成JSON
JSON.parseObject(jsonObject,Object.class)		//JSON转化成JavaBean
```





JavaBean

```java
public class User {
	
	private int id;
	private String email;
	private String password;
	
	public User(int id, String email, String password) {
		super();
		this.id = id;
		this.email = email;
		this.password = password;
	}

	//FastJson操作基于Getters和Setters，不能不写，这里为了省地方去掉了
}
```

main

```java
public static void main(String[] args) {
	
	//创建JavaBean对象
	User user1 = new User(20, "1210911104@qq.com", "Howl");
	
	//JavaBean对象转成json对象
	String jsonObject = JSON.toJSONString(user1);
	System.out.println("JavaBean对象转成json对象"  + "-------------" + jsonObject + "\n");
	
	//json对象转成JavaBean对象
	User user2 = JSON.parseObject(jsonObject, User.class);
	System.out.println("json对象转成JavaBean对象"  + "-------------" + user2.getId() + "--" + user2.getEmail() + "--" + user2.getPassword());
}
```

输出

```xml
JavaBean对象转成json对象-------------{"email":"1210911104@qq.com","id":20,"password":"Howl"}

json对象转成JavaBean对象-------------20--1210911104@qq.com--Howl
```

