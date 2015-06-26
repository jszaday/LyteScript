package com.lyte.core;

import com.lyte.objs.*;
import org.apache.commons.collections4.iterators.PeekingIterator;

import java.util.ArrayList;
import java.util.Iterator;
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
  public void applyTo(LyteValue self, LyteScope scope, LyteStack stack) {
    if (isSimpleInvokation()) {
      LyteValue value = scope.getVariable(self, stack, mPrimaryIdentifier);
      if (value.typeOf().equals("block")) {
        ((LyteBlock) value).invoke(self, stack);
      } else {
        stack.push(value);
      }
    } else {
      LyteValue retVal = resolve(self, scope, stack, true);
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

  public LyteValue resolve(LyteValue self, LyteScope scope, LyteStack stack, boolean fullyResolve) {
    PeekingIterator<LyteSpecifier> specifierIterator = new PeekingIterator<LyteSpecifier>(mSpecifiers.subList(0, mSpecifiers.size() - (fullyResolve ? 0 : 1)).iterator());
    LyteValue obj, lastObj = self;
    try {
      obj = scope.getVariable(lastObj, stack, mPrimaryIdentifier);

      if (shouldApply(specifierIterator)) {
        obj = obj.apply(lastObj, stack);
      }

      if (!shouldApply(specifierIterator)) {
        if (mPrimaryIdentifier.startsWith("#")) {
          lastObj = stack.peek();
        } else if (mPrimaryIdentifier.startsWith("@") || obj.typeOf().equals("block")) {
          lastObj = self;
        }
      } else {
        lastObj = obj;
      }

      while (specifierIterator.hasNext()) {
        LyteSpecifier specifier = specifierIterator.next();

        if (specifier.identifier != null) {
          obj = obj.getProperty(specifier.identifier);
        } else if (specifier.invokable != null) {
          obj = obj.getProperty(specifier.invokable.apply(obj, scope).toString());
        } else {
          List<LyteValue> arguments = new ArrayList<LyteValue>();
          // Add a clone of each of the arguments to the function
          for (LyteRawBlock argument : specifier.arguments) {
            // TODO Check if the ordering is correct & ensure that the block only has one result
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

        if (shouldApply(specifierIterator) && obj != null) {
          lastObj = obj = obj.apply(lastObj, stack);
        }
      }
    } catch (LyteError e) {
      throw e;
    } catch (Exception e) {
      throw new LyteError(e.getMessage());
    }
    return obj;
  }

  private static boolean shouldApply(PeekingIterator<LyteSpecifier> specifierIterator) {
    return !specifierIterator.hasNext() || (specifierIterator.hasNext() && (specifierIterator.peek().arguments == null));
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
