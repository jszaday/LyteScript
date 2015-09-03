package com.lyte.utils;

import com.lyte.objs.*;
import org.json.simple.parser.ContentHandler;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.util.ArrayDeque;

/**
 * Created by jszaday on 7/9/2015.
 */
public class LyteJsonParser implements ContentHandler {
  private static final JSONParser JSON_PARSER = new JSONParser();
  private static final LyteJsonParser SINGLETON = new LyteJsonParser();

  private ArrayDeque<LyteValue> mScopeStack;
  private ArrayDeque<String> mEntryStack;
  private LyteValue mFinalValue;

  public static synchronized LyteValue parseJson(String json) {
    try {
      JSON_PARSER.parse(json, SINGLETON);
    } catch (ParseException e) {
      throw new LyteError(e);
    }
    return SINGLETON.getFinalValue();
  }

  private LyteValue getFinalValue() {
    return mFinalValue;
  }

  @Override
  public void startJSON() throws ParseException, IOException {
    mScopeStack = new ArrayDeque<>();
    mEntryStack = new ArrayDeque<>();
  }

  @Override
  public void endJSON() throws ParseException, IOException {

  }

  @Override
  public boolean startObject() throws ParseException, IOException {
    mScopeStack.push(new LyteObject());
    return true;
  }

  @Override
  public boolean endObject() throws ParseException, IOException {
    addToScope(mScopeStack.pop());
    return true;
  }

  @Override
  public boolean startObjectEntry(String s) throws ParseException, IOException {
    mEntryStack.push(s);
    return true;
  }

  @Override
  public boolean endObjectEntry() throws ParseException, IOException {
    return true;
  }

  @Override
  public boolean startArray() throws ParseException, IOException {
    mScopeStack.push(new LyteList());
    return true;
  }

  @Override
  public boolean endArray() throws ParseException, IOException {
    addToScope(mScopeStack.pop());
    return true;
  }

  @Override
  public boolean primitive(Object o) throws ParseException, IOException {
    if (o == null) {
      addToScope(LyteUndefined.NULL);
    } else if (o instanceof Number) {
      addToScope(LyteNumber.valueOf(((Number) o).doubleValue()));
    } else if (o instanceof Boolean) {
      addToScope(LyteBoolean.valueOf((Boolean) o));
    } else {
      addToScope(new LyteString(o.toString()));
    }

    return true;
  }

  private void addToScope(LyteValue value) {
    LyteValue scope = mScopeStack.peek();

    if (scope == null) {
      mFinalValue = value;
    } else if (scope.is("object")) {
      scope.setProperty(mEntryStack.pop(), value);
    } else if (scope.is("list")) {
      ((LyteList) scope).add(value);
    }
  }
}
