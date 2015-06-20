package com.lyte.objs;

import com.lyte.core.LyteScope;
import com.lyte.core.LyteStatement;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by a0225785 on 6/17/2015.
 */
public class LyteRawBlock implements LyteValue {
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

  @Override
  public LyteValue clone(LyteScope scope) {
    return new LyteBlock(scope, mStatements, mArgs);
  }

  @Override
  public String toString() {
    return mArgs + " => " + mStatements;
  }

  @Override
  public String typeOf() {
    return "rawBlock";
  }

  @Override
  public boolean isTruthy() {
    return false;
  }
}
