package com.che300.example.juc;


import java.util.concurrent.TimeUnit;

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
