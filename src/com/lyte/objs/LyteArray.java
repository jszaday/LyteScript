package com.lyte.objs;

import com.lyte.core.LyteScope;
import com.lyte.core.LyteStack;

import java.util.ArrayList;

public class LyteArray implements LyteValue<ArrayList<LyteValue>> {
  private ArrayList<LyteValue> mList;

  public LyteArray(ArrayList<LyteValue> list) {
    mList = list;
  }

  public static Integer tryParse(String str) {
    try {
      return (int) Double.parseDouble(str);
    } catch (NumberFormatException nfe) {
      return null;
    }
  }

  public void setIndex(int index, LyteValue newValue) {
    // Expand the list up to the given index
    for (int i = mList.size(); i <= index; i++) {
      mList.add(LyteUndefined.UNDEFINED);
    }
    // Then finally perform the set
    mList.set(index, newValue);
  }

  @Override
  public ArrayList<LyteValue> get() {
    return mList;
  }

  @Override
  public void set(ArrayList<LyteValue> newValue) {
    mList = newValue;
  }

  @Override
  public LyteValue getProperty(String property) {
    Integer index;
    if ((index = tryParse(property)) != null) {
      try {
        return mList.get(index);
      } catch (IndexOutOfBoundsException e) {
        throw new LyteError("Index " + index + " out of bounds for array " + toString());
      }
    } else if (property.equals("length")) {
      return new LyteNumber(mList.size());
    } else {
      throw new LyteError("Cannot Resolve Property " + property + " from the array " + toString());
    }
  }

  @Override
  public void setProperty(String property, LyteValue newValue) {
    Integer index;
    if ((index = tryParse(property)) != null) {
      setIndex(index, newValue);
    } else {
      throw new LyteError("Cannot Set Property " + property + " of the array " + toString());
    }
  }

  @Override
  public boolean hasProperty(String property) {
    return property.equals("length") || (tryParse(property) != null);
  }

  @Override
  public boolean toBoolean() {
    return !mList.isEmpty();
  }

  @Override
  public double toNumber() {
    return mList.size();
  }

  @Override
  public LyteValue clone(LyteScope scope) {
    return new LyteArray((ArrayList<LyteValue>) mList.clone());
  }

  @Override
  public LyteValue apply(LyteValue self, LyteStack stack) {
    return this;
  }

  @Override
  public String toString() {
    return mList.toString();
  }

  @Override
  public String typeOf() {
    return "list";
  }
}
