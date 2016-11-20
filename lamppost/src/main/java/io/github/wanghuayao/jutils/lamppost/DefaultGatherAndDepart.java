package io.github.wanghuayao.jutils.lamppost;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicLong;

public class DefaultGatherAndDepart<T> implements GatherAndDepart<T> {

    private final int               cachedSize;
    private final long              cachedDelayMilli;
    private final DepartCallback<T> callback;

    private BlockingQueue<T>        cachedLogs  = new LinkedBlockingQueue<>();

    private Thread                  consumerThread;
    private boolean                 running     = true;
    private AtomicLong              timeerCount = new AtomicLong(0);


    /**
     * @param callback call back
     * @param cachedSize cached size
     * @param cachedDelayMilli delay in milliseconds
     */
    public DefaultGatherAndDepart(DepartCallback<T> callback, int cachedSize, long cachedDelayMilli) {
        this.callback = callback;
        this.cachedSize = cachedSize;
        this.cachedDelayMilli = cachedDelayMilli;
        initConsumerThread();
    }


    public void gather(T parameter) {
        if (running) {
            cachedLogs.add(parameter);
            int size = cachedLogs.size();
            if (size == cachedSize) {
                consumerThread.interrupt();
            } else if (size == 1 && timeerCount.compareAndSet(0L, 1L)) {
                new Timer().schedule(new TimerTask() {
                    public void run() {
                        timeerCount.getAndSet(0);
                        consumerThread.interrupt();
                    }
                }, cachedDelayMilli);
            }
        } else {
            throw new RuntimeException("The Gather is not alive.");
        }
    }


    @Override
    public void flash() {
        consumerThread.interrupt();
    }


    /**
     * 停止
     */
    public void close() {
        running = false;
        consumerThread.interrupt();
        try {
            consumerThread.join();
        } catch (InterruptedException e) {
            // ignore
        }
    }


    /**
     * 启动日志消费线程
     */
    private void initConsumerThread() {
        // 保存DB线程
        consumerThread = new Thread() {
            long sleepTime = Long.MAX_VALUE;


            @Override
            public void run() {
                while (running) {
                    this.startConsume();
                    try {
                        // wait next rolling time
                        Thread.sleep(sleepTime);
                    } catch (InterruptedException e) {
                        // ignore
                    }
                }
                this.startConsume();
            }


            private void startConsume() {
                while (true) {
                    List<T> items = new ArrayList<>();
                    cachedLogs.drainTo(items, cachedSize);
                    int size = items.size();
                    if (size > 0) {
                        // save to DB
                        try {
                            callback.call(items);
                        } catch (Exception e) {
                            throw new RuntimeException("Error to save cached items.", e);
                        }
                        sleepTime = cachedDelayMilli;
                    }
                    if (size != cachedSize) {
                        sleepTime = Long.MAX_VALUE;
                        break;
                    }
                }
            }
        };
        consumerThread.setName("TH-CollectAndDo-" + count.incrementAndGet());
        consumerThread.start();
    }

    private static AtomicLong count = new AtomicLong();
}
