package com.lyte.core;

import com.lyte.objs.*;

import java.util.ArrayDeque;
import java.util.LinkedList;

/**
 * Created by jszaday on 6/19/15.
 */
public class LyteStack extends ArrayDeque<LyteValue> {

  private LinkedList<LyteScope> mScopes;
  private LinkedList<LyteValue> mContexts;

  public LyteStack() {
    mScopes = new LinkedList<>();
    mContexts = new LinkedList<>();
  }

  public LyteStack(LyteScope scope, LyteValue self) {
    this();
    enterContext(scope, self);
  }

  public LyteValue applyInContext(LyteScope scope, LyteValue self, LyteValue value) {
    if (value.typeOf().equals("block")) {
      enterContext(scope, self);
      value = value.apply(this);
      leaveContext();
    }
    return value;
  }

  public void enterContext(LyteScope scope, LyteValue self) {
    mContexts.push(self);
    mScopes.push(scope);
  }

  public void leaveContext() {
    mContexts.pop();
    mScopes.pop();
  }

  public LyteValue getVariable(String name) {
    return mScopes.peek().getVariable(mContexts.peek(), this, name);
  }

  public void updateVariable(String name) {
    mScopes.peek().putVariable(this, name, pop());
  }

  public LyteValue getCurrentSelf() {
    if (mContexts.isEmpty()) {
      return LyteUndefined.UNDEFINED;
    } else {
      return mContexts.peek();
    }
  }

  public LyteScope getCurrentScope() {
    return mScopes.peek();
  }

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
