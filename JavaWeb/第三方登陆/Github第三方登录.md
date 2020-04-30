> 笔者第一次写网站只写了接收参数以及登录页面就兴奋了一整天，还特意地加上了第三方登录，想起当时的情景还历历在目。之前是照着被人的博客一步步完成第三方登录的功能，现在就要自己来理解完成了





## 1. OAuth 2.0

第三方登录需要用到OAuth 2.0的原理，那么我们得先了解其原理，然后再讲解第三方登录就会简单很多，后面会有具体实例与代码



OAuth 2.0是一种规范的授权机制，主要用来颁发令牌的，根据其规范可分为两个角色：客户端与资源所有者，资源所有者同意客户端访问后就会向其颁发令牌，客户端携带令牌去请求客户的数据。总结一句：资源所有者向第三方应用颁发令牌。



OAuth 2.0 规定了四种获得令牌的流程

* 授权码（authorization-code）
* 隐藏式（implicit）
* 密码式（password）
* 客户端凭证（client credentials）

**下面主要讲解广泛使用的 `授权码（authorization-code）` 方式**





#### 流程：

步骤一：第三方网站A 提供其他登录方式B 的链接，用户点击后会跳转到网站B。用户在跳转的网站B 登录后，会携带上授权码（code）跳回网站A



步骤二：网站A 拿到授权码（code）后，会在后端携带网站注册信息以及上面获取的授权码（code）向网站B 请求令牌（Token）



步骤三：网站B 收到令牌的请求并验证通过后，会向网站A 发送令牌（Token）



步骤四：网站A 获取到令牌（Token）后，就可以携带上令牌（Token）向网站B 请求用户数据了









## 2.前提准备

Github操作比较容易，不像微信QQ等流程较为麻烦，下面具体内容参考 [Github Apps](<https://developer.github.com/apps/>)



* Github账号
* 公网IP或域名
* 登录页面









## 3. 创建Github应用

![1588237249843](C:\Users\Howl\AppData\Roaming\Typora\typora-user-images\1588237249843.png)



其中授权回调地址的例子：http://localhost:8080/oauth/redirect （localhost要填写公网IP或域名），注册应用之后Github会给你 `Client ID` 和 `Client Secret`，记住他俩后面会用到









## 4. 请求用户的GitHub身份

第三方网站A 提供跳转的链接（当然可以设置成图标形式，下图是笔者第一次写的网站）

![1588239674758](C:\Users\Howl\AppData\Roaming\Typora\typora-user-images\1588239674758.png)




```
GET https://github.com/login/oauth/authorize?client_id=XXXXXXXXXXX
```

该地址带上了参数 `client_id` 就是步骤2.0中让你记住的Id



**代码例子**

```html
<a href="https://github.com/login/oauth/authorize?client_id=XXXXXXXX">Github第三方登录</a>
```









## 5. GitHub将用户重定向回您的站点

用户登录后Github将重定向回步骤3 填的回调地址，并带上了10分钟有效期的临时授权码（code），该授权码的接收参数为code。第三方网站获取到授权码后带上网站注册信息和授权码去交换令牌（Token）



重定向的地址例子，从中可获取code

```
http://127.0.0.1:8080/oauth/redirect?code=7647753523ada28dfabc
```



**代码例子**

```java
@RestController
@RequestMapping("/oauth")
public class OAuthController {

    @GetMapping("/redirect")
    public String redirect(String code){
        // 这样就获取到code了
        System.out.println(code);
    }
}
```



****



然后我们就携带下面的三个参数去POST请求下面的地址

```
POST https://github.com/login/oauth/access_token
```

|     名称      |  类型  |                 描述                 |
| :-----------: | :----: | :----------------------------------: |
|   client_id   | string | 您从GitHub收到的GitHub App的客户端ID |
| client_secret | string | 您从GitHub收到的GitHub App的客户密码 |
|     code      | string |      您上面收到的授权码（code）      |



**代码例子**

笔者使用SpringBoot自带的RestTemplate模板去使用Http

```java
// 请求地址
String url = "https://github.com/login/oauth/access_token";

// POST参数必须用这个
MultiValueMap<String,String> paramMap = new LinkedMultiValueMap();
paramMap.add("client_id","XXXXXXXXXXXXXXX");
paramMap.add("client_secret","XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX");
paramMap.add("code",code);

// 发送请求
RestTemplate restTemplate = new RestTemplate();
String content = restTemplate.postForObject(url,paramMap,String.class);
```









## 6. POST请求的默认响应

步骤5中我们POST请求了相应的地址，那么就会返回一个响应过来，默认响应如下：



```
access_token=e72e16c7e42f292c6912e7710c838347ae178b4a&token_type=bearer
```



**代码例子**

```java
// 获取access_token
int start = content.indexOf("=");
int end = content.indexOf("&");
String access_token = content.substring(start+1,end);
```

从上面的代码我们就正式拿到了令牌（Token）了，上面的access_token就是令牌









## 7. 获取用户信息

拿到令牌后就可以访问下面的地址来获取用户信息了



```
GET https://api.github.com/user?access_token=access_token
```



**代码例子**

```java
// 获取用户信息
String getUrl = "https://api.github.com/user?access_token=" + access_token;
String user = restTemplate.getForObject(getUrl,String.class);
```



响应的用户信息

```json
{
	"login": "Howl",
	"id": XXXXXX,
	"node_id": "XXXXX",
	"created_at": "2018-08-25T02:27:54Z",
	"updated_at": "2020-04-30T05:30:25Z"
    "还有挺多信息的" : "后面的省略了"
}
```









## 8. 完整代码

```java
@RestController
@RequestMapping("/oauth")
public class OAuthController {

    @GetMapping("/redirect")
    public String redirect(String code){

        // 请求地址
        String url = "https://github.com/login/oauth/access_token";

        // POST参数必须用这个
        MultiValueMap<String,String> paramMap = new LinkedMultiValueMap();
        paramMap.add("client_id","XXXXXXXXXXXX");
        paramMap.add("client_secret","XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX");
        paramMap.add("code",code);

        // 发送请求
        RestTemplate restTemplate = new RestTemplate();
        String content = restTemplate.postForObject(url,paramMap,String.class);

        // 获取access_token
        int start = content.indexOf("=");
        int end = content.indexOf("&");
        String access_token = content.substring(start+1,end);

        // 获取用户信息
        String getUrl = "https://api.github.com/user?access_token=" + access_token;
        String user = restTemplate.getForObject(getUrl,String.class);

        return user;
    }
}
```









****

参考

<https://developer.github.com/>

<http://www.ruanyifeng.com/blog/2019/04/oauth_design.html>

