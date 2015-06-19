package com.lyte.objs;

import com.lyte.core.LyteScope;

import java.util.ArrayList;

public class LyteArray implements LyteValue {
  private ArrayList<LyteValue> mList;

  private LyteArray(ArrayList<LyteValue> list) {
    mList = list;
  }

  public LyteArray() {
    this(new ArrayList<LyteValue>());
  }

  @Override
  public String typeOf() {
    return "list";
  }

  public LyteValue get(int index) {
    return mList.get(index);
  }

  public void set(int index, LyteValue value) {
    // Expand the list up to the given index
    for (int i = mList.size(); i <= index; i++) {
      // TODO Change to Undefined
      mList.add(null);
    }
    // Then finally perform the set
    mList.set(index, value);
  }

  public void add(LyteValue value) {
    mList.add(value);
  }

  public LyteArray map(LyteBlock block) {
    ArrayList<LyteValue> newList = new ArrayList<LyteValue>();

    for (LyteValue obj : mList) {
      // Invoke the function with the obj as an argument
      block.invoke(obj);
      // And add the result to the list
      newList.add(block.getScope().pop());
    }

    return new LyteArray(newList);
  }

  @Override
  public LyteValue clone(LyteScope scope) {
    return null;
  }
}
