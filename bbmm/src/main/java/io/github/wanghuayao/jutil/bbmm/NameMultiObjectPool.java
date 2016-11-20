package io.github.wanghuayao.jutil.bbmm;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Map&lt;NAME,List&lt;OBJ&gt;&gt; liked name Objects pool
 * 
 * @author wanghuayao
 * @param <NAME>
 * @param <OBJ>
 */
public class NameMultiObjectPool<NAME, OBJ> {

    private final static Logger                    LOG                            = LoggerFactory
                                                                                          .getLogger(NameMultiObjectPool.class);

    private final static int                       CFG_RETRY_TIMES                = 3;
    private final static long                      CFG_DEFAULT_TIMEOUT            = 3000;
    private final static TimeUnit                  CFG_DEFAULT_TIMEOUT_TIMEUNIT   = TimeUnit.MILLISECONDS;
    private final static long                      CFG_MAX_IDLE_MILLIS_FOR_REMOVE = TimeUnit.MINUTES.toMillis(11);

    private final static long                      SAFE_IDLE_TIME_INMILLIS        = TimeUnit.MINUTES.toMillis(10);

    // TODO : pool的大小限制还没有处理
    private final static long                      CFG_MAX_USED_TIME              = TimeUnit.MINUTES.toMillis(150);

    private final static int                       CFG_MAXITEMSIZE                = Integer.MAX_VALUE;

    private final Map<NAME, ObjectList<NAME, OBJ>> poolStore                      = new ConcurrentHashMap<>();
    private final NameObjectFactory<NAME, OBJ>     factory;
    private final PoolWatcher<NAME, OBJ>           poolWacther;

    private boolean                                isShutdown                     = false;


    public NameMultiObjectPool(NameObjectFactory<NAME, OBJ> factory) {
        this.factory = factory;

        // 初始化并启动检查线程
        poolWacther = new PoolWatcher<>(poolStore, factory);
        poolWacther.setName("KeyObjectPool_Keeper");
        poolWacther.start();
    }


    public Poolable<NAME, OBJ> borrow(NAME key) {
        return borrow(key, CFG_DEFAULT_TIMEOUT, CFG_DEFAULT_TIMEOUT_TIMEUNIT);
    }


    public Poolable<NAME, OBJ> borrow(NAME key, long timeout, TimeUnit timeUnit) {
        if (isShutdown) {
            throw new RuntimeException("this pool has shutdown.");
        }
        ObjectList<NAME, OBJ> lv = getAndSetAccessTime(key);
        if (lv == null) {
            synchronized (poolStore) {
                if (!poolStore.containsKey(key)) {
                    lv = new ObjectList<NAME, OBJ>();
                    poolStore.put(key, lv);
                } else {
                    lv = getAndSetAccessTime(key);
                }
            }
        }
        return getFromList(key, lv, timeUnit.toMillis(timeout));
    }


    public void returnAndDistoryObject(Poolable<NAME, OBJ> object) {
        NAME key = object.key();
        ObjectList<NAME, OBJ> lv = getAndSetAccessTime(key);
        if (!lv.inuse.remove(object)) {
            lv.idel.remove(object);
        }
        this.factory.destroy(object.get());
    }


    public void returnObject(Poolable<NAME, OBJ> object) {
        LOG.info("**return hiveconnection [{}]**", object.get().hashCode());
        NAME key = object.key();
        ObjectList<NAME, OBJ> lv = getAndSetAccessTime(key);
        if (lv.inuse.remove(object)) {
            object.beginIdle();
            lv.idel.add(object);
        } else {
            lv.idel.remove(object);
            this.factory.destroy(object.get());
        }
    }


    public void shutdown() {
        // stop watcher
        poolWacther.shutdown();
        poolWacther.interrupt();
        try {
            poolWacther.join(PoolWatcher.SLEEP_TIME);
        } catch (InterruptedException e) {
            // omit
        }

        // shutdown
        for (NAME key : poolStore.keySet()) {
            ObjectList<NAME, OBJ> ol = getAndSetAccessTime(key);
            while (ol.idel.size() > 0) {
                Poolable<NAME, OBJ> po = ol.idel.remove(0);
                factory.destroy(po.get());
            }
            while (ol.inuse.size() > 0) {
                Poolable<NAME, OBJ> po = ol.inuse.remove(0);
                factory.destroy(po.get());
            }
        }
    }


    private Poolable<NAME, OBJ> getFromList(NAME key, ObjectList<NAME, OBJ> ol, long millis) {
        long sleepMillis = millis / CFG_RETRY_TIMES;
        int times = 0;
        while (times < CFG_RETRY_TIMES) {
            synchronized (ol) {
                Poolable<NAME, OBJ> result = null;
                if (ol.idel.size() > 0) {
                    result = ol.idel.remove(0);
                    // 如果无效则，移除
                    if (!factory.valid(result.get(), result.idleFrom(), SAFE_IDLE_TIME_INMILLIS)) {
                        factory.destroy(result.get());
                        continue;
                    }
                } else if (ol.inuse.size() < CFG_MAXITEMSIZE) {
                    result = new Poolable<NAME, OBJ>(key, factory.create(key), this);
                }

                if (result != null) {
                    result.beginUse();
                    ol.inuse.add(result);

                    LOG.info("**use hiveconnection [{}]**", result.get().hashCode());
                    return result;
                }
            }

            try {
                Thread.sleep(sleepMillis);
                // 如果其他线程return了object，那么通知一下，终止睡觉
            } catch (InterruptedException e) {
                // omit
            }
            times--;
        }
        // no usage able Object
        throw new RuntimeException("no use able object");
    }

    private static class ObjectList<NAME, OBJ> {
        List<Poolable<NAME, OBJ>> inuse          = new ArrayList<>();
        List<Poolable<NAME, OBJ>> idel           = new ArrayList<>();
        long                      lastAccessTime = System.currentTimeMillis();
    }


    private ObjectList<NAME, OBJ> getAndSetAccessTime(NAME key) {

        ObjectList<NAME, OBJ> result = poolStore.get(key);
        if (result != null) {
            result.lastAccessTime = System.currentTimeMillis();
        }
        return result;
    }

    public static class PoolWatcher<NAME, OBJ> extends Thread {

        protected final static long                    SLEEP_TIME = 90000;

        private final Map<NAME, ObjectList<NAME, OBJ>> poolStore;
        private final NameObjectFactory<NAME, OBJ>     factory;


        protected PoolWatcher(Map<NAME, ObjectList<NAME, OBJ>> poolStore, NameObjectFactory<NAME, OBJ> factory) {
            this.poolStore = poolStore;
            this.factory = factory;
        }

        private boolean running = true;


        protected void shutdown() {
            running = false;
        }


        @Override
        public void run() {
            // clean pool
            while (running) {
                long beforeForRemove = System.currentTimeMillis() - CFG_MAX_IDLE_MILLIS_FOR_REMOVE;
                LOG.debug("Start do house work...");

                Iterator<NAME> keys = poolStore.keySet().iterator();
                while (keys.hasNext()) {
                    NAME key = keys.next();
                    LOG.debug(" NAME:{}", key);
                    ObjectList<NAME, OBJ> ol = poolStore.get(key);
                    if (ol.idel.isEmpty() && ol.inuse.isEmpty() && ol.lastAccessTime < beforeForRemove) {
                        synchronized (poolStore) {
                            if (ol.lastAccessTime < beforeForRemove) {
                                LOG.debug("   > remove because this is empty.");
                                keys.remove();
                            }
                        }
                        continue;
                    }

                    LOG.debug("   > inuse count : {}", ol.inuse.size());
                    for (Poolable<NAME, OBJ> po : ol.inuse) {
                        LOG.debug("     >> last use time {}, used times: {}", po.lastAccessTime(), po.useTimes());
                    }
                    synchronized (ol) {
                        LOG.debug("   > idle count : {}", ol.idel.size());
                        for (int i = 0; i < ol.idel.size(); i++) {
                            Poolable<NAME, OBJ> po = ol.idel.get(i);
                            LOG.debug("     >> idel from {}, used times: {}", po.idleFrom(), po.useTimes());
                            if (po.idleFrom() < beforeForRemove) {
                                LOG.debug("       >>> removed");
                                // remove
                                this.factory.destroy(po.get());
                                ol.idel.remove(i);
                            }
                        }
                    }
                }
                try {
                    Thread.sleep(SLEEP_TIME);
                } catch (InterruptedException e) {
                    // omit
                }
            }
        }
    }
}
