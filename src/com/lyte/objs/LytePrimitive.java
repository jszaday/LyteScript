package com.lyte.objs;

import com.lyte.core.LyteScope;

/**
 * Created by jszaday on 6/18/15.
 */
public abstract class LytePrimitive<T> implements LyteValue<T> {

    private T mValue;

    public LytePrimitive(T value) {
        mValue = value;
    }

    @Override
    public T get() {
        return mValue;
    }

    @Override
    public void set(T newValue) {
        mValue = newValue;
    }

    @Override
    public LyteValue getProperty(String property) {
        throw new LyteError("Cannot get property " + property + " from a(n) " + typeOf() + "!");
    }

    @Override
    public void setProperty(String property, LyteValue newValue) {
        throw new LyteError("Cannot set property " + property + " of a(n) " + typeOf() + "!");
    }

    @Override
    public boolean hasProperty(String property) {
        return false;
    }

    @Override
    public LyteValue apply(LyteValue self) {
        return this;
    }

    @Override
    public String toString() {
        return mValue.toString();
    }

    @Override
    public String typeOf() {
        return mValue.getClass().getSimpleName().toLowerCase();
    }

    @Override
    public LyteValue<T> clone(LyteScope scope) {
        return this;
    }
}