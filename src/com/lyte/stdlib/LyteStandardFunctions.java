package com.lyte.stdlib;

import com.lyte.core.*;
import com.lyte.objs.*;
import com.lyte.utils.LyteBeeper;
import com.lyte.utils.LyteJsonParser;
import com.lyte.utils.LyteYieldListener;
import org.apache.commons.lang3.StringEscapeUtils;

import javax.sound.sampled.LineUnavailableException;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

/**
 * Created by jszaday on 6/22/15.
 */
public class LyteStandardFunctions {

  public static String TOP_LEVEL_NAMESPACE = "Lyte";
  public static String VERSION = "0.0.1";

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

      if (value.is("block")) {
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

      if (value1.is("string") || value2.is("string")) {
        context.push(value1.toString() + value2.toString());
      } else if (value1.is("list") && value2.is("list")) {
        context.push(new LyteList((LyteList) value1, (LyteList) value2));
      } else {
        throw new LyteError("Cannot concatenate a(n) " + value1.typeOf() + " and a(n) " + value2.typeOf());
      }
    }
  };

  public static LyteNativeBlock coreIf = new LyteNativeBlock("Core", "If") {

    @Override
    public void invoke(LyteContext context) {
      LyteValue condition = context.pop();
      LyteValue trueValue = context.pop();
      LyteValue falseValue = context.pop();
      LyteValue selectedValue = condition.apply(context).toBoolean() ? trueValue : falseValue;

      if (selectedValue.is("block")) {
        ((LyteBlock) selectedValue).invoke(context);
      } else {
        context.push(selectedValue.apply(context));
      }
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
    public void invoke(final LyteContext context) {
      LyteValue obj = context.apply();

      if (obj.is("number")) {
        long number1 = (long) obj.toNumber();
        long number2 = (long) context.apply().toNumber();
        final LyteValue value = context.pop();
        if (!value.is("block")) {
          throw new LyteError("For expected a block, not a(n) " + value.typeOf());
        }
        if (number1 < number2) {
          for (long i = number1; i < number2; i++) {
            // Push the number onto the context.stack
            context.push(i);
            // Then invoke the function
            ((LyteBlock) value).invoke(context);
          }
        } else {
          for (long i = (number1 - 1); i >= number2; i--) {
            // Push the number onto the context.stack
            context.push(i);
            // Then invoke the function
            ((LyteBlock) value).invoke(context);
          }
        }
      } else {
        final LyteValue block = context.pop();

        if (!block.is("block")) {
          throw new LyteError("For expected a block, not a(n) " + block.typeOf());
        }

        LyteContext generatorContext = new LyteContext(obj, context);
        generatorContext.setListener(new LyteYieldListener() {
          @Override
          public void onYield(LyteValue value) {
            // Push the value onto the context.stack
            context.push(value);
            // Then invoke the function
            ((LyteBlock) block).invoke(context);
          }
        });
        obj.generator().invoke(generatorContext);
      }
    }
  };

  public static LyteNativeBlock coreWhile = new LyteNativeBlock("Core", "While") {
    @Override
    public void invoke(LyteContext context) {
      LyteValue value1 = context.pop();
      LyteValue value2 = context.pop();
      if (!value2.is("block")) {
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
      if (!value2.is("block")) {
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

  public static LyteNativeBlock coreSame = new LyteNativeBlock("Core", "Same", "Same?") {
    @Override
    public void invoke(LyteContext context) {
      context.push(context.apply() == context.apply());
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
      long val1 = (long) context.apply().toNumber();
      long val2 = (long) context.apply().toNumber();
      context.push(val1 & val2);
    }
  };

  public static LyteNativeBlock coreBitwiseOr = new LyteNativeBlock("Core", "BitwiseOr", null) {
    @Override
    public void invoke(LyteContext context) {
      long val1 = (long) context.apply().toNumber();
      long val2 = (long) context.apply().toNumber();
      context.push(val1 | val2);
    }
  };

  public static LyteNativeBlock coreBitwiseXor = new LyteNativeBlock("Core", "BitwiseXor", null) {
    @Override
    public void invoke(LyteContext context) {
      long val1 = (long) context.apply().toNumber();
      long val2 = (long) context.apply().toNumber();
      context.push(val1 ^ val2);
    }
  };

  public static LyteNativeBlock coreBitwiseNot = new LyteNativeBlock("Core", "BitwiseNot", null) {
    @Override
    public void invoke(LyteContext context) {
      long val1 = (long) context.apply().toNumber();
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

  public static LyteNativeBlock coreYield = new LyteNativeBlock("Core", "Yield") {

    @Override
    public void invoke(LyteContext context) {
      context.yield();
    }
  };

  public static LyteNativeBlock errorTry = new LyteNativeBlock("Error", "Try") {
    @Override
    public void invoke(LyteContext context) {
      LyteValue value1 = context.pop();
      LyteValue value2 = context.pop();

      if (!value1.is("block") || !value2.is("block")) {
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

  public static LyteNativeBlock systemCurrentDirectory = new LyteNativeBlock("System", "CurrentDirectory", null) {
    @Override
    public void invoke(LyteContext context) {
      context.push(System.getProperty("user.dir"));
    }
  };

  public static LyteNativeBlock systemPathSeparator = new LyteNativeBlock("System", "PathSeparator", null) {
    @Override
    public void invoke(LyteContext context) {
      context.push(File.pathSeparator);
    }
  };

  public static LyteNativeBlock systemSeparator = new LyteNativeBlock("System", "Separator", null) {
    @Override
    public void invoke(LyteContext context) {
      context.push(File.separator);
    }
  };


  public static LyteNativeBlock systemLineSeparator = new LyteNativeBlock("System", "LineSeparator", null) {
    @Override
    public void invoke(LyteContext context) {
      context.push(System.lineSeparator());
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

  public static LyteNativeBlock jsonParseJSON = new LyteNativeBlock("JSON", "ParseJSON", null) {
    @Override
    public void invoke(LyteContext context) {
      context.push(LyteJsonParser.parseJson(context.apply().toString()));
    }
  };

  public static LyteNativeBlock jsonToJSON = new LyteNativeBlock("JSON", "ToJSON", null) {
    @Override
    public void invoke(LyteContext context) {
      context.push(context.apply().toJSONString());
    }
  };
}
