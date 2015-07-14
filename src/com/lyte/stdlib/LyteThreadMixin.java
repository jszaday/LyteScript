package com.lyte.stdlib;

import com.lyte.core.LyteContext;
import com.lyte.core.LyteStack;
import com.lyte.objs.*;
import com.lyte.utils.LyteMemberBlock;
import com.lyte.utils.LyteSimpleInjectable;

/**
 * Created by a0225785 on 7/14/2015.
 */
public class LyteThreadMixin extends LyteSimpleInjectable {

  public static final String THREAD_METADATA = "thread";
  public static final String RUN = "run";

  private static final LyteThreadMixin singleton = new LyteThreadMixin();

  private LyteThreadMixin() {
    elevatedSet(RUN, LyteUndefined.NULL);
  }

  public static LyteNativeBlock threadStart = new LyteMemberBlock<LyteObject>("start") {

    @Override
    public void invoke(final LyteObject self, final LyteContext context) {
      if (!(self.hasProperty(RUN)  && self.getProperty(RUN).is("block"))) {
        throw new LyteError("Expected object to have run method!");
      } else if (self.hasMetadata(THREAD_METADATA)) {
        throw new LyteError("Thread has already been started once!");
      } else {
        // Create the thread
        Thread thread = new Thread() {
          @Override
          public void start() {
            ((LyteBlock) self.getProperty(RUN)).invoke(new LyteContext(self, context.scope, new LyteStack()));
          }
        };
        // Put it in the object's metadata
        self.putMetadata(THREAD_METADATA, thread);
        // and start it
        thread.start();
      }
    }
  };

  public static LyteNativeBlock threadJoin = new LyteMemberBlock<LyteObject>("join") {

    @Override
    public void invoke(LyteObject self, LyteContext context) {
      if (!self.hasMetadata(THREAD_METADATA)) {
        throw new LyteError("Thread has not been started!");
      } else {
        try {
          ((Thread) self.getMetadata(THREAD_METADATA)).join();
        } catch (InterruptedException e) {
          throw new LyteError(e);
        }
      }
    }
  };

  public static LyteNativeBlock threadIsAlive = new LyteMemberBlock<LyteObject>("isAlive") {

    @Override
    public void invoke(LyteObject self, LyteContext context) {
      if (!self.hasMetadata(THREAD_METADATA)) {
        context.push(false);
      } else {
        context.push(((Thread) self.getMetadata(THREAD_METADATA)).isAlive());
      }
    }
  };

  public static LyteNativeBlock threadId = new LyteMemberBlock<LyteObject>("id") {

    @Override
    public void invoke(LyteObject self, LyteContext context) {
      if (!self.hasMetadata(THREAD_METADATA)) {
        throw new LyteError("Thread has not been started!");
      } else {
        context.push(((Thread) self.getMetadata(THREAD_METADATA)).getId());
      }
    }
  };

  public static LyteThreadMixin getSingleton() {
    return singleton;
  }
}
