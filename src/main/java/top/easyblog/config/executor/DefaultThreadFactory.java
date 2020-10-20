package top.easyblog.config.executor;

import javax.annotation.Nonnull;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 线程工厂，用于实例化一个线程，并且给线程设定一些属性，比如设置线程名、设置线程优先级等
 * @author ：huangxin
 * @modified ：
 * @since ：2020/07/29 22:53
 */
public class DefaultThreadFactory implements ThreadFactory {

    private final String namePrefix;
    private final AtomicInteger nextId = new AtomicInteger(1);

    public DefaultThreadFactory(final String namePrefix) {
        this.namePrefix = namePrefix;
    }

    @Override
    public Thread newThread(@Nonnull Runnable task) {
        String threadName=namePrefix+nextId.getAndIncrement();
        return new Thread(null, task, threadName);
    }
}
