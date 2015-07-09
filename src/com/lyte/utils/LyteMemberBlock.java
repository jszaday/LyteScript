package com.lyte.utils;

import com.lyte.core.LyteContext;
import com.lyte.objs.LyteError;
import com.lyte.objs.LyteValue;
import com.lyte.stdlib.LyteNativeBlock;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * Created by a0225785 on 7/9/2015.
 */
public abstract class LyteMemberBlock<T extends LyteValue> extends LyteNativeBlock {

  private Class<T> mClass;

  public LyteMemberBlock(String fullname) {
    super("Member", fullname);

    try {
      inferClass();
    } catch (ClassNotFoundException e) {
      throw new LyteError(e);
    }
  }

  private void inferClass() throws ClassNotFoundException {
    Type mySuperclass = getClass().getGenericSuperclass();
    Type tType = ((ParameterizedType)mySuperclass).getActualTypeArguments()[0];
    String className = tType.toString().split(" ")[1];
    mClass = (Class<T>) Class.forName(className);
  }

  public abstract void invoke(T self, LyteContext context);

  @Override
  public void invoke(LyteContext context) {
    if (mClass.isInstance(context.self)) {
      invoke((T) context.self, context);
    } else {
      throw new LyteError("Cannot apply " + fullname + " to an " + context.self.typeOf() + "!");
    }
  }
}
