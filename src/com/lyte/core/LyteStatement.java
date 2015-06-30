package com.lyte.core;

import com.lyte.objs.LyteError;
import com.lyte.objs.LyteValue;

public abstract class LyteStatement {

  private String mLineNumber;

  public LyteStatement(String lineNumber) {
    mLineNumber = lineNumber;
  }

  public abstract void applyTo(LyteStack stack);

  public LyteValue apply(LyteStack stack) {
    int origStackSize = stack.size();
    this.applyTo(stack);
    if ((stack.size() - origStackSize) > 1) {
      throw new LyteError("Error Applying Statement, " + this + ", expected 1 return value instead found " + (stack.size() - origStackSize) + "!");
    } else if (!stack.isEmpty()) {
      return stack.pop();
    } else {
      return null;
    }
  }

  public String getLineNumber() {
    return mLineNumber;
  }
}
