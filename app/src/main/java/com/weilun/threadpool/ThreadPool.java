package com.weilun.threadpool;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by 胡伟强 on 2015/12/14.
 */
public class ThreadPool {
    private static ExecutorService threadPoolhreadPool= Executors.newCachedThreadPool();

    public static ExecutorService getThreadPool(){
        return threadPoolhreadPool;
    }
}
