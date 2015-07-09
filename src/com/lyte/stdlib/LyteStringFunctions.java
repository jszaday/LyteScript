package com.lyte.stdlib;

import com.lyte.core.LyteContext;
import com.lyte.core.LyteStack;
import com.lyte.objs.LyteList;
import com.lyte.objs.LyteError;
import com.lyte.objs.LyteString;
import com.lyte.objs.LyteValue;
import com.lyte.utils.LyteMemberBlock;
import com.lyte.utils.LyteSimpleInjectable;

/**
 * Created by a0225785 on 6/29/2015.
 */
public class LyteStringFunctions extends LyteSimpleInjectable {

  public static LyteNativeBlock stringSubstring = new LyteMemberBlock<LyteString>("substring") {

    @Override
    public void invoke(LyteString self, LyteContext context) {
      int value1 = (int) context.apply().toNumber();
      int value2 = (int) context.apply().toNumber();
      try {
        context.push(new LyteString(self.get().substring(value1, value2)));
      } catch (StringIndexOutOfBoundsException e) {
        throw new LyteError("Cannot take substring from " + value1 + " to " + value2 + " of \"" + self.get() + ",\" indices out of bounds!");
      }
    }
  };
  public static LyteNativeBlock stringContains = new LyteMemberBlock<LyteString>("contains") {

    @Override
    public void invoke(LyteString self, LyteContext context) {
      String value1 = context.apply().toString();
      context.push(self.get().contains(value1));
    }
  };
  public static LyteNativeBlock stringSearch = new LyteMemberBlock<LyteString>("search") {

    @Override
    public void invoke(LyteString self, LyteContext context) {
      String value1 = context.apply().toString();
      context.push(self.get().indexOf(value1));
    }
  };
  public static LyteNativeBlock stringSearchPast = new LyteMemberBlock<LyteString>("searchPast") {

    @Override
    public void invoke(LyteString self, LyteContext context) {
      String value1 = context.apply().toString();
      int value2 = (int) context.apply().toNumber();
      context.push(self.get().indexOf(value1, value2));
    }
  };
  public static LyteNativeBlock stringStartsWith = new LyteMemberBlock<LyteString>("startsWith") {

    @Override
    public void invoke(LyteString self, LyteContext context) {
      context.push(self.get().startsWith(context.apply().toString()));
    }
  };
  public static LyteNativeBlock stringEndsWith = new LyteMemberBlock<LyteString>("endsWith") {

    @Override
    public void invoke(LyteString self, LyteContext context) {
      context.push(self.get().endsWith(context.apply().toString()));
    }
  };
  public static LyteNativeBlock stringLength = new LyteMemberBlock<LyteString>("length") {

    @Override
    public void invoke(LyteString self, LyteContext context) {
      context.push(self.get().length());
    }
  };
  public static LyteNativeBlock stringReplace = new LyteMemberBlock<LyteString>("replace") {

    @Override
    public void invoke(LyteString self, LyteContext context) {
      String value1 = context.apply().toString();
      String value2 = context.apply().toString();
      context.push(self.get().replaceFirst(value1, value2));
    }
  };
  public static LyteNativeBlock stringReplaceAll = new LyteMemberBlock<LyteString>("replaceAll") {

    @Override
    public void invoke(LyteString self, LyteContext context) {
      String value1 = context.apply().toString();
      String value2 = context.apply().toString();
      context.push(self.get().replaceAll(value1, value2));
    }
  };
  public static LyteNativeBlock stringReverse = new LyteMemberBlock<LyteString>("reverse") {
    @Override
    public void invoke(LyteString self, LyteContext context) {
      context.push(new StringBuilder(self.get()).reverse().toString());
    }
  };
  public static LyteNativeBlock stringIsEmpty = new LyteMemberBlock<LyteString>("empty?") {
    @Override
    public void invoke(LyteString self, LyteContext context) {
      context.push(self.get().isEmpty());
    }
  };
  public static LyteNativeBlock stringMatches = new LyteMemberBlock<LyteString>("matches") {
    @Override
    public void invoke(LyteString self, LyteContext context) {
      context.push(self.get().matches(context.apply().toString()));
    }
  };
  public static LyteNativeBlock stringSplit = new LyteMemberBlock<LyteString>("split") {
    @Override
    public void invoke(LyteString self, LyteContext context) {
      String[] results = self.get().split(context.apply().toString());
      context.push(new LyteList(results));
    }
  };
  public static LyteNativeBlock stringConcat = new LyteMemberBlock<LyteString>("concat") {
    @Override
    public void invoke(LyteString self, LyteContext context) {
      context.push(self.get() + context.apply());
    }
  };
  public static LyteNativeBlock stringToUpperCase = new LyteMemberBlock<LyteString>("toUpperCase") {
    @Override
    public void invoke(LyteString self, LyteContext context) {
      context.push(self.get().toUpperCase());
    }
  };
  public static LyteNativeBlock stringToLowerCase = new LyteMemberBlock<LyteString>("toLowerCase") {
    @Override
    public void invoke(LyteString self, LyteContext context) {
      context.push(self.get().toLowerCase());
    }
  };
}
