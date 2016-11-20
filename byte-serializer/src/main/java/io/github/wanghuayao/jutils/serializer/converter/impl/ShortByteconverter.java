package io.github.wanghuayao.jutils.serializer.converter.impl;

import io.github.wanghuayao.jutils.serializer.converter.ByteConverter;

public class ShortByteconverter implements ByteConverter<Short> {

    @Override
    public byte[] toBytes(Short val) {
        byte[] byteVal = new byte[2];
        int intVal = val.intValue();
        byteVal[0] = (byte) ((intVal >>> 8) & 0xFF);
        byteVal[1] = (byte) (intVal & 0xFF);
        return byteVal;
    }


    @Override
    public Short fromBytes(byte[] bytes) {
        //@formatter:off
        return (short)(
               ((bytes[0] & 0xFF) << 8)
               |(bytes[1] & 0xFF));
    }
}
