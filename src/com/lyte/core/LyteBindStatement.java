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
  public void applyTo(LyteContext context) {
    if (mTarget.isSimpleInvokation()) {
      context.set(mTarget.getPrimaryIdentifier());
    } else {
      LyteValue val = context.resolve(mTarget, false);
      LyteInvokeStatement.LyteSpecifier specifier = mTarget.getLastSpecifier();
      if (specifier.identifier != null) {
        val.setProperty(specifier.identifier, context.stack.pop());
      } else {
        val.setProperty(specifier.invokable.apply(context).toString(), context.stack.pop());
      }
    }
  }
}
