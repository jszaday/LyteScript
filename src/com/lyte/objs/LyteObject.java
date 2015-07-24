package com.lyte.objs;

import com.lyte.core.LyteContext;
import com.lyte.core.LyteInvokeStatement;
import com.lyte.core.LyteScope;
import com.lyte.core.LyteStack;
import com.lyte.utils.LyteYieldListener;
import org.json.simple.JSONObject;

import java.util.*;

/**
 * Created by a0225785 on 6/17/2015.
 */
public class LyteObject extends HashMap<String, LyteValue> implements LyteValue<HashMap<String, LyteValue>>,
        LyteIterable {

  private LyteRawObject mBase = null;
  private HashMap<String, Object> mMetadata;
  private HashMap<String, LyteBlock> mGetters;
  private HashMap<String, LyteBlock> mSetters;

  public LyteObject() {
    mMetadata = new HashMap<>();
    mGetters = new HashMap<>();
    mSetters = new HashMap<>();
  }

  public LyteObject(HashMap<String, LyteValue> base) {
    this();
    putAll(base);
  }

  public LyteObject(LyteRawObject base) {
    this();
    mBase = base;
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
    if (this.containsKey(property)) {
      return get(property);
    } else if (mGetters.containsKey(property)) {
      return mGetters.get(property).apply(new LyteContext(this));
    } else {
      throw new LyteError("Cannot resolve property " + property + " of object " + super.toString());
    }
  }

  @Override
  public void setProperty(String property, LyteValue newValue) {
    if (mSetters.containsKey(property)) {
      mSetters.get(property).apply(new LyteContext(this, newValue));
    } else if (!mGetters.containsKey(property)) {
      put(property, newValue);
    } else {
      throw new LyteError("Cannot set property " + property + " of object " + super.toString());
    }
  }

  @Override
  public boolean hasProperty(String property) {
    return containsKey(property) || mGetters.containsKey(property);
  }

  public void putGetter(String property, LyteBlock block) {
    mGetters.put(property, block);
  }

  public void putSetter(String property, LyteBlock block) {
    mSetters.put(property, block);
  }

  public LyteObject mixWith(LyteValue value) {
    return mixWith(null, value);
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
    if (mixedCallback != null && context != null) {
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
      return hashCode();
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
  public LyteBlock generator() {
    LyteValue generator = getProperty("__generator");
    if (generator.is("block")) {
      return (LyteBlock) generator;
    } else {
      throw new LyteError("Cannot use a(n) " + generator.typeOf() + " as a generator method");
    }
  }

  @Override
  public Iterator<LyteValue> iterator() {
    final LinkedList<LyteValue> values = new LinkedList<>();
    final LyteContext generatorContext = new LyteContext(this);
    final LyteBlock generator = generator();

    generatorContext.setListener(new LyteYieldListener() {
      @Override
      public void onYield(LyteValue value) {
        // Add the value to the list
        values.add(value);
      }
    });
    generator.invoke(generatorContext);

    return values.iterator();
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
      return new LyteObject(this);
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
    return new HashSet<String>() {{
      addAll(LyteObject.this.keySet());
      addAll(mGetters.keySet());
    }};
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

//  @Override
//  public int compareTo(LyteValue o) {
//    if (hasProperty("__compareTo")) {
//      return (int) getProperty("__compareTo").apply(new LyteContext(this, o)).toNumber();
//    } else {
//      throw new LyteError("Object does not have a definition for comparison function.");
//    }
//  }
}
