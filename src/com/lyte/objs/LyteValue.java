package com.lyte.objs;

import com.lyte.core.LyteContext;
import com.lyte.core.LyteScope;
import com.lyte.core.LyteStack;
import org.json.simple.JSONAware;

import java.util.Set;

public interface LyteValue<T> extends JSONAware {

  T get();

  LyteValue getProperty(String property);
  void setProperty(String property, LyteValue newValue);
  boolean hasProperty(String property);

  boolean toBoolean();
  double toNumber();
  String toString();

  String typeOf();

  LyteValue<T> clone(LyteContext context);

  LyteValue apply(LyteContext context);

  boolean equals(LyteValue other);
  boolean equalsStrict(LyteValue other);
  boolean isSimpleComparison();

  boolean is(String type);
  Set<String> getProperties();
}
