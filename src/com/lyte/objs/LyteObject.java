package com.lyte.objs;

import com.lyte.core.LyteContext;
import com.lyte.core.LyteInvokeStatement;
import com.lyte.core.LyteScope;
import com.lyte.core.LyteStack;
import org.json.simple.JSONObject;

import java.util.HashMap;
import java.util.Set;

/**
 * Created by a0225785 on 6/17/2015.
 */
public class LyteObject extends HashMap<String, LyteValue> implements LyteValue<HashMap<String, LyteValue>> {

  private LyteRawObject mBase;
  private HashMap<String, Object> mMetadata;

  public LyteObject(LyteRawObject base) {
    mBase = base;
    mMetadata = new HashMap<>();
  }

  @Override
  public HashMap<String, LyteValue> get() {
    return this;
  }

  public void unsetProperty(String key) {
    remove(key);
  }

  @Override
  public LyteValue getProperty(String property) {
    if (hasProperty(property)) {
      return get(property);
    } else {
      throw new LyteError("Cannot Resolve Property " + property + " of object " + toString());
    }
  }

  @Override
  public void setProperty(String property, LyteValue newValue) {
    put(property, newValue);
  }

  @Override
  public boolean hasProperty(String property) {
    return containsKey(property);
  }

  public LyteObject mixWith(LyteContext context, LyteValue value) {
    LyteBlock mixedCallback = null;
    for (String property : (Set<String>) value.getProperties()) {
      if (property.equals("__init") && value.getProperty(property).is("block")) {
        mixedCallback = (LyteBlock) value.getProperty(property);
      } if (value.getProperty(property) == LyteUndefined.NULL && !hasProperty(property)) {
        throw new LyteError("Object does not implement virtual method " + property);
      } else if (!hasProperty(property)){
        setProperty(property, value.getProperty(property));
      }
    }
    if (mixedCallback != null) {
      mixedCallback.invoke(new LyteContext(this, context));
    }
    return this;
  }

  @Override
  public boolean toBoolean() {
    if (hasProperty("__toBoolean")) {
      return getProperty("__toBoolean").apply(new LyteContext(this)).toBoolean();
    } else {
      return !isEmpty();
    }
  }

  @Override
  public double toNumber() {
    if (hasProperty("__toNumber")) {
      return getProperty("__toNumber").apply(new LyteContext(this)).toNumber();
    } else {
      return size();
    }
  }

  @Override
  public String toString() {
    if (hasProperty("__toString")) {
      return getProperty("__toString").apply(new LyteContext(this)).toString();
    } else {
      return super.toString();
    }
  }

  @Override
  public String typeOf() {
    return "object";
  }

  @Override
  public LyteValue<HashMap<String, LyteValue>> clone(LyteContext context) {
    if (mBase != null) {
      return ((LyteObject) mBase.clone(context)).mixWith(context, this);
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
    return keySet();
  }

  @Override
  public String toJSONString() {
    LyteValue value;
    JSONObject obj = new JSONObject();
    for (String property : getProperties()) {
      if ((value = getProperty(property)).is("block")) {
        continue;
      } else {
        obj.put(property, value);
      }
    }
    return obj.toJSONString();
  }

  public void putMetadata(String key, Object value) {
    mMetadata.put(key, value);
  }

  public boolean hasMetadata(String key) {
    return mMetadata.containsKey(key);
  }

  public Object getMetadata(String key) {
    return mMetadata.get(key);
  }
}
