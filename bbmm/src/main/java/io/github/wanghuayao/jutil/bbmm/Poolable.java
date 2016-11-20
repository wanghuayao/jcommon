package io.github.wanghuayao.jutil.bbmm;

public class Poolable<NAME, OBJ> {
    private final NameMultiObjectPool<NAME, OBJ> pool;
    private final NAME                     key;
    private final OBJ                      o;
    private long                           lastAccessTime;
    private long                           idleFrom = Long.MAX_VALUE;
    private int                            useTimes = 0;


    protected Poolable(NAME key, OBJ o, NameMultiObjectPool<NAME, OBJ> pool) {
        this.key = key;
        this.o = o;
        this.pool = pool;
    }


    public NAME key() {
        return key;
    }


    public OBJ get() {
        return o;
    }


    public void releaseAndDistory() {
        pool.returnAndDistoryObject(this);
    }


    public void release() {
        pool.returnObject(this);
    }


    protected void beginUse() {
        useTimes++;
        lastAccessTime = System.currentTimeMillis();
        idleFrom = Long.MAX_VALUE;
    }


    protected void beginIdle() {
        lastAccessTime = Long.MAX_VALUE;
        idleFrom = System.currentTimeMillis();
    }


    protected long idleFrom() {
        return idleFrom;
    }


    protected long lastAccessTime() {
        return lastAccessTime;
    }


    protected int useTimes() {
        return useTimes;
    }
}
