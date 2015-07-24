package com.lyte.objs;

import org.json.simple.JSONValue;

import java.util.HashMap;

/**
 * Created by jszaday on 6/17/15.
 */
public class LyteNumber extends LytePrimitive<Double> implements LyteComparable {

  /* Cache of the integers from -128 to 127
   *  This behavior is copied from the JRE
   */
  private static final HashMap<Double, LyteNumber> cachedValues;
  private static final double CACHE_LOW = -128;
  private static final double CACHE_HIGH = 127;

  static {
    cachedValues = new HashMap<Double, LyteNumber>();

    for (double i = CACHE_LOW; i <= CACHE_HIGH; i += 1) {
      cachedValues.put(i, new LyteNumber(i));
    }
  }

  private LyteNumber(Double number) {
    super(number);
  }

  public static LyteNumber valueOf(String number, int radix) {
    return valueOf((double) Long.parseLong(number, radix));
  }

  public static LyteNumber valueOf(String number) {
    return valueOf(Double.parseDouble(number));
  }

  public static LyteNumber valueOf(Number number) {
    return valueOf(number.doubleValue());
  }

  public static LyteNumber valueOf(Double number) {
    if (cachedValues.containsKey(number)) {
      return cachedValues.get(number);
    } else {
      return new LyteNumber(number);
    }
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
  public boolean equals(LyteValue other) {
    try {
      return this.get() == other.toNumber();
    } catch (LyteError e) {
      return false;
    }
  }

  @Override
  public boolean isSimpleComparison() {
    return true;
  }

  @Override
  public String toString() {
    if (get() % 1.0 != 0) {
      return String.format("%s", get());
    } else {
      return String.format("%.0f", get());
    }
  }

  @Override
  public String toJSONString() {
    return JSONValue.toJSONString(this.get());
  }

  @Override
  public int compareTo(LyteValue o) {
    if (o.is(typeOf())) {
      return get().compareTo(((LyteNumber) o).get());
    } else {
      throw new LyteError("Cannot compare a(n) " + typeOf() + " to a(n) " + o.typeOf());
    }
  }
}
