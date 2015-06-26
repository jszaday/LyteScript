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

  private LyteScope(LyteScope parent, boolean useParentStack) {
    mParent = parent;
    mVariables = new HashMap<String, LyteValue>();
    mFinalVariables = new HashSet<String>();
  }

  private LyteScope(LyteScope parent) {
    this(parent, true);
  }

  private LyteValue getVariable(String name) {
    if (mVariables.containsKey(name)) {
      return mVariables.get(name);
    } else if (mParent != null) {
      return mParent.getVariable(name);
    } else {
      throw new LyteError("Undefined variable, " + name);
    }
  }

  public LyteValue getVariable(LyteValue self, LyteStack stack, String name) {
    if (name.startsWith("@") && self != null) {
      return self.getProperty(name.substring(1, name.length()));
    } else if (name.startsWith("#") && stack != null) {
      return stack.peek().getProperty(name.substring(1, name.length()));
    } else {
      return getVariable(name);
    }
  }

  private boolean hasVariable(String name) {
    if (mVariables.containsKey(name)) {
      return true;
    } else if (mParent != null) {
      return mParent.hasVariable(name);
    } else {
      return false;
    }
  }

  public boolean hasVariable(LyteValue self, LyteStack stack, String name) {
    if (name.startsWith("@") && self != null) {
      return self.hasProperty(name.substring(1, name.length()));
    } else if (name.startsWith("#") && stack != null) {
      return stack.peek().hasProperty(name.substring(1, name.length()));
    } else {
      return hasVariable(name);
    }
  }

  private void putVariable(String name, LyteValue value, boolean finalVariable) {
    if (mParent != null && mParent.hasVariable(name)) {
      mParent.putVariable(name, value, finalVariable);
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

  public void putVariable(LyteValue self, LyteStack stack, String name, LyteValue value, boolean finalVariable) {
    if (name.startsWith("@") && self != null) {
      self.setProperty(name.substring(1, name.length()), value);
    } else if (name.startsWith("#") && stack != null) {
      stack.peek().setProperty(name.substring(1, name.length()), value);
    } else {
      putVariable(name, value, finalVariable);
    }
  }

  public void putVariable(LyteValue self, LyteStack stack, String name, LyteValue value) {
    putVariable(self, stack, name, value, false);
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

  public void dump() {
    if (mParent != null) {
      mParent.dump();
      System.out.println(toString() + " : " + mVariables);
    }
  }
}
