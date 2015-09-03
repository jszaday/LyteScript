package com.lyte.core;

import com.lyte.objs.LyteError;
import com.lyte.objs.LyteObject;
import com.lyte.objs.LyteValue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * LyteBindStatement
 * 	A statement representing a "bind" operation.
 * @author Justin Szaday
 */
public class LyteBindStatement extends LyteStatement {

  private List<LyteInvokeStatement> mTargets;

  /**
   * A constructor that accepts multiple target expressions
   *
   * @param lineNumber The line number the statement represents
   * @param targets    The expression(s) that this operation stores its result(s) in
   */
  public LyteBindStatement(String lineNumber, List<LyteInvokeStatement> targets) {
    super(lineNumber);
    for (LyteInvokeStatement target : targets) {
      if (target.isFunctionInvokation()) {
        throw new LyteError("Cannot assign directly to the result of a function.");
      }
    }
    mTargets = targets;
  }

  /**
   * A constructor that accepts only one target expression
   *
   * @param lineNumber The line number the statement represents
   * @param target     The expression that this operation stores its result in
   */
  public LyteBindStatement(String lineNumber, LyteInvokeStatement target) {
    this(lineNumber, Arrays.asList(target));
  }

  /**
   * Returns the statement as a String
   */
  @Override
  public String toString() {
    return "Bind: " + mTargets.toString();
  }

  /**
   * Applies this operation to a given context
   */
  @Override
  public void applyTo(LyteContext context) {
    // For each of the targets
    for (LyteInvokeStatement target : mTargets) {
      // If the target is a simple assignment (something like var)
      if (target.isSimpleAssignment()) {
        // Then simply set the value of the target (via it's primary identifier)
        context.set(target.getPrimaryIdentifier());
      } else {
        // Otherwise, pop a value off the stack
        LyteValue newValue = context.pop();
        // And resolve the target object within the context
        LyteValue targetObj = context.resolve(target, false);
        // Then, get the last specifier (like in the case of obj.lastIdentifier)
        LyteInvokeStatement.LyteSpecifier specifier = target.getLastSpecifier();
        // If the specifier is an identifier
        if (specifier.identifier != null) {
          // Simply set the property of the target object to the new value
          targetObj.setProperty(specifier.identifier, newValue);
        } else {
          /* Otherwise, we have to treat the specifier as an invokable and apply it
           * (this occurs in the case of something like list[0], where 0 is an invokable)
           */
          targetObj.setProperty(specifier.invokables.apply(context).toString(), newValue);
        }
      }
    }
  }
}
