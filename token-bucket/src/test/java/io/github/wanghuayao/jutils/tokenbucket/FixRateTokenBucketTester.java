package io.github.wanghuayao.jutils.tokenbucket;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import io.github.wanghuayao.jutil.utils.ThreadUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import org.junit.Test;

/**
 * TokenBucketImpl的测试类
 * 
 * @author wanghuayao
 */
public class FixRateTokenBucketTester {

    @Test
    public void test_001_init() {
        TokenBucket tb = new FixRateTokenBucket(10, 5, 1, TimeUnit.SECONDS);
        assertFalse(tb.tryConsume(1));
    }


    @Test
    public void test_002_createOne() {
        TokenBucket tb = new FixRateTokenBucket(10, 5, 1, TimeUnit.SECONDS);
        assertFalse(tb.tryConsume(1));
        ThreadUtils.sleepAndCachedInterruptedException(1001, TimeUnit.MILLISECONDS);
        assertTrue(tb.tryConsume(5));
        assertFalse(tb.tryConsume(1));
        ThreadUtils.sleepAndCachedInterruptedException(2001, TimeUnit.MILLISECONDS);
        assertTrue(tb.tryConsume(5));
        assertTrue(tb.tryConsume(5));
        assertFalse(tb.tryConsume(1));
    }

    final static Random R = new Random();


    @Test
    public void test_003_create_a_lot_of() throws InterruptedException {
        long startNanos = System.nanoTime();
        final TokenBucket tb = new FixRateTokenBucket(50, 25, 1, TimeUnit.SECONDS);
        final AtomicLong csize = new AtomicLong();
        List<Thread> ts = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            Thread t = new Thread() {
                public void run() {
                    for (int i = 0; i < 4; i++) {
                        int size = R.nextInt(49) + 1;
                        if (i % 2 == 0) {
                            tb.consume(size);
                            csize.getAndAdd(size);
                        } else {
                            if (tb.tryConsume(size)) {
                                csize.getAndAdd(size);
                            }
                        }
                    }
                };
            };
            t.setName(String.format("T-%3d", i));
            t.start();
            ts.add(t);
        }
        for (Thread t : ts) {
            t.join();
        }

        double percent = (double) csize.get() / (double) TimeUnit.NANOSECONDS.toSeconds(System.nanoTime() - startNanos);

        System.out.println(percent);
        assertTrue("平均生成速率要小于25", percent < 25D);
        System.out.println(percent);

        long interval = TimeUnit.SECONDS.toNanos(1);
        long maxToken = (((System.nanoTime() - startNanos) / interval) * 25);

        System.out.println(maxToken);
        System.out.println(csize.get());

        assertTrue("取得的数量要小于应该生成的个数", maxToken >= csize.get());
    }


    @Test
    public void test_004_create_a_lot_of_try() throws InterruptedException {
        long startNanos = System.nanoTime();
        final TokenBucket tb = new FixRateTokenBucket(50, 25, 1, TimeUnit.SECONDS);
        final AtomicLong csize = new AtomicLong();
        List<Thread> ts = new ArrayList<>();
        for (int i = 0; i < 50; i++) {
            Thread t = new Thread() {
                public void run() {
                    for (int i = 0; i < 4; i++) {
                        int size = R.nextInt(30) + 1;

                        if (tb.tryConsume(size)) {
                            csize.getAndAdd(size);
                        }
                    }
                };
            };
            t.setName(String.format("T-%3d", i));
            t.start();
            ts.add(t);
        }
        for (Thread t : ts) {
            t.join();
        }

        long span = System.nanoTime() - startNanos;
        assertTrue("执行时间要小于等于1秒", TimeUnit.NANOSECONDS.toSeconds(span) <= 1);
        assertTrue("取得的数量要小于等于1秒内生成的token数", csize.get() <= 25);
    }


    @Test(expected = RuntimeException.class)
    public void test_005_withException() throws InterruptedException {
        final TokenBucket tb = new FixRateTokenBucket(50, 25, 1, TimeUnit.SECONDS);
        tb.consume();
        tb.consume(51);
    }


    @Test
    public void test_008_resize() throws InterruptedException {
        long startNanos = System.nanoTime();
        final TokenBucket tb = new FixRateTokenBucket(50, 25, 1, TimeUnit.SECONDS);
        tb.consume(11);
        tb.consume(20);

        tb.resize(200, 100);

        final AtomicLong csize = new AtomicLong();
        List<Thread> ts = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            Thread t = new Thread() {
                public void run() {
                    for (int i = 0; i < 4; i++) {
                        int size = R.nextInt(150) + 1;
                        if (i % 2 == 0) {
                            tb.consume(size);
                            csize.getAndAdd(size);
                        } else {
                            if (tb.tryConsume(size)) {
                                csize.getAndAdd(size);
                            }
                        }
                    }
                };
            };
            t.setName(String.format("T-%3d", i));
            t.start();
            ts.add(t);
        }
        for (Thread t : ts) {
            t.join();
        }

        double percent = (double) csize.get() / (double) TimeUnit.NANOSECONDS.toSeconds(System.nanoTime() - startNanos);

        System.out.println(percent);
        assertTrue("平均生成速率要小于100", percent < 100D);
        assertTrue("平均生成速率要大于50", percent > 50D);

        long interval = TimeUnit.SECONDS.toNanos(1);
        long maxToken = (((System.nanoTime() - startNanos) / interval) * 100);

        System.out.println(maxToken);
        System.out.println(csize.get());

        assertTrue("取得的数量要小于应该生成的个数", maxToken >= csize.get());
    }


    @Test(expected = RuntimeException.class)
    public void test_009_小于1_1() {
        new FixRateTokenBucket(0, 1, 1, TimeUnit.SECONDS);
    }


    @Test(expected = RuntimeException.class)
    public void test_009_小于1_2() {
        new FixRateTokenBucket(1, 0, 1, TimeUnit.SECONDS);
    }


    @Test(expected = RuntimeException.class)
    public void test_009_小于1_3() {
        new FixRateTokenBucket(1, 1, 0, TimeUnit.SECONDS);
    }


    @Test(expected = RuntimeException.class)
    public void test_010_容量太小了() {
        new FixRateTokenBucket(1, 2, 0, TimeUnit.SECONDS);
    }
}
