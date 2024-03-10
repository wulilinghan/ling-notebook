# 特点

由于ReentrantLock是java.util.concurrent包下提供的一套互斥锁，相比Synchronized，ReentrantLock类提供了一些高级功能，主要有以下三项：

1. 等待可中断，持有锁的线程长期不释放的时候，正在等待的线程可以选择放弃等待，这相当于Synchronized来说可以避免出现死锁的情况。通过lock.lockInterruptibly()来实现这个机制。
2. 公平锁，多个线程等待同一个锁时，必须按照申请锁的时间顺序获得锁，Synchronized锁是非公平锁，ReentrantLock**默认的构造函数是创建的非公平锁**，可以通过参数true设为公平锁，但公平锁表现的性能不是很好。
3. 锁绑定多个条件，一个ReentrantLock对象可以同时绑定对个对象。ReenTrantLock提供了一个Condition（条件）类，用来实现分组唤醒需要唤醒的线程们，而不是像synchronized要么随机唤醒一个线程要么唤醒全部线程。

# 使用方法：

基于API层面的互斥锁，需要lock()和unlock()方法配合try/finally语句块来完成

# 底层实现：

ReenTrantLock的实现是一种自旋锁，通过循环调用CAS操作来实现加锁。它的**性能比较好也是因为避免了使线程进入内核态的阻塞状态。**想尽办法避免线程进入内核的阻塞状态是我们去分析和理解锁设计的关键钥匙。

# 和synchronized区别：

1. **底层实现**上来说，synchronized 是J**VM层面的锁**，是**Java关键字**，通过monitor对象来完成（monitorenter与monitorexit），对象只有在同步块或同步方法中才能调用wait/notify方法；

ReentrantLock 是从jdk1.5以来（java.util.concurrent.locks.Lock）提供的**API层面**的锁。
synchronized 的实现涉及到锁的升级，具体为无锁、偏向锁、自旋锁、向OS申请重量级锁；
ReentrantLock实现则是通过利用CAS（CompareAndSwap）自旋机制保证线程操作的原子性和volatile保证数据可见性以实现锁的功能。

2. **是否可手动释放**：synchronized 不需要用户去手动释放锁，synchronized 代码执行完后系统会

自动让线程释放对锁的占用； ReentrantLock则需要用户去手动释放锁，如果没有手动释放锁，就可能
导致死锁现象。一般通过lock()和unlock()方法配合try/finally语句块来完成，使用释放更加灵活。

3. **是否可中断**：synchronized是不可中断类型的锁，除非加锁的代码中出现异常或正常执行完成；

ReentrantLock则可以中断，可通过trylock(long timeout,TimeUnit unit)设置超时方法或者将
lockInterruptibly()放到代码块中，调用interrupt方法进行中断。

4. **是否公平锁**：synchronized为非公平锁 ；ReentrantLock则即可以选公平锁也可以选非公平锁，

通过构造方法new ReentrantLock时传入boolean值进行选择，为空默认false非公平锁，true为公平
锁。
