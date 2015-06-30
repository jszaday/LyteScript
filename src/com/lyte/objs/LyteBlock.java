package com.lyte.objs;

import com.lyte.core.LyteContext;
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

  private void popArgs(LyteContext context) {
    if (mArgs == null) {
      return;
    }
    // For each of our args
    for (String arg : mArgs) {
      // Pop off a value and bind it to the arg's name
      context.set(arg);
    }
  }

  public void invoke(LyteContext context, LyteValue... args) {
    invoke(context, Arrays.asList(args));
  }

  public void invoke(LyteContext context, List<LyteValue> args) {
    for (int i = (args.size() - 1); i >= 0; i--) {
      context.stack.push(args.get(i));
    }
    invoke(context);
  }

  public void invoke(LyteContext context) {
    LyteScope originalScope = context.scope;
    LyteStatement statement = null;
    context.scope = mParentScope;
    // Enter a new scope
    if (mCanEnter) {
      context.scope = context.scope.enter();
    }
    // Pop any named arguments
    popArgs(context);
    // Then apply each of our statements to our scope
    Iterator<LyteStatement> statementIterator = get().iterator();
    while (statementIterator.hasNext()) {
      statement = statementIterator.next();
      try {
        statement.applyTo(context);
      } catch (LyteError e) {
        e.addLineNumber(statement.getLineNumber());
        throw e;
      }
    }
    context.scope = originalScope;
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
  public LyteValue<List<LyteStatement>> clone(LyteContext context) {
    return new LyteBlock(mParentScope, get(), mArgs, true);
  }

  public LyteValue<List<LyteStatement>> clone(LyteContext context, boolean canEnter) {
    return new LyteBlock(mParentScope, get(), mArgs, canEnter);
  }

  @Override
  public LyteValue apply(LyteContext context) {
    LyteStack stack = context.stack;
    int origStackSize = stack.size();
    invoke(context);
    if ((stack.size() - origStackSize) > 1) {
      throw new LyteError("Error Applying Block, " + this + ", expected 1 return value instead found " + (stack.size() - origStackSize) + "!");
    } else if (!stack.isEmpty()) {
      return stack.pop();
    } else {
      return null;
    }
  }
}
