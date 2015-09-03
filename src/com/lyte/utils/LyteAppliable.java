package com.lyte.utils;

import com.lyte.core.LyteContext;
import com.lyte.objs.LyteValue;

/**
 * Created by jszaday on 7/13/2015.
 */
public interface LyteAppliable {
  LyteValue apply(LyteContext context);
  boolean is(String type);
  String typeOf();
}
