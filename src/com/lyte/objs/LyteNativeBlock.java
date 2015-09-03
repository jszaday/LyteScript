package com.lyte.objs;

import com.lyte.core.LyteContext;
import com.lyte.core.LyteScope;
import com.lyte.core.LyteStack;
import com.lyte.objs.LyteBlock;
import com.lyte.objs.LyteObject;
import com.lyte.objs.LyteValue;

/**
 * Created by jszaday on 6/19/2015.
 */
public abstract class LyteNativeBlock extends LyteBlock {

  public final String alias;
  public final String namespace;
  public final String fullname;

  public LyteNativeBlock(String namespace, String alias) {
    this(namespace, null, alias);
  }

  public LyteNativeBlock(String namespace, String fullname, String alias) {
    this(namespace, fullname, alias, false);
  }

  protected LyteNativeBlock(String namespace, String fullname, String alias, boolean maintainsObjs) {
    super(null, null, null, false, false, maintainsObjs);
    this.alias = alias;
    this.namespace = namespace;

    if (fullname != null) {
      this.fullname = fullname;
    } else {
      this.fullname = alias;
    }
  }

  public abstract void invoke(LyteContext context);

  @Override
  public LyteValue clone(LyteContext context) {
    return this;
  }

  @Override
  public String toString() {
    return "[] => Native::Lyte." + namespace + "." + fullname;
  }
}
