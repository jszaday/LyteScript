package com.lyte.objs;

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
    public LyteBoolean toBoolean() {
        return new LyteBoolean(get() != 0);
    }

    @Override
    public LyteNumber toNumber() {
        return this;
    }

    @Override
    public String typeOf() {
        return "number";
    }
}
