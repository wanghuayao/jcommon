/**
 * Project: cetus-web
 * 
 * File Created at 2016-9-9
 * $Id$
 * 
 */
package io.github.wanghuayao.jutils.serializer.converter;

/**
 * ByteConverter
 * 
 * @author wanghuayao
 */
public interface ByteConverter<T> {

    public final static byte NULL = (byte) '\0';


    public byte[] toBytes(T val);


    public T fromBytes(byte[] bytes);
}
