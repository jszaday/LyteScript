package com.lyte.stdlib;

import com.lyte.core.LyteScope;
import com.lyte.core.LyteStack;
import com.lyte.objs.LyteObject;
import com.lyte.objs.LyteValue;

/**
 * Created by jszaday on 6/20/15.
 */
public class LyteMixWith extends LyteNativeBlock {
    public LyteMixWith(LyteScope parentScope) {
        super(parentScope);
    }

    @Override
    public String getSymbol() {
        return "MixWith";
    }

    @Override
    public void invoke(LyteObject self, LyteStack stack) {
        LyteValue value2 = stack.pop();
        LyteValue value1 = stack.pop();

        if (!(value1.typeOf().equals("object") && value2.typeOf().equals("object"))) {
            throw new RuntimeException("Cannot mix " + value1 + " with " + value2);
        }

        ((LyteObject) value1).mixWith((LyteObject) value2);
    }
}
