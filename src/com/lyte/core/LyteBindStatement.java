package com.lyte.core;

import com.lyte.objs.LyteObject;

/**
 * Created by jszaday on 6/18/15.
 */
public class LyteBindStatement implements LyteStatement {

  private LyteInvokeStatement mTarget;

  public LyteBindStatement(LyteInvokeStatement target) {
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
  public void applyTo(LyteScope scope) {
    System.out.println("Popping '" + scope.peek() + "' into " + mTarget);
    if (mTarget.isSimpleInvokation() && !scope.hasVariable(mTarget.getPrimaryIdentifier())) {
      scope.putVariable(mTarget.getPrimaryIdentifier(), scope.pop());
    } else {
      LyteObject obj = mTarget.resolveToObject(scope);
      LyteInvokeStatement.LyteSpecifier specifier = mTarget.getLastSpecifier();
      if (specifier.identifier != null) {
        obj.set(specifier.identifier, scope.pop());
      } else {
        // TODO Ensure the invokable only pops one result
        specifier.invokable.applyTo(scope);
        // TODO Double Check the key is correct
        obj.set(scope.pop().toString(), scope.pop());
      }
    }
  }
}
