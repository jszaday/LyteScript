package com.lyte.core;

import com.lyte.objs.*;

import java.util.ArrayDeque;
import java.util.LinkedList;

/**
 * Created by jszaday on 6/19/15.
 */
public class LyteStack extends ArrayDeque<LyteValue> {

  public LyteValue pop() {
    if (!this.isEmpty()) {
      return super.pop();
    } else {
      throw new LyteError("No value available, stack is empty.");
    }
  }

  public void push(String string) {
    push(new LyteString(string));
  }

  public void push(Double number) {
    push(new LyteNumber(number));
  }

  public void push(Boolean bool) {
    push(new LyteBoolean(bool));
  }

  public void push(Integer integer) {
    push(new LyteNumber(integer));
  }
}
