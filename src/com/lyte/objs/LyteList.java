package com.lyte.objs;

import com.lyte.core.LyteContext;
import com.lyte.core.LyteScope;
import com.lyte.core.LyteStack;
import com.lyte.stdlib.LyteListFunctions;
import com.lyte.stdlib.LyteNativeBlock;

import java.util.*;

public class LyteList implements LyteValue<LinkedList<LyteValue>> {
  private static final LyteListFunctions listFunctions = new LyteListFunctions();

  private LinkedList<LyteValue> mList;

  public LyteList() {
    mList = new LinkedList<LyteValue>();
  }

  public LyteList(Collection<LyteValue> list) {
    mList = new LinkedList<LyteValue>();
    mList.addAll(list);
  }

  public LyteList(LyteList list1, LyteList list2) {
    mList = new LinkedList<>();
    // TODO there's probably a better way to do this!
    mList.addAll(list1.get());
    mList.addAll(list2.get());
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
    if (index < 0) {
      throw new LyteError("Cannot have a negative index!");
    }
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
  public LyteValue clone(LyteContext context) {
    return new LyteList((LinkedList<LyteValue>) mList.clone());
  }

  @Override
  public LyteValue apply(LyteContext context) {
    return this;
  }

  @Override
  public boolean equals(LyteValue other) {
    if (other.is(typeOf())) {
      return equalsStrict(other);
    } else if (other.isSimpleComparison()) {
      return other.equals(this);
    } else {
      return false;
    }
  }

  @Override
  public boolean equalsStrict(LyteValue other) {
    if (other.is(typeOf())) {
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

  @Override
  public boolean is(String type) {
    return type.equals(typeOf());
  }

  @Override
  public Set<String> getProperties() {
    return new HashSet<String>() {{
      addAll(listFunctions.getProperties());

      for (int i = 0; i < size(); i++) {
        add(String.valueOf(i));
      }
    }};
  }
}
