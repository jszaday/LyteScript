package com.lyte.objs;

import com.lyte.core.LyteScope;
import com.lyte.core.LyteStack;
import com.lyte.core.LyteStatement;

import java.util.HashMap;

public class LyteRawObject implements LyteValue  {
  private LyteScope mCachedScope;

  private HashMap<String, LyteStatement> mProperties;

  public LyteRawObject() {
    mProperties = new HashMap<String, LyteStatement>();
  }

  public LyteStatement set(String key, LyteStatement value) {
    return mProperties.put(key, value);
  }

  public LyteValue clone() {
    if (mCachedScope == null) {
      throw new RuntimeException("No cached scope found for Raw Object.");
    } else {
      return clone(mCachedScope);
    }
  }

  @Override
  public boolean isTruthy() {
    return false;
  }

  @Override
  public LyteValue clone(LyteScope scope) {
    HashMap<String, LyteValue> properties = new HashMap<String, LyteValue>();
    // Cache the scope
    mCachedScope = scope;
    // TODO Use a more "global" stack...
    LyteStack stack = new LyteStack();
    for (String key : mProperties.keySet()) {
      mProperties.get(key).applyTo(scope, stack);
      if (stack.size() > 1) {
        throw new RuntimeException("Expected only one argument on the stack, instead found " + stack.size());
      }
      properties.put(key, stack.pop());
    }
    return new LyteObject(this, properties);
  }

  @Override
  public String typeOf() {
    return "rawObject";
  }

  @Override
  public String toString() {
    return mProperties.toString();
  }
}
