package io.github.wanghuayao.jutils.serializer.converter.impl;

import io.github.wanghuayao.jutils.serializer.converter.ByteConverter;

public class DoubleByteconverter implements ByteConverter<Double> {

    ByteConverter<Long> longByteConverter = new LongByteconverter();


    @Override
    public byte[] toBytes(Double val) {
        long rawLongVal = Double.doubleToRawLongBits(val);
        return longByteConverter.toBytes(rawLongVal);
    }


    @Override
    public Double fromBytes(byte[] bytes) {
        long rawLongVal = longByteConverter.fromBytes(bytes);
        return Double.longBitsToDouble(rawLongVal);
    }

}
