用request.getParameter("file")方法只能得到字符串，不管是否文件类型

form-data表单不能用普通方法获取，它是二进制封装，需要字节流操作，太过复杂，所以使用下面的封装jar包



需要的jar包

* commons-io
* Commons-fileupload



```java
//检查我们是否表单类型
boolean isMultipart = ServletFileUpload.isMultipartContent（request）;
```

```java
//1为基于磁盘的文件项创建工厂
DiskFileItemFactory factory = new DiskFileItemFactory（）;

//为基于磁盘的文件项创建工厂
DiskFileItemFactory factory = new DiskFileItemFactory（yourMaxMemorySize，yourTempDirectory）;
//设置工厂约束
//配置临时目录
ServletContext servletContext = this.getServletContext()；
File repository = (File) servletContext.getAttribute("javax.servlet.context.tempdir");
factory.setRepository(repository);
```

```java
//2创建一个新的文件上传处理程序
ServletFileUpload upload = new ServletFileUpload（factory）;
//设置upload的编码
upload.setHeaderEncoding("UTF-8");
```

```java
//3解析请求
List <FileItem>items = upload.parseRequest（request）;

//处理上传的项目，迭代器
//Iterator <FileItem> iter = items.iterator（）; 

for(FileItem item : items){ 
	//普通表单
    if（item.isFormField（））{ 
        String name = item.getFieldName();
    	String value = item.getString("UTF-8");
    } else { 
        //得到上传文件名
        String filename = item.getName();
        filename = filename.substring(filename.lastIndexOf("\\")+1);
        //文件名：E:\Data\Howl\bing\test.png
        
        //字节输入流
        InputStream in = item.getInputStream();
        
        //字节输出流
        String savepath = this.getServletContext().getRealPath("/upload");
        File file = new File(savepath + "\\" + filename);
        
        //扩展：这里可以用打散的文件
        FileOutputStream out = new FileOutputStream(file);
        
        int length = 0;
        byte[] bytes = new byte[1024];
        
        while(  (length = in.read(bytes)) != 1  ){
            out.write(bytes,0,length);
        }
        
        //删除临时文件，当大于设置大小时
        item.delete();
        in.close();
        out.close();
    } 
}


//String	substring(int beginIndex)
//返回一个字符串，该字符串是此字符串的子字符串。

//int	lastIndexOf(String str)
//返回指定子字符串最后一次出现的字符串中的索引。
```



#### form-data数据进行了二进制封装，所以使用request编码也无用







#### 打散文件夹

- 低四位生成一级目录
- 5-8位生成二级目录



```java
private String makeDirPath(String fileName, String savepath) {

  //通过文件名来算出一级目录和二级目录
  int hashCode = fileName.hashCode();
  int dir1 = hashCode & 0xf;
  int dir2 = (hashCode & 0xf0) >> 4;

  String dir = savepath + "\\" + dir1 + "\\" + dir2;

  //如果该目录不存在，就创建目录
  File file = new File(dir);
  if (!file.exists()) {

    file.mkdirs();
  }
  //返回全路径
  return dir;
}
```



#### 获取全部文件

```java
private void getAllFiles(File filePath, Map map) {

    if(!filePath.isFile()){
        File[] files = filePath.listFiles();
        for(File file = files){
            getAllFiles(file,map);
        }
    }else{
        String filePath = filePath.getName();
        String fileName = filePath.substring(filePath.lastIndexOf("\\") + 1);
        map.add(filePath,fileName);
    }
}
```





****

参考[Apache](<http://commons.apache.org/proper/commons-fileupload/using.html>)

参考[Java3y](<https://mp.weixin.qq.com/s?__biz=MzI4Njg5MDA5NA==&mid=2247485101&idx=2&sn=e738f334ced7497393ef56bcbad21d21&chksm=ebd747acdca0ceba2a543347f93c112f0993fa3358751cb0c281f88d6f29dc66a972093f2038&token=1741918942&lang=zh_CN###rd>)