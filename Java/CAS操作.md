> 最近忙着复习，笔记倒是写了很多但没有整理出来，后期抽时间统一上传



### 1. CAS

在并发编程下能经常看到CAS，全名Compare and Swap（比较和交换）。是JDK提供的非阻塞原子性操作，它通过硬件保证了`比较-交换`这个操作的原子性，主要是处理器级别提供了原子性操作。和重量级锁（Synchronized）对比，免去了线程上下文切换的开销，是个不错的轻量级锁



实现原理：该方法有四个参数，分别是对象内存位置，对象中变量的偏移量，变量预期值，变量更新值。如果对象obj内存偏移量为offset的变量的值为expect，则使用update替换旧的值expect





### 2. Unsafe

JDK的Unsafe类提供了一系列的compareAndSwap*方法，下面列出这次用到的方法

* long objectFieldOffet(Field file)	获取字段的偏移量
* boolean compareAndSwaoInt(Object obj,long offset,int expect,int update)      CAS方法



因为Unsafe是个直接操作内存的不安全类，所以JDK规定只有BootStrap类加载器加载的类才能使用Unsafe类，比如rt.jar包下的类就可以。那么我们自定义的使用Application加载器加载的类怎么使用Unsafe呢？答案是：万能的反射，方法如下



```java
import sun.misc.Unsafe;
import java.lang.reflect.Field;

public class UnsafeUtil {

    public static Unsafe getInstance() throws NoSuchFieldException, IllegalAccessException {
        
        // 获取字段
        Field filed = Unsafe.class.getDeclaredField("theUnsafe");
        
        // 设置可访问
        filed.setAccessible(true);
        
        // 返回Unsafe实例
        return (Unsafe)filed.get(null);
    }
}
```

**注意：使用IDEA方便，而eclipse不能直接导入import sun.misc.Unsafe，需要自行解决**





```java
import sun.misc.Unsafe;
import java.lang.reflect.Field;

public class Test {

	// 需要CAS修改的字段
    private int i = 0;

    public int getI() {
        return i;
    }

    public void setI(int i) {
        this.i = i;
    }


    public static void main(String[] args) throws NoSuchFieldException, IllegalAccessException {

        // 获取Unsafe实例
        Unsafe  unsafe = UnsafeUtil.getInstance();

        // 获取字段 i 的偏移量
        Test test = new Test();
        Field field = test.getClass().getDeclaredField("i");
        long offset = unsafe.objectFieldOffset(field);

        // CAS修改，返回布尔值
        boolean isSuccess = unsafe.compareAndSwapInt(test,offset,0,1);

        // 打印
        System.out.println(isSuccess);
        System.out.println(test.getI());

    }
}
```



打印

```
true
1
```

