package com.lyte.objs;

import com.lyte.core.LyteScope;
import com.lyte.core.LyteStack;
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
    this(parentScope, statements, null, true);
  }

  public LyteBlock(LyteScope parentScope, List<LyteStatement> statements, List<String> args, boolean shouldEnter) {
    if ((parentScope != null) && shouldEnter) {
      mScope = parentScope.enter();
    } else {
      mScope = parentScope;
    }
    mStatements = statements;
    mArgs = args;
  }

  private void popArgs(LyteStack stack) {
    if (mArgs == null) {
      return;
    }
    // For each of our args
    for (String arg : mArgs) {
      // Pop off a value and bind it to the arg's name
      mScope.putVariable(arg, stack.pop());
    }
  }

  public void invoke(LyteObject self, LyteStack stack, LyteValue... args) {
    invoke(self, stack, Arrays.asList(args));
  }

  public void invoke(LyteObject self, LyteStack stack, List<LyteValue> args) {
    for (int i = (args.size() - 1); i >= 0; i--) {
      stack.push(args.get(i));
    }
    invoke(self, stack);
  }

  public void invoke(LyteObject self, LyteStack stack) {
    // Set the "self" object
    mScope.setSelf(self);
    // Pop any named arguments
    popArgs(stack);
    // Then apply each of our statements to our scope
    for (LyteStatement statement : mStatements) {
      statement.applyTo(mScope, stack);
    }
  }

  @Override
  public LyteBoolean toBoolean() {
    return new LyteBoolean(true);
  }

  @Override
  public LyteNumber toNumber() {
    return new LyteNumber(0);
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
