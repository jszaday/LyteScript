package com.lyte.stdlib;

import com.lyte.core.LyteContext;
import com.lyte.objs.LyteFile;
import com.lyte.objs.LyteNativeBlock;
import com.lyte.utils.LyteMemberBlock;
import com.lyte.utils.LyteSimpleInjectable;

/**
 * Created by a0225785 on 7/17/2015.
 */
public class LyteFileMembers extends LyteSimpleInjectable {

  public static final String EXECUTABLE = "executable";
  public static final String READABLE = "readable";
  public static final String WRITABLE = "writable";

  public static LyteNativeBlock fileIsExecutable = new LyteMemberBlock<LyteFile>(EXECUTABLE + "?") {
    @Override
    public void invoke(LyteFile self, LyteContext context) {
      context.push(self.canExecute());
    }
  };

  public static LyteNativeBlock fileIsReadable = new LyteMemberBlock<LyteFile>(READABLE + "?") {
    @Override
    public void invoke(LyteFile self, LyteContext context) {
      context.push(self.canRead());
    }
  };

  public static LyteNativeBlock fileIsWritable = new LyteMemberBlock<LyteFile>(WRITABLE + "?") {
    @Override
    public void invoke(LyteFile self, LyteContext context) {
      context.push(self.canWrite());
    }
  };
}
