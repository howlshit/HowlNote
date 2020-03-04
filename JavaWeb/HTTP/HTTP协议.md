## 1.  准备知识

**TCP/IP协议**（Transmission Control Protocol / Internet Protocol）是计算机通讯必须遵守的规则，是不同的通信协议的大集合，其里面就包括了HTTP，TCP，IP，SMTP等协议



**TCP/IP协议这个大集合分为4层结构**

![1576213803287](C:\Users\Howl\AppData\Roaming\Typora\typora-user-images\1576213803287.png)



**其中TCP协议是传输层协议**

就是所谓的三次握手协议，确保数据可靠安全



**IP协议在网络层，它也是一种协议**

负责把数据包转送到目的地



**TCP/IP协议是基于TCP 和 IP 这两个协议共同工作的大协议**

TCP和IP协同负责建立连接和转送数据的



**HTTP是在应用层**

浏览器通过HTTP协议向服务器传输信息，其上层协议是TCP\IP









## 2. HTTP协议

Http（htyper text transform protocal）超文本传输协议，即规定如何在互联网上传输超文本（HTML）的协议



### 2.1 分类

HTTP分为 **请求** 和 **响应** 部分，二者十分相似。其中请求部分负责向服务器发送信息，而响应部分负责接收服务器传送过来信息





### 2.2 HTTP结构

根据分类其结构有

* 请求部分：**请求行**、**请求头（包括空行）**、**请求体**
* 响应部分：**响应行**、**响应头（包括空行）**、**响应体**



#### 2.2.1 请求部分

请求头

```html
<!--  空格隔开的三个部分分别表示请求方式、请求资源、请求协议和版本号  -->

POST /XXX/XXX/XXX.html HTTP/1.1
```

请求头

```html
<!--  提供客户端信息、及本次请求的描述  -->

HOST:请求的主机地址 www.baidu.com
User-Agent:请求代替 Mozilla/5.0……
Accept-Language: 客户端可识别的语言种类 zh-CN,zh-TW
Accept-Encoding: 客户端接受的压缩格式 gzip, deflate, br
```

空行

```html

```

请求体

```html
<!--  约定客户端表单数据的提交格式  -->

<!--  GET请求方式的请求体为空，只在请求行的请求地址里添加数据  -->
GET /XXX/XXX/XXX.html?user=007&user=Howl HTTP/1.1

<!--  POST请求方式请求体不为空  -->
user=007&user=Howl
```





#### 2.2.2 相应部分

响应头

```html
<!--  空格隔开的两个部分分别表示请求协议及版本号、状态码  -->

HTTP/1.1 200 OK
```

响应头

```html
<!--  提供响应数据的信息  -->

Date:响应时间 Wed, 19 Feb 2020 04:39:00 GMT
Content-Type: 响应内容格式 text/html;charset=utf-8
Content-Encoding: 响应个压缩格式 gzip
Content：响应信息长度 9527
```

空行

```html

```

响应体

```html
<!--  可能是图片、HTML、CSS、JS等  -->

XXXXXXXXXXX
```













## 3. 补充



### 请求方法

常见的有GET和POST，但是还有其他比如OPTIONS、PUT、DELETE等方法，这些方法当然有用处，现在比较流行的 **RESTful** 风格就是利用了这些不同的请求方法





### 常见状态码

| 状态码 | 解释                         |
| ------ | ---------------------------- |
| 200    | 请求成功                     |
| 204    | 服务器成功处理，但未返回内容 |
| 301    | 重定向                       |
| 302    | 转发                         |
| 401    | 请求要求用户的身份认证       |
| 404    | 找不到该对象                 |
| 500    | 服务器内部错误，无法完成请求 |





### 持久连接

HTTP1.1每一次连接能处理多次请求，利用了长连接技术，之前的1.0版本每一次连接只能处理一次请求



### 提升传输效率

使用压缩技术或分块传输





























