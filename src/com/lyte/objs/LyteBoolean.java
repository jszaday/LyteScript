package com.lyte.objs;

/**
 * Created by jszaday on 6/22/15.
 */
public class LyteBoolean extends LytePrimitive<Boolean> {

    public LyteBoolean(Boolean value) {
        super(value);
    }

    @Override
    public boolean toBoolean() {
        return get();
    }

    @Override
    public double toNumber() {
        return get() ? 1 : 0;
    }

    @Override
    public boolean equals(LyteValue other) {
        return other.toBoolean() == get();
    }

    @Override
    public boolean isSimpleComparison() {
        return true;
    }
}
