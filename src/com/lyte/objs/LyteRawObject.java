package com.lyte.objs;

import com.lyte.core.LyteScope;
import com.lyte.core.LyteStatement;

import java.util.HashMap;

public class LyteRawObject implements LyteValue  {

  private HashMap<String, LyteStatement> mProperties;

  public LyteRawObject() {
    mProperties = new HashMap<String, LyteStatement>();
  }

  public LyteStatement set(String key, LyteStatement value) {
    return mProperties.put(key, value);
  }

  @Override
  public LyteValue clone(LyteScope scope) {
    HashMap<String, LyteValue> properties = new HashMap<String, LyteValue>();
    for (String key : mProperties.keySet()) {
      // TODO Implement Checking To See If Only One Value Is Pushed
      mProperties.get(key).applyTo(scope);
      properties.put(key, scope.pop());
    }
    return new LyteObject(this, properties);
  }

  @Override
  public String typeOf() {
    return "rawObject";
  }

  @Override
  public String toString() {
    return mProperties.toString();
  }
}
