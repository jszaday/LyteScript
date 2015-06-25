package com.lyte.core;

import com.lyte.objs.LyteObject;
import com.lyte.objs.LyteValue;

/**
 * Created by jszaday on 6/18/15.
 */
public class LyteBindStatement extends LyteStatement {

  private LyteInvokeStatement mTarget;

  public LyteBindStatement(String lineNumber, LyteInvokeStatement target) {
    super(lineNumber);
    if (target.isFunctionInvokation()) {
      throw new RuntimeException("Cannot bind (directly) to the result of a function call!");
    } else {
      mTarget = target;
    }
  }

  @Override
  public String toString() {
    return "Bind: " + mTarget.toString(false);
  }

  @Override
  public void applyTo(LyteScope scope, LyteStack stack) {
    if (mTarget.isSimpleInvokation()) {
      scope.putVariable(mTarget.getPrimaryIdentifier(), stack.pop());
    } else {
      LyteValue val = mTarget.resolve(scope, stack, false);
      LyteInvokeStatement.LyteSpecifier specifier = mTarget.getLastSpecifier();
      if (specifier.identifier != null) {
        val.setProperty(specifier.identifier, stack.pop());
      } else {
        // TODO Ensure the invokable only pops one result
        specifier.invokable.applyTo(scope, stack);
        // TODO Double Check the key is correct
        val.setProperty(stack.pop().toString(), stack.pop());
      }
    }
  }
}
