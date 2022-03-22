package com.che300.example.juc;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

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
            //业务，判断 -> 执行 -> 通知
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
            //业务，判断 -> 执行 -> 通知
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
