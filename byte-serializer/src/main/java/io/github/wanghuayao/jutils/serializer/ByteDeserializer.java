package io.github.wanghuayao.jutils.serializer;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.math.BigInteger;

public class ByteDeserializer {

    @SuppressWarnings("unchecked")
    public final static <T> T deserialize(byte[] bytes, Class<T> clazz) {
        if (clazz == BigDecimal.class) {
            return (T) deserializeBigDecimal(bytes);
        } else if (clazz == Byte.class) {
            return (T) deserializeByte(bytes);
        } else if (clazz == Double.class) {
            return (T) deserializeDouble(bytes);
        } else if (clazz == Float.class) {
            return (T) deserializeFloat(bytes);
        } else if (clazz == Integer.class) {
            return (T) deserializeInteger(bytes);
        } else if (clazz == Long.class) {
            return (T) deserializeLong(bytes);
        } else if (clazz == Short.class) {
            return (T) deserializeShort(bytes);
        } else if (clazz == Boolean.class) {
            return (T) deserializeBoolean(bytes);
        }

        return null;
    }


    public final static BigDecimal deserializeBigDecimal(byte[] bytes) {
        int scale = deserializeInteger(bytes);
        byte[] intBytes = new byte[bytes.length - 4];
        System.arraycopy(bytes, 4, intBytes, 0, intBytes.length);
        BigInteger bigInteger = new BigInteger(intBytes);
        return new BigDecimal(bigInteger, scale);
    }


    public final static Byte deserializeByte(byte[] bytes) {
        return bytes[0];
    }


    public final static Double deserializeDouble(byte[] bytes) {
        long rawLongVal = deserializeLong(bytes);
        return Double.longBitsToDouble(rawLongVal);
    }


    public final static Float deserializeFloat(byte[] bytes) {
        int rawLongVal = deserializeInteger(bytes);
        return Float.intBitsToFloat(rawLongVal);
    }


    public final static Integer deserializeInteger(byte[] bytes) {
        //@formatter:off
        return   ((int) (bytes[0] & 0xFF) << 24)
               | ((int) (bytes[1] & 0xFF) << 16)
               | ((int) (bytes[2] & 0xFF) << 8)
               |  (int) (bytes[3] & 0xFF);
    }

    
    public final static Long deserializeLong(byte[] bytes) {
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

    
    public final static Short deserializeShort(byte[] bytes) {
        //@formatter:off
        return (short)(
               ((bytes[0] & 0xFF) << 8)
               |(bytes[1] & 0xFF));
    }

    
    public final static Boolean deserializeBoolean(byte[] bytes) {
        //@formatter:off
        return bytes[0] == 0 ? Boolean.FALSE : Boolean.TRUE;
    }


    
    public final static String deserializeString(byte[] bytes) {
        //@formatter:off
        try {
            return  new String(bytes, ByteSerializer.STRING_CHARSET_UTF8);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }
}
