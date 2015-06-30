package com.lyte.stdlib;

import com.lyte.core.LyteContext;
import com.lyte.core.LyteInvokeStatement;
import com.lyte.core.LyteScope;
import com.lyte.core.LyteStack;
import com.lyte.objs.*;

import java.lang.reflect.Field;
import java.util.List;

/**
 * Created by jszaday on 6/22/15.
 */
public class LyteStandardFunctions {

  public static String TOP_LEVEL_NAMESPACE = "Lyte";

  public static LyteNativeBlock coreTrue = new LyteNativeBlock("Core", "True") {
    @Override
    public void invoke(LyteContext context) {
      context.stack.push(true);
    }
  };

  public static LyteNativeBlock coreFalse = new LyteNativeBlock("Core", "False") {
    @Override
    public void invoke(LyteContext context) {
      context.stack.push(false);

    }
  };

  public static LyteNativeBlock coreUndefined = new LyteNativeBlock("Core", "Undefined") {
    @Override
    public void invoke(LyteContext context) {
      context.stack.push(LyteUndefined.UNDEFINED);

    }
  };

  public static LyteNativeBlock coreNot = new LyteNativeBlock("Core", "Not") {
    @Override
    public void invoke(LyteContext context) {
      context.stack.push(!context.stack.pop().apply(context).toBoolean());
    }
  };

  public static LyteNativeBlock coreIsUndefined = new LyteNativeBlock("Core", "IsUndefined", "Undefined?") {
    @Override
    public void invoke(LyteContext context) {
      context.stack.push(context.stack.pop() == LyteUndefined.UNDEFINED);
    }
  };

  public static LyteNativeBlock mathAdd = new LyteNativeBlock("Math", "Add", "+") {

    @Override
    public void invoke(LyteContext context) {
      double val1 = context.stack.pop().apply(context).toNumber();
      double val2 = context.stack.pop().apply(context).toNumber();
      context.stack.push(val1 + val2);
    }
  };

  public static LyteNativeBlock mathLessEquals = new LyteNativeBlock("Math", "LessEquals", "<=") {

    @Override
    public void invoke(LyteContext context) {
      double val1 = context.stack.pop().apply(context).toNumber();
      double val2 = context.stack.pop().apply(context).toNumber();
      context.stack.push(val2 <= val1);
    }
  };

  public static LyteNativeBlock mathSubtract = new LyteNativeBlock("Math", "Subtract", "-") {

    @Override
    public void invoke(LyteContext context) {
      double val1 = context.stack.pop().apply(context).toNumber();
      double val2 = context.stack.pop().apply(context).toNumber();
      context.stack.push(val2 - val1);
    }
  };

  public static LyteNativeBlock mathDivide = new LyteNativeBlock("Math", "Divide", "/") {

    @Override
    public void invoke(LyteContext context) {
      double val1 = context.stack.pop().apply(context).toNumber();
      double val2 = context.stack.pop().apply(context).toNumber();
      context.stack.push(val2 / val1);
    }
  };

  public static LyteNativeBlock mathIDivide = new LyteNativeBlock("Math", "IDivide", "IDiv") {

    @Override
    public void invoke(LyteContext context) {
      int val1 = (int) context.stack.pop().apply(context).toNumber();
      int val2 = (int) context.stack.pop().apply(context).toNumber();
      context.stack.push(val2 / val1);
    }
  };

  public static LyteNativeBlock mathMultiply = new LyteNativeBlock("Math", "Multiply", "*") {

    @Override
    public void invoke(LyteContext context) {
      double val1 = context.stack.pop().apply(context).toNumber();
      double val2 = context.stack.pop().apply(context).toNumber();
      context.stack.push(val1 * val2);
    }
  };

  public static LyteNativeBlock coreApply = new LyteNativeBlock("Core", "Apply") {
    @Override
    public void invoke(LyteContext context) {
      LyteValue value = context.stack.pop();

      if (value.typeOf().equals("block")) {
        ((LyteBlock) value).invoke(context);
      } else {
        context.stack.push(value);
      }
    }
  };

  public static LyteNativeBlock coreConcatenate = new LyteNativeBlock("Core", "Concatenate", "++") {
    @Override
    public void invoke(LyteContext context) {
      LyteValue value2 = context.stack.pop().apply(context);
      LyteValue value1 = context.stack.pop().apply(context);

      // TODO Implement other cases
      if (value1.typeOf().equals("string") || value2.typeOf().equals("string")) {
        context.stack.push(value1.toString() + value2.toString());
      }
    }
  };

  public static LyteNativeBlock ioEchoLn = new LyteNativeBlock("IO", "EchoLn") {
    @Override
    public void invoke(LyteContext context) {
      if (!context.stack.isEmpty()) {
        System.out.println(context.stack.pop().apply(context));
      } else {
        System.out.println();
      }
    }
  };

  public static LyteNativeBlock ioEcho = new LyteNativeBlock("IO", "Echo") {
    @Override
    public void invoke(LyteContext context) {
      System.out.print(context.stack.pop().apply(context));
    }
  };

  public static LyteNativeBlock coreIf = new LyteNativeBlock("Core", "If") {

    @Override
    public void invoke(LyteContext context) {
      LyteValue condition = context.stack.pop();
      LyteValue trueValue = context.stack.pop();
      LyteValue falseValue = context.stack.pop();
      LyteValue selectedValue = condition.apply(context).toBoolean() ? trueValue : falseValue;

      if (selectedValue.typeOf().equals("block")) {
        ((LyteBlock) selectedValue).invoke(context);
      } else {
        context.stack.push(selectedValue.apply(context));
      }
    }
  };

  public static LyteNativeBlock utilInstantiate = new LyteNativeBlock("Util", "Instantiate") {
    @Override
    public void invoke(LyteContext context) {
      // TODO this assumes the two things are on the same context.stack, we have to move scoping out of blocks!!!
      LyteValue value = context.stack.pop().apply(context);
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
      context.stack.push(obj);
    }
  };

  public static LyteNativeBlock coreMixWith = new LyteNativeBlock("Core", "MixWith") {
    @Override
    public void invoke(LyteContext context) {
      LyteValue value2 = context.stack.pop();
      LyteValue value1 = context.stack.pop();

      if (!(value1.typeOf().equals("object") && value2.typeOf().equals("object"))) {
        throw new LyteError("Cannot mix " + value1 + " with " + value2);
      }

      ((LyteObject) value1).mixWith((LyteObject) value2);
    }
  };

  public static LyteNativeBlock errorTry = new LyteNativeBlock("Error", "Try") {
    @Override
    public void invoke(LyteContext context) {
      LyteValue value1 = context.stack.pop();
      LyteValue value2 = context.stack.pop();

      if (!value1.typeOf().equals("block") || !value2.typeOf().equals("block")) {
        throw new LyteError("Try requires both parameters to be blocks!");
      }

      try {
        ((LyteBlock) value1).invoke(context);
      } catch (LyteError e) {
        context.stack.push(e);
        ((LyteBlock) value2).invoke(context);
      }
    }
  };

  public static LyteNativeBlock errorRaise = new LyteNativeBlock("Error", "Raise") {
    @Override
    public void invoke(LyteContext context) {
      // Throw the result to the wolves
      throw new LyteError(context.stack.pop().apply(context));
    }
  };

  public static LyteNativeBlock coreSwap = new LyteNativeBlock("Core", "Swap") {
    @Override
    public void invoke(LyteContext context) {
      LyteValue value1 = context.stack.pop();
      LyteValue value2 = context.stack.pop();
      context.stack.push(value1);
      context.stack.push(value2);
    }
  };

  public static LyteNativeBlock coreFor = new LyteNativeBlock("Core", "For") {
    @Override
    public void invoke(LyteContext context) {
      LyteValue value1 = context.stack.pop().apply(context);
      LyteValue value2;

      if (value1.typeOf().equals("list")) {
        value2 = context.stack.pop();

        if (!value2.typeOf().equals("block")) {
          throw new LyteError("For expected a block, not a(n) " + value2.typeOf());
        }

        for (LyteValue value : ((List<LyteValue>) value1.get())) {
          // Push the value onto the context.stack
          context.stack.push(value);
          // Then invoke the function
          ((LyteBlock) value2).invoke(context);
        }
      } else {
        int number1 = (int) value1.toNumber();
        int number2 = (int) context.stack.pop().apply(context).toNumber();
        value2 = context.stack.pop();
        if (!value2.typeOf().equals("block")) {
          throw new LyteError("For expected a block, not a(n) " + value2.typeOf());
        }
        if (number1 < number2) {
          for (int i = number1; i < number2; i++) {
            // Push the number onto the context.stack
            context.stack.push(i);
            // Then invoke the function
            ((LyteBlock) value2).invoke(context);
          }
        } else {
          for (int i = (number1 - 1); i >= number2; i--) {
            // Push the number onto the context.stack
            context.stack.push(i);
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
      LyteValue value1 = context.stack.pop();
      LyteValue value2 = context.stack.pop();
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
      LyteValue value1 = context.stack.pop();
      LyteValue value2 = context.stack.pop();
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
      context.stack.push(context.stack.isEmpty());
    }
  };

  public static LyteNativeBlock coreThis = new LyteNativeBlock("Core", "This") {
    @Override
    public void invoke(LyteContext context) {
      if (context.self != null) {
        context.stack.push(context.self);
      } else {
        context.stack.push(LyteUndefined.NULL);
      }
    }
  };

  public static LyteNativeBlock mathPow = new LyteNativeBlock("Math", "Pow", "**") {
    @Override
    public void invoke(LyteContext context) {
      double val1 = context.stack.pop().apply(context).toNumber();
      double val2 = context.stack.pop().apply(context).toNumber();
      context.stack.push(Math.pow(val2, val1));
    }
  };

  public static LyteNativeBlock mathRange2 = new LyteNativeBlock("Math", "Range2") {
    @Override
    public void invoke(LyteContext context) {
      double val1 = context.stack.pop().apply(context).toNumber();
      double val2 = context.stack.pop().apply(context).toNumber();
      LyteList range = new LyteList();

      if (val1 <= val2) {
        for (double i = val1; i <= val2; i += 1) {
          range.add(new LyteNumber(i));
        }
      } else {
        for (double i = val1; i >= val2; i -= 1) {
          range.add(new LyteNumber(i));
        }
      }

      context.stack.push(range);
    }
  };

  public static LyteNativeBlock mathRange3 = new LyteNativeBlock("Math", "Range3") {
    @Override
    public void invoke(LyteContext context) {
      double val1 = context.stack.pop().apply(context).toNumber();
      double val3 = context.stack.pop().apply(context).toNumber();
      double val2 = context.stack.pop().apply(context).toNumber();
      LyteList range = new LyteList();

      if (val1 != val2 && val3 == 0) {
        throw new LyteError("Impossible to reach " + val2 + " from " + val1 + " by incrementing by zero");
      }

      if (val1 <= val2) {
        if (val3 < 0) {
          throw new LyteError("Impossible to reach " + val2 + " from " + val1 + " by incrementing by " + val3);
        }

        for (double i = val1; i <= val2; i += val3) {
          range.add(new LyteNumber(i));
        }
      } else {
        if (val3 > 0) {
          throw new LyteError("Impossible to reach " + val2 + " from " + val1 + " by incrementing by " + val3);
        }

        for (double i = val1; i >= val2; i += val3) {
          range.add(new LyteNumber(i));
        }
      }

      context.stack.push(range);
    }
  };

  public static LyteNativeBlock coreEquals = new LyteNativeBlock("Core", "Equals", "==") {
    @Override
    public void invoke(LyteContext context) {
      LyteValue value1 = context.stack.pop().apply(context);
      LyteValue value2 = context.stack.pop().apply(context);
      context.stack.push(value1.equals(value2));
    }
  };

  public static LyteNativeBlock coreEqualsStrict = new LyteNativeBlock("Core", "EqualsStrict", "===") {
    @Override
    public void invoke(LyteContext context) {
      LyteValue value1 = context.stack.pop().apply(context);
      LyteValue value2 = context.stack.pop().apply(context);
      context.stack.push(value1.equalsStrict(value2));
    }
  };
}
