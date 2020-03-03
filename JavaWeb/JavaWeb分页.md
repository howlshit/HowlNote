* 平时我们查询的必要参数是写在Dao层的，但这样是不合理的，Dao层只是用来访问和操作数据库，不应该包含其他信息，所以通用做法创建一个Page对象，将分页信息全部放到里面





**Page对象**

```java
public class Page {
	
	private List<Object> list;	//存放查询的数据
	private int currentPage;	//当前页数
	private int pageSize;		//每页显示的数据条数
	private int totalRecord;	//总数据条数
	private int totalPage;		//总页数
	
	//构造函数，两个参数
	public Page(int currentPage, int pageSize) {
		super();
		this.currentPage = currentPage;
		this.pageSize = pageSize;
	}
	
	public List<Object> getList() {
		return list;
	}
	public void setList(List<Object> list) {
		this.list = list;
	}
	public int getCurrentPage() {
		return currentPage;
	}
	public void setCurrentPage(int currentPage) {
		this.currentPage = currentPage;
	}
	public int getPageSize() {
		return pageSize;
	}
	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}
	public int getTotalRecord() {
		return totalRecord;
	}
	public void setTotalRecord(int totalRecord) {
		this.totalRecord = totalRecord;
	}
	public int getTotalPage() {
		return totalPage;
	}
	public void setTotalPage(int totalPage) {
		this.totalPage = totalPage;
	}
}
```



**Service层**

```java
public Page getPageData(int currentPage,int pageSize){
	
	Page page = new Page(currentPage,pageSize);
	
    //通过Dao层把查询数据放到Page对象中
	page.setTotalPage(pageDao.getTotalPage);
	page.setTotalRecord(pageDao.getTotalRecord);
	page.setList( pageDao.setList(currentPage,pageSize) );
	
    //返回Page给Web层
	return page;
}
```



**Web层**

```java
public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    //获取前端传过来的参数
	int currentPage = Integer.parseInt( request.getParameter("currentPage") );
	int pageSize = Integer.parseInt( request.getParameter("pageSize") );
	
    //处理Page的Service对象
	PageService pageService = new PageService();
	
    //返回数据给前端，这里应该返回Json对象
	Page page = pageService.getPageData(currentPage, pageSize);
    
    //阿里巴巴有个开源的jar包，fastJson可以Bean和Json互转，具体操作可以看下面链接
    return JSON.toJSONString(page);
}
```

[FastJson](<https://www.cnblogs.com/Howlet/p/12032264.html>) 点这里看对象和Json互转