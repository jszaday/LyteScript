package com.lyte.stdlib;

import com.lyte.core.LyteStack;
import com.lyte.objs.LyteError;
import com.lyte.objs.LyteList;
import com.lyte.objs.LyteValue;
import com.lyte.utils.LyteSimpleInjectable;

/**
 * Created by a0225785 on 6/29/2015.
 */
public class LyteListFunctions extends LyteSimpleInjectable {
  public static LyteNativeBlock listPush = new LyteNativeBlock("List", "push") {
    @Override
    public void invoke(LyteValue self, LyteStack stack) {
      if (!self.typeOf().equals("list")) {
        throw new LyteError("Cannot apply push to an " + self.typeOf());
      }
      ((LyteList) self).get().add(stack.pop().apply(stack));
    }
  };
  public static LyteNativeBlock listPop = new LyteNativeBlock("List", "pop") {
    @Override
    public void invoke(LyteValue self, LyteStack stack) {
      if (!self.typeOf().equals("list")) {
        throw new LyteError("Cannot apply push to an " + self.typeOf());
      }
      stack.push(((LyteList) self).get().pop());
    }
  };
  public static LyteNativeBlock listLength = new LyteNativeBlock("List", "length") {
    @Override
    public void invoke(LyteValue self, LyteStack stack) {
      if (!self.typeOf().equals("list")) {
        throw new LyteError("Cannot apply push to an " + self.typeOf());
      }
      stack.push(((LyteList) self).get().size());
    }
  };
  public static LyteNativeBlock listIsEmpty = new LyteNativeBlock("List", "empty?") {
    @Override
    public void invoke(LyteValue self, LyteStack stack) {
      if (!self.typeOf().equals("list")) {
        throw new LyteError("Cannot apply push to an " + self.typeOf());
      }
      stack.push(((LyteList) self).get().isEmpty());
    }
  };
}
