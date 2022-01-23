package com.che300.example.thread.runnable;


/**
 * 实现runnable接口
 *
 * @author liujialiang
 */
public class MyTask implements Runnable {
    private String threadName;

    public MyTask(String threadName) {
        this.threadName = threadName;
    }

    public static void main(String[] args) {
        Thread t = new Thread(new MyTask("实现runnable接口的线程"));
        t.start();
    }

    @Override
    public void run() {
        System.out.println("启动线程名:" + threadName);
    }
}
