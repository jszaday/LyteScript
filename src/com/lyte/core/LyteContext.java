package com.lyte.core;

import com.lyte.objs.*;
import com.lyte.stdlib.LyteReflectionFunctions;
import com.lyte.utils.LyteInjectable;
import com.lyte.utils.LyteYieldListener;
import org.apache.commons.collections4.iterators.PeekingIterator;

import java.util.LinkedList;

import static com.lyte.core.LyteInvokeStatement.LyteSpecifier;

/**
 * Created by jszaday on 6/30/2015.
 */
public class LyteContext implements LyteInjectable {
  public LyteValue self;
  public LyteScope scope;
  public LyteStack stack;
  private LyteYieldListener listener;

  public LyteContext(LyteValue self, LyteScope scope, LyteStack stack) {
    this(self, scope, stack, null);
  }

  public LyteContext(LyteValue self, LyteScope scope, LyteStack stack, LyteYieldListener listener) {
    this.self = self;
    this.scope = scope;
    this.stack = stack;
    this.listener = listener;
  }

  public LyteContext(LyteValue self, LyteContext context) {
    this(self, context.scope, context.stack, context.listener);
  }

  public LyteContext(LyteValue self, LyteValue... args) {
    this(self, null, new LyteStack(args));
  }

  public void push(String string) {
    push(new LyteString(string));
  }

  public void push(Double number) {
    push(LyteNumber.valueOf(number));
  }

  public void push(Boolean bool) {
    push(LyteBoolean.valueOf(bool));
  }

  public void push(Integer integer) {
    push(LyteNumber.valueOf(integer));
  }

  public void push(Long number) {
    push(LyteNumber.valueOf(number.doubleValue()));
  }

  public void push(LyteValue value) {
    stack.push(value);
  }

  public LyteValue pop() {
    return stack.pop();
  }

  public LyteValue peek() {
    return stack.peek();
  }

  public LyteValue apply() {
    return stack.pop().apply(this);
  }

  public boolean isEmpty() {
    return stack.isEmpty();
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
    set(name, value, false);
  }

  public void set(String name) {
    set(name, stack.pop());
  }

  public void set(String name, boolean localOnly) {
    if (localOnly) {
      scope.putLocalVariable(name, stack.pop(), false);
    } else {
      set(name, stack.pop());
    }
  }

  public void set(String name, LyteValue value, boolean finalize) {
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
      scope.putVariable(name, value, finalize);
    }
  }

  public LyteValue resolve(LyteInvokeStatement invokeStatement, boolean fullyResolve) {
    return resolve(invokeStatement, fullyResolve, true);
  }

  public LyteValue resolve(LyteInvokeStatement invokeStatement, boolean fullyResolve, boolean applyLast) {
    String primaryIdentifier = invokeStatement.getPrimaryIdentifier();
    PeekingIterator<LyteSpecifier> specifierIterator = invokeStatement.getSpecifiersIterator(fullyResolve ? 0 : 1);
    LyteValue obj, lastObj = primaryIdentifier.startsWith("#") ? stack.peek() : self;

    try {
      if (primaryIdentifier.equals("#") || primaryIdentifier.equals("@")) {
        obj = lastObj;
      } else {
        obj = get(primaryIdentifier);
      }

      if (shouldApply(specifierIterator, applyLast)) {
        obj = obj.apply(new LyteContext(lastObj, scope, stack));
      }

      if (!shouldApply(specifierIterator, applyLast)) {
        if (primaryIdentifier.startsWith("#")) {
          lastObj = stack.peek();
        } else if (primaryIdentifier.startsWith("@") || obj.is("block")) {
          lastObj = self;
        }

        if (obj.is("block")) {
          ((LyteBlock) obj).pushObjContext(lastObj);
        }
      } else {
        lastObj = obj;
      }

      while (specifierIterator.hasNext()) {
        LyteSpecifier specifier = specifierIterator.next();

        if (specifier.identifier != null) {
          obj = obj.getProperty(specifier.identifier);
        } else if (specifier.invokables != null) {
          obj = obj.getProperty(specifier.invokables.apply(this).toString());
        } else {
          // Add a clone of each of the arguments to the function
          for (int i = (specifier.arguments.size() - 1); i >= 0; i--) {
            stack.push(specifier.arguments.get(i).clone(this, true, true));
          }
        }

        if (obj == null) {
          continue;
        } else if (shouldApply(specifierIterator, applyLast)) {
          if (specifier.arguments != null) {
            if (obj == LyteReflectionFunctions.reflectGet || obj == LyteReflectionFunctions.reflectEval) {
              obj = obj.apply(this);
            } else {
              obj = obj.apply(new LyteContext(lastObj, scope, stack));
            }
          }
          lastObj = obj = obj.apply(new LyteContext(lastObj, scope, stack));
        } else if (obj.is("block")) {
          ((LyteBlock) obj).pushObjContext(lastObj);
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

  public void yield() {
    yield(apply());
  }

  public void yield(LyteValue value) {
    if (listener != null) {
      listener.onYield(value);
    } else {
      throw new LyteError("Unable to yield to anything.");
    }
  }

  public void setListener(LyteYieldListener listener) {
    this.listener = listener;
  }

  public LyteContext enter(LyteContext context, boolean shouldEnter, LyteValue otherSelf) {
    return new LyteContext((otherSelf != null) ? otherSelf : context.self, shouldEnter ? scope.enter() : scope, context.stack, context.listener);
  }

  private static boolean shouldApply(PeekingIterator<LyteSpecifier> specifierIterator, boolean applyLast) {
    return (!specifierIterator.hasNext() && applyLast) || (specifierIterator.hasNext() && (specifierIterator.peek().arguments == null));
  }

  @Override
  public void inject(String name, LyteValue value) {
    scope.inject(name, value);
  }
}
