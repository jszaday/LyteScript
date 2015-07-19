package com.lyte.stdlib;

import com.lyte.core.LyteContext;
import com.lyte.objs.LyteError;
import com.lyte.objs.LyteNativeBlock;
import com.lyte.objs.LyteString;
import com.lyte.utils.LyteMemberBlock;
import com.lyte.utils.LyteSimpleInjectable;

import java.util.List;

/**
 * Created by a0225785 on 7/9/2015.
 */
public class LyteErrorMembers extends LyteSimpleInjectable {

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

  public static LyteNativeBlock printStackTrace = new LyteMemberBlock<LyteError>("printStackTrace") {

    @Override
    public void invoke(LyteError self, LyteContext context) {
      List<String> lines = self.getLines();
      if (lines.isEmpty()) {
        System.err.println("Error: " + self.getMessage());
      } else {
        System.err.println("Line: " + lines.get(0) +", Error: " + self.getMessage());
      }
      if (lines.size() > 1) {
        for (String line : lines.subList(1, lines.size())) {
          System.err.println("\tCalled by line " + line + ".");
        }
      }
    }
  };

  public static LyteNativeBlock generator = new LyteMemberBlock<LyteError>("__generator") {
    @Override
    public void invoke(LyteError self, LyteContext context) {
      for (String line : self.getLines()) {
        context.yield(new LyteString(line));
      }
    }
  };
}
