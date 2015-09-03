package com.lyte.objs;

import com.lyte.core.LyteContext;
import com.lyte.core.LyteScope;
import com.lyte.core.LyteStack;

import java.util.Set;

/**
 * Created by jszaday on 6/24/2015.
 */
public abstract class LyteRawValue<T> implements LyteValue<T> {

  @Override
  public T get() {
    return null;
  }

  @Override
  public LyteValue getProperty(String property) {
    throw new LyteError("You can't get the properties of a raw value!");
  }

  @Override
  public void setProperty(String property, LyteValue newValue) {
    throw new LyteError("You can't set the properties of a raw value!");
  }

  @Override
  public boolean hasProperty(String property) {
    return false;
  }

  @Override
  public boolean toBoolean() {
    return false;
  }

  @Override
  public double toNumber() {
    return 0;
  }

  @Override
  public String typeOf() {
    return "raw";
  }

  @Override
  public LyteValue apply(LyteContext context) {
    throw new LyteError("Cannot apply object of type " + typeOf() + "!");
  }

  @Override
  public boolean equals(LyteValue other) {
    return false;
  }

  @Override
  public boolean equalsStrict(LyteValue other) {
    return false;
  }

  @Override
  public boolean isSimpleComparison() {
    return false;
  }

  @Override
  public boolean is(String type) {
    return type.equals(typeOf());
  }

  @Override
  public Set<String> getProperties() {
    throw new LyteError("Cannot get the properties of " + typeOf() + "!");
  }

  @Override
  public String toJSONString() {
    throw new LyteError("Cannot encode a " + typeOf() + " value as JSON!");
  }
}
