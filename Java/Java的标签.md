> 此处是琐碎笔记，给自己一个提醒，Java要学的东西还很多



## 1. 条件判断退出双重循环

以前笔者如何退出双重循环呢? 利用循环条件判断，加上break、continue、return可以改变流程



```java
public static void main(String[] args) {
    
    // 此处利用了flag标记作为外层循环的判断
    boolean flag = true;
    for (int i = 0; i < 10 && flag != false; i++) {
        for (int j = 0; j < 10; j++) {
            System.out.println(i + "----" + j);
            if(j == 5){
                flag = false;
                break;
            }
        }
    }
}
```









## 2. 标签退出循环

现在刚知道Java还有标签这一特性，配合上面的关键字也可以做到，而且更加简便



```java
public static void main(String[] args) {
    
    // 标签的写法：非关键字 + ：
    outI:
    for (int i = 0; i < 10; i++) {
        outJ:
        for (int j = 0; j < 10; j++) {
            System.out.println(i + "----" + j);
            if(j == 5){
                break outI;
            }
        }
    }
}
```





**立Flag，要像Stream流那样熟悉操作才行**

