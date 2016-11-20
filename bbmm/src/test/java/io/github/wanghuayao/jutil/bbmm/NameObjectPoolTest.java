package io.github.wanghuayao.jutil.bbmm;

import io.github.wanghuayao.jutil.bbmm.NameMultiObjectPool;
import io.github.wanghuayao.jutil.bbmm.NameObjectFactory;
import io.github.wanghuayao.jutil.bbmm.Poolable;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * NameObjectPoolTest
 * 
 * @author wanghuayao
 */
public class NameObjectPoolTest {
    NameMultiObjectPool<String, String> kop;


    @Before
    public void before() {
        kop = new NameMultiObjectPool<>(new StringObjectFactory());
    }


    @After
    public void after() {
        kop.shutdown();
    }


    @Test
    public void test() {

        Poolable<String, String> aaa1 = kop.borrow("aaa");
        Poolable<String, String> aaa2 = kop.borrow("aaa");
        Assert.assertFalse(aaa1.get().equals(aaa2.get()));

        aaa1.release();
        Poolable<String, String> obj3 = kop.borrow("aaa");
        Assert.assertEquals(aaa1.get(), obj3.get());
        Assert.assertSame(aaa1, obj3);

    }

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
}
