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
  protected LyteScope mScope;

  public LyteBlock(LyteScope parentScope, List<LyteStatement> statements) {
    this(parentScope, statements, null, true);
  }

  public LyteBlock(LyteScope parentScope, List<LyteStatement> statements, List<String> args, boolean shouldEnter) {
    super(statements);
    if ((parentScope != null) && shouldEnter) {
      mScope = parentScope.enter();
    } else {
      mScope = parentScope;
    }
    mArgs = args;
  }

  private void popArgs(LyteValue self, LyteStack stack) {
    if (mArgs == null) {
      return;
    }
    // For each of our args
    for (String arg : mArgs) {
      // Pop off a value and bind it to the arg's name
      mScope.putVariable(self, stack, arg, stack.pop());
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
    // Pop any named arguments
    popArgs(self, stack);
    // Then apply each of our statements to our scope
    Iterator<LyteStatement> statementIterator = get().iterator();
    while (statementIterator.hasNext()) {
      statement = statementIterator.next();
      try {
        statement.applyTo(self, mScope, stack);
      } catch (LyteError e) {
        e.addLineNumber(statement.getLineNumber());
        throw e;
      }
    }
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
    return new LyteBlock(mScope.clone(), get(), mArgs, false);
  }

  @Override
  public LyteValue apply(LyteValue self) {
    LyteStack stack = new LyteStack();
    invoke(self, stack);
    if (stack.size() > 1) {
      throw new LyteError("Error Applying Block, " + this + ", expected 1 return value instead found " + stack.size() + "!");
    } else if (!stack.isEmpty()) {
      return stack.pop();
    } else {
      return LyteUndefined.UNDEFINED;
    }
  }
}
