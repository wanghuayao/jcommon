package io.github.wanghuayao.jutils.serializer;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;

public class ByteSerializer {

    final static String STRING_CHARSET_UTF8 = "UTF-8";


    public final static byte[] serialize(BigDecimal val) {
        byte[] scaleBytes = ByteSerializer.serialize(val.scale());
        byte[] intBytes = val.unscaledValue().toByteArray();
        byte[] result = new byte[scaleBytes.length + intBytes.length];
        System.arraycopy(scaleBytes, 0, result, 0, scaleBytes.length);
        System.arraycopy(intBytes, 0, result, scaleBytes.length, intBytes.length);
        return result;
    }


    public final static byte[] serialize(Byte val) {
        byte[] byteVal = new byte[1];
        byteVal[0] = val;
        return byteVal;
    }


    public final static byte[] serialize(Double val) {
        long rawLongVal = Double.doubleToRawLongBits(val);
        return serialize(rawLongVal);
    }


    public final static byte[] serialize(Float val) {
        int rawLongVal = Float.floatToIntBits(val);
        return serialize(rawLongVal);
    }


    public final static byte[] serialize(Integer val) {
        byte[] byteVal = new byte[4];
        int intVal = val.intValue();
        byteVal[0] = (byte) ((intVal >>> 24) & 0xFF);
        byteVal[1] = (byte) ((intVal >>> 16) & 0xFF);
        byteVal[2] = (byte) ((intVal >>> 8) & 0xFF);
        byteVal[3] = (byte) (intVal & 0xFF);
        return byteVal;
    }


    public final static byte[] serialize(Long val) {
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


    public final static byte[] serialize(Short val) {
        byte[] byteVal = new byte[2];
        int intVal = val.intValue();
        byteVal[0] = (byte) ((intVal >>> 8) & 0xFF);
        byteVal[1] = (byte) (intVal & 0xFF);
        return byteVal;
    }


    public final static byte[] serialize(Boolean val) {
        return val.booleanValue() ? new byte[] { (byte) 1 } : new byte[] { (byte) 0 };
    }


    public final static byte[] serialize(String val) {
        try {
            return val.getBytes(STRING_CHARSET_UTF8);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

}
