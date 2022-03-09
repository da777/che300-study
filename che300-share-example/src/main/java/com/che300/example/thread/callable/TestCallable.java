package com.che300.example.thread.callable;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.stream.Collectors;

/**
 * @author liujialiang
 */
public class TestCallable {

    private static class MyCallable implements Callable {

        @Override
        public Object call() throws Exception {
            System.out.println("callable 执行一个线程");
            return "callable 执行完成";
        }
    }

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        MyCallable myCallable = new MyCallable();
        ExecutorService service = Executors.newCachedThreadPool();
        Future future = service.submit(myCallable);
        Object o = future.get();
        System.out.println(o.toString());
        List<Integer> list = new ArrayList<>();
//        list.stream().collect(Collectors.toMap(Integer.MAX_VALUE, r -> {}));
    }
}
