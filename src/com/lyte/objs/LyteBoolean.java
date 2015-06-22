package com.lyte.objs;

/**
 * Created by jszaday on 6/22/15.
 */
public class LyteBoolean extends LytePrimitive<Boolean> {

    public LyteBoolean(Boolean value) {
        super(value);
    }

    @Override
    public boolean asBoolean() {
        return get();
    }
}
