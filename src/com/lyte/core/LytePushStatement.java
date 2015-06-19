package com.lyte.core;

import com.lyte.objs.LyteValue;

public class LytePushStatement implements LyteStatement {

  private LyteValue mValue;

  public LytePushStatement(LyteValue value) {
    mValue = value;
  }

  @Override
  public void applyTo(LyteScope scope) {
    // Instantiate a new instance of the object
    LyteValue obj = mValue.clone(scope);
    // And push it onto the stack
    scope.push(obj);
  }

  @Override
  public String toString() {
    return "Push: " + mValue;
  }
}
