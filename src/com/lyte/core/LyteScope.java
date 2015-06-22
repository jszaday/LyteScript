package com.lyte.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import com.lyte.objs.LyteObject;
import com.lyte.objs.LyteUndefined;
import com.lyte.objs.LyteValue;
import com.lyte.stdlib.LyteNativeBlock;

public class LyteScope {
  private HashMap<String, LyteValue> mVariables;
  private HashSet<String> mFinalVariables;
  private LyteScope mParent;
  private LyteObject mSelf;

  private LyteScope(LyteScope parent, boolean useParentStack) {
    mParent = parent;
    mVariables = new HashMap<String, LyteValue>();
    mFinalVariables = new HashSet<String>();
  }

  private LyteScope(LyteScope parent) {
    this(parent, true);
  }

  public LyteValue getVariable(String name) {
    if (name.startsWith("@")) {
      return mSelf.get(name.substring(1));
    } else if (mVariables.containsKey(name)) {
      return mVariables.get(name);
    } else if (mParent != null) {
      return mParent.getVariable(name);
    } else {
      return LyteUndefined.UNDEFINED;
    }
  }

  public boolean hasVariable(String name) {
    if (name.startsWith("@")) {
      return mSelf.hasProperty(name.substring(1));
    } else if (mVariables.containsKey(name)) {
      return true;
    } else if (mParent != null) {
      return mParent.hasVariable(name);
    } else {
      return false;
    }
  }

  public void putVariable(String name, LyteValue value, boolean finalVariable) {
    if (name.startsWith("@")) {
      mSelf.set(name.substring(1), value);
    } else if (mParent != null && mParent.hasVariable(name)) {
      mParent.putVariable(name, value);
    } else {
      if (mFinalVariables.contains(name)) {
        throw new RuntimeException("Cannot override the value of " + name);
      } else {
        mVariables.put(name, value);

        if (finalVariable) {
          mFinalVariables.add(name);
        }
      }
    }
  }

  public void putVariable(String name, LyteValue value) {
    putVariable(name, value, false);
  }

  public void finalizeVariable(String name) {
    mFinalVariables.add(name);
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

  public void setSelf(LyteObject self) {
    if (self != null || mSelf == null) {
      mSelf = self;
    }
  }

  public LyteObject getSelf() {
    return mSelf;
  }
}
