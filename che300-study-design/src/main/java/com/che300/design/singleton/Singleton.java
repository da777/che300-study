package com.che300.design.singleton;


/**
 * 单例模式---懒汉式
 *
 * @author jlliu
 */
public class Singleton {
    private static Singleton instance = null;

    private Singleton() {
    }
    // 双重校验锁
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
