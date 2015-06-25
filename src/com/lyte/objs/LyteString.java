package com.lyte.objs;


import com.lyte.core.LyteStack;
import com.lyte.stdlib.LyteNativeBlock;

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

    @Override
    public LyteValue getProperty(String property) {
        if (property.equals("substring")) {
            return stringSubstring;
        } else {
            throw new LyteError("Cannot invoke the property " + property + " of a string.");
        }
    }

    public static LyteNativeBlock stringSubstring = new LyteNativeBlock("String", "Substring") {
        @Override
        public boolean invoke(LyteValue self, LyteStack stack) {
            if (self instanceof LyteString) {
                int value1 = (int) stack.pop().toNumber();
                int value2 = (int) stack.pop().toNumber();
                ((LyteString) self).set(self.toString().substring(value1, value2 + 1));
                return true;
            } else {
                throw new LyteError("Cannot take the substring of " + self);
            }
        }
    };
}
