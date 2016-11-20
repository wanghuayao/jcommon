package io.github.wanghuayao.jutils.serializer.converter.impl;

import io.github.wanghuayao.jutils.serializer.converter.ByteConverter;

import java.nio.charset.Charset;

public class StringByteconverter implements ByteConverter<String> {

    private final static Charset UTF8 = Charset.forName("UTF-8");


    @Override
    public byte[] toBytes(String val) {
        return val.getBytes(UTF8);
    }


    @Override
    public String fromBytes(byte[] bytes) {
        //@formatter:off
        return  new String(bytes, UTF8);
    }
}
