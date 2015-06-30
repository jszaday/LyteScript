package com.lyte.objs;

import com.lyte.core.LyteScope;
import com.lyte.core.LyteStack;
import com.lyte.stdlib.LyteListFunctions;
import com.lyte.stdlib.LyteNativeBlock;

import java.util.LinkedList;

public class LyteList implements LyteValue<LinkedList<LyteValue>> {
  private static final LyteListFunctions listFunctions = new LyteListFunctions();

  private LinkedList<LyteValue> mList;

  public LyteList() {
    mList = new LinkedList<LyteValue>();
  }

  public LyteList(LinkedList<LyteValue> list) {
    mList = list;
  }

  public LyteList(Object[] list) {
    mList = new LinkedList<>();

    for (Object object : list) {
      add(new LyteString(object.toString()));
    }
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

  public void add(LyteValue value) {
    mList.add(value);
  }

  @Override
  public LinkedList<LyteValue> get() {
    return mList;
  }

  @Override
  public void set(LinkedList<LyteValue> newValue) {
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
    } else if (listFunctions.hasProperty(property)) {
      return listFunctions.getProperty(property);
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
    return listFunctions.hasProperty(property) || (tryParse(property) != null);
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
    return new LyteList((LinkedList<LyteValue>) mList.clone());
  }

  @Override
  public LyteValue apply(LyteStack stack) {
    return this;
  }

  @Override
  public boolean equals(LyteValue other) {
    if (other.typeOf().equals(typeOf())) {
      return equalsStrict(other);
    } else if (other.isSimpleComparison()) {
      return other.equals(this);
    } else {
      return false;
    }
  }

  @Override
  public boolean equalsStrict(LyteValue other) {
    if (other.typeOf().equals(typeOf())) {
      return other.get().equals(get());
    } else {
      return false;
    }
  }

  @Override
  public boolean isSimpleComparison() {
    return false;
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
