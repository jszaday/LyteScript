package com.lyte.objs;

import com.lyte.core.LyteScope;
import com.lyte.core.LyteStack;
import com.lyte.core.LyteStatement;

import java.util.HashMap;

public class LyteRawObject extends LyteRawValue<HashMap<String, LyteValue>>  {

  private LyteScope mCachedScope;

  private HashMap<String, LyteStatement> mProperties;

  public LyteRawObject() {
    mProperties = new HashMap<String, LyteStatement>();
  }

  public LyteStatement set(String key, LyteStatement value) {
    return mProperties.put(key, value);
  }

  @Override
  public LyteValue<HashMap<String, LyteValue>> clone(LyteScope scope) {
    HashMap<String, LyteValue> properties = new HashMap<String, LyteValue>();
    if (scope == null) {
      // Uncache the scope
      scope = mCachedScope;
    } else {
      // Cache the scope
      mCachedScope = scope;
    }
    // TODO Use a more "global" stack...
    LyteStack stack = new LyteStack();
    LyteObject newObject = new LyteObject(this);
    for (String key : mProperties.keySet()) {
      mProperties.get(key).applyTo(scope, stack);
      if (stack.size() > 1) {
        throw new RuntimeException("Expected only one argument on the stack, instead found " + stack.size());
      }
      LyteValue value = stack.pop();
      properties.put(key, value);
    }
    newObject.set(properties);
    return newObject;
  }

  @Override
  public LyteValue apply(LyteValue self) {
    return null;
  }

  @Override
  public String typeOf() {
    return "rawObject";
  }
}
