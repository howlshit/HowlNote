## 1. AQS

在JUC并发包下有locks接口，里面有三个抽象类

![1579764761627](C:\Users\Howl\AppData\Roaming\Typora\typora-user-images\1579764761627.png)

其中，AbstractQueuedSynchronizer为AQS，而且我们后面要讲的Lock显式锁的内部类（ReentrantLock、 ReadWriteLock）都是他的子类，根据名字可以知道他是抽象队列同步器，AQS是**ReentrantReadWriteLock和ReentrantLock的**基础，因为默认的实现都是在内部类Syn中，而Syn继承了AQS



根据其注释可知：

* AQS是包装好的实现锁的抽象同步器
* 依靠原子int表示状态（获取，释放）
* 内部维护队列，没有获取锁就排队
* 有重要的内部类ConditionObject，子类一般使用内部类来实现同步操作
* 有两种线程模式
  * 独占
  * 共享







## 2. 准备知识点

* CAS：compare and swap（比较与交换），不使用锁来实现多线程之间的变量同步
  * 涉及三个数：内存值V，比较值A，新值B
  * 当且仅当内存地址V的值与比较值A相等时，将内存地址V的值修改为B，否则就什么都不做。整个比较并替换的操作是一个原子操作
* 公平锁：线程按它们发出请求锁的顺序来获取锁







## 3. 状态

volatile保证状态可见性

```java
/**
 * The synchronization state.
 */
private volatile int state;
```

CAS算法保证原子性

```java
protected final boolean compareAndSetState(int expect, int update) {
    // See below for intrinsics setup to support this
	return unsafe.compareAndSwapInt(this, stateOffset, expect, update);
}
```





## 4. acquire 获取独占锁

acquire(int)尝试获取资源，如果获取失败，将线程插入等待队列。插入等待队列后，acquire(int)并没有放弃获取资源，而是根据前置节点状态状态判断是否应该继续获取资源，如果前置节点是头结点，继续尝试获取资源，如果前置节点是SIGNAL状态，就中断当前线程，否则继续尝试获取资源。直到当前线程被park()或者获取到资源，acquire(int)结束。

```java
public final void acquire(int arg) {
    if (!tryAcquire(arg) && acquireQueued(addWaiter(Node.EXCLUSIVE), arg))
        selfInterrupt();
}

//子类实现的类，模板模式
```





## 5. release 释放独占锁

首先调用子类的tryRelease()方法释放锁,然后唤醒后继节点,在唤醒的过程中,需要判断后继节点是否满足情况,如果后继节点不为且不是作废状态,则唤醒这个后继节点,否则从tail节点向前寻找合适的节点,如果找到,则唤醒

```java
public final boolean release(int arg) {
    if (tryRelease(arg)) {
        Node h = head;
        if (h != null && h.waitStatus != 0)
            unparkSuccessor(h);
        return true;
    }
    return false;
}
```







# 6. Lock显式锁

* 使用时最标准用法是在try之前调用lock方法，在finally代码块释放锁

* 主要说明两个常见子类：
  * ReentrantLock
  * ReentrantReadWriteLock





### 6.1 ReentrantLock



**构造方法**

```java
public ReentrantLock() {
        sync = new NonfairSync();
}
//默认非公平锁


//也支持公平锁
public ReentrantLock(boolean fair) {
    sync = fair ? new FairSync() : new NonfairSync();
}
```





**lock方法**

```java
/**
 * Performs lock.  Try immediate barge, backing up to normal
 * acquire on failure.
 */
final void lock() {
    if (compareAndSetState(0, 1)) //CAS保证
        setExclusiveOwnerThread(Thread.currentThread()); //尝试获取锁
    else
        acquire(1); //失败就调用AQS的acquire方法
}
```

```java
/**
 * Performs non-fair tryLock.  tryAcquire is implemented in
 * subclasses, but both need nonfair try for trylock method.
 */
final boolean nonfairTryAcquire(int acquires) {
    final Thread current = Thread.currentThread();
    int c = getState();
    if (c == 0) {  //如果锁是空闲的
        if (compareAndSetState(0, acquires)) {
            setExclusiveOwnerThread(current);
            return true;
        }
    }else if (current == getExclusiveOwnerThread()) { //重入锁
        int nextc = c + acquires;
        if (nextc < 0) // overflow
            throw new Error("Maximum lock count exceeded");
        setState(nextc);
        return true;
    }
    return false;
}
```





**unlock**

```java
public void unlock() {
    sync.release(1);
}
```

```java
public final boolean release(int arg) {
    if (tryRelease(arg)) {   //子类实现	
        Node h = head;
        if (h != null && h.waitStatus != 0)
            unparkSuccessor(h);
        return true;
    }
    return false;
}
```

```java
protected final boolean tryRelease(int releases) {
    int c = getState() - releases;  //计数器减 1
    if (Thread.currentThread() != getExclusiveOwnerThread())throw new IllegalMonitorStateException();
    boolean free = false;
    if (c == 0) {  //莫得锁了
        free = true;
        setExclusiveOwnerThread(null);   //锁线程设置为空
    }
    setState(c);
    return free;
}
```





### 6.2 ReentrantReadWriteLock

ReentrantReadWriteLock读写锁：

* 允许多个线程同时进入临界区读取数据
* 写操作互斥读和写
* 在读取多，写入少的情况就发挥作用了



读写锁有个接口：ReadWriteLock，定义了两个方法：readLcok()，writeLock()，而ReentrantReadWriteLock实现了该接口





**获取写锁**

```java
protected final boolean tryAcquire(int acquires) {
/*
 * Walkthrough:
 * 1. If read count nonzero or write count nonzero
 *    and owner is a different thread, fail.
 * 2. If count would saturate, fail. (This can only
 *    happen if count is already nonzero.)
 * 3. Otherwise, this thread is eligible for lock if
 *    it is either a reentrant acquire or
 *    queue policy allows it. If so, update state
 *    and set owner.
 */
    Thread current = Thread.currentThread();
    int c = getState();  //状态
    int w = exclusiveCount(c);
    if (c != 0) {
        // (Note: if c != 0 and w == 0 then shared count != 0)
        if (w == 0 || current != getExclusiveOwnerThread()) //不是当前线程
            return false;
        if (w + exclusiveCount(acquires) > MAX_COUNT) 	//大于饱和
            throw new Error("Maximum lock count exceeded");
        // Reentrant acquire
        setState(c + acquires);
        return true;
    }
    if (writerShouldBlock() || !compareAndSetState(c, c + acquires))
        return false;
    setExclusiveOwnerThread(current);
    return true;
}
```





**获取读锁**

```java
protected final int tryAcquireShared(int unused) {
    Thread current = Thread.currentThread();
    int c = getState();
    if (exclusiveCount(c) != 0 && getExclusiveOwnerThread() != current)  //存在写锁，但不在本线程
        return -1;
    int r = sharedCount(c);
    if (!readerShouldBlock() && r < MAX_COUNT && compareAndSetState(c, c + SHARED_UNIT)){
        if (r == 0) {
            firstReader = current;
            firstReaderHoldCount = 1;
        }else if (firstReader == current) {
            firstReaderHoldCount++;
        }else {
            HoldCounter rh = cachedHoldCounter;
            if (rh == null || rh.tid != getThreadId(current))
                cachedHoldCounter = rh = readHolds.get();
            else if (rh.count == 0)
                readHolds.set(rh);
            rh.count++;
        }
        return 1;
    }
    return fullTryAcquireShared(current);
}
```







## 7. 使用

* ReentrantLock

```java
public class Counter {
    private final Lock lock = new ReentrantLock();
    private int count;

    public void add(int n) {
        lock.lock();
        try {
            count += n;
        } finally {
            lock.unlock();
        }
    }
}
```

* ReentrantReadWriteLock

```java
public class Counter {
    private final ReadWriteLock rwlock = new ReentrantReadWriteLock();
    private final Lock rlock = rwlock.readLock();  //获取读锁
    private final Lock wlock = rwlock.writeLock(); //获取写锁
    private int[] counts = new int[10];

    public void inc(int index) {
        wlock.lock(); // 加写锁
        try {
            counts[index] += 1;
        } finally {
            wlock.unlock(); // 释放写锁
        }
    }

    public int[] get() {
        rlock.lock(); // 加读锁
        try {
            return Arrays.copyOf(counts, counts.length);
        } finally {
            rlock.unlock(); // 释放读锁
        }
    }
}
```

* condition

```java
class conditionTest {
    private final Lock lock = new ReentrantLock();
    private final Condition condition = lock.newCondition();  //一定要这样获取绑定条件

    public void test1() {
        lock.lock();
        try {
            //dosomenthing
            condition.signalAll();
        } finally {
            lock.unlock();
        }
    }

    public void test2() {
        lock.lock();
        try {
            //dosomething
            condition.await();
        } finally {
            lock.unlock();
        }
    }
}
```

