package com.lyte.objs;

import com.lyte.core.LyteContext;
import com.lyte.core.LyteScope;
import com.lyte.core.LyteStack;
import com.lyte.core.LyteStatement;

import java.util.HashMap;
import java.util.LinkedHashMap;

public class LyteRawObject extends LyteRawValue<HashMap<String, LyteValue>>  {

  private LyteScope mCachedScope;

  private LinkedHashMap<String, LyteStatement> mProperties;

  public LyteRawObject() {
    mProperties = new LinkedHashMap<String, LyteStatement>();
  }

  public LyteStatement set(String key, LyteStatement value) {
    return mProperties.put(key, value);
  }

  @Override
  public LyteValue<HashMap<String, LyteValue>> clone(LyteContext context) {
    if (context.scope == null) {
      // Uncache the scope
      context.scope = mCachedScope;
    } else {
      // Cache the scope
      mCachedScope = context.scope;
    }
    LyteObject newObject = new LyteObject(this);
    LyteContext objectContext = new LyteContext(newObject, context);

    for (String key : mProperties.keySet()) {
      newObject.setProperty(key, mProperties.get(key).apply(objectContext));
    }

    return newObject;
  }

  @Override
  public String typeOf() {
    return "rawObject";
  }
}
