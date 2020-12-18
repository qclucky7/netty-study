package com.netty.study.cacheline;

import org.junit.jupiter.api.Test;
import org.openjdk.jol.info.ClassLayout;
import org.openjdk.jol.info.GraphLayout;

/**
 * @author WangChen
 * @since 2020-12-18 14:25
 **/
public class JOLTest {


    /**
     * java.lang.Object object internals:
     *  OFFSET  SIZE   TYPE DESCRIPTION                VALUE
     *       0     4        (object header)            09 00 00 00 (00001001 00000000 00000000 00000000) (9)
     *       4     4        (object header)            00 00 00 00 (00000000 00000000 00000000 00000000) (0)  前两行Mark Word:包含HashCode、分代年龄、锁标志等。
     *       8     4        (object header)            e5 01 00 20 (11100101 00000001 00000000 00100000) (536871397) Klass Pointer:指向当前对象的Class对象的内存地址。
     *      12     4        (loss due to the next object alignment)  字节补齐
     * Instance size: 16 bytes
     * Space losses: 0 bytes internal + 4 bytes external = 4 bytes total
     */
    @Test
    public void myTest(){

        Object obj = new Object();

        System.out.println(ClassLayout.parseInstance(obj).toPrintable());

        synchronized (obj){
            System.out.println(ClassLayout.parseInstance(obj).toPrintable());
        }
    }

    @Test
    public void myTest2(){

        Object obj = new Object();
        //查询对象内部信息
        System.out.println(ClassLayout.parseInstance(obj).toPrintable());
        //查询对象外部信息 引用对象
        System.out.println(GraphLayout.parseInstance(obj).toPrintable());
        //对象总大小
        System.out.println(GraphLayout.parseInstance(obj).totalSize());

    }
}
