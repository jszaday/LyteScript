package com.lyte.stdlib;

import com.lyte.core.LyteScope;
import com.lyte.objs.LyteBlock;
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
  public void invoke() {
    LyteValue condition = mScope.pop();
    LyteBlock trueBlock = (LyteBlock) mScope.pop();
    LyteBlock falseBlock = (LyteBlock) mScope.pop();

    if (condition.typeOf().equals("block")) {
      ((LyteBlock) condition).invoke();
      System.out.println(condition);
      condition = mScope.pop();
    }

    // Coercion Would Occur Here!
  }
}
