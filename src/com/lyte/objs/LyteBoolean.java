package com.lyte.objs;

/**
 * Created by jszaday on 6/22/15.
 */
public class LyteBoolean extends LytePrimitive<Boolean> implements LyteComparable {

    public static final LyteBoolean TRUE = new LyteBoolean(true);
    public static final LyteBoolean FALSE = new LyteBoolean(false);

    private LyteBoolean(Boolean value) {
        super(value);
    }

    public static LyteBoolean valueOf(Boolean b) {
        return b ? TRUE : FALSE;
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

    @Override
    public String toJSONString() {
        return toString();
    }
    
    @Override
    public int compareTo(LyteValue o) {
        if (o.is(typeOf())) {
            return get().compareTo(((LyteBoolean) o).get());
        } else {
            throw new LyteError("Cannot compare a(n) " + typeOf() + " to a(n) " + o.typeOf());
        }
    }
}
