package com.haozan.caipiao.taskbasic;

/**
 * 线程操作
 * 
 * @author peter_wang
 * @create-time 2013-10-23 下午3:08:54
 */
public abstract class Task
    implements Runnable {

    @Override
    public void run() {
        try {
            runTask();
        }
        catch (Exception e) {

        }
    }

    /**
     * 执行线程，外面进行了try catch防止出错
     */
    public abstract void runTask();
}
