package com.lyte.utils;

import com.lyte.core.LyteContext;
import com.lyte.objs.LyteError;
import com.lyte.objs.LytePackage;
import com.lyte.objs.LyteValue;
import org.json.simple.JSONAware;
import org.json.simple.JSONObject;

import java.util.HashMap;
import java.util.Set;

/**
 * Created by jszaday on 6/29/2015.
 */
public abstract class LyteSimpleInjectable extends LytePackage implements LyteInjectable {

  public LyteSimpleInjectable() {
    LyteNativeInjector.injectNatives(this);
  }

  @Override
  public void inject(String name, LyteValue value) {
    if (!hasProperty(name)) {
      elevatedSet(name, value);
    } else {
      throw new RuntimeException("Attempted to duplicate property " + name);
    }
  }

  @Override
  public LyteValue<HashMap<String, LyteValue>> clone(LyteContext context) {
    throw new LyteError("Cannot clone a " + typeOf() + "!");
  }

  @Override
  public boolean is(String type) {
    return type.equals(typeOf());
  }

  @Override
  public String typeOf() {
    return "mixin";
  }
}
