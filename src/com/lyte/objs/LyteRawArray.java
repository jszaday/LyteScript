package com.lyte.objs;

import com.lyte.core.LyteContext;
import com.lyte.utils.LyteAppliable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;

/**
 * Created by jszaday on 6/18/15.
 */
public class LyteRawArray extends LyteRawValue<ArrayList<LyteValue>> {

  private ArrayList<LyteAppliable> mValues;

  public LyteRawArray() {
    mValues = new ArrayList<LyteAppliable>();
  }

  public boolean add(LyteAppliable value) {
    return mValues.add(value);
  }

  @Override
  public String typeOf() {
    return "rawArray";
  }

  @Override
  public String toString() {
    return mValues.toString();
  }

  @Override
  public LyteValue clone(LyteContext context) {
    LinkedList<LyteValue> values = new LinkedList<LyteValue>();

    for (LyteAppliable value : mValues) {
      if (value.is("rawRange")) {
        values.addAll((LyteList) ((LyteRawRange) value).clone(context));
      } else {
        values.add(value.apply(context));
      }
    }

    return new LyteList(values);
  }
}
