package com.netty.study.loadbalance;

import com.netty.study.loadbalance.loadbalance.Invoker;
import com.netty.study.loadbalance.loadbalance.LoadBalance;
import com.netty.study.loadbalance.loadbalance.SmoothRoundRobinLoadBalance;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

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


        SmoothRoundRobinLoadBalance smoothRoundRobinLoadBalance = new SmoothRoundRobinLoadBalance();


        for (int i = 0; i < 10 ; i++) {
            System.out.println(smoothRoundRobinLoadBalance.select(serverInvokers));
        }


    }
}
