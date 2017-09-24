package ibeacondata.net;

import java.util.concurrent.AbstractExecutorService;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by LK on 2016/10/9.
 */
public class DefaultThreadPool {
    //阻塞队列最大
    public static final int BLOCKING_QUEUE_SIZE = 20;
    public static final int THREAD_POOL_MAX_SIZE =10;

    public static final int THREAD_POOL_SIZE = 5;
    /**
     * 缓冲BaseRequest任务队列
     * ArrayBlockingQueue, （基于数组的并发阻塞队列） ，先进先出
     */
    public static ArrayBlockingQueue<Runnable> blockingQueue = new ArrayBlockingQueue<Runnable>(
            DefaultThreadPool.BLOCKING_QUEUE_SIZE);
    private volatile static DefaultThreadPool instance = null;
    /**
     * 线程池
     */
    public static AbstractExecutorService pool = new ThreadPoolExecutor(
            DefaultThreadPool.THREAD_POOL_SIZE,
            DefaultThreadPool.THREAD_POOL_MAX_SIZE,
            15L,
            TimeUnit.SECONDS,
            DefaultThreadPool.blockingQueue,
            new ThreadPoolExecutor.DiscardPolicy()
    );
    public static DefaultThreadPool getInstance(){
        if (DefaultThreadPool.instance == null){
            synchronized (DefaultThreadPool.class){
                if(DefaultThreadPool.instance == null){
                    DefaultThreadPool.instance = new DefaultThreadPool();
                }
            }
        }
        return DefaultThreadPool.instance;
    }

    public void removeAllTask(){
        DefaultThreadPool.blockingQueue.clear();
    }

    public void removeTaskFromQuene(final Object object){
        DefaultThreadPool.blockingQueue.remove(object);
    }

    /**
     * 关闭线程池，并等待任务执行完成，不接受新任务
     */
    public void shutdown(){
        if (pool != null){
            pool.shutdown();
        }
    }

    /**
     * 立即关闭线程池，并挂起所有正在执行的任务，不接受新任务
     */
    public void shutdownNow(){
        if (pool != null){
            pool.shutdownNow();
            try {
                //设置时间极短，强制关闭所有任务
                pool.awaitTermination(1,TimeUnit.MICROSECONDS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 执行任务
     * @param r
     */
    public void execute(final Runnable r){
        if (r !=null){
            try {
                DefaultThreadPool.pool.execute(r);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }
}
