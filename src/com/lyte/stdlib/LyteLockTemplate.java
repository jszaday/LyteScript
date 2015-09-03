package com.lyte.stdlib;

import com.lyte.core.LyteContext;
import com.lyte.objs.LyteError;
import com.lyte.objs.LyteNativeBlock;
import com.lyte.objs.LyteObject;
import com.lyte.objs.LyteValue;
import com.lyte.utils.LyteMemberBlock;
import com.lyte.utils.LyteSimpleInjectable;

import java.util.concurrent.Semaphore;

/**
 * Created by jszaday on 7/15/2015.
 */
public class LyteLockTemplate extends LyteSimpleInjectable {
  private static final String LOCK_METADATA = "lock";
  private static LyteLockTemplate singleton = null;

  private LyteLockTemplate() {
    super();
  }

  public static LyteNativeBlock lockConstructor = new LyteMemberBlock<LyteObject>("__constructor") {

    @Override
    public void invoke(LyteObject self, LyteContext context) {
      self.putMetadata(LOCK_METADATA, new Semaphore(1));
    }
  };

  public static LyteNativeBlock lockAcquire = new LyteMemberBlock<LyteObject>("acquire") {

    @Override
    public void invoke(LyteObject self, LyteContext context) {
      Semaphore semaphore;
      if ((semaphore = (Semaphore) self.getMetadata(LOCK_METADATA)) != null) {
        try {
          semaphore.acquire();
        } catch (InterruptedException e) {
          throw new LyteError(e);
        }
      } else {
        throw new LyteError("Cannot call " + fullname +" for an uninitialized lock object!");
      }
    }
  };

  public static LyteNativeBlock lockRelease = new LyteMemberBlock<LyteObject>("release") {

    @Override
    public void invoke(LyteObject self, LyteContext context) {
      Semaphore semaphore;
      if ((semaphore = (Semaphore) self.getMetadata(LOCK_METADATA)) != null) {
        semaphore.release();
      } else {
        throw new LyteError("Cannot call " + fullname +" for an uninitialized lock object!");
      }
    }
  };

  @Override
  public LyteValue clone(LyteContext context) {
    return new LyteObject(this);
  }

  @Override
  public String typeOf() {
    return "object";
  }

  public static LyteLockTemplate getSingleton() {
    if (singleton == null) {
      return (singleton = new LyteLockTemplate());
    } else {
      return singleton;
    }
  }
}
