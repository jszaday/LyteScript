package com.lyte.core;

import com.lyte.objs.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by a0225785 on 6/17/2015.
 */
public class LyteInvokeStatement extends LyteStatement {

  private String mPrimaryIdentifier;
  private List<LyteSpecifier> mSpecifiers;

  public LyteInvokeStatement(String lineNumber, String primaryIdentifier) {
    this(lineNumber, primaryIdentifier, new ArrayList<LyteSpecifier>());
  }

  public LyteInvokeStatement(String lineNumber, String primaryIdentifier, List<LyteSpecifier> specifiers) {
    super(lineNumber);
    mPrimaryIdentifier = primaryIdentifier;
    mSpecifiers = specifiers;
  }

  @Override
  public void applyTo(LyteScope scope, LyteStack stack) {
    System.out.println(this + " || Stack: " + stack);
    if (isSimpleInvokation()) {
      LyteValue value = scope.getVariable(mPrimaryIdentifier);
      if (value.typeOf().equals("block")) {
        ((LyteBlock) value).invoke(null, stack);
      } else {
        stack.push(value);
      }
    } else {
      LyteValue retVal = resolve(scope, stack, true);
      if (retVal != null) {
        stack.push(retVal);
      }
    }
  }

  public boolean isSimpleInvokation() {
    return mSpecifiers.isEmpty();
  }

  public String getPrimaryIdentifier() {
    return mPrimaryIdentifier;
  }

  public LyteSpecifier getLastSpecifier() {
    return mSpecifiers.get(mSpecifiers.size() - 1);
  }

  public LyteValue resolve(LyteScope scope, LyteStack stack, boolean fullyResolve) {
    LyteValue obj, lastObj;
    // Adjust the offset we are to fully resolve ourself
    int offset = fullyResolve ? 1 : 2;
    try {
      // Adjust the last object
      obj = scope.getVariable(mPrimaryIdentifier);
      // Applying it if necessary
      if ((!mSpecifiers.isEmpty() && (mSpecifiers.get(0).arguments == null)) || mSpecifiers.isEmpty()) {
        obj = obj.apply(null);
      }
      // And adjust the last object
      if (!obj.typeOf().equals("block")) {
        lastObj = obj;
      } else {
        lastObj = scope.getSelf();
      }
      for (int i = 0; i <= (mSpecifiers.size() - offset); i++) {
        LyteSpecifier specifier = mSpecifiers.get(i);
        boolean lastSpecifier = ((i + 1) >= mSpecifiers.size()) || (((i + 1) < mSpecifiers.size() && mSpecifiers.get(i + 1).arguments != null));
        if (specifier.identifier != null) {
          obj = obj.getProperty(specifier.identifier);
        } else if (specifier.invokable != null) {
          // TODO Ensure only one result is pushed onto the stack
          specifier.invokable.applyTo(scope, stack);
          obj = obj.getProperty(stack.pop().toString());
        } else {
          List<LyteValue> arguments = new ArrayList<LyteValue>();
          // TODO Check if the ordering is correct & ensure that the block only has one result
          // Add a clone of each of the arguments to the function
          for (LyteRawBlock argument : specifier.arguments) {
            arguments.add(argument.clone(scope));
          }
          // Then invoke the block itself w/ those arguments
          ((LyteBlock) obj).invoke(lastObj, stack, arguments);
          // And pop the result into the object
          if (!stack.isEmpty()) {
            obj = stack.pop();
          } else {
            obj = null;
          }
        }
        // Finally, apply the object if necessary (only when the next specifier is not an argument)
        if (!lastSpecifier) {
          lastObj = obj = obj.apply(lastObj);
        }
      }
    } catch (LyteError e) {
      throw e;
    } catch (Exception e) {
      throw new LyteError(e.getMessage());
    }
    return obj;
  }

  public boolean isFunctionInvokation() {
    return !isSimpleInvokation() && (getLastSpecifier().arguments != null);
  }

  public String toString(boolean withDescription) {
    if (withDescription) {
      return "Invoke: " + mPrimaryIdentifier + mSpecifiers;
    } else {
      return mPrimaryIdentifier + mSpecifiers;
    }
  }

  @Override
  public String toString() {
    return toString(true);
  }

  public static class LyteSpecifier {
    public final String identifier;
    public final LyteStatement invokable;
    public final List<LyteRawBlock> arguments;

    private LyteSpecifier(String identifier, LyteStatement invokable, List<LyteRawBlock> arguments) {
      this.identifier = identifier;
      this.invokable = invokable;
      this.arguments = arguments;
    }

    public LyteSpecifier(String identifier) {
      this(identifier, null, null);
    }

    public LyteSpecifier(LyteStatement invokable) {
      this(null, invokable, null);
    }

    public LyteSpecifier(List<LyteRawBlock> arguments) {
      this(null, null, arguments);
    }

    @Override
    public String toString() {
      if (identifier != null) {
        return "." + identifier;
      } else if (invokable != null) {
        return "[" + invokable + "]";
      } else {
        return "(" + arguments + ")";
      }
    }
  }
}
