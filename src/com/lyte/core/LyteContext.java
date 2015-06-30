package com.lyte.core;

import com.lyte.objs.LyteBlock;
import com.lyte.objs.LyteError;
import com.lyte.objs.LyteRawBlock;
import com.lyte.objs.LyteValue;
import org.apache.commons.collections4.iterators.PeekingIterator;

import java.util.ArrayList;
import java.util.List;

import static com.lyte.core.LyteInvokeStatement.LyteSpecifier;

/**
 * Created by a0225785 on 6/30/2015.
 */
public class LyteContext {
  public LyteValue self;
  public LyteScope scope;
  public LyteStack stack;

  public LyteContext(LyteValue self, LyteScope scope, LyteStack stack) {
    this.self = self;
    this.scope = scope;
    this.stack = stack;
  }

  public LyteValue apply() {
    return stack.pop().apply(this);
  }

  public LyteValue get(String name) {
    if (name.startsWith("@")) {
      if (self == null) {
        throw new LyteError("Cannot resolve property " + name);
      }
      return self.getProperty(name.substring(1, name.length()));
    } else if (name.startsWith("#")) {
      if (stack == null || stack.isEmpty()) {
        throw new LyteError("Cannot resolve property " + name);
      }
      return stack.peek().getProperty(name.substring(1, name.length()));
    } else {
      return scope.getVariable(name);
    }
  }

  public boolean has(String name) {
    if (name.startsWith("@")) {
      if (self == null) {
        return false;
      } else {
        return self.hasProperty(name.substring(1, name.length()));
      }
    } else if (name.startsWith("#")) {
      if (stack == null || stack.isEmpty()) {
        return false;
      } else {
        return stack.peek().hasProperty(name.substring(1, name.length()));
      }
    } else {
      return scope.hasVariable(name);
    }
  }

  public void set(String name, LyteValue value) {
    if (name.startsWith("@")) {
      if (self == null) {
        throw new LyteError("Cannot set property " + name);
      }
      self.setProperty(name.substring(1, name.length()), value);
    } else if (name.startsWith("#")) {
      if (stack == null || stack.isEmpty()) {
        throw new LyteError("Cannot set property " + name);
      }
      stack.peek().setProperty(name.substring(1, name.length()), value);
    } else {
      scope.putVariable(name, value, false);
    }
  }

  public void set(String name) {
    set(name, stack.pop());
  }

  public LyteValue resolve(LyteInvokeStatement invokeStatement, boolean fullyResolve) {
    String primaryIdentifier = invokeStatement.getPrimaryIdentifier();
    PeekingIterator<LyteSpecifier> specifierIterator = invokeStatement.getSpecifiersIterator(fullyResolve ? 0 : 1);
    LyteValue obj, lastObj = self;

    try {
      obj = get(primaryIdentifier);

      if (shouldApply(specifierIterator)) {
        obj = obj.apply(new LyteContext(lastObj, scope, stack));
      }

      if (!shouldApply(specifierIterator)) {
        if (primaryIdentifier.startsWith("#")) {
          lastObj = stack.peek();
        } else if (primaryIdentifier.startsWith("@") || obj.typeOf().equals("block")) {
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
          obj = obj.getProperty(specifier.invokable.apply(this).toString());
        } else {
          List<LyteValue> arguments = new ArrayList<LyteValue>();
          // Add a clone of each of the arguments to the function
          for (LyteRawBlock argument : specifier.arguments) {
            // TODO Check if the ordering is correct & ensure that the block only has one result
            arguments.add(argument.clone(this));
          }
          // Then invoke the block itself w/ those arguments
          ((LyteBlock) obj).invoke(new LyteContext(lastObj, null, stack), arguments);
          // And pop the result into the object
          if (!stack.isEmpty()) {
            obj = stack.pop();
          } else {
            obj = null;
          }
        }

        if (shouldApply(specifierIterator) && obj != null) {
          lastObj = obj = obj.apply(new LyteContext(lastObj, scope, stack));
        }
      }
    } catch (LyteError e) {
      throw e;
    } catch (Exception e) {
      e.printStackTrace();
      throw new LyteError(e.getMessage());
    }

    return obj;
  }

  private static boolean shouldApply(PeekingIterator<LyteSpecifier> specifierIterator) {
    return !specifierIterator.hasNext() || (specifierIterator.hasNext() && (specifierIterator.peek().arguments == null));
  }
}
