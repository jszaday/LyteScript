package com.lyte.objs;

import com.lyte.core.LyteScope;
import com.lyte.core.LyteStatement;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by a0225785 on 6/17/2015.
 */
public class LyteBlock implements LyteValue {

  private List<String> mArgs;
  private List<LyteStatement> mStatements;
  protected LyteScope mScope;

  public LyteBlock(LyteScope parentScope, List<LyteStatement> statements) {
    this(parentScope, statements, null);
  }

  public LyteBlock(LyteScope parentScope, List<LyteStatement> statements, List<String> args) {
    mScope = parentScope.enter();
    mStatements = statements;
    mArgs = args;
  }

  private void popArgs() {
    if (mArgs == null) {
      return;
    }
    // For each of our args
    for (String arg : mArgs) {
      // Pop off a value and bind it to the arg's name
      mScope.putVariable(arg, mScope.pop());
    }
  }

  public void invoke(LyteValue... args) {
    invoke(Arrays.asList(args));
  }

  public void invoke(List<LyteValue> args) {
    for (int i = (args.size() - 1); i >= 0; i--) {
      mScope.push(args.get(i));
    }
    invoke();
  }

  public void invoke() {
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

  @Override
  public String toString() {
    return (mArgs == null ? "[]" : mArgs) + " => " + mStatements;
  }
}
