package com.lyte.objs;

import com.lyte.core.LyteContext;
import com.lyte.stdlib.LyteListMembers;
import org.json.simple.JSONArray;

import java.util.*;

public class LyteList extends LinkedList<LyteValue> implements LyteValue<LinkedList<LyteValue>> {
  private static final LyteListMembers LIST_FUNCTIONS = new LyteListMembers();

  public LyteList() {
    super();
  }

  public LyteList(Collection<LyteValue> list) {
    addAll(list);
  }

  public LyteList(Object[] list) {
    for (Object object : list) {
      add(new LyteString(object.toString()));
    }
  }

  public LyteList(LyteList list1, LyteList list2) {
    addAll(list1.get());
    addAll(list2.get());
  }

  public static Integer tryParse(String str) {
    try {
      return (int) Double.parseDouble(str);
    } catch (NumberFormatException nfe) {
      return null;
    }
  }

  @Override
  public LyteValue set(int index, LyteValue newValue) {
    if (index < 0) {
      throw new LyteError("Cannot have a negative index!");
    }
    // Expand the list up to the given index
    for (int i = size(); i <= index; i++) {
      add(LyteUndefined.UNDEFINED);
    }
    // Then finally perform the set
    return super.set(index, newValue);
  }

  @Override
  public LinkedList<LyteValue> get() {
    return this;
  }

  @Override
  public LyteValue getProperty(String property) {
    Integer index;
    if ((index = tryParse(property)) != null) {
      try {
        return get(index);
      } catch (IndexOutOfBoundsException e) {
        throw new LyteError("Index " + index + " out of bounds for array " + toString());
      }
    } else if (LIST_FUNCTIONS.hasProperty(property)) {
      return LIST_FUNCTIONS.getProperty(property);
    } else {
      throw new LyteError("Cannot Resolve Property " + property + " from the array " + toString());
    }
  }

  @Override
  public void setProperty(String property, LyteValue newValue) {
    Integer index;
    if ((index = tryParse(property)) != null) {
      set(index, newValue);
    } else {
      throw new LyteError("Cannot Set Property " + property + " of the array " + toString());
    }
  }

  @Override
  public boolean hasProperty(String property) {
    return LIST_FUNCTIONS.hasProperty(property) || (tryParse(property) != null);
  }

  @Override
  public boolean toBoolean() {
    return !isEmpty();
  }

  @Override
  public double toNumber() {
    return hashCode();
  }

  @Override
  public LyteBlock generator() {
    return (LyteBlock) getProperty("__generator");
  }

  @Override
  public LyteValue clone(LyteContext context) {
    return new LyteList((LinkedList<LyteValue>) clone());
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
  public String typeOf() {
    return "list";
  }

  @Override
  public boolean is(String type) {
    return type.equals(typeOf());
  }

  @Override
  public Set<String> getProperties() {
    final int length = size();
    return new HashSet<String>() {{
      addAll(LIST_FUNCTIONS.getProperties());

      for (int i = 0; i < length; i++) {
        add(String.valueOf(i));
      }
    }};
  }

  @Override
  public String toJSONString() {
    JSONArray array = new JSONArray();
    array.addAll(this);
    return array.toJSONString();
  }
}
