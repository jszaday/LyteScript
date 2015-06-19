package com.lyte.core;

import com.lyte.objs.LyteRawBlock;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by a0225785 on 6/17/2015.
 */
public class LyteInvokeStatement implements LyteStatement {

  private String mPrimaryIdentifier;
  private List<LyteSpecifier> mSpecifiers;

  public LyteInvokeStatement(String primaryIdentifier) {
    this(primaryIdentifier, new ArrayList<LyteSpecifier>());
  }

  public LyteInvokeStatement(String primaryIdentifier, List<LyteSpecifier> specifiers) {
    mPrimaryIdentifier = primaryIdentifier;
    mSpecifiers = specifiers;
  }

  @Override
  public void applyTo(LyteScope scope) {

  }

  public String toString(boolean withDescription) {
    if (withDescription) {
      return "Invoke: " + mPrimaryIdentifier + mSpecifiers;
    } else {
      return mPrimaryIdentifier + mSpecifiers;
    }
  }

  @Override
  public String toString() {
    return toString(true);
  }

  public static class LyteSpecifier {
    public final String identifier;
    public final LyteStatement invokable;
    public final List<LyteRawBlock> arguments;

    private LyteSpecifier(String identifier, LyteStatement invokable, List<LyteRawBlock> arguments) {
      this.identifier = identifier;
      this.invokable = invokable;
      this.arguments = arguments;
    }

    public LyteSpecifier(String identifier) {
      this(identifier, null, null);
    }

    public LyteSpecifier(LyteStatement invokable) {
      this(null, invokable, null);
    }

    public LyteSpecifier(List<LyteRawBlock> arguments) {
      this(null, null, arguments);
    }

    @Override
    public String toString() {
      if (identifier != null) {
        return "." + identifier;
      } else if (invokable != null) {
        return "[" + invokable + "]";
      } else {
        return "(" + arguments + ")";
      }
    }
  }
}
