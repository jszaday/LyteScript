package com.lyte.utils;

import com.lyte.objs.LyteValue;

/**
 * Created by a0225785 on 6/29/2015.
 */
public interface LyteInjectable {
  void inject(String name, LyteValue value);
}
