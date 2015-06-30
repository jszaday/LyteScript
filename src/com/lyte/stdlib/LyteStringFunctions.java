package com.lyte.stdlib;

import com.lyte.core.LyteStack;
import com.lyte.objs.LyteList;
import com.lyte.objs.LyteError;
import com.lyte.objs.LyteString;
import com.lyte.objs.LyteValue;
import com.lyte.utils.LyteSimpleInjectable;

/**
 * Created by a0225785 on 6/29/2015.
 */
public class LyteStringFunctions extends LyteSimpleInjectable {

  public static LyteNativeBlock stringSubstring = new LyteStringBlock("substring") {

    @Override
    public void invoke(LyteString self, LyteStack stack) {
      int value1 = (int) stack.pop().apply(stack).toNumber();
      int value2 = (int) stack.pop().apply(stack).toNumber();
      try {
        stack.push(new LyteString(self.get().substring(value1, value2)));
      } catch (StringIndexOutOfBoundsException e) {
        throw new LyteError("Cannot take substring from " + value1 + " to " + value2 + " of \"" + self.get() + ",\" indices out of bounds!");
      }
    }
  };
  public static LyteNativeBlock stringContains = new LyteStringBlock("contains") {

    @Override
    public void invoke(LyteString self, LyteStack stack) {
      String value1 = stack.pop().apply(stack).toString();
      stack.push(self.get().contains(value1));
    }
  };
  public static LyteNativeBlock stringSearch = new LyteStringBlock("search") {

    @Override
    public void invoke(LyteString self, LyteStack stack) {
      String value1 = stack.pop().apply(stack).toString();
      stack.push(self.get().indexOf(value1));
    }
  };
  public static LyteNativeBlock stringSearchPast = new LyteStringBlock("searchPast") {

    @Override
    public void invoke(LyteString self, LyteStack stack) {
      String value1 = stack.pop().apply(stack).toString();
      int value2 = (int) stack.pop().apply(stack).toNumber();
      stack.push(self.get().indexOf(value1, value2));
    }
  };
  public static LyteNativeBlock stringStartsWith = new LyteStringBlock("startsWith") {

    @Override
    public void invoke(LyteString self, LyteStack stack) {
      if (self instanceof LyteString) {
        String value1 = stack.pop().apply(stack).toString();
        stack.push(self.get().startsWith(value1));
      } else {
        throw new LyteError("Cannot take the substring of " + self);
      }
    }
  };
  public static LyteNativeBlock stringEndsWith = new LyteStringBlock("endsWith") {

    @Override
    public void invoke(LyteString self, LyteStack stack) {
      if (self instanceof LyteString) {
        String value1 = stack.pop().apply(stack).toString();
        stack.push(self.get().endsWith(value1));
      } else {
        throw new LyteError("Cannot take the substring of " + self);
      }
    }
  };
  public static LyteNativeBlock stringLength = new LyteStringBlock("length") {

    @Override
    public void invoke(LyteString self, LyteStack stack) {
      stack.push(self.get().length());
    }
  };
  public static LyteNativeBlock stringReplace = new LyteStringBlock("replace") {

    @Override
    public void invoke(LyteString self, LyteStack stack) {
      String value1 = stack.pop().apply(stack).toString();
      String value2 = stack.pop().apply(stack).toString();
      stack.push(self.get().replaceFirst(value1, value2));
    }
  };
  public static LyteNativeBlock stringReplaceAll = new LyteStringBlock("replaceAll") {

    @Override
    public void invoke(LyteString self, LyteStack stack) {
      String value1 = stack.pop().apply(stack).toString();
      String value2 = stack.pop().apply(stack).toString();
      stack.push(self.get().replaceAll(value1, value2));
    }
  };
  public static LyteNativeBlock stringReverse = new LyteStringBlock("reverse") {
    @Override
    public void invoke(LyteString self, LyteStack stack) {
      stack.push(new StringBuilder(self.get()).reverse().toString());
    }
  };
  public static LyteNativeBlock stringIsEmpty = new LyteStringBlock("empty?") {
    @Override
    public void invoke(LyteString self, LyteStack stack) {
      stack.push(self.get().isEmpty());
    }
  };
  public static LyteNativeBlock stringMatches = new LyteStringBlock("matches") {
    @Override
    public void invoke(LyteString self, LyteStack stack) {
      stack.push(self.get().matches(stack.pop().apply(stack).toString()));
    }
  };
  public static LyteNativeBlock stringSplit = new LyteStringBlock("split") {
    @Override
    public void invoke(LyteString self, LyteStack stack) {
      String[] results = self.get().split(stack.pop().apply(stack).toString());
      stack.push(new LyteList(results));
    }
  };
  public static LyteNativeBlock stringConcat = new LyteStringBlock("concat") {
    @Override
    public void invoke(LyteString self, LyteStack stack) {
      stack.push(self.get() + stack.pop().apply(stack));
    }
  };
  public static LyteNativeBlock stringToUpperCase = new LyteStringBlock("toUpperCase") {
    @Override
    public void invoke(LyteString self, LyteStack stack) {
      stack.push(self.get().toUpperCase());
    }
  };
  public static LyteNativeBlock stringToLowerCase = new LyteStringBlock("toLowerCase") {
    @Override
    public void invoke(LyteString self, LyteStack stack) {
      stack.push(self.get().toLowerCase());
    }
  };

  private static abstract class LyteStringBlock extends LyteNativeBlock {
    public LyteStringBlock(String alias) {
      super("String", alias);
    }

    @Override
    public void invoke(LyteValue self, LyteStack stack) {
      if (self instanceof LyteString) {
        invoke((LyteString) self, stack);
      } else {
        throw new LyteError("Cannot take the substring of " + self);
      }
    }

    public abstract void invoke(LyteString self, LyteStack stack);
  }
}
