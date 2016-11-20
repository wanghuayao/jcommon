/**
 * Project: cetus-web
 * 
 * File Created at 2016-9-9
 * $Id$
 * 
 */
package io.github.wanghuayao.jutils.serializer.converter;

import io.github.wanghuayao.jutils.serializer.converter.impl.BigDecimalByteconverter;
import io.github.wanghuayao.jutils.serializer.converter.impl.ByteByteconverter;
import io.github.wanghuayao.jutils.serializer.converter.impl.DoubleByteconverter;
import io.github.wanghuayao.jutils.serializer.converter.impl.FloatByteconverter;
import io.github.wanghuayao.jutils.serializer.converter.impl.IntByteconverter;
import io.github.wanghuayao.jutils.serializer.converter.impl.LongByteconverter;
import io.github.wanghuayao.jutils.serializer.converter.impl.ShortByteconverter;
import io.github.wanghuayao.jutils.serializer.converter.impl.StringByteconverter;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * ByteConverterFactory
 * 
 * @author wanghuayao
 */
public class ByteConverterFactory {
    private final static Logger                          LOG                = LoggerFactory
                                                                                    .getLogger(ByteConverterFactory.class);
    private final static Class<?>                        DEFAULT_TYPE       = String.class;

    private final static Map<Class<?>, ByteConverter<?>> SUPORTED_CONVERTER = new HashMap<>();
    static {
        SUPORTED_CONVERTER.put(String.class, new StringByteconverter());
        SUPORTED_CONVERTER.put(Double.class, new DoubleByteconverter());
        SUPORTED_CONVERTER.put(Long.class, new LongByteconverter());
        SUPORTED_CONVERTER.put(Integer.class, new IntByteconverter());
        SUPORTED_CONVERTER.put(Float.class, new FloatByteconverter());
        SUPORTED_CONVERTER.put(BigDecimal.class, new BigDecimalByteconverter());
        SUPORTED_CONVERTER.put(Byte.class, new ByteByteconverter());
        SUPORTED_CONVERTER.put(Short.class, new ShortByteconverter());
    }


    @SuppressWarnings("unchecked")
    public static <T> ByteConverter<T> create(Class<T> clazz) {
        if (SUPORTED_CONVERTER.containsKey(clazz)) {
            return (ByteConverter<T>) SUPORTED_CONVERTER.get(clazz);
        }

        LOG.warn("ByteConverter 使用[{}]代替不支持的类型[{}]", DEFAULT_TYPE, clazz);
        return (ByteConverter<T>) SUPORTED_CONVERTER.get(DEFAULT_TYPE);
    }
}
