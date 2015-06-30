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
    LyteStack stack = new LyteStack();
    for (LyteStatement statement : mStatements) {
      statement.applyTo(null, scope, stack);
      if (stack.size() > 1) {
        throw new RuntimeException("Expected only one argument on the stack, instead found " + stack.size());
      }
      values.add(stack.pop());
    }

    return new LyteList(values);
  }
}
