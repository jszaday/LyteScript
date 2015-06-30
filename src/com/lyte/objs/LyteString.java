package com.lyte.objs;


import com.lyte.stdlib.LyteStringFunctions;

/**
 * Created by jszaday on 6/22/15.
 */
public class LyteString extends LytePrimitive<String> {

  private static final LyteStringFunctions sFunctions = new LyteStringFunctions();

  public LyteString(String value) {
    super(value);
  }

  public LyteString(char value) {
    super(Character.toString(value));
  }

  @Override
  public boolean toBoolean() {
    return get().length() > 0;
  }

  @Override
  public double toNumber() {
    try {
      return Double.parseDouble(get());
    } catch (NumberFormatException e) {
      throw new LyteError("Cannot cast string '" + get() + "' to double!");
    }
  }

  @Override
  public LyteValue getProperty(String property) {
    Integer index;
    if ((index = LyteList.tryParse(property)) != null) {
      try {
        return new LyteString(get().charAt(index));
      } catch (StringIndexOutOfBoundsException e) {
        throw new LyteError("Index out of bounds, " + index + "!");
      }
    } else if (sFunctions.hasProperty(property)) {
      return sFunctions.getProperty(property);
    } else {
      System.err.println(sFunctions);
      throw new LyteError("Cannot invoke the property " + property + " of a string.");
    }
  }

  @Override
  public boolean equals(LyteValue other) {
    if (other.typeOf().equals(typeOf())) {
      return equalsStrict(other);
    } else if (other.isSimpleComparison()) {
      return other.equals(this);
    } else {
      return other.toString().equals(this);
    }
  }
  
  @Override
  public boolean isSimpleComparison() {
    return true;
  }
}
