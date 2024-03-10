---
title: volatile作用？底层实现？单例模式中volatile的作用？
---

# 作用

保证数据的“可见性”：被volatile修饰的变量能够保证每个线程能够获取该变量的最新值，从而避免出现数据脏读的现象。
禁止指令重排：在多线程操作情况下，指令重排会导致计算结果不一致 

# 底层实现

“观察加入volatile关键字和没有加入volatile关键字时所生成的汇编代码发现，加入volatile关键字
时，会多出一个lock前缀指令”
lock前缀指令实际上相当于一个内存屏障（也成内存栅栏），内存屏障会提供3个功能：
1）它确保指令重排序时不会把其后面的指令排到内存屏障之前的位置，也不会把前面的指令排到内
存屏障的后面；即在执行到内存屏障这句指令时，在它前面的操作已经全部完成；
2）它会强制将对缓存的修改操作立即写入主存；
3）如果是写操作，它会导致其他CPU中对应的缓存行无效。

**单例模式中volatile的作用**
防止代码读取到instance不为null时，instance引用的对象有可能还没有完成初始化。

```java
class Singleton {

    //禁止指令重排
    private volatile static Singleton instance = null;

    private Singleton() {
    }

    public static Singleton getInstance() {
        if (instance == null) {
            synchronized (Singleton.class) {
                if (instance == null) {
                    instance = new Singleton();
                }
            }
        }
        return instance;
    }
}
```
