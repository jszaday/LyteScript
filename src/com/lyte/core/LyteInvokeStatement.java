package com.lyte.core;

import com.lyte.objs.*;
import org.apache.commons.collections4.iterators.PeekingIterator;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by jszaday on 6/17/2015.
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
    LyteValue value;

    if (isSimpleInvokation()) {
      value = context.get(mPrimaryIdentifier);
    } else {
      value = context.resolve(this, true, false);
    }

    if (value == null) {
      return;
    } else if (value.is("block")) {
      ((LyteBlock) value).invoke(context);
    } else {
      context.stack.push(value);
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
    public final LyteRawBlock invokables;
    public final List<LyteRawBlock> arguments;

    private LyteSpecifier(String identifier, LyteRawBlock invokables, List<LyteRawBlock> arguments) {
      this.identifier = identifier;
      this.invokables = invokables;
      this.arguments = arguments;
    }

    public LyteSpecifier(String identifier) {
      this(identifier, null, null);
    }

    public LyteSpecifier(LyteRawBlock invokables) {
      this(null, invokables, null);
    }

    public LyteSpecifier(List<LyteRawBlock> arguments) {
      this(null, null, arguments);
    }

    @Override
    public String toString() {
      if (identifier != null) {
        return "." + identifier;
      } else if (invokables != null) {
        return invokables.get().toString();
      } else {
        return "(" + arguments + ")";
      }
    }
  }
}
