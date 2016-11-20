package io.github.wanghuayao.jutils.tokenbucket;

import io.github.wanghuayao.jutil.utils.ThreadUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.ReentrantLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * TokenBucket 实现类(固定周期)
 * 
 * @author wanghuayao
 */
public class FixRateTokenBucket implements TokenBucket {
    private final static Logger LOG            = LoggerFactory.getLogger(FixRateTokenBucket.class);
    /**
     * 桶的大小
     */
    private AtomicLong          size           = new AtomicLong();

    private final ReentrantLock locker         = new ReentrantLock();

    /**
     * 时间间隔（纳秒）
     */
    private final long          intervalInNanos;
    /**
     * 时间间隔（毫秒）
     */
    private final long          timeWaitForTokens;
    /**
     * 每个时间周期生成的token数
     */
    private long                tokensPerPeriod;
    /**
     * 此容器容量
     */
    private long                capacity;

    /**
     * 追后一次更新桶大小的时间
     */
    private long                lastRefillTime = 0;

    /**
     * 取得tokens的进程
     */
    private List<Thread>        threads        = Collections.synchronizedList(new ArrayList<Thread>());


    /**
     * 构造函数
     * 
     * @param capacity 容量
     * @param tokensPerPeriod 每个时间周期生成Token的个数
     * @param interval 间隔时间
     * @param timeUnit 间隔时间的单位
     */
    public FixRateTokenBucket(long capacity, long tokensPerPeriod, long interval, TimeUnit timeUnit) {

        if (interval < 1 || !this.resize(capacity, tokensPerPeriod)) {
            throw new RuntimeException("参数不正确，要求：capacity >= tokensPerPeriod >= 1, interval >= 1");
        }

        this.intervalInNanos = timeUnit.toNanos(interval);
        // 最低1毫秒
        this.timeWaitForTokens = Math.max(timeUnit.toMillis(interval * 2), 1);

        this.lastRefillTime = System.nanoTime();
        size.set(0);
        createTokenThread.setDaemon(true);
        createTokenThread.start();
    }


    @Override
    public boolean resize(long capacity, long tokensPerPeriod) {
        if (tokensPerPeriod < 1) {
            return false;
        }
        if (capacity < tokensPerPeriod) {
            return false;
        }
        this.capacity = capacity;
        this.tokensPerPeriod = tokensPerPeriod;

        long curSize = size.get();
        while (curSize > capacity) {
            if (size.compareAndSet(curSize, capacity)) {
                break;
            }
            curSize = size.get();
        }
        return true;
    }


    @Override
    public void consume() {
    }


    @Override
    public void consume(long tockenCount) {
        while (!tryConsume(tockenCount)) {
            Thread currentThread = Thread.currentThread();
            threads.add(currentThread);
            createTokenThread.interrupt();
            if (ThreadUtils.sleepAndCachedInterruptedException(timeWaitForTokens, TimeUnit.MILLISECONDS)) {
                LOG.debug("自己睡醒了。");
            }
        }
    }


    /**
     * 尝试取得consume大小的
     */
    @Override
    public boolean tryConsume(long tockenCount) {
        if (tockenCount <= 0) {
            return true;
        }
        if (tockenCount <= this.capacity) {
            do {
                long currentSize = size.get();
                long postSize = currentSize - tockenCount;
                if (postSize >= 0) {
                    if (size.compareAndSet(currentSize, postSize)) {
                        return true;
                    } else {
                        Thread.yield();
                    }
                } else {
                    // 木有的话，生成一下看看
                    if (!createToken(true)) {
                        break;
                    }
                }
            } while (true);
            return false;
        } else {
            throw new RuntimeException("the request size[" + tockenCount + "] is greater then bucket capacity["
                    + capacity + "]");
        }
    }

    /**
     * 生成token thread
     */
    //@formatter:off
    private Thread createTokenThread = new Thread() {
           public void run() {
               this.setName("tockent-bucket");
               // sleep
               ThreadUtils.endlessSleep();
               while (true) {
                   // 生成token,可以等待
                   createToken(false);
                   // 成功生成新的token后通知各线程
                    while (!threads.isEmpty()) {
                        Thread t = threads.remove(0);
                        t.interrupt();
                   }
                   ThreadUtils.endlessSleep();
               }
           };
       };
       //@formatter:on

    // 生成token
    private boolean createToken(boolean noWait) {
        if (!locker.tryLock()) {
            // 没有锁上
            if (noWait) {
                // 不可等的
                return false;
            } else {
                locker.lock();
            }
        }
        try {
            long dueryFromLast = System.nanoTime() - lastRefillTime;
            long numPeriods;
            if (dueryFromLast < intervalInNanos) {
                if (noWait) {
                    return false;
                }
                // 深睡眠
                ThreadUtils.deepSleep(intervalInNanos - dueryFromLast, TimeUnit.NANOSECONDS);
                // 只经历了一个周期
                numPeriods = 1;
            } else {
                // 从上次调用到现在为止经过了几个时间周期
                numPeriods = dueryFromLast / intervalInNanos;
            }

            // 本次生成到几点为止的时间
            lastRefillTime += numPeriods * intervalInNanos;

            long newSize = Math.min(numPeriods * tokensPerPeriod + size.get(), capacity);
            size.set(newSize);
            return true;
        } finally {
            locker.unlock();
        }
    }
}
