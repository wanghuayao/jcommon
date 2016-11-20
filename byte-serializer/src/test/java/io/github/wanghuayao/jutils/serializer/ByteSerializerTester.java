package io.github.wanghuayao.jutils.serializer;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class ByteSerializerTester {
    @Test
    public void test_int() {
        int[] forTests = new int[] { Integer.MIN_VALUE, Integer.MAX_VALUE, 0, -1, 1 };
        for (int test : forTests) {
            System.out.println(test);
            int val = ByteDeserializer.deserializeInteger(ByteSerializer.serialize(test));
            assertEquals("Test serialize [" + test + "]", test, val);
        }
    }
}
