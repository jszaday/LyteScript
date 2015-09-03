package com.lyte.objs;

import com.lyte.core.LyteContext;
import com.lyte.core.LytePushStatement;
import com.lyte.gen.LyteParser;
import com.lyte.utils.LyteAppliable;
import com.lyte.utils.LyteRangeMaker;

import java.util.List;

/**
 * Created by jszaday on 7/13/2015.
 */
public class LyteRawRange extends LyteRawValue {

  private LyteAppliable mStart;
  private LyteAppliable mFinish;
  private LyteAppliable mStep;

  public LyteRawRange(LyteAppliable start, LyteAppliable step, LyteAppliable finish) {
    mStart = start;
    mStep = step;
    mFinish = finish;
  }

  @Override
  public LyteValue clone(LyteContext context) {
    double start = mStart.apply(context).toNumber();
    double finish = mFinish.apply(context).toNumber();

    if (mStep == null) {
      return LyteRangeMaker.range(start, finish);
    } else {
      return LyteRangeMaker.range(start, mStep.apply(context).toNumber(), finish);
    }
  }

  @Override
  public String typeOf() {
    return "rawRange";
  }
}
