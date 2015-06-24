package com.lyte.core;

import java.util.HashMap;
import java.util.HashSet;

import com.lyte.objs.LyteError;
import com.lyte.objs.LyteObject;
import com.lyte.objs.LyteValue;

public class LyteScope {
  private HashMap<String, LyteValue> mVariables;
  private HashSet<String> mFinalVariables;
  private LyteScope mParent;
  private LyteValue mSelf;

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
      return mSelf.getProperty(name.substring(1));
    } else if (mVariables.containsKey(name)) {
      return mVariables.get(name);
    } else if (mParent != null) {
      return mParent.getVariable(name);
    } else {
      throw new LyteError("Undefined variable, " + name);
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
      mSelf.setProperty(name.substring(1), value);
    } else if (mParent != null && mParent.hasVariable(name)) {
      mParent.putVariable(name, value);
    } else {
      if (mFinalVariables.contains(name)) {
        throw new LyteError("Cannot override the value of " + name);
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

  public LyteScope clone() {
    LyteScope newSibling = mParent.enter();
    // TODO Determine Implementation
    return newSibling;
  }

  public static LyteScope newGlobal() {
    return new LyteScope(null, false);
  }

  public String getStackTrace() {
    return toString() + " called by:\n" + getStackTrace(mParent);
  }

  private static String getStackTrace(LyteScope scope) {
    return scope.toString() + "\n" + (scope.mParent != null ? getStackTrace(scope.mParent) : "");
  }

  public void printStackTrace() {
    System.out.print(getStackTrace());
  }

  public void setSelf(LyteValue self) {
    if (self != null || mSelf == null) {
      mSelf = self;
    }
  }

  public LyteValue getSelf() {
    return mSelf;
  }
}
