package com.lyte.objs;

import com.lyte.core.LyteScope;
import com.lyte.core.LyteStack;

public interface LyteValue<T> {

  T get();
  void set(T newValue);

  LyteValue getProperty(String property);
  void setProperty(String property, LyteValue newValue);
  boolean hasProperty(String property);

  boolean toBoolean();
  double toNumber();
  String toString();

  String typeOf();

  LyteValue<T> clone(LyteScope scope);

  LyteValue apply(LyteStack stack);

  boolean equals(LyteValue other);
  boolean equalsStrict(LyteValue other);
  boolean isSimpleComparison();
}
