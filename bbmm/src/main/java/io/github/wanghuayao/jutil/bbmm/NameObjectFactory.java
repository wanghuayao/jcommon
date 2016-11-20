package io.github.wanghuayao.jutil.bbmm;

public interface NameObjectFactory<NAME, OBJ> {

    public OBJ create(NAME key);


    public boolean valid(OBJ o, long idelFrom, long safeTimeSpan);


    public void destroy(OBJ o);
}
