package com.che300.example.juc;

public class SynchronizedTest {
    public void method() {
        synchronized (this) {
            System.out.println("synchronized 代码块");
        }
    }
    public static void main(String[] args) {
        SynchronizedTest synchronizedTest = new SynchronizedTest();
        synchronizedTest.method();
    }
}
