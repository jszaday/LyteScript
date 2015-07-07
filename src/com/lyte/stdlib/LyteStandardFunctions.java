package com.lyte.stdlib;

import com.lyte.core.*;
import com.lyte.objs.*;
import com.lyte.utils.LyteBeeper;
import org.apache.commons.lang3.StringEscapeUtils;
import org.omg.SendingContext.RunTime;

import javax.sound.sampled.LineUnavailableException;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Collections;
import java.util.List;

/**
 * Created by jszaday on 6/22/15.
 */
public class LyteStandardFunctions {

  public static String TOP_LEVEL_NAMESPACE = "Lyte";
  public static String VERSION = "0.0.1";

  private static final LyteStream stdIn = new LyteStream(System.in);
  private static final LyteStream stdOut = new LyteStream(System.out);

  public static LyteNativeBlock systemExit = new LyteNativeBlock("System", "Exit") {
    @Override
    public void invoke(LyteContext context) {
      if (context.isEmpty()) {
        System.exit(-1);
      } else {
        try {
          System.exit((int) context.apply().toNumber());
        } catch (Exception e) {
          System.exit(-1);
        }
      }
    }
  };

  public static LyteNativeBlock coreTrue = new LyteNativeBlock("Core", "True") {
    @Override
    public void invoke(LyteContext context) {
      context.push(true);
    }
  };

  public static LyteNativeBlock coreFalse = new LyteNativeBlock("Core", "False") {
    @Override
    public void invoke(LyteContext context) {
      context.push(false);

    }
  };

  public static LyteNativeBlock coreUndefined = new LyteNativeBlock("Core", "Undefined") {
    @Override
    public void invoke(LyteContext context) {
      context.push(LyteUndefined.UNDEFINED);
    }
  };

  public static LyteNativeBlock coreNull= new LyteNativeBlock("Core", "Null") {
    @Override
    public void invoke(LyteContext context) {
      context.push(LyteUndefined.NULL);
    }
  };

  public static LyteNativeBlock coreIsUndefined = new LyteNativeBlock("Core", "IsUndefined", "Undefined?") {
    @Override
    public void invoke(LyteContext context) {
      context.push(context.pop() == LyteUndefined.UNDEFINED);
    }
  };

  public static LyteNativeBlock coreIsNull = new LyteNativeBlock("Core", "IsNull", "Null?") {
    @Override
    public void invoke(LyteContext context) {
      context.push(context.pop() == LyteUndefined.NULL);
    }
  };

  public static LyteNativeBlock coreApply = new LyteNativeBlock("Core", "Apply") {
    @Override
    public void invoke(LyteContext context) {
      LyteValue value = context.pop();

      if (value.typeOf().equals("block")) {
        ((LyteBlock) value).invoke(context);
      } else {
        context.push(value);
      }
    }
  };

  public static LyteNativeBlock coreConcatenate = new LyteNativeBlock("Core", "Concatenate", "++") {
    @Override
    public void invoke(LyteContext context) {
      LyteValue value2 = context.apply();
      LyteValue value1 = context.apply();

      if (value1.typeOf().equals("string") || value2.typeOf().equals("string")) {
        context.push(value1.toString() + value2.toString());
      } else if (value1.typeOf().equals("list") && value2.typeOf().equals("list")) {
        context.push(new LyteList((LyteList) value1, (LyteList) value2));
      } else {
        throw new LyteError("Cannot concatenate a(n) " + value1.typeOf() + " and a(n) " + value2.typeOf());
      }
    }
  };

  public static LyteNativeBlock ioStdIn = new LyteNativeBlock("IO", "StdIn") {
    @Override
    public void invoke(LyteContext context) {
      context.push(stdIn);
    }
  };

  public static LyteNativeBlock ioStdOut = new LyteNativeBlock("IO", "StdOut") {
    @Override
    public void invoke(LyteContext context) {
      context.push(stdOut);
    }
  };

  public static LyteNativeBlock ioEchoLn = new LyteNativeBlock("IO", "EchoLn") {
    @Override
    public void invoke(LyteContext context) {
      if (!context.isEmpty()) {
        LyteValue value1 = context.apply();
        if (value1.typeOf().equals("stream")) {
          LyteStream stream = (LyteStream) value1;
          if (context.isEmpty()) {
            stream.writeLine("");
          } else {
            stream.writeLine(context.apply().toString());
          }
          stream.flush();
        } else {
          System.out.println(value1);
        }
      } else {
        System.out.println();
      }
    }
  };

  public static LyteNativeBlock ioEcho = new LyteNativeBlock("IO", "Echo") {
    @Override
    public void invoke(LyteContext context) {
      LyteValue value1 = context.apply();
      if (value1.typeOf().equals("stream")) {
        ((LyteStream) value1).write(context.apply().toString());
        ((LyteStream) value1).flush();
      } else {
        System.out.print(value1);
      }
    }
  };

  public static LyteNativeBlock ioReadLn = new LyteNativeBlock("IO", "ReadLn") {
    @Override
    public void invoke(LyteContext context) {
      context.push(stdIn.readLine());
    }
  };

  public static LyteNativeBlock ioRead = new LyteNativeBlock("IO", "Read") {
    @Override
    public void invoke(LyteContext context) {
      context.push(stdIn.read());
    }
  };

  public static LyteNativeBlock coreIf = new LyteNativeBlock("Core", "If") {

    @Override
    public void invoke(LyteContext context) {
      LyteValue condition = context.pop();
      LyteValue trueValue = context.pop();
      LyteValue falseValue = context.pop();
      LyteValue selectedValue = condition.apply(context).toBoolean() ? trueValue : falseValue;

      if (selectedValue.typeOf().equals("block")) {
        ((LyteBlock) selectedValue).invoke(context);
      } else {
        context.push(selectedValue.apply(context));
      }
    }
  };

  public static LyteNativeBlock utilInstantiate = new LyteNativeBlock("Util", "Instantiate") {
    @Override
    public void invoke(LyteContext context) {
      // TODO this assumes the two things are on the same context.stack, we have to move scoping out of blocks!!!
      LyteValue value = context.apply();
      if (!value.typeOf().equals("object")) {
        throw new LyteError("Cannot Instantiate a(n) " + value.typeOf() + ".");
      }
      if (!value.hasProperty("__constructor")) {
        throw new LyteError("Error, object has no constructor!");
      }
      LyteObject obj = (LyteObject) value.clone(new LyteContext(null, null, context.stack));
      try {
        ((LyteBlock) obj.getProperty("__constructor")).invoke(new LyteContext(obj, null, context.stack));
      } catch (ClassCastException e) {
        throw new LyteError("Expected __constructor to be a block for object " + obj);
      }
      obj.unsetProperty("__constructor");
      context.push(obj);
    }
  };

  public static LyteNativeBlock coreMixWith = new LyteNativeBlock("Core", "MixWith") {
    @Override
    public void invoke(LyteContext context) {
      LyteValue value2 = context.pop();
      LyteValue value1 = context.pop();

      if (!(value1.typeOf().equals("object") && value2.typeOf().equals("object"))) {
        throw new LyteError("Cannot mix " + value1 + " with " + value2);
      }

      ((LyteObject) value1).mixWith((LyteObject) value2);
    }
  };

  public static LyteNativeBlock errorTry = new LyteNativeBlock("Error", "Try") {
    @Override
    public void invoke(LyteContext context) {
      LyteValue value1 = context.pop();
      LyteValue value2 = context.pop();

      if (!value1.typeOf().equals("block") || !value2.typeOf().equals("block")) {
        throw new LyteError("Try requires both parameters to be blocks!");
      }

      try {
        ((LyteBlock) value1).invoke(context);
      } catch (LyteError e) {
        context.push(e);
        ((LyteBlock) value2).invoke(context);
      }
    }
  };

  public static LyteNativeBlock errorRaise = new LyteNativeBlock("Error", "Raise") {
    @Override
    public void invoke(LyteContext context) {
      // Throw the result to the wolves
      throw new LyteError(context.apply());
    }
  };

  public static LyteNativeBlock coreSwap = new LyteNativeBlock("Core", "Swap") {
    @Override
    public void invoke(LyteContext context) {
      LyteValue value1 = context.pop();
      LyteValue value2 = context.pop();
      context.push(value1);
      context.push(value2);
    }
  };

  public static LyteNativeBlock corePop = new LyteNativeBlock("Core", "Pop") {
    @Override
    public void invoke(LyteContext context) {
      context.pop();
    }
  };

  public static LyteNativeBlock coreDup = new LyteNativeBlock("Core", "Duplicate", "Dup") {
    @Override
    public void invoke(LyteContext context) {
      context.push(context.peek());
    }
  };

  public static LyteNativeBlock coreFor = new LyteNativeBlock("Core", "For") {
    @Override
    public void invoke(LyteContext context) {
      LyteValue value1 = context.apply();
      LyteValue value2;

      if (value1.typeOf().equals("list")) {
        value2 = context.pop();

        if (!value2.typeOf().equals("block")) {
          throw new LyteError("For expected a block, not a(n) " + value2.typeOf());
        }

        for (LyteValue value : ((List<LyteValue>) value1.get())) {
          // Push the value onto the context.stack
          context.push(value);
          // Then invoke the function
          ((LyteBlock) value2).invoke(context);
        }
      } else {
        int number1 = (int) value1.toNumber();
        int number2 = (int) context.apply().toNumber();
        value2 = context.pop();
        if (!value2.typeOf().equals("block")) {
          throw new LyteError("For expected a block, not a(n) " + value2.typeOf());
        }
        if (number1 < number2) {
          for (int i = number1; i < number2; i++) {
            // Push the number onto the context.stack
            context.push(i);
            // Then invoke the function
            ((LyteBlock) value2).invoke(context);
          }
        } else {
          for (int i = (number1 - 1); i >= number2; i--) {
            // Push the number onto the context.stack
            context.push(i);
            // Then invoke the function
            ((LyteBlock) value2).invoke(context);
          }
        }
      }
    }
  };

  public static LyteNativeBlock coreWhile = new LyteNativeBlock("Core", "While") {
    @Override
    public void invoke(LyteContext context) {
      LyteValue value1 = context.pop();
      LyteValue value2 = context.pop();
      if (!value2.typeOf().equals("block")) {
        throw new LyteError("While expected a block, not a(n) " + value2.typeOf());
      }
      while (value1.apply(context).toBoolean()) {
        ((LyteBlock) value2).invoke(context);
      }
    }
  };

  public static LyteNativeBlock coreUntil = new LyteNativeBlock("Core", "Until") {
    @Override
    public void invoke(LyteContext context) {
      LyteValue value1 = context.pop();
      LyteValue value2 = context.pop();
      if (!value2.typeOf().equals("block")) {
        throw new LyteError("Until expected a block, not a(n) " + value2.typeOf());
      }
      while (!value1.apply(context).toBoolean()) {
        ((LyteBlock) value2).invoke(context);
      }
    }
  };

  public static LyteNativeBlock coreIsStackEmpty = new LyteNativeBlock("Core", "IsStackEmpty", "StackEmpty?") {
    @Override
    public void invoke(LyteContext context) {
      context.push(context.isEmpty());
    }
  };

  public static LyteNativeBlock coreThis = new LyteNativeBlock("Core", "This") {
    @Override
    public void invoke(LyteContext context) {
      if (context.self != null) {
        context.push(context.self);
      } else {
        context.push(LyteUndefined.NULL);
      }
    }
  };

  public static LyteNativeBlock coreEquals = new LyteNativeBlock("Core", "Equals", "==") {
    @Override
    public void invoke(LyteContext context) {
      LyteValue value1 = context.apply();
      LyteValue value2 = context.apply();
      context.push(value1.equals(value2));
    }
  };

  public static LyteNativeBlock coreEqualsStrict = new LyteNativeBlock("Core", "EqualsStrict", "===") {
    @Override
    public void invoke(LyteContext context) {
      LyteValue value1 = context.apply();
      LyteValue value2 = context.apply();
      context.push(value1.equalsStrict(value2));
    }
  };

  public static LyteNativeBlock coreNotEquals = new LyteNativeBlock("Core", "NotEquals", "!=") {
    @Override
    public void invoke(LyteContext context) {
      LyteValue value1 = context.apply();
      LyteValue value2 = context.apply();
      context.push(!value1.equals(value2));
    }
  };

  public static LyteNativeBlock coreNotEqualsStrict = new LyteNativeBlock("Core", "NotEqualsStrict", "!==") {
    @Override
    public void invoke(LyteContext context) {
      LyteValue value1 = context.apply();
      LyteValue value2 = context.apply();
      context.push(!value1.equalsStrict(value2));
    }
  };

  public static LyteNativeBlock ioOpenFile = new LyteNativeBlock("IO", "OpenFile") {
    @Override
    public void invoke(LyteContext context) {
      String filename = context.apply().toString();
      String mode = context.apply().toString();

      try {
        switch (mode) {
          case "r":
            context.push(new LyteStream(new FileReader(filename)));
            break;
          case "w":
            context.push(new LyteStream(new FileWriter(filename)));
            break;
          case "a":
            context.push(new LyteStream(new FileWriter(filename, true)));
            break;
          default:
            throw new LyteError("Cannot open file in mode " + mode);
        }
      } catch (IOException e) {
        throw new LyteError(e.getMessage());
      }
    }
  };

  public static LyteNativeBlock coreTypeOf = new LyteNativeBlock("Core", "TypeOf", "Type?") {
    @Override
    public void invoke(LyteContext context) {
      context.push(context.pop().typeOf());
    }
  };

  public static LyteNativeBlock coreImport = new LyteNativeBlock("Core", "Import") {
    @Override
    public void invoke(LyteContext context) {
      try {
        (new LyteClassLoader()).load(context, context.apply().toString());
      } catch (IOException e) {
        throw new LyteError(e.getMessage());
      }
    }
  };

  public static LyteNativeBlock coreAnd = new LyteNativeBlock("Core", "And") {
    @Override
    public void invoke(LyteContext context) {
      LyteValue value2 = context.pop();
      LyteValue value1 = context.apply();

      if (!value1.toBoolean()) {
        context.push(false);
      } else {
        context.push(value2.apply(context).toBoolean());
      }
    }
  };

  public static LyteNativeBlock coreOr = new LyteNativeBlock("Core", "Or") {
    @Override
    public void invoke(LyteContext context) {
      LyteValue value2 = context.pop();
      LyteValue value1 = context.apply();

      if (value1.toBoolean()) {
        context.push(true);
      } else {
        context.push(value2.apply(context).toBoolean());
      }
    }
  };

  public static LyteNativeBlock coreNot = new LyteNativeBlock("Core", "Not") {
    @Override
    public void invoke(LyteContext context) {
      context.push(!context.apply().toBoolean());
    }
  };

  public static LyteNativeBlock coreBitwiseAnd = new LyteNativeBlock("Core", "BitwiseAnd", null) {
    @Override
    public void invoke(LyteContext context) {
      int val1 = (int) context.apply().toNumber();
      int val2 = (int) context.apply().toNumber();
      context.push(val1 & val2);
    }
  };

  public static LyteNativeBlock coreBitwiseOr = new LyteNativeBlock("Core", "BitwiseOr", null) {
    @Override
    public void invoke(LyteContext context) {
      int val1 = (int) context.apply().toNumber();
      int val2 = (int) context.apply().toNumber();
      context.push(val1 | val2);
    }
  };

  public static LyteNativeBlock coreBitwiseXor = new LyteNativeBlock("Core", "BitwiseXor", null) {
    @Override
    public void invoke(LyteContext context) {
      int val1 = (int) context.apply().toNumber();
      int val2 = (int) context.apply().toNumber();
      context.push(val1 ^ val2);
    }
  };

  public static LyteNativeBlock coreBitwiseNot = new LyteNativeBlock("Core", "BitwiseNot", null) {
    @Override
    public void invoke(LyteContext context) {
      int val1 = (int) context.apply().toNumber();
      context.push(~val1);
    }
  };

  public static LyteNativeBlock coreVersion = new LyteNativeBlock("Core", "Version", null) {
    @Override
    public void invoke(LyteContext context) {
      context.push(VERSION);
    }
  };

  public static LyteNativeBlock coreDig = new LyteNativeBlock("Core", "Dig") {
    @Override
    public void invoke(LyteContext context) {
      int depth = (int) context.apply().toNumber();
      LyteStack stack = context.stack;
      if (stack.size() < depth) {
        throw new LyteError("Cannot dig up value " + depth + ", not enough values.");
      } else if (!(depth == 0 || depth == 1)) {
        stack.push(stack.remove(depth - 1));
      }
    }
  };

  public static LyteNativeBlock coreToNumber = new LyteNativeBlock("Core", "ToNumber") {
    @Override
    public void invoke(LyteContext context) {
      context.push(context.apply().toNumber());
    }
  };

  public static LyteNativeBlock coreToString = new LyteNativeBlock("Core", "ToString") {
    @Override
    public void invoke(LyteContext context) {
      context.push(context.apply().toString());
    }
  };

  public static LyteNativeBlock coreToBoolean = new LyteNativeBlock("Core", "ToBoolean") {
    @Override
    public void invoke(LyteContext context) {
      context.push(context.apply().toBoolean());
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

  public static LyteNativeBlock systemCurrentDirectory = new LyteNativeBlock("System", "CurrentDirectory", null) {
    @Override
    public void invoke(LyteContext context) {
      context.push(System.getProperty("user.dir"));
    }
  };

  public static LyteNativeBlock systemPathSeperator = new LyteNativeBlock("System", "PathSeperator", null) {
    @Override
    public void invoke(LyteContext context) {
      context.push(File.pathSeparator);
    }
  };

  public static LyteNativeBlock systemSeperator = new LyteNativeBlock("System", "Separator", null) {
    @Override
    public void invoke(LyteContext context) {
      context.push(File.separator);
    }
  };

  public static LyteNativeBlock systemPlatform = new LyteNativeBlock("System", "Platform", null) {
    @Override
    public void invoke(LyteContext context) {
      context.push(System.getProperty("os.name"));
    }
  };

  public static LyteNativeBlock systemExecute = new LyteNativeBlock("System", "Execute", null) {
    @Override
    public void invoke(LyteContext context) {
      String command = context.apply().toString();
      try {
        Process p = Runtime.getRuntime().exec(command);
        p.waitFor();
        context.push(new LyteStream(p.getInputStream()));
      } catch (Exception e) {
        throw new LyteError(e.getMessage());
      }
    }
  };

  public static LyteNativeBlock systemBeep = new LyteNativeBlock("System", "Beep", null) {
    @Override
    public void invoke(LyteContext context) {
      final int f = (int) context.apply().toNumber();
      final int ms = (int) context.apply().toNumber();
      try {
        LyteBeeper.beep(f, ms);
      } catch (LineUnavailableException e) {
        throw new LyteError("No audio line available.");
      }
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
