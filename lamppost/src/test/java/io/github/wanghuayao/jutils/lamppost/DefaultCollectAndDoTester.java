package io.github.wanghuayao.jutils.lamppost;

import static org.junit.Assert.assertEquals;
import io.github.wanghuayao.jutils.lamppost.GatherAndDepart.DepartCallback;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class DefaultCollectAndDoTester {

    private final static String[]          EMPTY_ARRAY = new String[] {};

    private static GatherAndDepart<String> gatherAndDepart;

    private static int                     size        = 0;


    @BeforeClass
    public static void before() {
        gatherAndDepart = new DefaultGatherAndDepart<>(new DepartCallback<String>() {
            @Override
            public void call(List<String> parameters) throws Exception {
                size += parameters.size();
                System.out.println(Arrays.toString(parameters.toArray(EMPTY_ARRAY)));
            }
        }/* call back */, 50/* list size */, 1000/* timeout */);
    }


    @Test
    public void test_001() throws Exception {
        final int THREAD_SIZE = 50;
        final int COUNT_PRE_THREAD = 50;
        Thread[] ths = new Thread[THREAD_SIZE];
        for (int i = 0; i < THREAD_SIZE; i++) {
            ths[i] = new Thread() {
                @Override
                public void run() {

                    Random r = new Random();
                    int stop = r.nextInt(COUNT_PRE_THREAD);
                    int count = 0;
                    for (int j = 0; j < COUNT_PRE_THREAD; j++) {
                        if (stop != 0 && count <= 3 && j % stop == 0) {
                            count++;
                            try {
                                Thread.sleep((int) r.nextInt(1500));
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                        gatherAndDepart.gather(this.getName() + "_" + j);
                    }
                }
            };
            ths[i].start();
        }

        for (Thread th : ths) {
            if (th.isAlive()) {
                th.join();
            }
        }

        gatherAndDepart.close();

        assertEquals(THREAD_SIZE * COUNT_PRE_THREAD, size);
    }


    @Test(expected = RuntimeException.class)
    public void test_002() throws Exception {
        gatherAndDepart.gather("exception");

    }
}
