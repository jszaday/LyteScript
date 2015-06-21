package com.lyte.stdlib;

import com.lyte.core.LyteScope;
import com.lyte.core.LyteStack;
import com.lyte.objs.LyteObject;
import com.lyte.objs.LytePrimitive;
import com.lyte.objs.LyteValue;

/**
 * Created by jszaday on 6/20/15.
 */
public class LyteConcatenate extends LyteNativeBlock {

    public LyteConcatenate(LyteScope parentScope) {
        super(parentScope);
    }

    @Override
    public String getSymbol() {
        return "++";
    }

    @Override
    public void invoke(LyteObject self, LyteStack stack) {
        LyteValue value2 = stack.pop();
        LyteValue value1 = stack.pop();
        // TODO Implement other cases
        if (value1.typeOf().equals("string")) {
            stack.push(new LytePrimitive<String>(value1.toString() + value2.toString()));
        }
    }
}
