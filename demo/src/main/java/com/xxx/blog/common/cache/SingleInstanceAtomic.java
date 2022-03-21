package com.xxx.blog.common.cache;

import java.util.concurrent.atomic.AtomicInteger;

public class SingleInstanceAtomic {
    private static volatile SingleInstanceAtomic single = null;
    private static AtomicInteger num = new AtomicInteger(255555);

    private SingleInstanceAtomic(){
    }

    public static int getNum(){
        int andDecrement = num.getAndDecrement();
        return andDecrement;
    }

    public static SingleInstanceAtomic getInstance(){
        if(single == null){
            synchronized (SingleInstanceAtomic.class){
                if(single == null){
                    single = new SingleInstanceAtomic();
                }
            }
        }
        return single;
    }

}
