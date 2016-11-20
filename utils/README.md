# bbmm(babamama)

named multi-object pool.
NAME<sup>[1]</sup> ----- <sup>[1...n]</sup>OBJECT

# Usage
``` java

// 1、create Object Factory Class
public static class StringObjectFactory implements NameObjectFactory<String, String> {

    @Override
    public String create(String name) {
        String o = name + "-" + System.nanoTime();
        System.out.println("create : " + o);
        return o;
    }


    @Override
    public void destroy(String o) {
        System.out.println("distory : " + o);
    }


    @Override
    public boolean valid(String o, long idelFrom, long safeTimeSpan) {
        System.out.println("valid : " + o);
        return true;
    }
}

// 2、Create pool
NameMultiObjectPool<String, String> pool = new NameMultiObjectPool<>(new StringObjectFactory());

// 3、get object
Poolable<String, String> poolableObj1 = pool.borrow("name1");
String obj1 = poolableObj1.get();

// 4、return to pool
poolableObj1.release();
// or destroy it directly
poolableObj1.releaseAndDistory();

// at the last, close the pool
pool.shutdown();

```

# TODO
* pool size limit 
* setting customization

