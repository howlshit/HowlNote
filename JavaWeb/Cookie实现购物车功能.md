* 这里的购物车暂时存放书，后期把参数改成Object，把方法抽取成接口，只要实现了接口的Object类都可以放进购物项，这样就实现了购物任何物品
* 使用购物项因为一个购物项可以包含某种商品的数量，总价等，反之则需要把商品重复存放到购物车，没有用户体验
* 购物车用HashMap，键存放书id，值存放购物项



### 1. 设计bean

书

```java
public class Book implements Serializable{
	
    //因为对象传输需要实现序列化接口
	//后面代码中id作为Map的键，而键只能为String
	String id;
	String name;
	double price;
	
	public Book(String id, String name, double price) {
		this.id = id;
		this.name = name;
		this.price = price;
	}

	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public double getPrice() {
		return price;
	}
	public void setPrice(double price) {
		this.price = price;
	}

	@Override
	public String toString() {
		return "Book [id=" + id + ", name=" + name + ", price=" + price + "]";
	}
}
```

购物项

```java
public class CartItem implements Serializable{
	
	private Book book;
	private int quantity;
	private double price;
	
	public Book getBook() {
		return book;
	}
	public void setBook(Book book) {
		this.book = book;
	}
	public int getQuantity() {
		return quantity;
	}
	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}
	public double getPrice() {
		return book.getPrice() * quantity;
	}
	public void setPrice(double price) {
		this.price = price;
	}
	
	@Override
	public String toString() {
		return "CartItem [book=" + book + ", quantity=" + quantity + ", price=" + price + "]";
	}
}
```

购物车

```java
public class Cart<K, V> implements Serializable{

    //键为书名id，储存实物
	private double totalPrice;
	private HashMap<String,CartItem> bookMap = new HashMap<String, CartItem>();
	
	public void addBook(Book book){
		//从购物车找对应书籍的购物项
		CartItem cartItem = bookMap.get(book.getId());
		//若没有该书的购物项，新建一个
		if(cartItem == null){
			cartItem = new CartItem();
			cartItem.setBook(book);
			cartItem.setQuantity(1);
			bookMap.put(book.getId(), cartItem);
		}else{
			cartItem.setQuantity(cartItem.getQuantity() + 1);
		}
	}
	public void deleteBook(Book book){
		CartItem cartItem = bookMap.get(book.getId());
		if(cartItem == null){
			//do nothing
		}else if(cartItem.getQuantity() == 1){
			bookMap.remove(book.getId());
		}else{
			cartItem.setQuantity(cartItem.getQuantity() - 1);
		}
	}
	public double getPrice(){
		//遍历购物车里的购物项
		for(Map.Entry set : bookMap.entrySet()){
			//String bookId = (String) set.getKey();
			CartItem cartItem = (CartItem) set.getValue();
			totalPrice += cartItem.getPrice();
		}
		return totalPrice;
	}

	public HashMap<String, CartItem> getBookMap() {
		return bookMap;
	}
	public void setBookMap(HashMap<String, CartItem> bookMap) {
		this.bookMap = bookMap;
	}
	public double getTotalPrice() {
		return totalPrice;
	}
	public void setTotalPrice(double totalPrice) {
		this.totalPrice = totalPrice;
	}
}
```





### 2. 购物车序列化存放到Cookie



#### 2.1 模仿购物车添加商品

```java
//往购物车添加书本
Cart cart = new Cart();
cart.addBook(new Book("1","且听风吟",10.5f));
cart.addBook(new Book("1","且听风吟",10.5f));
cart.addBook(new Book("1","且听风吟",10.5f));
cart.addBook(new Book("2","我们仨",5.5f));
cart.deleteBook(new Book("1","且听风吟",10.5f));
cart.deleteBook(new Book("2","我们仨",5.5f));
cart.deleteBook(new Book("3","解忧杂货店",20.5f));
```

#### 2.2 购车从序列化存入Cookie

* 其中Cookie不能有[ ] ( ) = , " / ? @ : ;特殊字符，需要URL编码
* ByteArrayOutputStream.toString()把字节数组内容转化成字符串

```java
//	-----------------------------购物车对象序列化------------------------[开始]
ByteArrayOutputStream bos= new ByteArrayOutputStream();
ObjectOutputStream oos = new ObjectOutputStream(bos);
oos.writeObject(cart);
String objectString = URLEncoder.encode(bos.toString("ISO-8859-1"),"UTF-8");
//	-----------------------------购物车对象序列化------------------------[完]	

//	-----------------------------给客户端添加cookie------------------------[开始]
response.setContentType("text/html;charset=UTF-8");
Cookie cookie = new Cookie("name", objectString);
cookie.setMaxAge(1000);
response.addCookie(cookie);
//	-----------------------------给客户端添加cookie------------------------[完]
```





### 3. 服务器读取Cookie

* 遍历所有Cookie，找到Cart

```java
Cookie[] cookies = request.getCookies();
if(cookies != null){
	for(Cookie cookieLoop : cookies){
		String name = cookieLoop.getName();
		String value = URLDecoder.decode(cookieLoop.getValue(), "UTF-8");
		if(name == "Cart"){
			ByteArrayInputStream bis = new ByteArrayInputStream(value.getBytes("ISO-8859-1"));
			ObjectInputStream ois = new ObjectInputStream(bis);
			try {
				Cart cart1 = (Cart) ois.readObject();
				HashMap cartMap = cart1.getBookMap();
				for(Object cartItem : cartMap.values()){
					//遍历购物项并打印
					System.out.println(cartItem.toString());
				}
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
	}
}
```





### 4. 测试结果

```xml
CartItem [book=Book [id=1, name=且听风吟, price=10.5], quantity=2, price=0.0]
<!-- 剩下且听风吟 * 2 -->
```



