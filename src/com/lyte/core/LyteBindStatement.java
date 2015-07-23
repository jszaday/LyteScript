package com.lyte.core;

import com.lyte.objs.LyteError;
import com.lyte.objs.LyteObject;
import com.lyte.objs.LyteValue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by jszaday on 6/18/15.
 */
public class LyteBindStatement extends LyteStatement {

  private List<LyteInvokeStatement> mTargets;

  public LyteBindStatement(String lineNumber, List<LyteInvokeStatement> targets) {
    super(lineNumber);
    for (LyteInvokeStatement target : targets) {
      if (target.isFunctionInvokation()) {
        throw new LyteError("Cannot assign directly to the result of a function.");
      }
    }
    mTargets = targets;
  }

  public LyteBindStatement(String lineNumber, LyteInvokeStatement target) {
    this(lineNumber, Arrays.asList(target));
  }

  @Override
  public String toString() {
    return "Bind: " + mTargets.toString();
  }

  @Override
  public void applyTo(LyteContext context) {
    for (LyteInvokeStatement target : mTargets) {
      if (target.isSimpleAssignment()) {
        context.set(target.getPrimaryIdentifier());
      } else {
        LyteValue newValue = context.stack.pop();
        LyteValue targetObj = context.resolve(target, false);
        LyteInvokeStatement.LyteSpecifier specifier = target.getLastSpecifier();
        if (specifier.identifier != null) {
          targetObj.setProperty(specifier.identifier, newValue);
        } else {
          targetObj.setProperty(specifier.invokables.apply(context).toString(), newValue);
        }
      }
    }
  }
}
