package com.lyte.objs;

import com.lyte.core.LyteScope;
import com.lyte.core.LyteStack;
import com.lyte.core.LyteStatement;

import java.util.ArrayList;
import java.util.LinkedList;

/**
 * Created by jszaday on 6/18/15.
 */
public class LyteRawArray extends LyteRawValue<ArrayList<LyteValue>> {

  private ArrayList<LyteStatement> mStatements;

  public LyteRawArray() {
    mStatements = new ArrayList<LyteStatement>();
  }

  public boolean add(LyteStatement statement) {
    return mStatements.add(statement);
  }

  @Override
  public String typeOf() {
    return "rawArray";
  }

  @Override
  public String toString() {
    return mStatements.toString();
  }

  @Override
  public LyteValue clone(LyteScope scope) {
    LinkedList<LyteValue> values = new LinkedList<LyteValue>();

    // TODO Use a more "global" stack...
    LyteStack stack = new LyteStack(scope, null);
    for (LyteStatement statement : mStatements) {
      values.add(statement.apply(stack));
    }

    return new LyteList(values);
  }
}
