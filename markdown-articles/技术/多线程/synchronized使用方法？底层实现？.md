---
title: synchronized使用方法？底层实现？
url: https://www.yuque.com/tangsanghegedan/ilyegg/sh54dx
---

# 使用方法

- **修饰实例⽅法**: 作⽤于当前对象实例加锁，进⼊同步代码前要获得当前对象实例的锁
- **修饰静态⽅法**: 也就是给当前类加锁，会作⽤于类的所有对象实例，因为静态成员不属于任何⼀个实例对象，是类成员（ static 表明这是该类的⼀个静态资源，不管new了多少个对象，只有⼀份）。所以如果⼀个线程A调⽤⼀个实例对象的⾮静态 synchronized ⽅法，⽽线程B需要调⽤这个实例对象所属类的静态 synchronized ⽅法，是允许的，不会发⽣互斥现象，因为访问静态synchronized ⽅法占⽤的锁是当前类的锁，⽽访问⾮静态 synchronized ⽅法占⽤的锁是当前实例对象锁。
- **修饰代码块**: 指定加锁对象，对给定对象加锁，进⼊同步代码库前要获得给定对象的锁。

总结：synchronized锁住的资源只有两类：一个是对象，一个是类。

# 底层实现

对象头是我们需要关注的重点，它是synchronized实现锁的基础，因为synchronized申请锁、上锁、释放锁都与对象头有关。对象头主要结构是由 Mark Word 和 Class Metadata Address 组成，其中 Mark Word 存储对象的hashCode、锁信息或分代年龄或GC标志等信息， Class MetadataAddress 是类型指针指向对象的类元数据，JVM通过该指针确定该对象是哪个类的实例。
锁也分不同状态，JDK6之前只有两个状态：无锁、有锁（重量级锁），而在JDK6之后对synchronized进行了优化，新增了两种状态，总共就是四个状态：无锁状态、偏向锁、轻量级锁、重量级锁，其中无锁就是一种状态了。锁的类型和状态在对象头 Mark Word 中都有记录，在申请锁、锁升级等过程中JVM都需要读取对象的 Mark Word 数据。
每一个锁都对应一个monitor对象，在HotSpot虚拟机中它是由ObjectMonitor实现的（C++实现）。每个对象都存在着一个monitor与之关联，对象与其monitor之间的关系有存在多种实现方式，如monitor可以与对象一起创建销毁或当线程试图获取对象锁时自动生成，但当一个monitor被某个线程持有后，它便处于锁定状态。
