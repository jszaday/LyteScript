package com.lyte.stdlib;

import com.lyte.core.LyteContext;
import com.lyte.objs.LyteError;
import com.lyte.objs.LyteNativeBlock;
import com.lyte.utils.LyteSimpleInjectable;

/**
 * Created by a0225785 on 7/14/2015.
 */
public class LyteThreadFunctions extends LyteSimpleInjectable {

  public static LyteNativeBlock threadThread = new LyteNativeBlock("Thread", "Thread", null) {
    @Override
    public void invoke(LyteContext context) {
      context.push(LyteThreadMixin.getSingleton());
    }
  };

  public static LyteNativeBlock threadSleep = new LyteNativeBlock("Thread", "Sleep", null) {
    @Override
    public void invoke(LyteContext context) {
      long ms = (long) context.apply().toNumber();

      try {
        Thread.sleep(ms);
      } catch (InterruptedException e) {
        throw new LyteError(e);
      }
    }
  };

  public static LyteNativeBlock threadAvailableProcessors = new LyteNativeBlock("Thread", "AvailableProcessors", null) {
    @Override
    public void invoke(LyteContext context) {
      context.push(Runtime.getRuntime().availableProcessors());
    }
  };

  public static LyteNativeBlock threadLock = new LyteNativeBlock("Thread", "Lock", null) {
    @Override
    public void invoke(LyteContext context) {
      context.push(LyteLockTemplate.getSingleton());
    }
  };
}
