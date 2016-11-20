package io.github.wanghuayao.jutils.serializer.converter.impl;

import io.github.wanghuayao.jutils.serializer.converter.ByteConverter;

public class IntByteconverter implements ByteConverter<Integer> {

    @Override
    public byte[] toBytes(Integer val) {
        byte[] byteVal = new byte[4];
        int intVal = val.intValue();
        byteVal[0] = (byte) ((intVal >>> 24) & 0xFF);
        byteVal[1] = (byte) ((intVal >>> 16) & 0xFF);
        byteVal[2] = (byte) ((intVal >>> 8) & 0xFF);
        byteVal[3] = (byte) (intVal & 0xFF);
        return byteVal;
    }


    @Override
    public Integer fromBytes(byte[] bytes) {
        //@formatter:off
        return   ((int) (bytes[0] & 0xFF) << 24)
               | ((int) (bytes[1] & 0xFF) << 16)
               | ((int) (bytes[2] & 0xFF) << 8)
               |  (int) (bytes[3] & 0xFF);
    }

}
