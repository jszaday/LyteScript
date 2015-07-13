package com.lyte.core;

import com.lyte.objs.LyteError;
import com.lyte.objs.LyteValue;
import com.lyte.utils.LyteAppliable;

public abstract class LyteStatement implements LyteAppliable {

  private String mLineNumber;

  public LyteStatement(String lineNumber) {
    mLineNumber = lineNumber;
  }

  public abstract void applyTo(LyteContext context);

  @Override
  public LyteValue apply(LyteContext context) {
    LyteStack stack = context.stack;
    int origStackSize = stack.size();
    this.applyTo(context);
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

  @Override
  public String typeOf() {
    return "statement";
  }

  @Override
  public boolean is(String type) {
    return type.equals(typeOf());
  }
}
