package com.lyte.objs;

import com.lyte.core.LyteContext;
import com.lyte.core.LytePushStatement;
import com.lyte.core.LyteScope;
import com.lyte.core.LyteStatement;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by a0225785 on 6/17/2015.
 */
public class LyteRawBlock extends LyteRawValue<List<LyteStatement>> {
  private static int sNumInstances = 0;
  public final int instanceNumber;
  private ArrayList<String> mArgs;
  private ArrayList<LyteStatement> mStatements;
  private LyteRawBlock mParent;

  private LyteRawBlock(LyteRawBlock parent) {
    mParent = parent;
    mStatements = new ArrayList<LyteStatement>();
    mArgs = new ArrayList<String>();
    instanceNumber = ++sNumInstances;
  }

  public static LyteRawBlock newGlobal() {
    return new LyteRawBlock(null);
  }

  public boolean addStatement(LyteStatement statement) {
    return mStatements.add(statement);
  }

  public boolean addArg(String arg) {
    return mArgs.add(arg);
  }

  public LyteRawBlock enter() {
    return new LyteRawBlock(this);
  }

  public LyteRawBlock leave() {
    return mParent;
  }

  public boolean isEmpty() {
    return mStatements.isEmpty();
  }

  public List<LyteStatement> getStatements() { return mStatements; }

  private void setParent(LyteRawBlock parent) {
    mParent = parent;
  }

  public LyteRawBlock simplify() {
    if (mStatements.size() == 1 && (mStatements.get(0) instanceof LytePushStatement)) {
      LyteValue value = ((LytePushStatement) mStatements.get(0)).getValue();

      if (value instanceof LyteRawBlock) {
        ((LyteRawBlock) value).setParent(mParent);
        return (LyteRawBlock) value;
      } else {
        return this;
      }
    } else {
      return this;
    }
  }

  public LyteValue<List<LyteStatement>> clone(LyteContext context) {
    return new LyteBlock(context, mStatements, mArgs, true, false);
  }

  public LyteValue<List<LyteStatement>> clone(LyteContext context, boolean canEnter) {
    return new LyteBlock(context, mStatements, mArgs, canEnter, false);
  }

  public LyteValue<List<LyteStatement>> clone(LyteContext context, boolean canEnter, boolean hasSelf) {
    return new LyteBlock(context, mStatements, mArgs, canEnter, hasSelf);
  }

  @Override
  public String toString() {
    return mArgs + " => " + mStatements;
  }

  @Override
  public String typeOf() {
    return "rawBlock";
  }
}
