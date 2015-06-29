package com.lyte.stdlib;

import com.lyte.core.LyteStack;
import com.lyte.objs.LyteError;
import com.lyte.objs.LyteString;
import com.lyte.objs.LyteValue;
import com.lyte.utils.LyteSimpleInjectable;

/**
 * Created by a0225785 on 6/29/2015.
 */
public class LyteStringFunctions extends LyteSimpleInjectable {

  public static LyteNativeBlock stringSubstring = new LyteNativeBlock("String", "substring") {
    @Override
    public void invoke(LyteValue self, LyteStack stack) {
      if (self instanceof LyteString) {
        int value1 = (int) stack.pop().apply(self, stack).toNumber();
        int value2 = (int) stack.pop().apply(self, stack).toNumber();
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

  public static LyteNativeBlock stringContains = new LyteNativeBlock("String", "contains") {
    @Override
    public void invoke(LyteValue self, LyteStack stack) {
      if (self instanceof LyteString) {
        String value1 = stack.pop().apply(self, stack).toString();
        stack.push(self.toString().contains(value1));
      } else {
        throw new LyteError("Cannot take the substring of " + self);
      }
    }
  };

  public static LyteNativeBlock stringSearch = new LyteNativeBlock("String", "search") {
    @Override
    public void invoke(LyteValue self, LyteStack stack) {
      if (self instanceof LyteString) {
        String value1 = stack.pop().apply(self, stack).toString();
        stack.push(self.toString().indexOf(value1));
      } else {
        throw new LyteError("Cannot take the substring of " + self);
      }
    }
  };

  public static LyteNativeBlock stringSearchPast = new LyteNativeBlock("String", "searchPast") {
    @Override
    public void invoke(LyteValue self, LyteStack stack) {
      if (self instanceof LyteString) {
        String value1 = stack.pop().apply(self, stack).toString();
        int value2 = (int) stack.pop().apply(self, stack).toNumber();
        stack.push(self.toString().indexOf(value1, value2));
      } else {
        throw new LyteError("Cannot take the substring of " + self);
      }
    }
  };

  public static LyteNativeBlock stringStartsWith = new LyteNativeBlock("String", "startsWith") {
    @Override
    public void invoke(LyteValue self, LyteStack stack) {
      if (self instanceof LyteString) {
        String value1 = stack.pop().apply(self, stack).toString();
        stack.push(self.toString().startsWith(value1));
      } else {
        throw new LyteError("Cannot take the substring of " + self);
      }
    }
  };

  public static LyteNativeBlock stringEndsWith = new LyteNativeBlock("String", "endsWith") {
    @Override
    public void invoke(LyteValue self, LyteStack stack) {
      if (self instanceof LyteString) {
        String value1 = stack.pop().apply(self, stack).toString();
        stack.push(self.toString().endsWith(value1));
      } else {
        throw new LyteError("Cannot take the substring of " + self);
      }
    }
  };

  public static LyteNativeBlock stringLength = new LyteNativeBlock("String", "length") {
    @Override
    public void invoke(LyteValue self, LyteStack stack) {
      if (self instanceof LyteString) {
        stack.push(self.toString().length());
      } else {
        throw new LyteError("Cannot take the substring of " + self);
      }
    }
  };
}
