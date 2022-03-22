package com.che300.example.juc;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author jlliu
 */
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
