package com.che300.example.thread.threadpool;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author liujialiang
 */
public class TreadPool {
    public static void main(String[] args) {
        ExecutorService service = new ThreadPoolExecutor(20,
                20,
                0L,
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(),
                new NameThreadFactory(),new ThreadPoolExecutor.CallerRunsPolicy());
        for (int i = 0; i < 10; i++) {
            service.execute(() -> {
                System.out.println(Thread.currentThread().getName());
            });
        }
        service.shutdown();
    }

    static class NameThreadFactory implements ThreadFactory {
        AtomicInteger threadNum = new AtomicInteger();
        public NameThreadFactory() {}
        @Override
        public Thread newThread(Runnable r) {
            Thread thread = new Thread(r);
            threadNum.incrementAndGet();
            thread.setName("测试ThreadFactoryName-" + threadNum);
            return thread;
        }
    }
}
