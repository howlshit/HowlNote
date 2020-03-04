**预备知识**

* 邮箱服务器，提供邮件服务
* STMP协议（Simple Mail Transfer Protocol），用于从源地址到目的地址传输邮件的规范，默认25端口
* POP3协议（Post Office Protocol 3），接收电子邮件，远程管理邮件的协议，默认110端口
* 发送邮件的过程

![1578995948905](C:\Users\Howl\AppData\Roaming\Typora\typora-user-images\1578995948905.png)





















**导包**

这里使用maven

```xml
<!-- https://mvnrepository.com/artifact/com.sun.mail/javax.mail -->
<dependency>
    <groupId>com.sun.mail</groupId>
    <artifactId>javax.mail</artifactId>
    <version>1.6.2</version>
</dependency>
```





**编写工具类**

```java
/*
 * @author Howl
 * @date 2020/1/6
 * @version 1.0
 * @description 邮件工具类
 */
public class MailUtils {
	
	//变量提升
	private static Session session;
	
	//静态代码块
	static{
		/*
		 *  Properties配置文件的读取类
		 *  host，SMTP主机名
		 *  port，端口号
		 *  auth，用户认证
		 *  class，规定要使用SSL加密套接字
		 *  
		 *  Authenticator认证器
		 *  授权邮箱，授权码（文末有解释）
		 *  应用程序创建会话时注册该子类的实例并调用getPasswordAuthentication，返回一个PasswordAuthentication
		 *  所以重写getPasswordAuthentication返回一个该类的对象，该类构造方法PasswordAuthentication(String userName, char[] password)
		 *  
		 *  session邮件会话
		 *  收集邮件API使用的属性和默认值，单个默认会话可以由桌面上的多个应用程序共享，也可以创建未共享的会话
		 */
		
		Properties props = new Properties();
		props.put("mail.smtp.host", "smtp.qq.com"); 
		props.put("mail.smtp.port", "465"); 		
		props.put("mail.smtp.auth", "true"); 		
		props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
		
		Authenticator authenticator = new Authenticator() {
            public PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication("授权邮箱","授权码");
            }
        };
        
        session = Session.getInstance(props, authenticator);
        
	}
	
	/*
	 * @param mailAddress,收件人
	 * @param subject,标题
	 * @param text,内容
	 * 
	 */
	public static void sendMail(String mailAddress,String subject,String text) throws AddressException, MessagingException{
		/*
		 * message邮件载体（需要运行时环境）
		 * setFrom，发件人（参数是InternetAddress，属于Java的网络编程）
		 * setRecipient，收件人（第一个TO--发送,后面是收件人）
		 * setSubject，设置标题
		 * send，静态发送邮件
		 */
		MimeMessage message = new MimeMessage(session);
        message.setFrom(new InternetAddress("1111111111@qq.com"));
        message.setRecipient(Message.RecipientType.TO, new InternetAddress(mailAddress));
        message.setSubject(subject);
        message.setText(text, "utf-8");
        Transport.send(message);
		
	}

}
```





**发送**

```java
public class MailTest {
	
	public static void main(String[] args) throws AddressException, MessagingException {
		
		MailUtils.sendMail("1111111111@qq.com", "9527", "去吉野家吗？");
		
	}
}
```





**收件**

![1578291653550](C:\Users\Howl\AppData\Roaming\Typora\typora-user-images\1578291653550.png)



**授权邮箱，授权码**

以QQ邮箱为例，设置-->账号 找到并开启对应服务

授权邮箱就是QQ邮箱，授权码在开启服务后提示

![1578291995564](C:\Users\Howl\AppData\Roaming\Typora\typora-user-images\1578291995564.png)









****



[JavaxMail官方文档](<https://docs.oracle.com/javaee/6/api/javax/mail/package-frame.html>)