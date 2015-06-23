package com.lyte.objs;

import com.lyte.core.LyteScope;

public interface LyteValue {
  String typeOf();
  LyteValue clone(LyteScope scope);
  LyteBoolean toBoolean();
  LyteNumber toNumber();
}
