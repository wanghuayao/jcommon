package io.github.wanghuayao.jutils.serializer.converter.impl;

import io.github.wanghuayao.jutils.serializer.converter.ByteConverter;

public class ByteByteconverter implements ByteConverter<Byte> {

    @Override
    public byte[] toBytes(Byte val) {
        byte[] byteVal = new byte[1];
        byteVal[0] = val;
        return byteVal;
    }


    @Override
    public Byte fromBytes(byte[] bytes) {
        return bytes[0];
    }
}
