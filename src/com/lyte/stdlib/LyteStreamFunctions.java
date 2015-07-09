package com.lyte.stdlib;

import com.lyte.core.LyteContext;
import com.lyte.objs.*;
import com.lyte.utils.LyteMemberBlock;
import com.lyte.utils.LyteSimpleInjectable;

import java.io.IOException;

/**
 * Created by a0225785 on 7/1/2015.
 */
public class LyteStreamFunctions extends LyteSimpleInjectable {

  public static final LyteNativeBlock streamRead = new LyteMemberBlock<LyteStream>("read") {
    @Override
    public void invoke(LyteStream self, LyteContext context) {
      context.push(self.read());
    }
  };

  public static final LyteNativeBlock streamReadLn = new LyteMemberBlock<LyteStream>("readLn") {
    @Override
    public void invoke(LyteStream self, LyteContext context) {
      String line = self.readLine();
      context.push(line == null ? LyteUndefined.UNDEFINED : new LyteString(line));
    }
  };

  public static final LyteNativeBlock streamClose = new LyteMemberBlock<LyteStream>("close") {
    @Override
    public void invoke(LyteStream self, LyteContext context) {
      try {
        self.close();
      } catch (IOException e) {
        throw new LyteError(e.getMessage());
      }
    }
  };

  public static final LyteNativeBlock streamIsReadable = new LyteMemberBlock<LyteStream>("readable?") {
    @Override
    public void invoke(LyteStream self, LyteContext context) {
      context.push(self.isReadable());
    }
  };

  public static final LyteNativeBlock streamIsWritable = new LyteMemberBlock<LyteStream>("writable?") {
    @Override
    public void invoke(LyteStream self, LyteContext context) {
      context.push(!self.isReadable());
    }
  };

  public static final LyteNativeBlock streamWrite = new LyteMemberBlock<LyteStream>("write") {
    @Override
    public void invoke(LyteStream self, LyteContext context) {
      LyteValue value = context.apply();
      if (value.is("string")) {
        self.write(value.toString());
      } else {
        self.write((int) value.toNumber());
      }
    }
  };

  public static final LyteNativeBlock streamWriteLn = new LyteMemberBlock<LyteStream>("writeLn") {
    @Override
    public void invoke(LyteStream self, LyteContext context) {
      self.writeLine(context.apply().toString());
    }
  };

  public static final LyteNativeBlock streamFlush = new LyteMemberBlock<LyteStream>("flush") {
    @Override
    public void invoke(LyteStream self, LyteContext context) {
      self.flush();
    }
  };

}
