# JUC并发编程

## 一、锁

当两条线程同时访问一个类的时候，可能会带来一些问题。并发线程重入可能会带来内存泄漏、程序不可控等。在多线程间的通讯还是共享数据都需要使用Java的锁机制来处理代码产生的问题。


### 1、传统的synchronized锁

**生产者与消费者synchronized版**

```java
public class PCSynchronized {
    public static void main(String[] args) {
        TestSynchronized testLock = new TestSynchronized();
        new Thread(() -> {
            for (int i = 0; i < 10; i++) {
                testLock.consumer();
            }
        }, "A").start();

        new Thread(() -> {
            for (int i = 0; i < 10; i++) {
                testLock.product();
            }
        }, "B").start();
    }
}

class TestSynchronized {
    private int number = 0;

    /**
     * 生产者
     */
    public synchronized void product() {
        try {
            while (number != 0) {
                //阻塞并释放锁
                this.wait();
            }
            number++;
            System.out.println("线程:" + Thread.currentThread().getName() + " 生产了:" + number);
            //唤醒
            this.notifyAll();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 消费者
     */
    public synchronized void consumer() {
        try {
            while (number == 0) {
                //阻塞并释放锁
                this.wait();
            }
            number--;
            System.out.println("线程:" + Thread.currentThread().getName() + " 消费了:" + number);
            //唤醒
            this.notifyAll();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
```

![image-20220312020843737](C:\Users\liujialiang\AppData\Roaming\Typora\typora-user-images\image-20220312020843737.png)

### 2、ReentrantLock可重入锁

**生产者与消费者ReentrantLock版本**

```java
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class PCReentrantLock {
    public static void main(String[] args) {
        TestReentrantLock testLock = new TestReentrantLock();
        new Thread(() -> {
            for (int i = 0; i < 10; i++) {
                testLock.consumer();
            }
        }, "A").start();

        new Thread(() -> {
            for (int i = 0; i < 10; i++) {
                testLock.product();
            }
        }, "B").start();
    }
}

class TestReentrantLock {
    private int number = 0;
    Lock lock = new ReentrantLock();
    Condition condition = lock.newCondition();

    /**
     * 生产者
     */
    public void product() {
        lock.lock();
        try {
            while (number != 0) {
                condition.await();
            }
            number++;
            System.out.println("线程:" + Thread.currentThread().getName() + " 生产了:" + number);
            condition.signalAll();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }
    /**
     * 消费者
     */
    public void consumer() {
        lock.lock();
        try {
            while (number == 0) {
                condition.await();
            }
            number--;
            System.out.println("线程:" + Thread.currentThread().getName() + " 消费了:" + number);
            condition.signalAll();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }
}
```

![image-20220312020803654](C:\Users\liujialiang\AppData\Roaming\Typora\typora-user-images\image-20220312020803654.png)

上述两种加锁示例代码实现了生产者和消费者之间的协同运行，线程B生产(number = 0)后、线程A才进行消费（number = 1），否则会被阻塞等待、释放锁，直到被其他线程唤醒。

### 3、虚假唤醒

1. 定义

   > 当线程从等待状态中被唤醒时，只是发现未满足其正在等待的条件时，就会发生虚假唤醒。 之所以称其为虚假的，是因为该线程似乎无缘无故被唤醒。 虚假唤醒不会无缘无故发生，通常是因为在发起唤醒号和等待线程最终运行之间的临界时间内，线程不再满足竞态条件。

2. 举个例子

   某蛋糕店进行蛋糕的生产和销售，由于蛋糕的特殊性，要求店里库存的蛋糕不能大于100，避免卖不出去浪费，示例代码如下：

   ```java
   public class SpuriousWakeUps {
       public static void main(String[] args) {
           Cookie cookie = new Cookie();
           new Thread(() -> {
               for (int i = 0; i < 30; i++) {
                   try {
                       TimeUnit.SECONDS.sleep(1);
                   } catch (InterruptedException e) {
                       e.printStackTrace();
                   }
                   cookie.create();
               }
           },"生产线程A").start();
   
           new Thread(() -> {
               for (int i = 0; i < 30; i++) {
                   try {
                       TimeUnit.SECONDS.sleep(1);
                   } catch (InterruptedException e) {
                       e.printStackTrace();
                   }
                   cookie.sale();
               }
           },"销售线程A").start();
   
           //以上只有两个线程，生产线程和销售线程，会交替执行，多线程下会出现虚假唤醒的情况
           new Thread(() -> {
               for (int i = 0; i < 30; i++) {
                   try {
                       TimeUnit.SECONDS.sleep(1);
                   } catch (InterruptedException e) {
                       e.printStackTrace();
                   }
                   cookie.create();
               }
           },"生产线程B").start();
   
           new Thread(() -> {
               for (int i = 0; i < 30; i++) {
                   try {
                       TimeUnit.SECONDS.sleep(1);
                   } catch (InterruptedException e) {
                       e.printStackTrace();
                   }
                   cookie.sale();
               }
           },"销售线程B").start();
   
       }
   }
   
   class Cookie {
       private int count;
   
       /**
        * 生产蛋糕
        */
       public synchronized void create() {
           if (count >= 100) {
               try {
                   this.wait();
               } catch (InterruptedException e) {
                   e.printStackTrace();
               }
           }
           count++;
           System.out.println(Thread.currentThread().getName() + "生产了一个蛋糕，当前蛋糕数目为：" + count);
           this.notifyAll();
       }
   
       /**
        * 销售蛋糕
        */
       public synchronized void sale() {
           if (count <= 0) {
               try {
                   TimeUnit.SECONDS.sleep(1);
                   this.wait();
               } catch (InterruptedException e) {
                   e.printStackTrace();
               }
           }
           count--;
           System.out.println(Thread.currentThread().getName() + "出售了一个蛋糕，当前蛋糕数目为：" + count);
           this.notifyAll();
       }
   }
   ```

   ![image-20220312174405444](C:\Users\liujialiang\AppData\Roaming\Typora\typora-user-images\image-20220312174405444.png)

​	测试结果可以看到生产和销售线程输出的结果出现了2、-1，分析下原因，一个程调用了wait()方法后阻塞、释放锁，当被其他线程唤醒后会从wait()处的下一行代码继续执行，

（1）4个线程启动

（2）两个销售线程A、B发现蛋糕库存为0，线程调用wait()被挂起

（3）生产线程A生产了一个蛋糕，库存 +1，此时蛋糕数量为 1，调用notifyAll()唤醒其他线程

（4）销售线程A被唤醒，出售一个蛋糕 库存 -1，此时蛋糕数量为 0，调用notifyAll()唤醒其他线程

（5）销售线程B被唤醒，线程B从wait()后开始执行，没有判断当前的蛋糕数量，出售一个蛋糕 库存 -1，此时蛋糕数量为 -1，调用notifyAll()唤醒其他线程

这个时候就出现了蛋糕数量为-1的情况。

解决的方法也很简单：

```java
//把
if (count >= 100) {
    try {
        this.wait();
    } catch (InterruptedException e) {
        e.printStackTrace();
    }
}
//改为
while(count >= 100) {
    try {
        this.wait();
    } catch (InterruptedException e) {
        e.printStackTrace();
    }
}
```



### 4、Condition实现精准通知唤醒

> Condition接口提供了类似Object的监视器方法，与Lock配合可以实现等待/通知模式，要想理解Condition原理，需要先了解AQS。AQS中维护了一个volatile int state（共享资源）和一个CLH队列。当state=1时代表当前对象锁已经被占用，其他线程来加锁时则会失败，失败的线程被放入一个FIFO的等待队列中，然后会被UNSAFE.park()操作挂起，等待已经获得锁的线程释放锁才能被唤醒。详细原理可自行查阅资料。

```java
public class ConditionTest {

    public static void main(String[] args) {
        ConditionNotify conditionNotify = new ConditionNotify();

        new Thread(() -> {
            for (int i = 0; i < 10; i++) {
                conditionNotify.printB();
            }
        },"BBB").start();

        new Thread(() -> {
            for (int i = 0; i < 10; i++) {
                conditionNotify.printA();
            }
        },"AAA").start();

        new Thread(() -> {
            for (int i = 0; i < 10; i++) {
                conditionNotify.printC();
            }
        },"CCC").start();
    }
}

class ConditionNotify {
    private Lock lock = new ReentrantLock();
    private Condition conditionA = lock.newCondition();
    private Condition conditionB = lock.newCondition();
    private Condition conditionC = lock.newCondition();
    //1A  2B  3C
    private int number = 1;

    public void printA() {
        lock.lock();
        try {
            //业务，判断 -> 执行 -> 通知
            while (number != 1) {
                conditionA.await();
            }
            System.out.println(Thread.currentThread().getName() + "线程，当前number为: " + number);
            number++;
            conditionB.signal();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }

    public void printB() {
        lock.lock();
        try {
            while (number != 2) {
                conditionB.await();
            }
            System.out.println(Thread.currentThread().getName() + "线程，当前number为: " + number);
            number++;
            conditionC.signal();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }

    public void printC() {
        lock.lock();
        try {
            while (number != 3) {
                conditionC.await();
            }
            System.out.println(Thread.currentThread().getName() + "线程，当前number为: " + number);
            number = 1;
            conditionA.signal();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }
}
```

该例子中AAA、BBB、CCC 线程使用condition实现了三个线程间的精准唤醒调用。

![image-20220312181849364](C:\Users\liujialiang\AppData\Roaming\Typora\typora-user-images\image-20220312181849364.png)



### 5、synchronized与Lock的区别

1. Synchronized 是内置的Java关键字；Lock 是一个Java类
2. Synchronized 无法判断锁的状态；Lock 可以判断是否获取到了锁
3. Synchronized 已获取锁的线程执行完同步代码，释放锁，若线程发生异常，JVM会让线程释放锁；Lock 必须在finally中手动释放锁，若不释放则会产生死锁
4. Synchronized 可重入锁，不可以中断的，非公平锁；Lock 可重入锁，可以判断锁，可自己设置公平 / 非公平锁
5. Synchronized 使用Object对象本身的wait 、notify、notifyAll调度机制；Lock 可以使用Condition进行线程之间的调度
6. Synchronized 适合少量的代码同步问题；Lock 适合大量的同步代码



## 二、集合类线程安全

### 1、集合线程不安全

```java
public static void listNotSafe() {
    List<String> list = new CopyOnWriteArrayList<>();
    //模拟30个线程同时向list添加元素
    for (int i = 0; i < 30; i++) {
        new Thread(() -> {
            list.add(UUID.randomUUID().toString().substring(0, 8));
            System.out.println(list);
        }).start();
    }
}
```

![image-20220312145311102](C:\Users\liujialiang\AppData\Roaming\Typora\typora-user-images\image-20220312145311102.png)

**list集合在多线程环境下会抛出  java.util.ConcurrentModificationException  异常 ，此外HashMap也是线程不安全的。**

### 2、解决办法

1. 使用线程安全类 Vector

   ```java
   new Vector();
   ```

2. 使用 Collections 工具类封装 ArrayList

   ```java
   Collections.synchronizedList(new ArrayList<>());
   ```

3. 使用 java.util.concurrent.CopyOnWriteArrayList;

   ```java
   new CopyOnWriteArrayList<>();
   ```

使用 CopyOnWriteArrayList 改写上述示例，测试不会抛出  java.util.ConcurrentModificationException 异常

```java
public static void listNotSafe() {
    List<String> list = new CopyOnWriteArrayList<>();
    for (int i = 0; i < 30; i++) {
        new Thread(() -> {
            list.add(UUID.randomUUID().toString().substring(0, 8));
            System.out.println(list);
        }).start();
	}
}
```

除了CopyOnWriteArrayList外，JUC包还提供了CopyOnWriteArraySet、ConcurrentHashMap等，它们的用法和CopyOnWriteArrayList相似，想要了解底层线程安全的原理可以阅读源码，这里就不多介绍了。

### 3、BlockingQueue阻塞队列

在对线程并发处理，使用线程池的时候会用到阻塞队列，集合框架BlockingQueue结构如下图

![image-20220312153900536](C:\Users\liujialiang\AppData\Roaming\Typora\typora-user-images\image-20220312153900536.png)

LinkedBlockingQueue和ArrayBlockingQueue具有队列的性质，遵循先进先出(FIFO)的规则，操作队列中的元素，写入时，如果队列满了必须阻塞等待，读取时，如果队列是空的必须阻塞等待添加元素。常用的方法有以下四组：

|     操作     | 抛出异常  | 有返回值且不抛出异常 | 阻塞等待 |       超时等待       |
| :----------: | :-------: | :------------------: | :------: | :------------------: |
|   添加元素   |   add()   |       offer()        |  put()   | offer(，，) 超时时间 |
|   删除元素   | remove()  |        poll()        |  take()  |  poll(，) 超时时间   |
| 查看队首元素 | element() |        peek()        |    -     |                      |

```java
public class BlockingQueueTest {
    public static void main(String[] args) throws InterruptedException {
        BlockingQueue<String> blockingQueue = new LinkedBlockingQueue<>(3);

        //1、抛出异常示例
        System.out.println(blockingQueue.add("A"));
        System.out.println(blockingQueue.add("B"));
        System.out.println(blockingQueue.add("C"));
        //查看队首元素,队列为空抛出异常，不删除元素
        System.out.println(blockingQueue.element());
        //再添加第四个元素会抛出异常
        //System.out.println(blockingQueue.add("D"));
        for (int i = 0; i < 3; i++) {
            System.out.println(blockingQueue.remove());
        }
        //再次remove会抛出异常
        //blockingQueue.remove();


        //2、有返回值且不抛出异常
        System.out.println(blockingQueue.offer("A"));
        System.out.println(blockingQueue.offer("B"));
        System.out.println(blockingQueue.offer("C"));
        //添加第四个元素不抛异常，返回false
        System.out.println(blockingQueue.offer("D"));
        //查看队首元素,队列为空不抛出异常，有返回值，不删除元素
        System.out.println(blockingQueue.peek());
        for (int i = 0; i < 4; i++) {
            //队列只有三个元素，取第四个元素不抛异常，返回null
            System.out.println(blockingQueue.poll());
        }

        
        //3、阻塞等待
        blockingQueue.put("A");
        blockingQueue.put("B");
        blockingQueue.put("C");
        //添加第四个元素队列满了，会一直阻塞下去
        //blockingQueue.put("D");

        for (int i = 0; i < 3; i++) {
            //队列只有三个元素，取第四个元素不抛异常，返回null
            System.out.println(blockingQueue.take());
        }
        //队列只有三个元素，取第四个元素时空队列，会一直阻塞下去
        //System.out.println(blockingQueue.take());


        //4、超时等待
        System.out.println(blockingQueue.offer("A"));
        System.out.println(blockingQueue.offer("B"));
        System.out.println(blockingQueue.offer("C"));
        //添加第四个元素队列已满,会等待 2 秒，最后跳过该操作。
        System.out.println(blockingQueue.offer("D", 2,TimeUnit.SECONDS));
        for (int i = 0; i < 4; i++) {
            //队列只有三个元素，取第四个元素会阻塞等待 2 秒，最后跳过该操作。
            System.out.println(blockingQueue.poll(2,TimeUnit.SECONDS));
        }
    }
}
```



## 三、线程池

### 1、定义

> 线程池是一种线程的使用模式，它为了降低线程使用中频繁的创建和销毁所带来的资源消耗与代价。
> 通过创建一定数量的线程，让他们时刻准备就绪等待新任务的到达，而任务执行结束之后再重新回来继续待命。线程池结构如下图：

![image-20220313201906329](C:\Users\liujialiang\AppData\Roaming\Typora\typora-user-images\image-20220313201906329.png)

- 主线程创建任务后向线程池提交执行，如果核心处理线程未满，则直接执行线程，否则线程加入BlockingQueue阻塞队列

- 当线程池中核心线程有空闲会从队列中取任务来执行，如果此时核心线程和队列都满了，会创建新的线程来执行任务，线程数不会超过maximumPoolSize

- 如果队列且当前线程数已达到maximumPoolSize，则会根据线程池设定的拒绝策略来处理

- 拒绝策略有四种：AbortPolicy（丢弃任务并抛出RejectedExecutionException异常）、DiscardPolicy（丢弃任务，但是不抛出异常。如果线程队列已满，则后续提交的任务都会被丢弃，且是静默丢弃）、DiscardOldestPolicy（丢弃队列最前面的任务，然后重新提交被拒绝的任务）、CallerRunsPolicy（由调用线程处理该任务）。

- 注意 execute()和submit()的区别。submit()提供Future < T > 类型的返回值，executor()无返回值，sumbit()不会抛出异常。除非你调用Future.get()，excute()会抛出异常，submit()入参可以为Callable，也可以为Runnable。excute()入参Runnable；此外，使用这两种提交线程执行的方式，任务在线程池corePool已满的情况下，执行的顺序是有区别的，具体可自行测试。

### 2、创建线程池

常见的线程池有 newSingleThreadExecutor()、newFixedThreadPool()、 newCachedThreadPool()、newScheduledThreadPool()等，上述的几种创建线程的方法底层都是创建ThreadPoolExecutor；但是阿里巴巴Java开发规范建议我们使用手动创建的线程池ThreadPoolExecutor()，这样开发者就能更清楚的了解线程池的工作原理。

```java
public ThreadPoolExecutor(int corePoolSize,
                          int maximumPoolSize,
                          long keepAliveTime,
                          TimeUnit unit,
                          BlockingQueue<Runnable> workQueue,
                          ThreadFactory threadFactory,
                          RejectedExecutionHandler handler) {
    if (corePoolSize < 0 ||
        maximumPoolSize <= 0 ||
        maximumPoolSize < corePoolSize ||
        keepAliveTime < 0)
        throw new IllegalArgumentException();
    if (workQueue == null || threadFactory == null || handler == null)
        throw new NullPointerException();
    this.acc = System.getSecurityManager() == null ?
        null :
    AccessController.getContext();
    this.corePoolSize = corePoolSize;
    this.maximumPoolSize = maximumPoolSize;
    this.workQueue = workQueue;
    this.keepAliveTime = unit.toNanos(keepAliveTime);
    this.threadFactory = threadFactory;
    this.handler = handler;
}
```

ThreadPoolExecutor类的构造方法有七个参数：

- corePoolSize： 线程池核心线程数
- maximumPoolSize：线程池最大数
- keepAliveTime： 空闲线程存活时间
- unit： 空闲线程存活时间单位
- workQueue： 线程池所使用的缓冲队列
- threadFactory：线程池创建线程使用的工厂
- handler： 线程池对拒绝任务的处理策略

```java
public class TreadPool {
    public static void main(String[] args) {
        ExecutorService service = new ThreadPoolExecutor(10, 20, 0L, TimeUnit.SECONDS, new LinkedBlockingQueue<>(), new NameThreadFactory(),new ThreadPoolExecutor.CallerRunsPolicy());
        for (int i = 0; i < 100; i++) {
            service.execute(() -> {
                System.out.println(Thread.currentThread().getName());
            });
        }
        service.shutdown();
    }
    
    static class NameThreadFactory implements ThreadFactory {
        AtomicInteger threadNum = new AtomicInteger();
        public NameThreadFactory() {}
        @Override
        public Thread newThread(Runnable r) {
            Thread thread = new Thread(r);
            threadNum.incrementAndGet();
            thread.setName("测试ThreadFactoryName-" + threadNum);
            return thread;
        }
    }
}
```

上述的示例代码手动创建了一个线程池，其核心线程数为：10，最大线程数为：20，空闲线程存活时间为0秒，使用LinkedBlockingQueue阻塞队列作为缓冲队列，拒绝策略为：CallerRunsPolicy（由调用线程处理该任务）。基于这些参数，可根据业务需求来创建合适的线程池，比如使用DelayedWorkQueue()延迟队列创建一个定时线程池。最后不要忘记关闭线程池。

**使用线程池的时候可以根据线程业务类型，判断任务是CPU密集型还是IO密集型，调整线程池的参数设置来提高程序的执行效率**

## 四、原理进阶

### 1、volatile关键字

要研究volatile关键字，首先要了解Java内存模型（JMM），Java 内存模型抽象了线程和主内存之间的关系，就比如说线程之间的共享变量必须存储在主内存中。Java 内存模型主要目的是为了屏蔽系统和硬件的差异，避免一套代码在不同的平台下产生的效果不一致。要解决这个问题，就需要把变量声明为 **`volatile`** ，这就指示 JVM，这个变量是共享且不稳定的，每次使用它都到主存中进行读取。

- **主内存**：所有线程创建的实例对象都存放在主内存中，不管该实例对象是成员变量还是方法中的本地变量(也称局部变量)
- **本地内存** ：每个线程都有一个私有的本地内存来存储共享变量的副本，并且，每个线程只能访问自己的本地内存，无法访问其他线程的本地内存。本地内存是 JMM 抽象出来的一个概念，存储了主内存中的共享变量副本。

![image-20220320145904803](C:\Users\liujialiang\AppData\Roaming\Typora\typora-user-images\image-20220320145904803.png)

**`volatile` 关键字 是为了保证变量在线程间的可见性，并不能保证原子性，除了保证变量的可见性 ，还有一个重要的作用就是防止 JVM 的指令重排。**

举个例子，我们在实现单例模式的时候要在变量前加**volatile**关键字，就是为了防止JVM指令重排，如果不加**volatile**则单例模式是存在问题，正常new 一个对象有三个步骤：

1. 给HeadlessWebDriverPool对象分配内存
2. 初始化HeadlessWebDriverPool对象
3. 设置headlessWebDriverPool指向分配的内存地址

假设线程A来获取实例，判断headlessWebDriverPool为null，需要new一个新对象，在创建对象过程中由于指令重排，已经执行了1、3,此时对象还没初始化，这时 对象已经不是null了，同时线程B调用getInstance()方法if判断对象不为null直接返回了未初始化的对象，所以使用这个实例的时候会报错，一定要加上**volatile**

```java
    private static volatile HeadlessWebDriverPool headlessWebDriverPool;
    private void headlessWebDriverPool() {}
    public static HeadlessWebDriverPool getInstance() {
        if (headlessWebDriverPool == null) {
            synchronized (HeadlessWebDriverPool.class) {
                if (headlessWebDriverPool == null) {
                    headlessWebDriverPool = new HeadlessWebDriverPool();
                }
            }
        }
        return headlessWebDriverPool;
    }
```

`synchronized` 关键字和 `volatile` 关键字是两个互补的存在，而不是对立的存在！

- **`volatile` 关键字**是线程同步的**轻量级实现**，所以 **`volatile `性能肯定比`synchronized`关键字要好** 。但是 **`volatile` 关键字只能用于变量而 `synchronized` 关键字可以修饰方法以及代码块** 。
- **`volatile` 关键字能保证数据的可见性，但不能保证数据的原子性。`synchronized` 关键字两者都能保证。**
- **`volatile`关键字主要用于解决变量在多个线程之间的可见性，而 `synchronized` 关键字解决的是多个线程之间访问资源的同步性。**

### 2、synchronized关键字

**在开头的生产者与消费者的例子中使用了synchronized关键字，用来处理同步问题的，synchronized 关键字最主要的三种使用方式：**

**（1）修饰实例方法:** 作用于当前对象实例加锁，进入同步代码前要获得 **当前对象实例的锁**

```java
synchronized void method() {
    //业务代码
}
```

**（2）修饰静态方法:** 也就是给当前类加锁，会作用于类的所有对象实例 ，进入同步代码前要获得 **当前 class 的锁**。因为静态成员不属于任何一个实例对象，是类成员（ *static 表明这是该类的一个静态资源，不管 new 了多少个对象，只有一份*）。所以，如果一个线程 A 调用一个实例对象的非静态 `synchronized` 方法，而线程 B 需要调用这个实例对象所属类的静态 `synchronized` 方法，是允许的，不会发生互斥现象，**因为访问静态 `synchronized` 方法占用的锁是当前类的锁，而访问非静态 `synchronized` 方法占用的锁是当前实例对象锁**。

```java
synchronized static void method() {
    //业务代码
}
```

**（3）修饰代码块** ：指定加锁对象，对给定对象/类加锁。`synchronized(this|object)` 表示进入同步代码库前要获得**给定对象的锁**。`synchronized(类.class)` 表示进入同步代码前要获得 **当前 class 的锁**

```java
synchronized(this) {
    //业务代码
}
```

**总结：**

- `synchronized` 关键字加到 `static` 静态方法和 `synchronized(class)` 代码块上都是是给 Class 类上锁。
- `synchronized` 关键字加到实例方法上是给对象实例上锁。
- 尽量不要使用 `synchronized(String a)` 因为 JVM 中，字符串常量池具有缓存功能！

**synchronized 同步语句块的情况，在idea中使用javap -v class来查看字节码**

![image-20220322222258225](C:\Users\liujialiang\AppData\Roaming\Typora\typora-user-images\image-20220322222258225.png)

从上面我们可以看出：**`synchronized` 同步语句块的实现使用的是 `monitorenter` 和 `monitorexit` 指令，其中 `monitorenter` 指令指向同步代码块的开始位置，`monitorexit` 指令则指明同步代码块的结束位置。**当执行 `monitorenter` 指令时，线程试图获取锁也就是获取 **对象监视器 `monitor`** 的持有权。在 Java 虚拟机(HotSpot)中，Monitor 是基于 C++实现的，由[ObjectMonitoropen in new window](https://github.com/openjdk-mirror/jdk7u-hotspot/blob/50bdefc3afe944ca74c3093e7448d6b889cd20d1/src/share/vm/runtime/objectMonitor.cpp)实现的。每个对象中都内置了一个 `ObjectMonitor`对象。另外，`wait/notify`等方法也依赖于`monitor`对象，这就是为什么只有在同步的块或者方法中才能调用`wait/notify`等方法，否则会抛出`java.lang.IllegalMonitorStateException`的异常的原因。



**synchronized 修饰方法的的情况，在idea中使用javap -v class来查看字节码**

![image-20220322222045600](C:\Users\liujialiang\AppData\Roaming\Typora\typora-user-images\image-20220322222045600.png)

`synchronized` 修饰的方法并没有 `monitorenter` 指令和 `monitorexit` 指令，取得代之的确实是 `ACC_SYNCHRONIZED` 标识，该标识指明了该方法是一个同步方法。JVM 通过该 `ACC_SYNCHRONIZED` 访问标志来辨别一个方法是否声明为同步方法，从而执行相应的同步调用。`synchronized` 同步语句块的实现使用的是 `monitorenter` 和 `monitorexit` 指令，其中 `monitorenter` 指令指向同步代码块的开始位置，`monitorexit` 指令则指明同步代码块的结束位置。`synchronized` 修饰的方法并没有 `monitorenter` 指令和 `monitorexit` 指令，取得代之的确实是 `ACC_SYNCHRONIZED` 标识，该标识指明了该方法是一个同步方法。**不过两者的本质都是对对象监视器 monitor 的获取。感兴趣的可以去了解Java对象的结构。**



- 信号量Semaphore
- CountDownLatch
- CyclicBarrier
- ReadWriteLock
- AQS
- CAS