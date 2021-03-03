package com.netty.study.file;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * @author WangChen
 * @since 2020-12-31 14:34
 **/
public class QueueTest {

    private static BlockingQueue<String> queue = new LinkedBlockingQueue<>(1024);

    public static void main(String[] args) throws InterruptedException {

        Thread thread = new Thread(() -> {
            while (true) {
                System.out.println("阻塞队列中...");
                try {
                    queue.take();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

        thread.start();

        TimeUnit.SECONDS.sleep(2);
        System.out.println("打断之前:" + thread.isInterrupted());
        thread.interrupt();
        System.out.println("打断之后:" + thread.isInterrupted());
        TimeUnit.SECONDS.sleep(100);
    }
}
