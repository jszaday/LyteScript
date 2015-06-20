package com.lyte.objs;

import com.lyte.core.LyteScope;

/**
 * Created by jszaday on 6/17/15.
 */
public class LyteNumber extends LytePrimitive<Double> {

    public LyteNumber(String number, int radix) {
        this(Integer.parseInt(number, radix));
    }

    public LyteNumber(String number) {
        super(Double.parseDouble(number));
    }

    public LyteNumber(Integer number) {
        super(number.doubleValue());
    }

    public LyteNumber(Double number) {
        super(number);
    }

    @Override
    public boolean isTruthy() {
        return (get() != 0);
    }

    @Override
    public String typeOf() {
        return "number";
    }
}
