package com.example.demo.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.*;

/**
 * @author xzh
 * @since 2021/7/16 11:07 上午
 */
@RequestMapping("/test/async")
@RestController
public class TestAsync {
    @GetMapping("/x2")
    public String x2() throws InterruptedException, ExecutionException {
        ExecutorService executorService = Executors.newFixedThreadPool(2);
        System.out.println(executorService.hashCode());
        System.out.println("--------主线程开始"+Thread.currentThread().getName());
        // 创建一个callable，2 秒后 返回一个 string
        Callable<String> callable=()->{
            System.out.println("异步线程开始"+Thread.currentThread().getName());
            Thread.sleep(2000);
            System.out.println("异步线程结束"+Thread.currentThread().getName());
            return "异步线程结果";
        };
        System.out.println("--------主线程结束"+Thread.currentThread().getName());

        System.out.println("提交任务之前");
        Future<String> submit = executorService.submit(callable);
        System.out.println("提交任务之后");
        String s = submit.get();
        System.out.println("获取返回值："+s);
        return s;
    }
    /**
     * 每两秒输出一个随机数，
     * 原因就是get方法是阻塞的啊，
     * 要执行完才会得到结果的
     * */
    @GetMapping("/x3")
    public void x3() throws ExecutionException, InterruptedException {
        /** 线程池的核心线程数 3 */
        ExecutorService service = Executors.newFixedThreadPool(3);
        for (int i = 1; i <= 100; i++) {
            //提交任务之后，返回结果是Future，此时线程还没有结束，等待任务的返回值呢
            Future<String> result = service.submit(new TestThread());
            //输出返回值
            System.out.println(result.get());
        }
        service.shutdown();
    }

    /**
     *100个任务就会全都分配下去，
     * 进入到队列中，然后核心线程数为3个，所以就会3个3个执行线程，所以结果也是三个一输出，三个一输出。
     * 假如你的核心线程数要是比任务多，那就一次性全都输出啦（2秒中同时执行任务！）
     */
    @GetMapping("/x4")
    public void x4() throws ExecutionException, InterruptedException {
        //线程池的核心线程数：3个
        ExecutorService es = Executors.newFixedThreadPool(3);
        //List集合用来存储Future
        List<Future> list=new ArrayList<Future>();
        for (int i = 1; i <=100; i++) {
            //提交任务之后，返回结果是Future，此时线程还没有结束，等待任务的返回值呢
            Future<String> s = es.submit(new TestThread());
            list.add(s);
        }
        System.out.println("asf");
        for (Future f : list) {
            System.out.println(f.get());
        }
        es.shutdown();
    }
}
class TestThread implements Callable<String>{
    @Override
    public String call() throws Exception {
        Thread.sleep(1000);
        int a = new Random().nextInt(10);
        return Thread.currentThread().getName()+"==="+a;
    }
}
