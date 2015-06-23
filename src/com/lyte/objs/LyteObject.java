package com.lyte.objs;

import com.lyte.core.LyteInvokeStatement;
import com.lyte.core.LyteScope;
import com.lyte.core.LyteStack;

import java.util.HashMap;
import java.util.Set;

/**
 * Created by a0225785 on 6/17/2015.
 */
public class LyteObject implements LyteValue {
  private HashMap<String, LyteValue> mProperties;
  private LyteRawObject mBase;

  public LyteObject() {
    this(null, new HashMap<String, LyteValue>());
  }

  protected LyteObject(LyteRawObject base, HashMap<String, LyteValue> properties) {
    mBase = base;
    mProperties = properties;
  }

  public void setProperties(HashMap<String, LyteValue> properties) {
    mProperties = properties;
  }

  public boolean hasProperty(String key) {
    return mProperties.containsKey(key);
  }

  public Set<String> keySet() {
    return mProperties.keySet();
  }

  public LyteValue set(String key, LyteValue value) {
    return mProperties.put(key, value);
  }

  // TODO Finalize behavior of mixWith, should it override and clone values?
  public LyteObject mixWith(LyteObject object) {
    for (String key : object.keySet()) {
      set(key, object.get(key));
    }
    return this;
  }

  public LyteValue get(String key) {
    if (hasProperty(key)) {
      return mProperties.get(key);
    } else {
      // TODO Eventually this will grab methods within an object!
      return LyteUndefined.UNDEFINED;
    }
  }

  public void unset(String key) {
    mProperties.remove(key);
  }

  public LyteObject clone() {
    if (mBase != null) {
      return ((LyteObject) mBase.clone()).mixWith(this);
    } else {
      // TODO Implement this
      return null;
    }
  }

  @Override
  public LyteValue clone(LyteScope scope) {
    if (mBase != null) {
      return ((LyteObject) mBase.clone(scope)).mixWith(this);
    } else {
      // TODO Implement this
      return null;
    }
  }


  @Override
  public int hashCode() {
    return mProperties.hashCode();
  }

  @Override
  public String typeOf() {
    if (hasProperty("__typeOf")) {
      return LyteInvokeStatement.applyIfNeeded(get("__typeOf"), this, new LyteStack()).toString();
    } else {
      return "object";
    }
  }

  @Override
  public LyteBoolean toBoolean() {
    return new LyteBoolean(true);
  }

  @Override
  public String toString() {
    if (hasProperty("__toString")) {
      return LyteInvokeStatement.applyIfNeeded(get("__toString"), this, new LyteStack()).toString();
    } else {
      return mProperties.toString();
    }
  }

  @Override
  public LyteNumber toNumber() {
    if (hasProperty("__toNumber")) {
      return (LyteNumber) LyteInvokeStatement.applyIfNeeded(get("__toNumber"), this, new LyteStack());
    } else {
      return new LyteNumber(mProperties.size());
    }
  }

  public static boolean isObject(LyteValue value) {
    return value instanceof LyteObject;
  }
}
