package io.github.wanghuayao.jutils.lamppost;

import java.util.List;

/**
 * MysqlCommandExecLogService
 * 
 * @author wanghuayao
 */
public interface GatherAndDepart<T> extends AutoCloseable {

    /**
     * gather the operation
     * 
     * @param item
     */
    public void gather(T parameter);


    /**
     * flash
     */
    public void flash();

    /**
     * callback
     * 
     * @author wanghuayao
     * @param <T>
     */
    public interface DepartCallback<T> {
        void call(List<T> parameters) throws Exception;
    }
}
