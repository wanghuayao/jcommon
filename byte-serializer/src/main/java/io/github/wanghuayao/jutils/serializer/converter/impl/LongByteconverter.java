package io.github.wanghuayao.jutils.serializer.converter.impl;

import io.github.wanghuayao.jutils.serializer.converter.ByteConverter;

public class LongByteconverter implements ByteConverter<Long> {

    @Override
    public byte[] toBytes(Long val) {
        byte[] byteVal = new byte[8];
        byteVal[0] = (byte) ((val >>> 56) & 0xFF);
        byteVal[1] = (byte) ((val >>> 48) & 0xFF);
        byteVal[2] = (byte) ((val >>> 40) & 0xFF);
        byteVal[3] = (byte) ((val >>> 32) & 0xFF);
        byteVal[4] = (byte) ((val >>> 24) & 0xFF);
        byteVal[5] = (byte) ((val >>> 16) & 0xFF);
        byteVal[6] = (byte) ((val >>> 8) & 0xFF);
        byteVal[7] = (byte) (val & 0xFF);

        return byteVal;
    }


    @Override
    public Long fromBytes(byte[] bytes) {
        // @formatter:off
        return    ((long) (bytes[0] & 0xFF) << 56) 
                | ((long) (bytes[1] & 0xFF) << 48)
                | ((long) (bytes[2] & 0xFF) << 40)
                | ((long) (bytes[3] & 0xFF) << 32)
                | ((long) (bytes[4] & 0xFF) << 24) 
                | ((long) (bytes[5] & 0xFF) << 16)
                | ((long) (bytes[6] & 0xFF) << 8) 
                |  (long) (bytes[7] & 0xFF);
    }
}
