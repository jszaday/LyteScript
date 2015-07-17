package com.lyte.stdlib;

import com.lyte.core.LyteContext;
import com.lyte.objs.LyteList;
import com.lyte.objs.LyteNativeBlock;
import com.lyte.utils.LyteMemberBlock;
import com.lyte.utils.LyteSimpleInjectable;

/**
 * Created by a0225785 on 6/29/2015.
 */
public class LyteListMembers extends LyteSimpleInjectable {
  public static LyteNativeBlock listPush = new LyteMemberBlock<LyteList>("push") {
    @Override
    public void invoke(LyteList self, LyteContext context) {
      self.push(context.apply());
    }
  };
  public static LyteNativeBlock listPop = new LyteMemberBlock<LyteList>("pop") {
    @Override
    public void invoke(LyteList self, LyteContext context) {
      context.push(self.pop());
    }
  };
  public static LyteNativeBlock listLength = new LyteMemberBlock<LyteList>("length") {
    @Override
    public void invoke(LyteList self, LyteContext context) {
      context.push(self.size());
    }
  };
  public static LyteNativeBlock listIsEmpty = new LyteMemberBlock<LyteList>("empty?") {
    @Override
    public void invoke(LyteList self, LyteContext context) {
      context.push(self.isEmpty());
    }
  };
}
