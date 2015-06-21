package com.lyte.stdlib;

import com.lyte.core.LyteScope;
import com.lyte.core.LyteStack;
import com.lyte.objs.LyteBlock;
import com.lyte.objs.LyteObject;
import com.lyte.objs.LyteValue;

/**
 * Created by jszaday on 6/19/15.
 */
public class LyteEcho extends LyteNativeBlock {

    public LyteEcho(LyteScope parentScope) {
        super(parentScope);
    }

    @Override
    public String getSymbol() {
        return "Echo";
    }

    @Override
    public void invoke(LyteObject self, LyteStack stack) {
        LyteValue value = stack.pop();
        if (value.typeOf().equals("block")) {
            ((LyteBlock) value).invoke(self, stack);
            value = stack.pop();
        }
        System.out.println(value);
    }
}
