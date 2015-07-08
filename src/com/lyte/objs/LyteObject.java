package com.lyte.objs;

import com.lyte.core.LyteContext;
import com.lyte.core.LyteInvokeStatement;
import com.lyte.core.LyteScope;
import com.lyte.core.LyteStack;

import java.util.HashMap;
import java.util.Set;

/**
 * Created by a0225785 on 6/17/2015.
 */
public class LyteObject implements LyteValue<HashMap<String, LyteValue>> {

  private HashMap<String, LyteValue> mProperties;
  private LyteRawObject mBase;

  public LyteObject(LyteRawObject base, HashMap<String, LyteValue> properties) {
    mProperties = properties;
    mBase = base;
  }

  public LyteObject(HashMap<String, LyteValue> properties) {
    this(null, properties);
  }

  @Override
  public HashMap<String, LyteValue> get() {
    return mProperties;
  }

  @Override
  public void set(HashMap<String, LyteValue> newValue) {
    mProperties = newValue;
  }

  public void unsetProperty(String key) {
    mProperties.remove(key);
  }

  @Override
  public LyteValue getProperty(String property) {
    if (hasProperty(property)) {
      return mProperties.get(property);
    } else {
      throw new LyteError("Cannot Resolve Property " + property + " of Object " + toString());
    }
  }

  @Override
  public void setProperty(String property, LyteValue newValue) {
    mProperties.put(property, newValue);
  }

  @Override
  public boolean hasProperty(String property) {
    return mProperties.containsKey(property);
  }

  public LyteObject mixWith(LyteObject object) {
    for (String key : object.get().keySet()) {
      setProperty(key, object.getProperty(key));
    }
    return this;
  }

  @Override
  public boolean toBoolean() {
    if (hasProperty("__toBoolean")) {
      return getProperty("__toBoolean").apply(new LyteContext(this)).toBoolean();
    } else {
      return !mProperties.isEmpty();
    }
  }

  @Override
  public double toNumber() {
    if (hasProperty("__toNumber")) {
      return getProperty("__toNumber").apply(new LyteContext(this)).toNumber();
    } else {
      return mProperties.size();
    }
  }

  @Override
  public String toString() {
    if (hasProperty("__toString")) {
      return getProperty("__toString").apply(new LyteContext(this)).toString();
    } else {
      return mProperties.toString();
    }
  }

  @Override
  public String typeOf() {
    return "object";
  }

  @Override
  public LyteValue<HashMap<String, LyteValue>> clone(LyteContext context) {
    if (mBase != null) {
      return ((LyteObject) mBase.clone(context)).mixWith(this);
    } else {
      throw new LyteError("Cannot clone an object without a base!");
    }
  }

  @Override
  public LyteValue apply(LyteContext context) {
    return this;
  }


  @Override
  public boolean equals(LyteValue other) {
    if (isSimpleComparison()) {
      return getProperty("__equals").apply(new LyteContext(this, other)).toBoolean();
    } else if (other.is(typeOf())) {
      return equalsStrict(other);
    } else if (other.isSimpleComparison()) {
      return other.equals(this);
    } else {
      return false;
    }
  }

  @Override
  public boolean equalsStrict(LyteValue other) {
    return other.is(typeOf()) && other.get().equals(get());
  }

  @Override
  public boolean isSimpleComparison() {
    return hasProperty("__equals");
  }

  @Override
  public boolean is(String type) {
    return type.equals(typeOf());
  }

  @Override
  public Set<String> getProperties() {
    return mProperties.keySet();
  }
}
