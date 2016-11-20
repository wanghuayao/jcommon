# token-bucket
Fix rate token generator.


# Usage
``` java
// define
TokenBucket tokenBucket = new FixRateTokenBucket(10/*capacity*/, 5/*tokensPerPeriod*/, 1/*period*/, TimeUnit.SECONDS);

// consume one token, if there has no token to consume the thread will block.
tokenBucket.consume();

// consume more then one token, if there has no token to consume the thread will block.
tokenBucket.consume(10);

// consume more then one token, if has token consume it and return true otherwise return false.
boolean ifHasToken = tokenBucket.tryConsume(10);

```
