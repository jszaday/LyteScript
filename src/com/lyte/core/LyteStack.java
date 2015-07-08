package com.lyte.core;

import com.lyte.objs.*;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.LinkedList;

/**
 * Created by jszaday on 6/19/15.
 */
public class LyteStack extends LinkedList<LyteValue> {

  public LyteStack() {
    super();
  }

  public LyteStack(LyteValue... values) {
    super(Arrays.asList(values));
  }

  @Override
  public LyteValue pop() {
    if (!this.isEmpty()) {
      return super.pop();
    } else {
      throw new LyteError("No value available, stack is empty.");
    }
  }

  @Override
  public LyteValue peek() {
    if (!this.isEmpty()) {
      return super.peek();
    } else {
      throw new LyteError("No value available, stack is empty.");
    }
  }
}
