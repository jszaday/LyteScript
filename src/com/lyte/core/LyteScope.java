package com.lyte.core;

import java.util.ArrayDeque;
import java.util.HashMap;

import com.lyte.objs.LyteValue;

public class LyteScope {
  public HashMap<String, LyteValue> mVariables;
  public ArrayDeque<LyteValue> mStack;
  public LyteScope mParent;

  public LyteScope(LyteScope parent, boolean useParentStack) {
    mParent = parent;
    mVariables = new HashMap<String, LyteValue>();

    if (useParentStack) {
      mStack = parent.getStack();
    } else {
      mStack = new ArrayDeque<LyteValue>();
    }
  }

  public LyteScope(LyteScope parent) {
    this(parent, false);
  }

  public LyteValue getVariable(String name) {
    if (mVariables.containsKey(name)) {
      return mVariables.get(name);
    } else if (mParent != null) {
      return mParent.getVariable(name);
    } else {
      return null;
    }
  }

  public boolean hasVariable(String name) {
    if (mVariables.containsKey(name)) {
      return true;
    } else if (mParent != null) {
      return mParent.hasVariable(name);
    } else {
      return false;
    }
  }

  public void putVariable(String name, LyteValue value) {
    if (mParent.hasVariable(name)) {
      mParent.putVariable(name, value);
    } else {
      mVariables.put(name, value);
    }
  }

  public LyteValue pop() {
    if (mStack.isEmpty()) {
      // TODO Change to Undefined
      return null;
    } else {
      return mStack.pop();
    }
  }

  public void push(LyteValue obj) {
    mStack.push(obj);
  }

  private ArrayDeque<LyteValue> getStack() {
    return mStack;
  }

  public static LyteScope generateRootScope() {
    return new LyteScope(null);
  }
}
