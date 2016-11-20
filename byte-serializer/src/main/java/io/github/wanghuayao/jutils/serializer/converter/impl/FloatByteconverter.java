package io.github.wanghuayao.jutils.serializer.converter.impl;

import io.github.wanghuayao.jutils.serializer.converter.ByteConverter;

public class FloatByteconverter implements ByteConverter<Float> {

    ByteConverter<Integer> intByteConverter = new IntByteconverter();


    @Override
    public byte[] toBytes(Float val) {
        int rawLongVal = Float.floatToIntBits(val);
        return intByteConverter.toBytes(rawLongVal);
    }


    @Override
    public Float fromBytes(byte[] bytes) {
        int rawLongVal = intByteConverter.fromBytes(bytes);
        return Float.intBitsToFloat(rawLongVal);
    }

}
