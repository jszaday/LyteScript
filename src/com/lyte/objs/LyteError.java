package com.lyte.objs;

import com.lyte.core.LyteScope;

/**
 * Created by a0225785 on 6/23/2015.
 */
public class LyteError extends RuntimeException implements LyteValue {

  public LyteScope mScope;

  public LyteError(String value) {
    super(value);
  }

  public LyteError(LyteValue value) {
    super(value.toString());
  }

  public LyteError attachScope(LyteScope scope) {
    mScope = scope;
    return this;
  }

  @Override
  public LyteBoolean toBoolean() {
    return new LyteBoolean(true);
  }

  @Override
  public LyteNumber toNumber() {
    return new LyteNumber(this.hashCode());
  }

  @Override
  public String toString() {
    return "Reason: " + getMessage() + (mScope != null ? ("\n" + mScope.getStackTrace()) : "");
  }

  @Override
  public String typeOf() {
    return "error";
  }

  @Override
  public LyteValue clone(LyteScope scope) {
    return this;
  }
}
