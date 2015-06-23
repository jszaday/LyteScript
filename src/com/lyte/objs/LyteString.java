package com.lyte.objs;


/**
 * Created by jszaday on 6/22/15.
 */
public class LyteString extends LytePrimitive<String> {
    public LyteString(String value) {
        super(value);
    }

    @Override
    public LyteBoolean toBoolean() {
        return new LyteBoolean(get().length() > 0);
    }

    @Override
    public LyteNumber toNumber() {
        return new LyteNumber(Double.parseDouble(get()));
    }
}
