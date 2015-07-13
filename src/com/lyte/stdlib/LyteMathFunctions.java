package com.lyte.stdlib;

import com.lyte.core.LyteContext;
import com.lyte.objs.*;
import com.lyte.utils.LyteRangeMaker;

/**
 * Created by a0225785 on 7/1/2015.
 */
public class LyteMathFunctions {

  public static LyteNativeBlock mathNaN = new LyteNativeBlock("Math", "NaN") {
    @Override
    public void invoke(LyteContext context) {
      context.push(Double.NaN);
    }
  };

  public static LyteNativeBlock mathPi = new LyteNativeBlock("Math", "Pi", null) {
    @Override
    public void invoke(LyteContext context) {
      context.push(Math.PI);
    }
  };

  public static LyteNativeBlock mathE = new LyteNativeBlock("Math", "E", null) {
    @Override
    public void invoke(LyteContext context) {
      context.push(Math.E);
    }
  };

  public static LyteNativeBlock mathPositiveInf = new LyteNativeBlock("Math", "PositiveInfinity", "Inf") {
    @Override
    public void invoke(LyteContext context) {
      context.push(Double.POSITIVE_INFINITY);
    }
  };

  public static LyteNativeBlock mathNegativeInf = new LyteNativeBlock("Math", "NegativeInfinity", "-Inf") {
    @Override
    public void invoke(LyteContext context) {
      context.push(Double.NEGATIVE_INFINITY);
    }
  };

  public static LyteNativeBlock mathIsNaN = new LyteNativeBlock("Math", "IsNaN", "NaN?") {
    @Override
    public void invoke(LyteContext context) {
      Double val1 = context.apply().toNumber();
      context.push(val1.isNaN());
    }
  };

  public static LyteNativeBlock mathIsInfinite = new LyteNativeBlock("Math", "IsInfinite", "Inf?") {
    @Override
    public void invoke(LyteContext context) {
      Double val1 = context.apply().toNumber();
      context.push(val1.isInfinite());
    }
  };

  public static LyteNativeBlock mathAbs = new LyteNativeBlock("Math", "Abs", null) {
    @Override
    public void invoke(LyteContext context) {
      context.push(Math.abs(context.apply().toNumber()));
    }
  };

  public static LyteNativeBlock mathLess = new LyteMathBlock("Less", "<") {

    @Override
    public LyteValue compute(double val1, double val2) {
      return LyteBoolean.valueOf(val2 < val1);
    }
  };

  public static LyteNativeBlock mathGreater = new LyteMathBlock("Greater", ">") {

    @Override
    public LyteValue compute(double val1, double val2) {
      return LyteBoolean.valueOf(val2 > val1);
    }
  };

  public static LyteNativeBlock mathLessEquals = new LyteMathBlock("LessEquals", "<=") {

    @Override
    public LyteValue compute(double val1, double val2) {
      return LyteBoolean.valueOf(val2 <= val1);
    }
  };

  public static LyteNativeBlock mathGreaterEquals = new LyteMathBlock("GreaterEquals", ">=") {

    @Override
    public LyteValue compute(double val1, double val2) {
      return LyteBoolean.valueOf(val2 >= val1);
    }
  };

  public static LyteNativeBlock mathAdd = new LyteMathBlock("Add", "+") {

    @Override
    public LyteValue compute(double val1, double val2) {
      return LyteNumber.valueOf(val1 + val2);
    }
  };

  public static LyteNativeBlock mathSubtract = new LyteMathBlock("Subtract", "-") {

    @Override
    public LyteValue compute(double val1, double val2) {
      return LyteNumber.valueOf(val2 - val1);
    }
  };

  public static LyteNativeBlock mathMultiply = new LyteMathBlock("Multiply", "*") {
    @Override
    public LyteValue compute(double val1, double val2) {
      return LyteNumber.valueOf(val1 * val2);
    }
  };

  public static LyteNativeBlock mathDivide = new LyteMathBlock("Divide", "/") {

    @Override
    public LyteValue compute(double val1, double val2) {
      return LyteNumber.valueOf(val2 / val1);
    }
  };

  public static LyteNativeBlock mathIDivide = new LyteMathBlock("IDivide", "IDiv") {

    @Override
    public LyteValue compute(double val1, double val2) {
      return LyteNumber.valueOf((long) val2 / (long) val1);
    }
  };

  public static LyteNativeBlock mathModulus = new LyteMathBlock("Modulus", "Mod") {

    @Override
    public LyteValue compute(double val1, double val2) {
      return LyteNumber.valueOf((long) val2 % (long) val1);
    }
  };

  public static LyteNativeBlock mathPow = new LyteMathBlock("Pow", "^") {

    @Override
    public LyteValue compute(double val1, double val2) {
      return LyteNumber.valueOf(Math.pow(val2, val1));
    }
  };

  public static LyteNativeBlock mathCbrt = new LyteNativeBlock("Math", "Cbrt", null) {

    @Override
    public void invoke(LyteContext context) {
      context.push(Math.cbrt(context.apply().toNumber()));
    }
  };

  public static LyteNativeBlock mathSqrt = new LyteNativeBlock("Math", "Sqrt", null) {

    @Override
    public void invoke(LyteContext context) {
      context.push(Math.sqrt(context.apply().toNumber()));
    }
  };

  public static LyteNativeBlock mathMax = new LyteMathBlock("Max") {

    @Override
    public LyteValue compute(double val1, double val2) {
      return LyteNumber.valueOf(Math.max(val2, val1));
    }
  };

  public static LyteNativeBlock mathMin = new LyteMathBlock("Min") {

    @Override
    public LyteValue compute(double val1, double val2) {
      return LyteNumber.valueOf(Math.min(val2, val1));
    }
  };

  public static LyteNativeBlock mathSin = new LyteNativeBlock("Math", "Sin", null) {
    @Override
    public void invoke(LyteContext context) {
      context.push(Math.sin(context.apply().toNumber()));
    }
  };

  public static LyteNativeBlock mathSinh = new LyteNativeBlock("Math", "SinH", null) {
    @Override
    public void invoke(LyteContext context) {
      context.push(Math.sinh(context.apply().toNumber()));
    }
  };

  public static LyteNativeBlock mathAsin = new LyteNativeBlock("Math", "Asin", null) {
    @Override
    public void invoke(LyteContext context) {
      context.push(Math.asin(context.apply().toNumber()));
    }
  };

  public static LyteNativeBlock mathCos = new LyteNativeBlock("Math", "Cos", null) {
    @Override
    public void invoke(LyteContext context) {
      context.push(Math.cos(context.apply().toNumber()));
    }
  };

  public static LyteNativeBlock mathCosh = new LyteNativeBlock("Math", "CosH", null) {
    @Override
    public void invoke(LyteContext context) {
      context.push(Math.cosh(context.apply().toNumber()));
    }
  };

  public static LyteNativeBlock mathAcos = new LyteNativeBlock("Math", "Acos", null) {
    @Override
    public void invoke(LyteContext context) {
      context.push(Math.acos(context.apply().toNumber()));
    }
  };

  public static LyteNativeBlock mathTan = new LyteNativeBlock("Math", "Tan", null) {
    @Override
    public void invoke(LyteContext context) {
      context.push(Math.tan(context.apply().toNumber()));
    }
  };

  public static LyteNativeBlock mathTanh = new LyteNativeBlock("Math", "TanH", null) {
    @Override
    public void invoke(LyteContext context) {
      context.push(Math.tanh(context.apply().toNumber()));
    }
  };

  public static LyteNativeBlock mathAtan = new LyteNativeBlock("Math", "Atan", null) {
    @Override
    public void invoke(LyteContext context) {
      context.push(Math.atan(context.apply().toNumber()));
    }
  };

  public static LyteNativeBlock mathAtan2 = new LyteMathBlock("Atan2") {

    @Override
    public LyteValue compute(double val1, double val2) {
      return LyteNumber.valueOf(Math.atan2(val2, val1));
    }
  };

  public static LyteNativeBlock mathExp = new LyteNativeBlock("Math", "Exp", null) {
    @Override
    public void invoke(LyteContext context) {
      context.push(Math.exp(context.apply().toNumber()));
    }
  };

  public static LyteNativeBlock mathExpm1 = new LyteNativeBlock("Math", "Expm1", null) {
    @Override
    public void invoke(LyteContext context) {
      context.push(Math.expm1(context.apply().toNumber()));
    }
  };

  public static LyteNativeBlock mathLog = new LyteNativeBlock("Math", "Log", null) {
    @Override
    public void invoke(LyteContext context) {
      context.push(Math.log(context.apply().toNumber()));
    }
  };

  public static LyteNativeBlock mathLog10 = new LyteNativeBlock("Math", "Log10", null) {
    @Override
    public void invoke(LyteContext context) {
      context.push(Math.log10(context.apply().toNumber()));
    }
  };

  public static LyteNativeBlock mathLog1p = new LyteNativeBlock("Math", "Log1p", null) {
    @Override
    public void invoke(LyteContext context) {
      context.push(Math.log1p(context.apply().toNumber()));
    }
  };

  public static LyteNativeBlock mathCeil = new LyteNativeBlock("Math", "Ceil", null) {
    @Override
    public void invoke(LyteContext context) {
      context.push(Math.ceil(context.apply().toNumber()));
    }
  };

  public static LyteNativeBlock mathFloor = new LyteNativeBlock("Math", "Floor", null) {
    @Override
    public void invoke(LyteContext context) {
      context.push(Math.floor(context.apply().toNumber()));
    }
  };

  public static LyteNativeBlock mathRound = new LyteNativeBlock("Math", "Round", null) {
    @Override
    public void invoke(LyteContext context) {
      context.push(Math.round(context.apply().toNumber()));
    }
  };

  public static LyteNativeBlock mathNextAfter = new LyteMathBlock("NextAfter") {
    @Override
    public LyteValue compute(double val1, double val2) {
      return LyteNumber.valueOf(Math.nextAfter(val1, val2));
    }
  };

  public static LyteNativeBlock mathNextUp = new LyteNativeBlock("Math", "NextUp", null) {
    @Override
    public void invoke(LyteContext context) {
      context.push(Math.nextUp(context.apply().toNumber()));
    }
  };

  public static LyteNativeBlock mathNextDown = new LyteNativeBlock("Math", "NextDown", null) {
    @Override
    public void invoke(LyteContext context) {
      context.push(Math.nextAfter(context.apply().toNumber(), Double.NEGATIVE_INFINITY));
    }
  };

  public static LyteNativeBlock mathIEEERemainder = new LyteMathBlock("IEEERemainder") {

    @Override
    public LyteValue compute(double val1, double val2) {
      return LyteNumber.valueOf(Math.IEEEremainder(val1, val2));
    }
  };

  public static LyteNativeBlock mathUlp = new LyteNativeBlock("Math", "Ulp", null) {
    @Override
    public void invoke(LyteContext context) {
      context.push(Math.ulp(context.apply().toNumber()));
    }
  };

  public static LyteNativeBlock mathCopySign = new LyteMathBlock("CopySign") {

    @Override
    public LyteValue compute(double val1, double val2) {
      return LyteNumber.valueOf(Math.copySign(val1, val2));
    }
  };

  public static LyteNativeBlock mathSignum = new LyteNativeBlock("Math", "Signum", null) {
    @Override
    public void invoke(LyteContext context) {
      context.push(Math.signum(context.apply().toNumber()));
    }
  };

  public static LyteNativeBlock mathGetExponent = new LyteNativeBlock("Math", "GetExponent", null) {
    @Override
    public void invoke(LyteContext context) {
      context.push(Math.getExponent(context.apply().toNumber()));
    }
  };

  public static LyteNativeBlock mathHypotenuse = new LyteMathBlock("Hypotenuse") {
    @Override
    public LyteValue compute(double val1, double val2) {
      return LyteNumber.valueOf(Math.hypot(val1, val2));
    }
  };

  public static LyteNativeBlock mathScaleB = new LyteMathBlock("Scalb") {
    @Override
    public LyteValue compute(double val1, double val2) {
      return LyteNumber.valueOf(Math.scalb(val1, (int) val2));
    }
  };

  public static LyteNativeBlock mathToDegrees = new LyteNativeBlock("Math", "ToDegrees", null) {
    @Override
    public void invoke(LyteContext context) {
      context.push(Math.toDegrees(context.apply().toNumber()));
    }
  };

  public static LyteNativeBlock mathToRadians = new LyteNativeBlock("Math", "ToRadians", null) {
    @Override
    public void invoke(LyteContext context) {
      context.push(Math.toRadians(context.apply().toNumber()));
    }
  };

  public static LyteNativeBlock mathRandom = new LyteNativeBlock("Math", "Random", null) {
    @Override
    public void invoke(LyteContext context) {
      context.push(Math.random());
    }
  };

  public static LyteNativeBlock mathRange2 = new LyteMathBlock("Range2", true) {

    @Override
    public LyteValue compute(double val1, double val2) {
      return LyteRangeMaker.range(val1, val2);
    }
  };

  public static LyteNativeBlock mathRange3 = new LyteNativeBlock("Math", "Range3") {
    @Override
    public void invoke(LyteContext context) {
      double val1 = context.apply().toNumber();
      double val3 = context.apply().toNumber();
      double val2 = context.apply().toNumber();
      context.push(LyteRangeMaker.range(val1, val3, val2));
    }
  };

  private static abstract class LyteMathBlock extends LyteNativeBlock {

    public LyteMathBlock(String fullname, boolean hasAlias) {
      this(fullname, hasAlias ? fullname : null);
    }

    public LyteMathBlock(String alias) {
      this(alias, null);
    }

    public LyteMathBlock(String fullname, String alias) {
      super("Math", fullname, alias);
    }

    public abstract LyteValue compute(double val1, double val2);

    @Override
    public void invoke(LyteContext context) {
      context.push(compute(context.apply().toNumber(), context.apply().toNumber()));
    }
  }
}
