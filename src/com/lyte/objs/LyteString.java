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

  public LyteString(char value) {
    super(Character.toString(value));
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
    Integer index;
    if ((index = LyteArray.tryParse(property)) != null) {
      try {
        return new LyteString(get().charAt(index));
      } catch (StringIndexOutOfBoundsException e) {
        throw new LyteError("Index out of bounds, " + index + "!");
      }
    } else if (property.equals("substring")) {
      return stringSubstring;
    } else if (property.equals("length")) {
      return new LyteNumber(get().length());
    } else {
      throw new LyteError("Cannot invoke the property " + property + " of a string.");
    }
  }

  public static LyteNativeBlock stringSubstring = new LyteNativeBlock("String", "Substring") {
    @Override
    public void invoke(LyteValue self, LyteStack stack) {
      if (self instanceof LyteString) {
        int value1 = (int) stack.pop().apply(self).toNumber();
        int value2 = (int) stack.pop().apply(self).toNumber();
        try {
          stack.push(new LyteString(self.toString().substring(value1, value2)));
        } catch (StringIndexOutOfBoundsException e) {
          throw new LyteError("Cannot take substring from " + value1 + " to " + value2 + " of \"" + self.toString() + ",\" indices out of bounds!");
        }
      } else {
        throw new LyteError("Cannot take the substring of " + self);
      }
    }
  };
}
