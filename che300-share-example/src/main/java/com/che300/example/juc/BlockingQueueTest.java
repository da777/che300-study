package com.che300.example.juc;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

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
