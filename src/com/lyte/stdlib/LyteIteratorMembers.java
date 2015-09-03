package com.lyte.stdlib;

import com.lyte.core.LyteContext;
import com.lyte.objs.LyteIterator;
import com.lyte.objs.LyteNativeBlock;
import com.lyte.utils.LyteMemberBlock;
import com.lyte.utils.LyteSimpleInjectable;

/**
 * Created by jszaday on 7/24/2015.
 */
public class LyteIteratorMembers extends LyteSimpleInjectable {
  public static LyteNativeBlock hasNext = new LyteMemberBlock<LyteIterator>("next?") {

    @Override
    public void invoke(LyteIterator self, LyteContext context) {
      context.push(self.hasNext());
    }
  };
  public static LyteNativeBlock next = new LyteMemberBlock<LyteIterator>("next") {

    @Override
    public void invoke(LyteIterator self, LyteContext context) {
      context.push(self.next());
    }
  };
  public static LyteNativeBlock remove = new LyteMemberBlock<LyteIterator>("remove") {

    @Override
    public void invoke(LyteIterator self, LyteContext context) {
      self.remove();
    }
  };
  public static LyteNativeBlock generator = new LyteMemberBlock<LyteIterator>("__generator") {

    @Override
    public void invoke(LyteIterator self, LyteContext context) {
      while (self.hasNext()) {
        context.yield(self.next());
      }
    }
  };
}
