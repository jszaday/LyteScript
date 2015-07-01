package com.lyte.stdlib;

import com.lyte.core.LyteContext;
import com.lyte.objs.LyteError;
import com.lyte.objs.LyteList;
import com.lyte.utils.LyteSimpleInjectable;

/**
 * Created by a0225785 on 6/29/2015.
 */
public class LyteListFunctions extends LyteSimpleInjectable {
  public static LyteNativeBlock listPush = new LyteNativeBlock("List", "push") {
    @Override
    public void invoke(LyteContext context) {
      if (!context.self.typeOf().equals("list")) {
        throw new LyteError("Cannot apply push to an " + context.self.typeOf());
      }
      ((LyteList) context.self).get().add(context.stack.pop().apply(context));
    }
  };
  public static LyteNativeBlock listPop = new LyteNativeBlock("List", "pop") {
    @Override
    public void invoke(LyteContext context) {
      if (!context.self.typeOf().equals("list")) {
        throw new LyteError("Cannot apply push to an " + context.self.typeOf());
      }
      context.push(((LyteList) context.self).get().pop());
    }
  };
  public static LyteNativeBlock listLength = new LyteNativeBlock("List", "length") {
    @Override
    public void invoke(LyteContext context) {
      if (!context.self.typeOf().equals("list")) {
        throw new LyteError("Cannot apply push to an " + context.self.typeOf());
      }
      context.push(((LyteList) context.self).get().size());
    }
  };
  public static LyteNativeBlock listIsEmpty = new LyteNativeBlock("List", "empty?") {
    @Override
    public void invoke(LyteContext context) {
      if (!context.self.typeOf().equals("list")) {
        throw new LyteError("Cannot apply push to an " + context.self.typeOf());
      }
      context.push(((LyteList) context.self).get().isEmpty());
    }
  };
}
