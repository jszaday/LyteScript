package com.lyte.objs;

import com.lyte.core.LyteScope;

/**
 * Created by jszaday on 6/19/15.
 */
public enum LyteUndefined implements LyteValue {

    NULL("null"), UNDEFINED("undefined");

    private String mType;

    private LyteUndefined(String value) {
        mType = value;
    }

    @Override
    public String typeOf() {
        return mType;
    }

    @Override
    public String toString() {
        return mType;
    }

    @Override
    public LyteValue clone(LyteScope scope) {
        return this;
    }

    @Override
    public boolean asBoolean() {
        return false;
    }
}
