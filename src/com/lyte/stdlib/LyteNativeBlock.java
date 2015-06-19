package com.lyte.stdlib;

import com.lyte.core.LyteScope;
import com.lyte.objs.LyteBlock;
import com.lyte.objs.LyteValue;

/**
 * Created by a0225785 on 6/19/2015.
 */
public abstract class LyteNativeBlock extends LyteBlock {

  protected LyteNativeBlock(LyteScope parentScope) {
    super(parentScope, null);
  }

  public abstract String getSymbol();

  @Override
  public abstract void invoke();

  @Override
  public LyteValue clone(LyteScope scope) {
    return this;
  }
}
