package com.lyte.objs;

import com.lyte.core.LyteContext;
import com.lyte.core.LyteScope;
import com.lyte.core.LyteStatement;

import java.util.HashMap;
import java.util.LinkedList;

public class LyteRawObject extends LyteRawValue<HashMap<String, LyteValue>>  {

  private LyteScope mCachedScope;

  private LinkedList<LyteKVPair> mKeyValues;

  public LyteRawObject() {
    mKeyValues = new LinkedList<>();
  }

  public void setProperty(LyteValue key, LyteStatement value, LyteMemberType memberType) {
    mKeyValues.add(new LyteKVPair(key, value, memberType));
  }

  public void setProperty(String key, LyteStatement value) {
    setProperty(new LyteString(key), value);
  }

  public void setProperty(LyteValue key, LyteStatement value) {
    setProperty(key, value, LyteMemberType.NORMAL);
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

    for (LyteKVPair pair : mKeyValues) {
      String key = pair.key.apply(context).toString();
      LyteValue value = pair.value.apply(objectContext);

      switch(pair.memberType) {
        case GETTER:
          if (value.is("block")) {
            newObject.putGetter(key, (LyteBlock) value);
            break;
          }
        case SETTER:
          if (value.is("block")) {
            newObject.putSetter(key, (LyteBlock) value);
            break;
          }
        case NORMAL:
          newObject.setProperty(key, value);
          break;
      }
    }

    return newObject;
  }

  @Override
  public String typeOf() {
    return "rawObject";
  }

  public enum LyteMemberType {
    GETTER, SETTER, NORMAL;
  };

  private static class LyteKVPair {
    public final LyteMemberType memberType;
    public final LyteValue key;
    public final LyteStatement value;

    public LyteKVPair(LyteValue key, LyteStatement value, LyteMemberType memberType) {
      this.key = key;
      this.value = value;
      this.memberType = memberType;
    }
  }
}
