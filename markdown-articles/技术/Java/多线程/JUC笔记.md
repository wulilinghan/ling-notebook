

​		JUC 是指`java.util.concurrent`包， JUC 包含许多线程安全、测试良好、高性能的并发构建块。毫不客气的说，创建JUC的目的就是要实现 Collection 框架对数据结构所执行的并发操作。通过提供一组可靠的、高性能并发构建块，从而提升应用程序并发类的线程安全性、可伸缩性、性能、可读性和可靠性。



# CompletableFuture 

## 代码demo

​		Java中已经引入了 Future 接口，用于描述一个异步计算的结果。虽然 Future 以及相关使用方法提供了异步执行任务的能力，但是对于结果的获取却是很不方便，只能通过阻塞或者轮询的方式得到任务的结果。
阻塞的方式显然和我们的异步编程的初衷相违背，轮询的方式又会耗费无谓的 CPU 资源，而且也不能及时地得到计算结果。

```java
public static void main(String[] args) throws ExecutionException, InterruptedException {
		FutureTask f = new FutureTask(() -> {
			Thread.sleep(3000);
			return "我是阻塞...";
		});

		new Thread(f).start();

		while (true) {
			if (f.isDone()) {
				System.out.println(f.get());
				break;
			} else {
				Thread.sleep(1000);
				System.out.println("等待结果返回...");
			}
		}

		System.out.println("main thread is over ");
		// System.out.println(f.get());
	}

```

当 FutureTask 执行时发生阻塞，当然的，我们开发中一般的，回把get放到最后。



CompletableFuture 类实现了 Future 和 CompletionStage 两个接口。并且对 Future 进行了扩展。

![28995-hjemugmgrok.png](https://raw.githubusercontent.com/wulilh/PicBed/main/img2023/20230212232640272.png)

再来看看 CompletionStage ，CompletionStage 代表异步计算中的一个阶段或步骤。CompletionStage 异步计算过程中的某一个阶段，一个阶段完成以后可能会触发另外一个阶段，有些类似 Linux 系统的管道分隔符传参数。但又像 StringBuffer ，操作之后返回了自己。但又很像 Optional 判断了对象之后对对象的某一个阶段的比较做不同的操作。
其中 CompletableFuture 最常用的方法有两个：

- 执行类：supplyAsync 有返回值 / runAsync 无返回值
- 获取结果类： join / get 获取返回值

```java
public static void main(String[] args) throws ExecutionException, InterruptedException {
		// 创建线程池(可以自定义线程池)
		ExecutorService executorService = Executors.newCachedThreadPool();
		// 异步方法
		CompletableFuture<Void> voidCompletableFuture = CompletableFuture.runAsync(() -> {
			// TODO
		});
		// 异步方法带线程池
		CompletableFuture<Void> voidCompletableFuture1 = CompletableFuture.runAsync(() -> {
			// TODO
		}, executorService);

		// 带返回值的异步方法
		CompletableFuture<String> stringCompletableFuture = CompletableFuture.supplyAsync(() -> {
			// TODO
			return "带返回值的异步方法";
		});
		// 带返回值并且带线程池的异步方法
		CompletableFuture<String> stringCompletableFuture1 = CompletableFuture.supplyAsync(() -> {
			// TODO
			return "带返回值并且带线程池的异步方法";
		}, executorService);

		System.out.println(voidCompletableFuture.get()); // 结果为：null
		System.out.println(voidCompletableFuture1.get());// 结果为：null
		System.out.println(stringCompletableFuture.get());// 结果为：带返回值的异步方法
		System.out.println(stringCompletableFuture1.join());// 结果为：带返回值并且带线程池的异步方法
	}

```

这个只是 CompletableFuture 最常用的方法。我们可以看出，supplyAsync 的两个方法都带有返回值，而且返回值类型和返回值的类型一样。join 和 get 都是获取异步编排之后的结果。最大的区别就是: join 没有异常抛出，而 get 有。接下来，我们来看看 CompletableFuture 其他的操作：

1、获得结果和触发计算

```java
public static void main(String[] args) throws ExecutionException, InterruptedException, TimeoutException {
		// 创建线程池(可以自定义线程池)
		ExecutorService executorService = Executors.newCachedThreadPool();

		// 带返回值并且带线程池的异步方法
		CompletableFuture<String> stringCompletableFuture = CompletableFuture.supplyAsync(() -> {
			try {
				Thread.sleep(2000);
				// TODO
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			return "带返回值并且带线程池的异步方法";
		}, executorService);

		/**
		 * 获取结果,但是会有异常抛出或者需要处理
		 */
		System.out.println(stringCompletableFuture.get());

		/**
		 * 获取结果,没有异常需要抛出或者处理
		 */
		System.out.println(stringCompletableFuture.join());

		/**
		 * 设置超时时间，如果在规定时间内没有获得结果，抛出异常：java.util.concurrent.TimeoutException
		 */
		System.out.println(stringCompletableFuture.get(1, TimeUnit.SECONDS));

		/**
		 * 没有计算完成的情况下，会返回定义的这个替代结果
		 * 计算完成，返回计算完成后的结果
		 */
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		System.out.println(stringCompletableFuture.getNow("没有结果"));

		/**
		 * 是否打断future的get操作
		 * 如果打断get操作成功，complete = true ,则最终get的值为complete给的值。
		 * 如果打算get失败，complete = false ,则最终的结果就是get的值。
		 * 例如：当前的CompletableFutures计算为2s
		 * 1、如果当前直接打算操作，之后再get取值，complete = true get的值为complete定义的值
		 * 2、如果当前操作之前线程等待3s,CompletableFutures已近完成了操作，则打断失败，complete = false get的值就是计算的值
		 */
		System.out.println(stringCompletableFuture.complete("没有结果"));
		System.out.println(stringCompletableFuture.get());
	}

```

2、对计算结果进行处理

```java
public static void main(String[] args) throws ExecutionException, InterruptedException, TimeoutException {
		// 创建线程池(可以自定义线程池)
		ExecutorService executorService = Executors.newCachedThreadPool();

		/**
		 * thenApply,handle都是对程序的下一步一步的执行，但是最大的区别在于：
		 * handle传递两个参数，并且抛出异常时，程序继续往下执行。
		 * thenApply只传递一个参数，有异常时，程序中断，直接到exceptionally
		 * 都存在xxxAsync，也是一个带线程池，一个不带。当不带Async时，由当先线程处理，带了之后，则去线程池中获取一个新的线程执行。
		 *
		 * exceptionally -> try/catch
		 * handle + whenComplete -> try/finally
		 */
		CompletableFuture<Integer> future = CompletableFuture.supplyAsync(() -> {
			return 1;
		}, executorService).thenApply((t) -> {
			return t + 2;
		}).thenApplyAsync(t -> {
			// int a = 10/0;
			return t + 3;
		}, executorService).handle((re, ex) -> {
			// int a = 10/0;
			return re + 4;
		}).handleAsync((re, ex) -> {
			return re + 5;
		}, executorService).whenComplete((result, e) -> {
			System.out.println("result=" + result);
		}).exceptionally(e -> {
			e.printStackTrace();
			return null;
		});

		System.out.println(future.join());
	}

```

3、对计算结果进行消费

```java
public static void main(String[] args) {
		// 创建线程池(可以自定义线程池)
		ExecutorService executorService = Executors.newCachedThreadPool();

		/**
		 * thenAccept 接收任务的处理结果，并消费处理，无返回结果
		 * 如下thenAccept只对接受到的结果进行消费
		 */
		CompletableFuture.supplyAsync(() -> {
			return 1;
		}, executorService).thenApply(r -> {
			return r + 2;
		}).thenAccept(r -> {
			// TODO r
		});

		/**
		 * thenRun、thenApply、thenAccept对比：
		 * thenRun 没有接受值，也没有返回值，只是执行操作 。下面代码打印：null
		 * thenApply 可以接受上一步的结果进行操作，并且返回，下面代码打印：2
		 * thenAccept 对上一步的结果进行消费，只能接受操作，并没有返回值，下面代码打印：null
		 */
		System.out.println(CompletableFuture.supplyAsync(() -> {
			return 1;
		}).thenRun(() -> {

		}).join());

		System.out.println(CompletableFuture.supplyAsync(() -> {
			return 1;
		}).thenApply(r -> {
			return r + 1;
		}).join());

		System.out.println(CompletableFuture.supplyAsync(() -> {
			return 1;
		}).thenAccept(r -> {

		}).join());
	}

```

4、对计算速度进行选用 

```java
    public static void main(String[] args) {

        /**
         * applyToEither 比较两个CompletableFuture返回的结果，那个执行的快，返回执行快的结果
         * 例如：我们经常玩的暴力摩托的游戏，谁先到终点谁获胜
         */
        CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> {
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return "我比较慢";
        }).applyToEither(CompletableFuture.supplyAsync(() -> {
            return "我比较快!";
        }), res -> {
            return res;
        });

        // 最后输出的结果：我比较快
        System.out.println(future.join());
    }

```

5、对计算结果进行合并

```java
public static void main(String[] args) {

        /**
         * thenCombine 等待所有的CompletableFuture完成后，返回每个结果做处理
         *
         */
        CompletableFuture<Integer> future = CompletableFuture.supplyAsync(() -> {
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return 10;
        }).thenCombine(CompletableFuture.supplyAsync(() -> {
            return 20;
        }), (res1, res2) -> {
            return res1 + res2;
        }).thenCombine(CompletableFuture.supplyAsync(()->{
            return 30;
        }),(res1,res2)->{
            /**
             * 注意这个地方的res1是上面等待完成返回结果计算之后的值
             */
            return res1 + res2;
        });

        // 等待1s后输出的结果：60
        System.out.println(future.join());
    }

```

## 使用建议

- CompletableFuture 一定要搭配自己的定义的自定义线程池使用
- 由于统计或者需要返回给前端多个数据的可以运用 CompletableFuture 把多个接口合并成一个
- 数据的导入导出可以使用 CompletableFuture 来做提升效率
- 涉及复杂流程的多方面调用，可以使用 CompletableFuture 来提升代码速度



# volatile 关键字

## Java内存模型之JMM


### JMM

​		JMM (Java 内存模型Java Memory Model，简称JMM)本身是一种抽象的概念并不真实存在，它仅仅描述的是一组**约定或规范**，通过这组规范定义了程序中(尤其是多线程)各个变量的读写访问方式并决定一个线程对共享变量的写入何时以及如何变成对另一个线程可见，关键技术点都是围绕多线程的原子性、可见性和有序性展开的。

**JMM并不是真实存在的，而是一种抽象的概念。**

### JMM三大特性

JMM规范下，存在三大特性：

- 可见性
- 原子性
- 有序性

**可见性**：当一个线程修改了某一个共享变量的值，其他线程是否能够立即知道该变更，JMM规定了所有的变量都存储在主内存中。

​		Java中普通的共享变量不保证可见性，因为数据修改被写入内存的时机是不确定的，多线程并发下很可能出现"脏读"，**所以每个线程都有自己的工作内存，线程自己的工作内存中保存了该线程使用到的变量的主内存副本拷贝，线程对变量的所有操作（读取，赋值等）都必需在线程自己的工作内存中进行，而不能够直接读写主内存中的变量**。不同线程之间也无法直接访问对方工作内存中的变量，线程间变量值的传递均需要通过主内存来完成。

![51763-r7six131uw.png](https://raw.githubusercontent.com/wulilh/PicBed/main/img2023/20230212234617132.png)

**原子性**：指一个操作是不可中断的，即多线程环境下，操作不能被其他线程干扰。
这个有点像事务的原子性，不可中断破坏。

**有序性**：对于一个线程的执行代码而言，我们总是习惯性认为代码的执行总是从上到下，有序执行。**但为了提供性能，编译器和处理器通常会对指令序列进行重新排序。**
指令重排可以保证串行语义一致，但没有义务保证多线程间的语义也一致，即可能产生"脏读"，简单说，两行以上不相干的代码在执行的时候有可能先执行的不是第一条，不见得是从上到下顺序执行，执行顺序会被优化。

```java
public static void main(String[] args) {
        int x = 10;     // 语句1
        int y = 20;     // 语句2

        int z = x + 10; // 语句3
        int i = y + x;  // 语句4
    }
```

我们看到的Java语言是这样的，但是实际上，语句的执行顺序：1234、1324、2134这程序都能正常的运行且结果一致。

单线程环境里面确保程序最终执行结果和代码顺序执行的结果一致。

处理器在进行重排序时必须要考虑指令之间的数据依赖性多线程环境中线程交替执行，由于编译器优化重排的存在，两个线程中使用的变量能否保证一致性是无法确定的，结果无法预测。

### 多线程对变量的读写过程

**读取过程**：
		由于JVM运行程序的实体是线程，而每个线程创建时JVM都会为其创建一个工作内存(有些地方称为栈空间)，工作内存是每个线程的私有数据区域，而Java内存模型中规定所有变量都存储在主内存，主内存是共享内存区域，所有线程都可以访问，但线程对变量的操作(读取赋值等)必须在工作内存中进行，首先要将变量从主内存拷贝到的线程自己的工作内存空间，然后对变量进行操作，操作完成后再将变量写回主内存，不能直接操作主内存中的变量，各个线程中的工作内存中存储着主内存中的变量副本拷贝，因此不同的线程间无法访问对方的工作内存，线程间的通信(传值)必须通过主内存来完成，其简要访问过程如下图:

![68168-4qvx2we6615.png](https://raw.githubusercontent.com/wulilh/PicBed/main/img2023/20230212235948110.png)

JMM定义了线程和主内存之间的抽象关系
1 线程之间的共享变量存储在主内存中(从硬件角度来说就是内存条)
2 每个线程都有一个私有的本地工作内存，本地工作内存中存储了该线程用来读/写共享变量的副本(从硬件角度来说就是CPU的缓存，比如寄存器、L1、L2、L3缓存等)

通过上述描述可以得出：

- 我们定义的所有共享变量都储存在物理主内存中
- 每个线程都有自己独立的工作内存，里面保存该线程使用到的变量的副本(主内存中该变量的一份拷贝)
- 线程对共享变量所有的操作都必须先在线程自己的工作内存中进行后写回主内存，不能直接从主内存中读写(不能越级)
- 不同线程之间也无法直接访问其他线程的工作内存中的变量，线程间变量值的传递需要通过主内存来进行(同级不能相互访问)

### 多线程先行发生原则（happens-before）

**在JMM中，如果一个操作执行的结果需要对另一个操作可见性或者代码重排序，那么这两个操作之间必须存在happens-before关系。**
比如说：int x = 5 线程A执行，int y = x 线程B执行。y是否等于5呢？ 
如果线程A的操作（x = 5）happens-before(先行发生)线程B的操作（y = x）,那么可以确定线程B执行后y = 5 一定成立; 如果他们不存在happens-before原则，那么y = 5 不一定成立。

如果Java内存模型中所有的有序性都仅靠volatile和synchronized来完成，那么有很多操作都将会变得非常啰嗦，但是我们在编写Java并发代码的时候并没有察觉到这一点。

我们没有时时、处处、次次，添加volatile和synchronized来完成程序，这是因为Java语言中JMM原则下有一个“先行发生”(Happens-Before)的原则限制和规矩。

它是判断数据是否存在竞争，线程是否安全的非常有用的手段。依赖这个原则，我们可以通过几条简单规则一揽子解决并发环境下两个操作之间是否可能存在冲突的所有问题，而不需要陷入Java内存模型苦涩难懂的底层编译原理之中。

happens-before之8条：

- 次序规则：一个线程内，按照代码顺序，写在前面的操作先行发生于写在后面的操作；
- 锁定规则：一个unLock操作先行发生于后面((这里的“后面”是指时间上的先后))对同一个锁的lock操作；
- volatile变量规则：对一个volatile变量的写操作先行发生于后面对这个变量的读操作，前面的写对后面的读是可见的，这里的“后面”同样是指时间上的先后。
- 传递规则：如果操作A先行发生于操作B，而操作B又先行发生于操作C，则可以得出操作A先行发生于操作C；
- 线程启动规则(Thread Start Rule)：Thread对象的start()方法先行发生于此线程的每一个动作；
- 线程中断规则(Thread Interruption Rule)：对线程interrupt()方法的调用先行发生于被中断线程的代码检测到中断事件的发生；
- 线程终止规则(Thread Termination Rule)：线程中的所有操作都先行发生于对此线程的终止检测，我们可以通过Thread::join()方法是否结束、Thread::isAlive()的返回值等手段检测线程是否已经终止执行。
- 对象终结规则(Finalizer Rule)：一个对象的初始化完成（构造函数执行结束）先行发生于它的finalize()方法的开始。换句话说：对象没有完成初始化之前，是不能调用finalized()方法的。

## volatile关键字

volatile满足JMM的两个关键的特性：**可见性**和**有序性**。

### volatile可见性

当写一个volatile变量时，JMM会把该线程对应的本地内存中的共享变量值立即刷新回主内存中。
当读一个volatile变量时，JMM会把该线程对应的本地内存设置为无效，直接从主内存中读取共享变量。
所以volatile的写内存语义是直接刷新到主内存中，读的内存语义是直接从主内存中读取。

那volatile凭什么可以保证可见性和有序性？？？

#### 内存屏障

>  内存屏障（也称内存栅栏，内存栅障，屏障指令等，是一类同步屏障指令，是CPU或编译器在对内存随机访问的操作中的一个同步点，使得此点之前的所有读写操作都执行后才可以开始执行此点之后的操作），避免代码重排序。
>
> 内存屏障其实就是一种JVM指令，Java内存模型的重排规则会要求Java编译器在生成JVM指令时插入特定的内存屏障指令，通过这些内存屏障指令，volatile实现了Java内存模型中的可见性和有序性，但volatile无法保证原子性。
>
> 内存屏障之前的所有写操作都要回写到主内存，内存屏障之后的所有读操作都能获得内存屏障之前的所有写操作的最新结果(实现了可见性)。

因此重排序时，不允许把内存屏障之后的指令重排序到内存屏障之前。
一句话：对一个volatile域的写，happens-before于任意后续对这个volatile域的读，也叫写后读。

通过相关资料的查询：JVM中提供了四类内存屏障指令，分别是LoadLoad、StoreStore、LoadStore、StoreLoad。

| 屏障类型   | 指令示例                 | 说明                                                         |
| ---------- | ------------------------ | ------------------------------------------------------------ |
| LoadLoad   | Load1;LoadLoad;Load2     | 保证load1的读取操作在load2及后续读取操作之前执行             |
| StoreStore | Store1;StoreStore;Store2 | 在store2及其后的写操作执行前，保证store1的写操作已刷新到主内存 |
| LoadStore  | Load1;LoadStore;Store2   | 在stroe2及其后的写操作执行前，保证load1的读操作已读取结束    |
| StoreLoad  | Store1;StoreLoad;Load2   | 保证store1的写操作已刷新到主内存之后，load2及其后的读操作才能执行 |

解释一下说明就是：
volatile写的时候：
1.在每个volatile写操作的前⾯插⼊⼀个StoreStore屏障
2.在每个volatile写操作的后⾯插⼊⼀个StoreLoad屏障

![64809-6bao7d8olnh.png](https://raw.githubusercontent.com/wulilh/PicBed/main/img2023/20230213000601791.png)

volatile读的时候：
3.在每个volatile读操作的后⾯插⼊⼀个LoadLoad屏障
4.在每个volatile读操作的后⾯插⼊⼀个LoadStore屏障

![65794-dx73pkekgb.png](https://raw.githubusercontent.com/wulilh/PicBed/main/img2023/20230213000630713.png)

这正是这些指令屏障，保证了volatile的可见性。

#### Java内存模型中定义的8种工作内存与主内存之间的原子操作

read(读取) → load(加载) → use(使用) → assign(赋值) → store(存储) → write(写入) → lock(锁定) → unlock(解锁)

![img](https://raw.githubusercontent.com/wulilh/PicBed/main/img2023/20230213000708514.png)

通过上述图可以梳理得出：

- read：作用于主内存，将变量的值从主内存传输到工作内存，主内存到工作内存
- load：作用于工作内存，将read从主内存传输的变量值放入工作内存变量副本中，即数据加载
- use：作用于工作内存，将工作内存变量副本的值传递给执行引擎，每当JVM遇到需要该变量的字节码指令时会执行该操作
- assign：作用于工作内存，将从执行引擎接收到的值赋值给工作内存变量，每当JVM遇到- -个给变最赋值字节码指令时会执行该操作
- store：作用于工作内存，将赋值完毕的工作变量的值写回给主内存
- write：作用于主内存，将store传输过 来的变量值赋值给主内存中的变量

由于上述只能保证单条指令的原子性，针对多条指令的组合性原子保证，没有大面积加锁，所以，JVM提供了另外两个原子指令:

- lock：作用于主内存，将一个变量标记为一个线程独占的状态，只是写时候加锁，就只是锁了写变量的过程。
- unlock：作用于主内存，把一个处于锁定状态的变量释放，然后才能被其他线程占用。

### volatile没有原子性

> volatile变量的复合操作(如i++)不具有原子性。

原子性指的是一个操作是不可中断的，即使是在多线程环境下，一个操作一旦开始就不会被其他线程影响。

```
public void add()
{
        //不具备原子性，该操作是先读取值，然后写回一个新值，相当于原来的值加上1，分3步完成
        i++; 
 }
```

如果第二个线程在第一个线程读取旧值和写回新值期间读取i的域值，那么第二个线程就会与第一个线程一起看到同一个值，并执行相同值的加1操作，这也就造成了线程安全失败，因此对于add方法必须使用synchronized修饰，以便保证线程安全。

![36139-5bhwe410cjx.png](https://raw.githubusercontent.com/wulilh/PicBed/main/img2023/20230213000757410.png)

如图，当volatile读写保存的时候都是原子的，但是就是执行操作的时候，需要分三步进行，无法保证其原子性。

![52061-2wbet3mn4re.png](https://raw.githubusercontent.com/wulilh/PicBed/main/img2023/20230213000818725.png)

再次看上面的图，read-load-use和assign-store-write成为了两个不可分割的原子操作，但是在use和assign之间依然有极小的一段真空期，有可能变量会被其他线程读取，导致写丢失一次。但是无论在哪一个时间点主内存的变量和任一工作内存的变量的值都是相等的。
这个特性就导致了volatile变量不适合参与到依赖当前值的运算，如i = i + 1; i++;之类的。
那么依靠可见性的特点volatile可以用在哪些地方呢？ 通常volatile用做保存某个状态的boolean值或者int值。

### volatile有序性（禁止重排）

不存在数据依赖关系，可以重排序；存在数据依赖关系，禁止重排序。
数据依赖性：若两个操作访问同一变量，且这两个操作中有一个为写操作，此时两操作间就存在数据依赖性。

例如：3+2+1 = 1+2+3 数据不存在依赖，可以重排。

其实volatile的底层实现是通过内存屏障来保证volatile有关的禁止指令重排的行为。

当第一个操作为volatile读时，不论第二个操作是什么，都不能重排序。这个操作保证了volatile读之后的操作不会被重排到volatile读之前。
当第二个操作为volatile写时，不论第一个操作是什么，都不能重排序。这个操作保证了volatile写之前的操作不会被重排到volatile写之后。
当第一个操作为volatile写时，第二个操作为volatile读时，不能重排。

## 总结

### volitile的可见性和有序性

- volatile写之前的操作，都禁止重排序到volatile之后。
- volatile读之后的操作，都禁止重排序到volatile 之前。
- volatile写之后volatile读，禁止重排序的。

### 正确使用volitile

- 单一赋值可以（类似于赋值booblean或者数值不计算），但是含复合运算赋值不可以(i++之类)。
- 状态标志，判断业务是否结束（多线程可见性共享变量）。
- 开销较低的读，写锁策略。如果写多，读少。可以只在写的操作上加锁。
- DCL双端锁的发布（双重检查锁单例模式）。

```
class Singleton {

    /**
     * double check lock
     */
    private volatile static Singleton singleton;

    public Singleton() {
    }

    public static Singleton getInstance() {
        if (singleton == null) {
            synchronized (Singleton.class) {
                if (singleton == null) {
                    singleton = new Singleton();
                }
            }
        }
        return singleton;
    }


    /**
     * 内部类的方式
     */
    private Singleton(){}

    private static class SingletonHandler{
        private static Singleton instance = new Singleton();
    }

    public static Singleton getInstance(){
        return SingletonHandler.instance;
    }
}

```

