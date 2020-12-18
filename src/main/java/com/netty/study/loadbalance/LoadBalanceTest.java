package com.netty.study.loadbalance;

import com.netty.study.loadbalance.loadbalance.Invoker;
import com.netty.study.loadbalance.loadbalance.RandomLoadBalance;
import com.netty.study.loadbalance.loadbalance.SimpleRoundRobinLoadBalance;
import com.netty.study.loadbalance.loadbalance.SmoothWeightedRoundRobinLoadBalance;
import org.junit.jupiter.api.Test;

import java.sql.Time;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * @author WangChen
 * @since 2020-12-11 10:27
 **/
public class LoadBalanceTest {

    @Test
    public void test() throws Exception {

        List<Invoker> serverInvokers = Arrays.asList(
                new ServerInvoker(5, "权重5"),
                new ServerInvoker(3, "权重3"),
                new ServerInvoker(2, "权重2")
        );



//        RandomLoadBalance randomLoadBalance = new RandomLoadBalance();
//
//        int weight5=0;
//        int weight3=0;
//        int weight2=0;
//
//        for (int i = 0; i < 100 ; i++) {
//            int weight = randomLoadBalance.select(serverInvokers).getWeight();
//            if (5 == weight){
//                weight5++;
//            } else if (3 == weight){
//                weight3++;
//            } else if (2 == weight){
//                weight2++;
//            }
//        }
//
//        System.out.println("weight5=" + weight5 + "  " + "weight3=" + weight3 + "  " + "wight2=" + weight2);


        SmoothWeightedRoundRobinLoadBalance smoothWeightedRoundRobinLoadBalance = new SmoothWeightedRoundRobinLoadBalance();


//        for (int i = 0; i < 10 ; i++) {
//            System.out.println(smoothWeightedRoundRobinLoadBalance.select(serverInvokers));
//        }

//        SimpleRoundRobinLoadBalance simpleRoundRobinLoadBalance = new SimpleRoundRobinLoadBalance();
//
//        for (int i = 0; i < 9 ; i++) {
//            System.out.println(simpleRoundRobinLoadBalance.select(serverInvokers));
//        }

        new Thread(() -> {
            try {
                System.out.println(smoothWeightedRoundRobinLoadBalance.select(serverInvokers));
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        new Thread(() -> {
            try {
                System.out.println(smoothWeightedRoundRobinLoadBalance.select(serverInvokers));
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        new Thread(() -> {
            try {
                System.out.println(smoothWeightedRoundRobinLoadBalance.select(serverInvokers));
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        new Thread(() -> {
            try {
                System.out.println(smoothWeightedRoundRobinLoadBalance.select(serverInvokers));
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        TimeUnit.SECONDS.sleep(300);


    }

    public static void main(String[] args) throws InterruptedException {

        List<Invoker> serverInvokers = Arrays.asList(
                new ServerInvoker(5, "权重5"),
                new ServerInvoker(3, "权重3"),
                new ServerInvoker(2, "权重2")
        );


        SmoothWeightedRoundRobinLoadBalance smoothWeightedRoundRobinLoadBalance = new SmoothWeightedRoundRobinLoadBalance();

        RandomLoadBalance randomLoadBalance = new RandomLoadBalance();

        ExecutorService pool = Executors.newFixedThreadPool(30);


//        for (int i = 0; i < 10 ; i++) {
//            System.out.println(smoothWeightedRoundRobinLoadBalance.select(serverInvokers));
//        }

//        SimpleRoundRobinLoadBalance simpleRoundRobinLoadBalance = new SimpleRoundRobinLoadBalance();
//
//        for (int i = 0; i < 9 ; i++) {
//            System.out.println(simpleRoundRobinLoadBalance.select(serverInvokers));
//        }

        for (int i = 0; i < 30 ; i++) {
            pool.submit(() -> {
                try {
                    System.out.println(randomLoadBalance.select(serverInvokers));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }

    }
}
