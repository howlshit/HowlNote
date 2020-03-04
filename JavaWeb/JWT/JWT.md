> 以下内容 翻译、择抄、适当修改自 [JWT官网](https://jwt.io/)，当了一次大自然的搬运工





**打开官网你就会看到这么一个介绍：**

JSON Web Tokens are an open, industry standard RFC 7519 method for representing claims securely between two parties. JWT.IO allows you to decode, verify and generate JWT

**翻译过来就是：**

JWT是一个开源的、行业标准的RFC 7519方法，用于安全地声明双方。JWT.IO允许你解码，验证，生成JWT（JWT.IO是官网网页内嵌的一个JWT生成器）







# 1. 什么是 JSON Web Token(JWT)



JWT是一个开源标准（RFC 7519），它定义了一种紧凑且自包含的方式，用于在各方之间安全地传递信息（此信息是一个JSON对象）。此信息是经过数字签名的，因此可以被验证和信任。JWT可以使用密匙签名（兼用HMAC算法）或使用RSA或ECDSA的公用/专用密钥对来进行签名



尽管JWT可以进行加密以便在各方之间提供保密性，但是我们将重点关注已签名的令牌（指JWT）。已签名的令牌可以验证其中声明的完整性，而加密的令牌的这些声明则对其他各方隐藏。当使用公钥/私钥对来对令牌进行签名时，签名还证明只有持有私钥的一方才是对令牌进行签名的一方（即身份认证）









# 2. 我们什么时候应该使用JWT



* 授权：这是JWT的最常见用法。一旦用户登录，每个后续请求将包括JWT，从而允许用户访问该令牌允许的路由，服务和资源。单点登录是当今广泛使用的一项功能，因为它的开销很小并且轻松跨域



* 信息交换：JWT是在各方之间安全地传输信息的好方法。因为可以对JWT进行签名（例如，使用公钥/私钥对），所以您可以确定发件人是他们所说的人。此外，由于签名是使用头部和有效负载计算的，因此您还可以验证内容是否遭到篡改











# 3. JWT的结构



JWT以紧凑的形式由三部分组成，这些部分由点 `.` 分隔，分别是：

- Header
- Payload
- Signature

因此，JWT通常如下所示

`xxxxx.yyyyy.zzzzz`



让我们来分解不同的部分：





##  3.1 Header（头部）

头部通常由两部分组成：令牌的类型和所使用的签名算法（如HMAC SHA256或RSA）



例如：

```json
{
  "alg": "HS256",
  "typ": "JWT"
}
```

然后，上面的JSON被Base64Url编码以形成JWT的第一部分







## 3.2 Payload（有效负载）

令牌的第二部分是有效负载，其中包含声明，而声明是有关实体的（通常是用户）和其他数据的声明，声明有三种类型：注册的、公共的、私有的



* **注册声明**（建议但不强制使用）
  * iss: 签发者
  * exp: 到期时间
  * sub: 主题
  * aud: 受众群众
  * jti: 身份标识（用于回避重放攻击）
  * [others](https://tools.ietf.org/html/rfc7519#section-4.1)

>请注意，声明名称仅是三个字符，因为JWT是紧凑的



* **公开声明**（可以添加任何信息，不建议添加敏感信息）



* **私有声明**（为共享信息而创建的自定义声明）



有效负载的事例：

```json
{
  "sub": "1234567890",
  "name": "John Doe",
  "admin": true
}
```

然后，对有效负载进行Base64Url编码，以形成JSON Web令牌的第二部分



> 请注意，对于已签名的令牌，此信息尽管可以防止篡改，但任何人都可以读取。除非将其加密，否则请勿将机密信息放入JWT的有效负载或头部中







## 3.3 Signature（签名）

要创建签名部分，你必须获取`编码后的头部`，`编码后的有效负载`、`密匙`以及头部声明的`加密算法`，并对他们进行签名



例如：若要用HMAC SHA256算法，则将通过以下方式创建签名：

```java
HMACSHA256(
  base64UrlEncode(header) + "." +
  base64UrlEncode(payload),
  secret
)
```

签名用于验证消息在此过程中没有更改，并且对于使用私钥进行签名的令牌，它还可以验证JWT的发送者是它所说的真实身份





## 3.4 放在一起组成JWT

输出是三个由点分隔的Base64-URL字符串，可以在HTML和HTTP环境中轻松传递这些字符串，与基于XML的标准（例如SAML）相比，它更紧凑



下面显示了一个JWT，它已对先前的标头和有效负载进行了编码，并用一个秘密进行了签名

```json
base64UrlEncode(header) + . + base64UrlEncode(payload) + . + Signature
```

![1581327127112](C:\Users\Howl\AppData\Roaming\Typora\typora-user-images\1581327127112.png)



> 如果您想使用JWT并将这些概念付诸实践，则可以使用[jwt.io Debugger](http://jwt.io/)解码（官网的JWT编辑器），验证和生成JWT











# 4. JWT如何工作？

在身份验证中，当用户使用其凭据成功登录时，将返回 JWT。由于令牌是凭据，因此必须格外小心以防止安全问题。通常，令牌的保留时间不应超过要求的时间



由于缺乏安全性，你也不应该将敏感的会话数据存储在浏览器中



每当用户想要访问受保护的路由或资源时，用户代理通常应使用持有者模式，在HTTP请求头中设Authorization为JWT，请求头内容应如下所示：

```html
Authorization: Bearer <token>
```

在某些情况下，这可以是无状态授权机制。服务器的受保护路由将在Authorization标头中检查有效的JWT ，如果存在，则将允许用户访问受保护的资源。如果JWT包含必要的数据，则可以减少查询数据库中某些操作的需求（比如用户名），尽管这种情况并非总是如此



如果令牌是在Authorization请求头中发送的，则跨域资源共享（CORS）不会成为问题，因为它不使用cookie

可将JWT存于LocalStoage（个人补充）



>请注意，使用签名的令牌，令牌中包含的所有信息都会暴露给用户或其他方，即使他们无法更改它。这意味着您不应将机密信息放入令牌中













# 5. 为什么要使用JWT

由于JSON没有XML冗长，因此在编码时JSON也较小，从而使JWT比SAML更为紧凑。这使得JWT是在HTML和HTTP环境中传递的不错的选择



JSON解析器在大多数编程语言中都很常见，因为它们直接映射到对象。相反，XML没有自然的文档到对象映射。与SAML断言相比，这使使用JWT更加容易



关于用法，JWT是在Internet规模上使用的。这强调了在多个平台（尤其是移动平台）上对JSON Web令牌进行客户端处理的简便性



cookie+session这种模式通常是保存在服务器内存中，而且服务从单服务到多服务会面临的session共享问题，随着用户量的增多，开销就会越大。而JWT不是这样的，只需要服务端生成token，客户端保存这个token，每次请求携带这个token，服务端认证解析即可（个人补充）













# 6. 缺点（个人补充）

* **注销后JWT还有效**，由于JWT存放于客户端，用户点击注销后无法操作客户端的JWT，导致在JWT的过期时间前还是有效，笔者的解决方法是在服务器端建立一个黑名单，在用户点击注销后将该用户放入黑名单，下次进入先去查看黑名单中是否存在该用户，这又和JWT背道而驰，在服务器端存储数据
* **续签**，若每次发现快过了有效期，则服务器端生成一个新的JWT发送给客户端，客户端检查新旧JWT不一致则替换











# 7. 简单事例

笔者就使用JWT官网排名靠前的java-jwt来举例说明了，以为就一个包而没有使用maven和Springboot管理，一个个依赖独自去仓库下载，血的教训，那么列出所需的包

* java-jwt-3.9.0
* commons-codec-1.12
* jackson-databind-2.10.0.pr3
* jackson-annotations-2.10.0.pr3
* jackson-core-2.10.0.pr3





**定义JWT工具类**

```java
public class JWTUtil {
	
	// 密匙，本应从配置文件读取
	private static String secret = "1234567890";
	
	
	/**
	 * 创建一个JWT
	 * @param username
	 * @return token
	 */
	public static String createJWT(String username){
		
		// 过期多少秒
		long expSecond = 60 * 60;	// 一小时
		
		// 单纯为了使用1.8新时间API
		LocalDateTime nowLocalDateTime = LocalDateTime.now();
		LocalDateTime expLocalDateTime = nowLocalDateTime.plusSeconds(expSecond);
		
		// 时间戳
		Instant nowInstant = nowLocalDateTime.atZone(ZoneId.systemDefault()).toInstant();
		Instant expInstant = expLocalDateTime.atZone(ZoneId.systemDefault()).toInstant();
		
		// 转成jwt的date类型
		Date nowDate = Date.from(nowInstant);
		Date expDate = Date.from(expInstant);
		
		// 变量提升
		String token = null;
		try {
			token = JWT.create()
					.withIssuer("Howl")			// 签发者
					.withIssuedAt(nowDate)		// 签发时间
					.withExpiresAt(expDate)		// 到期时间
					.withClaim("username", username)	// 自定义声明
					.sign(Algorithm.HMAC256(secret));	// 签名
		} catch (JWTVerificationException  e) {
			System.out.println("Claim不能转成json或密匙无效将抛出JWTCreationException");
		}
		return token;
	}
	
	
	/**
	 * 验证token是否正确
	 * @param token
	 * @return T/F
	 */
	public static boolean verifyJWT(String token){
		
		JWTVerifier verifier = JWT.require(Algorithm.HMAC256(secret))
				.withIssuer("Howl")
				.build();		// 匹配指定的token发布者 Howl
		try {
			verifier.verify(token);		// 通过验证
		} catch (Exception e) {
			return false;				// 验证失败
		}
		return true;
	}
	
	
	/**
	 * 返回声明的集合，用来获取值
	 * @param token
	 * @return Claims集合
	 */
	public static Map getClaim(String token){
		
		// 解密
		JWTVerifier verifier = JWT.require(Algorithm.HMAC256(secret))
				.withIssuer("Howl")
				.build();
				
		// 解密后的JWT
		 DecodedJWT decodedJWT = verifier.verify(token);	
		
		return decodedJWT.getClaims();
	}
}
```

**简单使用**

```java
public static void main(String[] args) {
		
		// 根据传入的username生成token
		String token = JWTUtil.createJWT("Howl");
		
		// 验证token是否正确
		if(JWTUtil.verifyJWT(token)){
			System.out.println("验证正确");
		}else{
			System.out.println("验证失败");
		}
		
		// 获取声明的集合
		Map<String,Claim> map = JWTUtil.getClaim(token);
		
		// 获取声明
		System.out.println(map.get("username").asString());		// 获取用户名
		System.out.println(map.get("exp").asDate());			//获取过期时间
		
}
```

```
验证正确
Howl
Tue Feb 11 11:06:56 GMT+08:00 2020
```

















