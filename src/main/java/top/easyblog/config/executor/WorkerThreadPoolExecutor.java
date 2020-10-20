package top.easyblog.config.executor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.util.concurrent.*;

/**
 * 线程池
 *
 * @author ：huangxin
 * @modified ：
 * @since ：2020/02/27 08:58
 */
@Configuration
@Component
@Slf4j
public class WorkerThreadPoolExecutor {

    /**获取设备CPU个数*/
    private static final int AVAILABLE_CPU_COUNT = Runtime.getRuntime().availableProcessors();
    /**核心线程数*/
    private static final int CORE_POOL_SIZE = Math.max(2, Math.min(AVAILABLE_CPU_COUNT - 1, 4));
    /**最大线程数*/
    private static final int MAX_POOL_SIZE = AVAILABLE_CPU_COUNT * 2 + 1;
    /**工作线程空闲后，保持存活时间*/
    private static final Long KEEP_ALIVE_TIME = 30L;
    private static final TimeUnit KEEP_ALIVE_TIME_UNIT=TimeUnit.SECONDS;
    /**任务过多后，存储任务的一个阻塞队列*/
    private static final  BlockingQueue<Runnable> WORK_QUEUE = new ArrayBlockingQueue<>(100);
    /**线程池任务满载后采取的任务拒绝策略*/
    private static final RejectedExecutionHandler REJECT_HANDLER = new ThreadPoolExecutor.CallerRunsPolicy();
    /***线程名*/
    private static final ThreadLocal<String> THREAD_NAME =new ThreadLocal<>();


    public static void  setThreadName(String name){
        THREAD_NAME.set(name);
    }


    @Bean(value = "executor")
    public Executor executor() {
        DefaultThreadFactory threadFactory = setThreadFactory();
        return new ThreadPoolExecutor(CORE_POOL_SIZE, MAX_POOL_SIZE,
                KEEP_ALIVE_TIME, KEEP_ALIVE_TIME_UNIT,
                WORK_QUEUE, threadFactory, REJECT_HANDLER);
    }

    private DefaultThreadFactory setThreadFactory() {
        String threadName = THREAD_NAME.get();
        if(threadName==null||"".equals(threadName)){
            return new DefaultThreadFactory("service");
        }
        THREAD_NAME.remove();
        return new DefaultThreadFactory(threadName);
    }




}
