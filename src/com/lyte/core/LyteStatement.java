package com.lyte.core;

public abstract class LyteStatement {

  private String mLineNumber;

  public LyteStatement(String lineNumber) {
    mLineNumber = lineNumber;
  }

  public abstract void applyTo(LyteScope scope, LyteStack stack);
  
  public String getLineNumber() {
    return mLineNumber;
  }
}
