package com.lyte.stdlib;

import com.lyte.core.LyteCompiler;
import com.lyte.core.LyteContext;
import com.lyte.core.LyteEvaluator;
import com.lyte.core.LyteInvokeStatement;
import com.lyte.objs.LyteError;
import com.lyte.objs.LyteRawBlock;

/**
 * Created by a0225785 on 7/2/2015.
 */
public class LyteReflectionFunctions {
  public static final LyteNativeBlock reflectEval = new LyteNativeBlock("Reflect", "Evaluate", "Eval") {
    @Override
    public void invoke(LyteContext context) {
      String program = context.apply().toString();
      try (LyteEvaluator evaluator = new LyteEvaluator(context, program)) {
        evaluator.run();
      } catch (Exception e) {
        throw new LyteError(e.getMessage());
      }
    }
  };

  public static final LyteNativeBlock reflectGet = new LyteNativeBlock("Reflect", "Get", null) {
    @Override
    public void invoke(LyteContext context) {
      String target = context.apply().toString();
      LyteRawBlock block = (new LyteCompiler()).compile(target);
      if (block.get().size() != 1) {
        throw new LyteError("Expected exactly one target, instead found " + block.get().size());
      } else if (!(block.getStatement(0) instanceof LyteInvokeStatement)) {
        throw new LyteError("Expected an invocation statement, instead found \"" + target + "\"");
      }
      context.push(context.resolve((LyteInvokeStatement) block.getStatement(0), true, false));
    }
  };

  public static final LyteNativeBlock reflectFinalize = new LyteNativeBlock("Reflect", "Finalize") {
    @Override
    public void invoke(LyteContext context) {
      String target = context.apply().toString();
      if (target.startsWith("@") || target.startsWith("#") || target.matches("[\\.\\(\\)\\[\\]]")) {
        throw new LyteError("Invalid target " + target);
      } else {
        context.scope.finalizeVariable(target);
      }
    }
  };
}
