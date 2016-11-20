package io.github.wanghuayao.jutils.tokenbucket;

/**
 * TokenCrock
 * 
 * @author wanghuayao
 */
public interface TokenBucket {

    /**
     * 消费1个token
     */
    void consume();


    /**
     * 消费 {@linkplain tockenCount} 个token
     * 
     * @param tockenCount
     */
    void consume(long tockenCount);


    /**
     * 尝试消费 {@linkplain tockenCount} 个token
     * 
     * @param tockenCount
     */
    boolean tryConsume(long tockenCount);


    /**
     * 重新调整桶的各项参数
     * 
     * @param capacity 容量
     * @param tokensPerPeriod 每个时间周期生成Token的个数
     * @param interval 间隔时间
     * @param timeUnit 间隔时间的单位
     * @return 返回
     */
    boolean resize(long capacity, long tokensPerPeriod);
}
