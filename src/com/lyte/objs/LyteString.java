package com.lyte.objs;


/**
 * Created by jszaday on 6/22/15.
 */
public class LyteString extends LytePrimitive<String> {
    public LyteString(String value) {
        super(value);
    }

    @Override
    public boolean toBoolean() {
        return get().length() > 0;
    }

    @Override
    public double toNumber() {
        try {
            return Double.parseDouble(get());
        } catch (NumberFormatException e) {
            throw new LyteError("Cannot cast string '" + get() + "' to double!");
        }
    }
}
