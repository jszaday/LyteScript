package com.lyte.core;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;

import com.lyte.objs.LyteValue;
import com.lyte.stdlib.LyteNativeBlock;

public class LyteScope {
  private HashMap<String, LyteValue> mVariables;
  private LyteScope mParent;

  private LyteScope(LyteScope parent, boolean useParentStack) {
    mParent = parent;
    mVariables = new HashMap<String, LyteValue>();
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

  public void injectNative(Class... nativeClasses) {
    Class nativeClass = null;
    try {
      for (int i = 0; i < nativeClasses.length; i++) {
        nativeClass = nativeClasses[i];
        LyteNativeBlock nativeBlock = (LyteNativeBlock) nativeClass.getDeclaredConstructor(LyteScope.class).newInstance(this);
        putVariable(nativeBlock.getSymbol(), nativeBlock);
      }
    } catch (Exception e) {
      System.err.println("Could not inject native of type " + nativeClass.getName());
    }
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
  }
}
