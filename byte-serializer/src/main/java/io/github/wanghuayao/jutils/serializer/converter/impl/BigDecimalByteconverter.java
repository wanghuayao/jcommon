package io.github.wanghuayao.jutils.serializer.converter.impl;

import io.github.wanghuayao.jutils.serializer.converter.ByteConverter;

import java.math.BigDecimal;
import java.math.BigInteger;

public class BigDecimalByteconverter implements ByteConverter<BigDecimal> {
    ByteConverter<Integer> intByteConverter = new IntByteconverter();


    @Override
    public byte[] toBytes(BigDecimal val) {
        byte[] scaleBytes = intByteConverter.toBytes(val.scale());
        byte[] intBytes = val.unscaledValue().toByteArray();
        byte[] result = new byte[scaleBytes.length + intBytes.length];
        System.arraycopy(scaleBytes, 0, result, 0, scaleBytes.length);
        System.arraycopy(intBytes, 0, result, scaleBytes.length, intBytes.length);
        return result;
    }


    @Override
    public BigDecimal fromBytes(byte[] bytes) {
        int scale = intByteConverter.fromBytes(bytes);
        byte[] intBytes = new byte[bytes.length - 4];
        System.arraycopy(bytes, 4, intBytes, 0, intBytes.length);
        BigInteger bigInteger = new BigInteger(intBytes);
        return new BigDecimal(bigInteger, scale);
    }
}
