package com.lyte.stdlib;

import com.lyte.core.LyteContext;
import com.lyte.objs.LyteError;
import com.lyte.utils.LyteMemberBlock;
import com.lyte.utils.LyteSimpleInjectable;

/**
 * Created by a0225785 on 7/9/2015.
 */
public class LyteErrorFunctions extends LyteSimpleInjectable {

  public static LyteNativeBlock message = new LyteMemberBlock<LyteError>("message") {

    @Override
    public void invoke(LyteError self, LyteContext context) {
      context.push(self.getMessage());
    }
  };

  public static LyteNativeBlock stackTrace = new LyteMemberBlock<LyteError>("stackTrace") {

    @Override
    public void invoke(LyteError self, LyteContext context) {
      context.push(self.getLyteStackTrace());
    }
  };
}
