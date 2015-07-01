package com.lyte.stdlib;

import com.lyte.core.LyteContext;
import com.lyte.objs.*;
import com.lyte.utils.LyteSimpleInjectable;

import java.io.IOException;

/**
 * Created by a0225785 on 7/1/2015.
 */
public class LyteStreamFunctions extends LyteSimpleInjectable {

  public static final LyteNativeBlock streamRead = new LyteStreamBlock("read") {
    @Override
    public void invoke(LyteStream self, LyteContext context) {
      context.push(self.read());
    }
  };

  public static final LyteNativeBlock streamReadLn = new LyteStreamBlock("readLn") {
    @Override
    public void invoke(LyteStream self, LyteContext context) {
      String line = self.readLine();
      context.push(line == null ? LyteUndefined.UNDEFINED : new LyteString(line));
    }
  };

  public static final LyteNativeBlock streamClose = new LyteStreamBlock("close") {
    @Override
    public void invoke(LyteStream self, LyteContext context) {
      try {
        self.close();
      } catch (IOException e) {
        throw new LyteError(e.getMessage());
      }
    }
  };

  public static final LyteNativeBlock streamIsReadable = new LyteStreamBlock("readable?") {
    @Override
    public void invoke(LyteStream self, LyteContext context) {
      context.push(self.isReadable());
    }
  };

  public static final LyteNativeBlock streamIsWritable = new LyteStreamBlock("writable?") {
    @Override
    public void invoke(LyteStream self, LyteContext context) {
      context.push(!self.isReadable());
    }
  };

  public static final LyteNativeBlock streamWrite = new LyteStreamBlock("write") {
    @Override
    public void invoke(LyteStream self, LyteContext context) {
      LyteValue value = context.apply();
      if (value.typeOf().equals("string")) {
        self.write(value.toString());
      } else {
        self.write((int) value.toNumber());
      }
    }
  };

  public static final LyteNativeBlock streamWriteLn = new LyteStreamBlock("writeLn") {
    @Override
    public void invoke(LyteStream self, LyteContext context) {
      self.writeLine(context.apply().toString());
    }
  };

  public static final LyteNativeBlock streamFlush = new LyteStreamBlock("flush") {
    @Override
    public void invoke(LyteStream self, LyteContext context) {
      self.flush();
    }
  };

  private static abstract class LyteStreamBlock extends LyteNativeBlock {

    public LyteStreamBlock(String alias) {
      super("Stream", alias);
    }

    public abstract void invoke(LyteStream self, LyteContext context);

    @Override
    public void invoke(LyteContext context) {
      if (!context.self.typeOf().equals("stream")) {
        throw new LyteError("Cannot invoke the function " + alias + " on a(n) " + context.self.typeOf());
      } else {
        invoke((LyteStream) context.self, context);
      }
    }
  }
}
