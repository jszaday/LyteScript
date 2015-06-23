package com.lyte.objs;

/**
 * Created by jszaday on 6/22/15.
 */
public class LyteBoolean extends LytePrimitive<Boolean> {

    public LyteBoolean(Boolean value) {
        super(value);
    }

    @Override
    public LyteBoolean toBoolean() {
        return this;
    }

    @Override
    public LyteNumber toNumber() {
        return get() ? new LyteNumber(1) : new LyteNumber(0);
    }
}
