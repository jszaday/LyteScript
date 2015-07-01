package com.lyte.objs;

import com.lyte.core.LyteContext;
import com.lyte.core.LyteScope;

import java.util.HashMap;

/**
 * Created by jszaday on 6/22/15.
 */
public class LytePackage extends LyteObject {
  public LytePackage() {
    super(null);
    set(new HashMap<String, LyteValue>());
  }

  public LytePackage getNamespace(String key) {
    if (!hasProperty(key)) {
      super.setProperty(key, new LytePackage());
    }
    return (LytePackage) getProperty(key);
  }

  public void elevatedSet(String key, LyteValue value) {
    super.setProperty(key, value);
  }

  @Override
  public LyteValue<HashMap<String, LyteValue>> clone(LyteContext context) {
    return this;
  }

  @Override
  public void setProperty(String key, LyteValue value) {
    throw new LyteError("Can't assign to " + key);
  }

  public void addAll(LytePackage lytePackage) {
    for (String key : lytePackage.get().keySet()) {
      elevatedSet(key, lytePackage.getProperty(key));
    }
  }
}
