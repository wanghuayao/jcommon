# Lanppost

Cached the operation parameter in a List in memory,
when the list is full or timeout passed cached parameter list to the callback method,
the default implement **DefaultGatherAndDepart** is thread safe.

the effect of this library is change **db.insert(Entity)** to **db.insert(List&lt;Entity&gt;)**

# Usage
``` java
// define
GatherAndDepart<String> gatherAndDepart = new DefaultGatherAndDepart<>(new DepartCallback<String>() {
        @Override
        public void call(List<String> parameters) throws Exception {
        	// TODO do something
        }
    }/* call back */, 50/* list size */, 1000/* timeout */);

// gather the parameter
gatherAndDepart.gather("aaaa");

// call DepartCallback.call immediately
gatherAndDepart.flash();

// flash the parameter list and stop gather any parameter
gatherAndDepart.close();

```
