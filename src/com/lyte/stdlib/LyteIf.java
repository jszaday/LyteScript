package com.lyte.stdlib;

import com.lyte.core.LyteScope;
import com.lyte.core.LyteStack;
import com.lyte.objs.LyteBlock;
import com.lyte.objs.LyteObject;
import com.lyte.objs.LyteValue;

/**
 * Created by a0225785 on 6/19/2015.
 */
public class LyteIf extends LyteNativeBlock {

  public LyteIf(LyteScope parentScope) {
    super(parentScope);
  }

  @Override
  public String getSymbol() {
    return "If";
  }

  @Override
  public void invoke(LyteObject self, LyteStack stack) {
    LyteValue condition = stack.pop();
    LyteBlock trueBlock = (LyteBlock) stack.pop();
    LyteBlock falseBlock = (LyteBlock) stack.pop();

    if (condition.typeOf().equals("block")) {
      ((LyteBlock) condition).invoke(self, stack);
      System.out.println(condition);
      condition = stack.pop();
    }

    if (condition.isTruthy()) {
      trueBlock.invoke(self, stack);
    } else {
      falseBlock.invoke(self, stack);
    }
  }
}
