package com.lyte.objs;

import com.lyte.core.LyteScope;
import com.lyte.core.LyteStack;

/**
 * Created by jszaday on 6/19/15.
 */
public enum LyteUndefined implements LyteValue {

    NULL("null"), UNDEFINED("undefined");

    private String mType;

    LyteUndefined(String value) {
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
    public boolean toBoolean() {
        return false;
    }

    @Override
    public double toNumber() {
        return 0;
    }


    @Override
    public Object get() {
        return this;
    }

    @Override
    public void set(Object newValue) {
        throw new LyteError("Cannot set the value of a(n) " + typeOf() + " value!");
    }

    @Override
    public LyteValue getProperty(String property) {
        throw new LyteError("Cannot get property " + property + " from a(n) " + typeOf() + "!");
    }

    @Override
    public void setProperty(String property, LyteValue newValue) {
        throw new LyteError("Cannot set property " + property + " from a(n) " + typeOf() + "!");
    }

    @Override
    public boolean hasProperty(String property) {
        return false;
    }

    @Override
    public LyteValue clone(LyteScope scope) {
        return this;
    }

    @Override
    public LyteValue apply(LyteValue self, LyteStack stack) {
        return this;
    }
}
