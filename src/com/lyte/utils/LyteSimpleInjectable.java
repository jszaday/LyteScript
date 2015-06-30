package com.lyte.utils;

import com.lyte.objs.LyteValue;

import java.util.HashMap;

/**
 * Created by a0225785 on 6/29/2015.
 */
public abstract class LyteSimpleInjectable implements LyteInjectable {
  private final HashMap<String, LyteValue> mFunctions;

  public LyteSimpleInjectable() {
    mFunctions = new HashMap<>();
    LyteNativeInjector.injectNatives(this);
  }

  public boolean hasProperty(String name) {
    return mFunctions.containsKey(name);
  }

  public LyteValue getProperty(String name) {
    return mFunctions.get(name);
  }

  @Override
  public void inject(String name, LyteValue value) {
    if (!hasProperty(name)) {
      mFunctions.put(name, value);
    } else {
      throw new RuntimeException("Attempted to duplicate property " + name);
    }
  }

  @Override
  public String toString() {
    return mFunctions.toString();
  }
}
