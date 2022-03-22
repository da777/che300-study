package com.che300.example.juc;

/**
 * @author jlliu
 */
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
