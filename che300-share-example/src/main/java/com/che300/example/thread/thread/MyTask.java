package com.che300.example.thread.thread;


/**
 * @author liujialiang
 */
public class MyTask extends Thread{
    private final String threadName;

    public MyTask(String name) {
        this.threadName = name;
    }

    public static void main(String[] args) {
        Thread t = new MyTask("继承Thread实现创建线程");
        t.start();
    }
    @Override
    public void run(){
        System.out.println("启动线程名:" + threadName);
    }
}
