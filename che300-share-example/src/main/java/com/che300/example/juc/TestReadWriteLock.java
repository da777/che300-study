package com.che300.example.juc;


import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class TestReadWriteLock {

    public static void main(String[] args) {
        MyCache myCache = new MyCache();

        //写入
        for (int i = 1; i <= 10; i++) {
            final int temp = i;
            new Thread(() -> myCache.put(temp + "", temp + ""), "线程:" + i).start();
        }

        //读取
        for (int i = 1; i <= 10; i++) {
            final int temp = i;
            new Thread(() -> myCache.get(temp + ""), "线程:" + i).start();
        }
    }


}

class MyCache {
    private volatile Map<String, Object> cache = new HashMap<>();
    private ReadWriteLock readWriteLock = new ReentrantReadWriteLock();

    public void put(String key, Object value) {
        readWriteLock.writeLock().lock();
        try {
            System.out.println(Thread.currentThread().getName() + "写入:" + key);
            cache.put(key, value);
            System.out.println(Thread.currentThread().getName() + "写入OK");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            readWriteLock.writeLock().unlock();
        }
    }


    public void get(String key) {
        readWriteLock.readLock().lock();
        try {
            System.out.println(Thread.currentThread().getName() + "读取:" + key);
            Object value = cache.get(key);
            System.out.println(Thread.currentThread().getName() + "读取OK");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            readWriteLock.readLock().unlock();
        }
    }
}
