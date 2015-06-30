package com.lyte.objs;

import com.lyte.core.LyteScope;
import com.lyte.core.LyteStack;
import com.lyte.core.LyteStatement;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * Created by a0225785 on 6/17/2015.
 */
public class LyteBlock extends LytePrimitive<List<LyteStatement>> {

  private List<String> mArgs;
  protected LyteScope mParentScope;
  protected LyteScope mScope;
  private boolean mCanEnter;

  public LyteBlock(LyteScope parentScope, List<LyteStatement> statements) {
    this(parentScope, statements, null, true);
  }

  public LyteBlock(LyteScope parentScope, List<LyteStatement> statements, List<String> args, boolean canEnter) {
    super(statements);
    mParentScope = parentScope;
    mArgs = args;
    mCanEnter = canEnter;
  }

  private void popArgs(LyteStack stack) {
    if (mArgs == null) {
      return;
    }
    // For each of our args
    for (String arg : mArgs) {
      // Pop off a value and bind it to the arg's name
      mScope.putVariable(arg, stack.pop(), false);
    }
  }

  public void invoke(LyteValue self, LyteStack stack, LyteValue... args) {
    invoke(self, stack, Arrays.asList(args));
  }

  public void invoke(LyteValue self, LyteStack stack, List<LyteValue> args) {
    for (int i = (args.size() - 1); i >= 0; i--) {
      stack.push(args.get(i));
    }
    invoke(self, stack);
  }

  public void invoke(LyteValue self, LyteStack stack) {
    LyteStatement statement = null;
    // Enter a new scope
    if (mCanEnter) {
      mScope = mParentScope.enter();
    } else {
      mScope = mParentScope;
    }
    // Enter the current Context
    stack.enterContext(mScope, self);
    // Pop any named arguments
    popArgs(stack);
    // Then apply each of our statements to our scope
    Iterator<LyteStatement> statementIterator = get().iterator();
    while (statementIterator.hasNext()) {
      statement = statementIterator.next();
      try {
        statement.applyTo(stack);
      } catch (LyteError e) {
        e.addLineNumber(statement.getLineNumber());
        throw e;
      }
    }
    // Leave the current Context
    stack.leaveContext();
  }

  @Override
  public String typeOf() {
    return "block";
  }

  @Override
  public String toString() {
    return (mArgs == null ? "[]" : mArgs) + " => " + get();
  }

  @Override
  public boolean toBoolean() {
    return true;
  }

  @Override
  public double toNumber() {
    return get().size();
  }

  @Override
  public LyteValue<List<LyteStatement>> clone(LyteScope scope) {
    return new LyteBlock(mParentScope, get(), mArgs, true);
  }

  public LyteValue<List<LyteStatement>> clone(LyteScope scope, boolean canEnter) {
    return new LyteBlock(mParentScope, get(), mArgs, canEnter);
  }

  @Override
  public LyteValue apply(LyteStack stack) {
    int origStackSize = stack.size();
    invoke(stack.getCurrentSelf(), stack);
    if ((stack.size() - origStackSize) > 1) {
      throw new LyteError("Error Applying Block, " + this + ", expected 1 return value instead found " + (stack.size() - origStackSize) + "!");
    } else if (!stack.isEmpty()) {
      return stack.pop();
    } else {
      return null;
    }
  }
}
