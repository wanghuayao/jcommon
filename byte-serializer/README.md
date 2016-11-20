# byte-serializer
Serialize numeric data type to byte array and deserialize from byte array.

Current support Types:
---------------------------
* BigDecimal <=> byte array
* Double <=> byte array
* Float <=> byte array
* Integer <=> byte array
* Long <=> byte array
* Short <=> byte array
* String <=> byte array
* Byte <=> byte array

# Usage
``` java
int val = 123;

// serialize
byte[] byteVal = ByteSerializer.serialize(val);

// deserivalize
int result = ByteDeserializer.deserializeInteger(byteVal);

```
