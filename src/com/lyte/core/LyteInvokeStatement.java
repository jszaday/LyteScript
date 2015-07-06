package com.lyte.core;

import com.lyte.objs.*;
import org.apache.commons.collections4.iterators.PeekingIterator;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by a0225785 on 6/17/2015.
 */
public class LyteInvokeStatement extends LyteStatement {

  private String mPrimaryIdentifier;
  private List<LyteSpecifier> mSpecifiers;

  public LyteInvokeStatement(String lineNumber, String primaryIdentifier) {
    this(lineNumber, primaryIdentifier, new ArrayList<LyteSpecifier>());
  }

  public LyteInvokeStatement(String lineNumber, String primaryIdentifier, List<LyteSpecifier> specifiers) {
    super(lineNumber);
    mPrimaryIdentifier = primaryIdentifier;
    mSpecifiers = specifiers;
  }

  @Override
  public void applyTo(LyteContext context) {
    if (isSimpleInvokation()) {
      LyteValue value = context.get(mPrimaryIdentifier);
      if (value.typeOf().equals("block")) {
        ((LyteBlock) value).invoke(context);
      } else {
        context.stack.push(value);
      }
    } else {
      LyteValue retVal = context.resolve(this, true);
      if (retVal != null) {
        context.stack.push(retVal);
      }
    }
  }

  public PeekingIterator<LyteSpecifier> getSpecifiersIterator(int offset) {
    if (!mSpecifiers.isEmpty()) {
      return new PeekingIterator<LyteSpecifier>(mSpecifiers.subList(0, mSpecifiers.size() - offset).iterator());
    } else {
      return new PeekingIterator<LyteSpecifier>(mSpecifiers.iterator());
    }
  }

  public boolean isSimpleInvokation() {
    return mSpecifiers.isEmpty() && !(mPrimaryIdentifier.startsWith("#") || mPrimaryIdentifier.startsWith("@"));
  }

  public boolean isSimpleAssignment() {
    return mSpecifiers.isEmpty();
  }

  public String getPrimaryIdentifier() {
    return mPrimaryIdentifier;
  }

  public LyteSpecifier getLastSpecifier() {
    return mSpecifiers.get(mSpecifiers.size() - 1);
  }

  public boolean isFunctionInvokation() {
    return !isSimpleInvokation() && (!mSpecifiers.isEmpty() && getLastSpecifier().arguments != null);
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
