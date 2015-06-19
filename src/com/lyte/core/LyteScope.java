package com.lyte.core;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;

import com.lyte.objs.LyteValue;
import com.lyte.stdlib.LyteNativeBlock;

public class LyteScope {
  private HashMap<String, LyteValue> mVariables;
  private ArrayDeque<LyteValue> mStack;
  private LyteScope mParent;

  private LyteScope(LyteScope parent, boolean useParentStack) {
    mParent = parent;
    mVariables = new HashMap<String, LyteValue>();

    if (useParentStack) {
      mStack = parent.getStack();
    } else {
      mStack = new ArrayDeque<LyteValue>();
    }
  }

  private LyteScope(LyteScope parent) {
    this(parent, true);
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
    if (mParent != null && mParent.hasVariable(name)) {
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

  public LyteValue peek() {
    return mStack.peek();
  }

  public void push(LyteValue obj) {
    if (obj != null) {
      mStack.push(obj);
    }
  }

  public void injectNative(Class nativeClass) {
    try {
      LyteNativeBlock nativeBlock = (LyteNativeBlock) nativeClass.getDeclaredConstructor(LyteScope.class).newInstance(this);
      putVariable(nativeBlock.getSymbol(), nativeBlock);
    } catch (Exception e) {
      System.err.println("Could not inject native of type " + nativeClass.getName());
      e.printStackTrace();
    }
  }

  private ArrayDeque<LyteValue> getStack() {
    return mStack;
  }

  public LyteScope enter() {
    return new LyteScope(this);
  }

  public LyteScope leave() {
    return mParent;
  }

  public static LyteScope newGlobal() {
    return new LyteScope(null, false);
  }

  private static void printStackTrace(LyteScope scope) {
    System.out.println(scope.toString());

    if(scope.mParent != null) {
      printStackTrace(scope.mParent);
    }
  }

  public void printStackTrace() {
    System.out.println(toString() + " called by:");
    printStackTrace(mParent);
    System.out.println("Stack is currently: " + mStack);
  }
}
