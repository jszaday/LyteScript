package com.lyte.objs;

import com.lyte.core.LyteScope;

/**
 * Created by jszaday on 6/17/15.
 */
public class LyteNumber extends LytePrimitive<Double> {

  public LyteNumber(String number, int radix) {
    this(Integer.parseInt(number, radix));
  }

  public LyteNumber(String number) {
    this(Double.parseDouble(number));
  }

  public LyteNumber(Integer number) {
    this(number.doubleValue());
  }

  public LyteNumber(Double number) {
    super(number);
  }

  @Override
  public boolean toBoolean() {
    return get() != 0;
  }

  @Override
  public double toNumber() {
    return get();
  }

  @Override
  public String typeOf() {
    return "number";
  }

  @Override
  public String toString() {
    if (get() % 1.0 != 0) {
      return String.format("%s", get());
    } else {
      return String.format("%.0f", get());
    }
  }
}
