package com.lyte.objs;

import com.lyte.core.LyteContext;
import com.lyte.stdlib.LyteErrorMembers;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created by jszaday on 6/23/2015.
 */
public class LyteError extends RuntimeException implements LyteValue<RuntimeException> {

  private static final LyteErrorMembers ERROR_FUNCTIONS = new LyteErrorMembers();
  private ArrayList<String> mLineNumbers = new ArrayList<String>();

  public LyteError(Exception e) {
    super(e.getMessage());
  }

  public LyteError(String value) {
    super(value);
  }

  public LyteError(LyteValue value) {
    super(value.is("error") ? ((LyteError) value).getMessage() : value.toString());
  }

  @Override
  public RuntimeException get() {
    return this;
  }

  public LyteList getLyteStackTrace() {
    LyteList stackTrace = new LyteList();

    for (String lineNumber : mLineNumbers) {
      stackTrace.add(new LyteString(lineNumber));
    }

    return stackTrace;
  }

  public List<String> getLines() {
    return mLineNumbers;
  }

  @Override
  public LyteValue getProperty(String property) {
    return ERROR_FUNCTIONS.getProperty(property);
  }

  @Override
  public void setProperty(String property, LyteValue newValue) {
    throw new LyteError("Cannot set the property " + property + " of an Error Object!");
  }

  @Override
  public boolean hasProperty(String property) {
    return ERROR_FUNCTIONS.hasProperty(property);
  }

  @Override
  public boolean toBoolean() {
    return true;
  }

  @Override
  public double toNumber() {
    return hashCode();
  }

  @Override
  public String typeOf() {
    return "error";
  }

  @Override
  public LyteValue<RuntimeException> clone(LyteContext context) {
    return this;
  }

  @Override
  public LyteValue apply(LyteContext context) {
    return this;
  }

  @Override
  public String toString() {
    return (!mLineNumbers.isEmpty() ? "Line " + mLineNumbers.get(0) + ": " : "") + getMessage();
  }

  public void addLineNumber(String lineNumber) {
    mLineNumbers.add(lineNumber);
  }

  @Override
  public boolean equals(LyteValue other) {
    if (other.is(typeOf())) {
      return equalsStrict(other);
    } else if (other.isSimpleComparison()) {
      return other.equals(this);
    } else {
      return false;
    }
  }

  @Override
  public boolean equalsStrict(LyteValue other) {
    return this == other;
  }

  @Override
  public boolean isSimpleComparison() {
    return false;
  }

  @Override
  public boolean is(String type) {
    return type.equals(typeOf());
  }

  @Override
  public Set<String> getProperties() {
    return ERROR_FUNCTIONS.getProperties();
  }

  @Override
  public String toJSONString() {
    JSONObject object = new JSONObject();
    object.put("message", getMessage());
    object.put("stackTrace", getLyteStackTrace());
    return object.toJSONString();
  }
}
