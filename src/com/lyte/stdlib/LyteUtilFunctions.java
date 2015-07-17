package com.lyte.stdlib;

import com.lyte.core.LyteContext;
import com.lyte.objs.*;
import org.apache.commons.lang3.StringEscapeUtils;

/**
 * Created by a0225785 on 7/17/2015.
 */
public class LyteUtilFunctions {


  public static LyteNativeBlock utilInstantiate = new LyteNativeBlock("Util", "Instantiate") {
    @Override
    public void invoke(LyteContext context) {
      // TODO this assumes the two things are on the same context.stack, we have to move scoping out of blocks!!!
      LyteValue value = context.apply();
      if (!value.is("object")) {
        throw new LyteError("Cannot Instantiate a(n) " + value.typeOf() + ".");
      }
      LyteObject obj = (LyteObject) value.clone(new LyteContext(null, null, context.stack));
      if (value.hasProperty("__constructor")) {
        try {
          ((LyteBlock) obj.getProperty("__constructor")).invoke(new LyteContext(obj, null, context.stack));
        } catch (ClassCastException e) {
          throw new LyteError("Expected __constructor to be a block for object " + obj);
        }
        obj.unsetProperty("__constructor");
      }
      context.push(obj);
    }
  };

  public static LyteNativeBlock utilMixWith = new LyteNativeBlock("Util", "MixWith") {
    @Override
    public void invoke(LyteContext context) {
      LyteValue value2 = context.pop();
      LyteValue value1 = context.pop();

      if (!value1.is("object") || !(value2.is("object") || value2.is("mixin"))) {
        throw new LyteError("Cannot mix a(n) " + value1 + " with a(n) " + value2);
      }

      ((LyteObject) value1).mixWith(context, value2);
    }
  };

  public static LyteNativeBlock utilMakeList = new LyteNativeBlock("Util", "MakeList") {
    @Override
    public void invoke(LyteContext context) {
      LyteList list = new LyteList(context.stack);
      context.stack.clear();
      context.stack.push(list);
    }
  };

  public static LyteNativeBlock utilCharToInt = new LyteNativeBlock("Util", "CharToInt", null) {
    @Override
    public void invoke(LyteContext context) {
      String c = context.apply().toString();
      if (c.length() != 1) {
        throw new LyteError("Expected String to be one character long, instead found \"" + c + "\"");
      } else {
        context.push((int) c.charAt(0));
      }
    }
  };

  public static LyteNativeBlock utilIntToChar = new LyteNativeBlock("Util", "IntToChar", null) {
    @Override
    public void invoke(LyteContext context) {
      int c = (int) context.apply().toNumber();
      context.push(Character.toString((char) c));
    }
  };

  public static LyteNativeBlock utilEscapeString = new LyteNativeBlock("Util", "EscapeString", null) {
    @Override
    public void invoke(LyteContext context) {
      String c = context.apply().toString();
      context.push(StringEscapeUtils.escapeEcmaScript(c));
    }
  };

  public static LyteNativeBlock utilUnescapeString = new LyteNativeBlock("Util", "UnescapeString", null) {
    @Override
    public void invoke(LyteContext context) {
      String c = context.apply().toString();
      context.push(StringEscapeUtils.unescapeEcmaScript(c));
    }
  };
}
