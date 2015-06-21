package com.lyte.objs;

import com.lyte.core.LyteScope;
import com.lyte.core.LyteStack;

import java.util.ArrayList;

public class LyteArray extends LyteObject {
  private ArrayList<LyteValue> mList;

  public LyteArray(ArrayList<LyteValue> list) {
    mList = list;
  }

  public LyteArray() {
    this(new ArrayList<LyteValue>());
  }

  @Override
  public LyteValue get(String index) {
    return get(Integer.parseInt(index));
  }

  public LyteValue get(int index) {
    try {
      return mList.get(index);
    } catch (IndexOutOfBoundsException e) {
      return LyteUndefined.UNDEFINED;
    }
  }

  @Override
  public LyteValue set(String key, LyteValue value) {
    return set((int) Double.parseDouble(key), value);
  }

  public LyteValue set(int index, LyteValue value) {
    LyteValue oldValue = get(index);
    // Expand the list up to the given index
    for (int i = mList.size(); i <= index; i++) {
      mList.add(LyteUndefined.UNDEFINED);
    }
    // Then finally perform the set
    mList.set(index, value);
    return oldValue;
  }

  public void add(LyteValue value) {
    mList.add(value);
  }

  @Override
  public boolean isTruthy() {
    return !mList.isEmpty();
  }

  @Override
  public LyteValue clone(LyteScope scope) {
    return null;
  }

  @Override
  public String toString() {
    return mList.toString();
  }
}
