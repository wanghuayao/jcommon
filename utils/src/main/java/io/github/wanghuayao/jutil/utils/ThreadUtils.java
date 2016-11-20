package io.github.wanghuayao.jutil.utils;

import java.util.concurrent.TimeUnit;

/**
 * ThreadUtils
 * 
 * @author wanghuayao
 */
public class ThreadUtils {

    /**
     * Sleep and cached InterruptedException.
     * 
     * @param millis
     * @return true:normal end, false: Interrupted
     */
    public final static boolean sleepAndCachedInterruptedException(long sleepFor, TimeUnit unit) {
        if (sleepFor <= 0) {
            return true;
        }
        try {
            unit.sleep(sleepFor);
            return true;
        } catch (InterruptedException e) {
            // omit
            return false;
        }
    }


    /**
     * endless sleep until interrupted.
     * 
     * @param millis
     * @return true:normal end, false: Interrupted
     */
    public final static void endlessSleep() {

        try {
            Thread.sleep(Long.MAX_VALUE);
        } catch (InterruptedException e) {
            // omit
        }
    }


    /**
     * sleep until timeout.
     * 
     * @param nanos
     */
    public final static void deepSleep(long sleepFor, TimeUnit unit) {
        if (sleepFor < 0) {
            throw new IllegalArgumentException("sleepFor can't be minus.");
        }
        long startTimeInNanos = System.nanoTime();
        long leftNanos = unit.toNanos(sleepFor);
        boolean isInterrupted = false;
        while (leftNanos > 0) {
            try {
                TimeUnit.NANOSECONDS.sleep(leftNanos);
                leftNanos = 0;
            } catch (InterruptedException e) {
                isInterrupted = true;
                leftNanos -= (System.nanoTime() - startTimeInNanos);
            }
        }

        if (isInterrupted) {
            Thread.currentThread().interrupt();
        }
    }
}
