package com.lyte.objs;

import com.lyte.core.LyteScope;
import com.lyte.core.LyteStatement;

/**
 * Created by a0225785 on 6/17/2015.
 */
public class LyteBlock implements LyteValue {

  private LyteScope mScope;
  private String[] mArgs;
  private LyteStatement[] mStatements;

  public LyteBlock(LyteScope parentScope,  LyteStatement[] statements) {
    this(parentScope, statements, new String[0]);
  }

  public LyteBlock(LyteScope parentScope,  LyteStatement[] statements, String[] args) {
    mScope = new LyteScope(parentScope, true);
    mStatements = statements;
    mArgs = args;
  }

  public LyteScope getScope() {
    return mScope;
  }

  private void popArgs() {
    // For each of our args
    for (String arg : mArgs) {
      // Pop off a value and bind it to the arg's name
      mScope.putVariable(arg, mScope.pop());
    }
  }

  public void invoke(LyteValue... args) {
    // Push each of the arguments onto the stack
    for (LyteValue arg : args) {
      mScope.push(arg);
    }
    // Pop any named arguments
    popArgs();
    // Then apply each of our statements to our scope
    for (LyteStatement statement : mStatements) {
      statement.applyTo(mScope);
    }
  }

  @Override
  public LyteValue clone(LyteScope scope) {
    return null;
  }

  @Override
  public String typeOf() {
    return "block";
  }
}
