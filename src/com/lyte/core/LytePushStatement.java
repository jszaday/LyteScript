package com.lyte.core;

import com.lyte.objs.LyteValue;

public class LytePushStatement extends LyteStatement {

  private LyteValue mValue;

  public LytePushStatement(String lineNumber, LyteValue value) {
    super(lineNumber);
    mValue = value;
  }

  public LyteValue getValue() {
    return mValue;
  }

  @Override
  public void applyTo(LyteScope scope, LyteStack stack) {
    // Instantiate a new instance of the object
    LyteValue obj = mValue.clone(scope);
    // And push it onto the stack
    stack.push(obj);
  }

  @Override
  public String toString() {
    return "Push: " + mValue;
  }
}
