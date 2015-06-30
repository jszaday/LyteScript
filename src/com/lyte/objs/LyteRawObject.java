package com.lyte.objs;

import com.lyte.core.LyteContext;
import com.lyte.core.LyteScope;
import com.lyte.core.LyteStack;
import com.lyte.core.LyteStatement;

import java.util.HashMap;

public class LyteRawObject extends LyteRawValue<HashMap<String, LyteValue>>  {

  private LyteScope mCachedScope;

  private HashMap<String, LyteStatement> mProperties;

  public LyteRawObject() {
    mProperties = new HashMap<String, LyteStatement>();
  }

  public LyteStatement set(String key, LyteStatement value) {
    return mProperties.put(key, value);
  }

  @Override
  public LyteValue<HashMap<String, LyteValue>> clone(LyteContext context) {
    HashMap<String, LyteValue> properties = new HashMap<String, LyteValue>();
    if (context.scope == null) {
      // Uncache the scope
      context.scope = mCachedScope;
    } else {
      // Cache the scope
      mCachedScope = context.scope;
    }
    // TODO Use a more "global" stack...
    LyteObject newObject = new LyteObject(this);
    for (String key : mProperties.keySet()) {
      properties.put(key, mProperties.get(key).apply(context));
    }
    newObject.set(properties);
    return newObject;
  }

  @Override
  public String typeOf() {
    return "rawObject";
  }
}
