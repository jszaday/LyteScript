package com.lyte.stdlib;

import com.lyte.core.LyteContext;
import com.lyte.core.LyteStack;
import com.lyte.objs.*;
import com.lyte.utils.LyteMemberBlock;
import com.lyte.utils.LyteNativeInjector;

import java.util.Set;

/**
 * Created by jszaday on 7/8/2015.
 */
public class LyteTestFunctions {
  private static final String TEST_NAMESPACE = "Test";
  private static final LyteString SUCCESS = new LyteString("Success");

  private abstract static class LyteTestBlock extends LyteNativeBlock {

    public LyteTestBlock(String fullname) {
      super(TEST_NAMESPACE, fullname, null);
    }

    protected abstract boolean testCondition(LyteContext context);

    @Override
    public void invoke(LyteContext context) {
      String message = context.apply().toString();
      if (!testCondition(context)) {
        throw new LyteError(message);
      }
    }
  }

  public static LyteNativeBlock test = new LyteNativeBlock(TEST_NAMESPACE, "Test", null) {
    @Override
    public void invoke(LyteContext context) {
      LyteValue value = context.apply();
      LyteContext testContext = new LyteContext(value, context.scope, new LyteStack());
      Set<String> properties = value.getProperties();
      LyteObject result = new LyteObject();

      if (!value.is("object")) {
        throw new LyteError("Cannot test an object of type " + value.typeOf() + "!");
      }

      for (String property : properties) {
        if (property.startsWith("__") || !value.getProperty(property).is("block")) {
          continue;
        }

        try {
          LyteBlock test = (LyteBlock) value.getProperty(property);
          test.invoke(testContext);
          result.setProperty(property, SUCCESS);
        } catch (Exception e) {
          result.setProperty(property, new LyteString(e.getMessage()));
        }
      }
      // Inject the print results function
      result.setProperty(printResults.fullname, printResults);
      context.push(result);
    }
  };

  public static LyteNativeBlock fail = new LyteTestBlock("Fail") {

    @Override
    protected boolean testCondition(LyteContext context) {
      return false;
    }
  };

  public static LyteNativeBlock assertEquals = new LyteTestBlock("AssertEquals") {

    @Override
    protected boolean testCondition(LyteContext context) {
      return context.apply().equals(context.apply());
    }
  };

  public static LyteNativeBlock assertNotEquals = new LyteTestBlock("AssertNotEquals") {

    @Override
    protected boolean testCondition(LyteContext context) {
      return !context.apply().equals(context.apply());
    }
  };

  public static LyteNativeBlock assertEqualsStrict = new LyteTestBlock("AssertEqualsStrict") {

    @Override
    protected boolean testCondition(LyteContext context) {
      return context.apply().equalsStrict(context.apply());
    }
  };

  public static LyteNativeBlock assertNotEqualsStrict = new LyteTestBlock("AssertNotEqualsStrict") {

    @Override
    protected boolean testCondition(LyteContext context) {
      return !context.apply().equalsStrict(context.apply());
    }
  };

  public static LyteNativeBlock assertSame = new LyteTestBlock("AssertSame") {
    @Override
    protected boolean testCondition(LyteContext context) {
      return context.apply() == context.apply();
    }
  };

  public static LyteNativeBlock assertDifferent = new LyteTestBlock("AssertDifferent") {

    @Override
    protected boolean testCondition(LyteContext context) {
      return context.apply() != context.apply();
    }
  };

  public static LyteNativeBlock assertTrue = new LyteTestBlock("AssertTrue") {
    @Override
    protected boolean testCondition(LyteContext context) {
      return context.apply().toBoolean();
    }
  };

  public static LyteNativeBlock assertFalse = new LyteTestBlock("AssertFalse") {

    @Override
    protected boolean testCondition(LyteContext context) {
      return !context.apply().toBoolean();
    }
  };

  public static LyteNativeBlock assertNull = new LyteTestBlock("AssertNull") {
    @Override
    protected boolean testCondition(LyteContext context) {
      return context.apply() == LyteUndefined.NULL;
    }
  };

  public static LyteNativeBlock assertNotNull = new LyteTestBlock("AssertNotNull") {

    @Override
    protected boolean testCondition(LyteContext context) {
      return context.apply() != LyteUndefined.NULL;
    }
  };

  public static LyteNativeBlock assertUndefined = new LyteTestBlock("AssertUndefined") {
    @Override
    protected boolean testCondition(LyteContext context) {
      return context.apply() == LyteUndefined.UNDEFINED;
    }
  };

  public static LyteNativeBlock assertDefined = new LyteTestBlock("AssertDefined") {

    @Override
    protected boolean testCondition(LyteContext context) {
      return context.apply() != LyteUndefined.UNDEFINED;
    }
  };

  public static LyteNativeBlock assertRaises = new LyteTestBlock("AssertRaises") {

    @Override
    protected boolean testCondition(LyteContext context) {
      String expectedError = context.apply().toString();
      try {
        context.apply();
        return false;
      } catch (LyteError e) {
        return expectedError.equals(e.getMessage());
      }
    }
  };

  private static LyteNativeBlock printResults = new LyteMemberBlock<LyteObject>("printResults") {

    @Override
    public void invoke(LyteObject self, LyteContext context) {
      LyteValue result;
      for (String property : self.getProperties()) {
        if (property.equals(this.fullname)) {
          continue;
        } else if ((result = self.getProperty(property)).equals(SUCCESS)) {
          System.out.println("Test \"" + property + "\" Passed!");
        } else {
          System.out.println("Test \"" + property + "\" Failed Because \"" + result + "\"");
        }
      }
    }
  };
}
