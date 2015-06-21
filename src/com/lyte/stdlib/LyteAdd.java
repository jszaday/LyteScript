package com.lyte.stdlib;

import com.lyte.core.LyteScope;
import com.lyte.core.LyteStack;
import com.lyte.objs.*;

/**
 * Created by jszaday on 6/19/15.
 */
public class LyteAdd extends LyteNativeBlock {
    public LyteAdd(LyteScope parentScope) {
        super(parentScope);
    }

    @Override
    public String getSymbol() {
        return "+";
    }

    @Override
    public void invoke(LyteObject self, LyteStack stack) {
        LyteValue value1 = stack.pop();
        if (value1.typeOf().equals("block")) {
            ((LyteBlock) value1).invoke(self, stack);
            value1 = stack.pop();
        }
        LyteValue value2 = stack.pop();
        if (value2.typeOf().equals("block")) {
            ((LyteBlock) value2).invoke(self, stack);
            value2 = stack.pop();
        }
        if (!value1.typeOf().equals("number") || !value2.typeOf().equals("number")) {
            // Type Coerce 'Em
        }
        stack.push(new LyteNumber(((LytePrimitive<Double>) value1).get() + ((LytePrimitive<Double>) value2).get()));
    }
}
