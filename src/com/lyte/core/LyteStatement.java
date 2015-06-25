package com.lyte.core;

import com.lyte.objs.LyteError;
import com.lyte.objs.LyteValue;

public abstract class LyteStatement {

  private String mLineNumber;

  public LyteStatement(String lineNumber) {
    mLineNumber = lineNumber;
  }

  public abstract void applyTo(LyteValue self, LyteScope scope, LyteStack stack);

  public LyteValue apply(LyteValue self, LyteScope scope) {
    LyteStack stack = new LyteStack();
    this.applyTo(self, scope, stack);
    if (stack.size() != 1) {
      throw new LyteError("Expected 1 Return Value from " + toString() + " instead found " + stack.size());
    }
    return stack.pop();
  }

  public String getLineNumber() {
    return mLineNumber;
  }
}
