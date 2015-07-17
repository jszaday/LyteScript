package com.lyte.core;

import com.lyte.objs.*;
import com.lyte.stdlib.*;
import com.lyte.utils.LyteNativeInjector;
import org.antlr.v4.runtime.*;

import java.io.*;
import java.util.Scanner;

/**
 * Created by jszaday on 6/22/15.
 */
public class LyteEvaluator implements AutoCloseable {

  // Scoping Stuff
  private boolean mReplMode = true;
  private Reader mReader;
  private LyteContext mContext;
  private LyteCompiler mCompiler;

  private static LyteScope globalSingleton;

  static {
    // Initialize the singleton
    globalSingleton = LyteScope.newGlobal();
    // and inject the native functions
    LyteNativeInjector.injectNatives(LyteStandardFunctions.class, globalSingleton, LyteStandardFunctions.TOP_LEVEL_NAMESPACE);
    LyteNativeInjector.injectNatives(LyteIOFunctions.class, globalSingleton, LyteStandardFunctions.TOP_LEVEL_NAMESPACE);
    LyteNativeInjector.injectNatives(LyteReflectionFunctions.class, globalSingleton, LyteStandardFunctions.TOP_LEVEL_NAMESPACE);
    LyteNativeInjector.injectNatives(LyteMathFunctions.class, globalSingleton, LyteStandardFunctions.TOP_LEVEL_NAMESPACE);
    LyteNativeInjector.injectNatives(LyteTestFunctions.class, globalSingleton, LyteStandardFunctions.TOP_LEVEL_NAMESPACE);
    LyteNativeInjector.injectNatives(LyteThreadFunctions.class, globalSingleton, LyteStandardFunctions.TOP_LEVEL_NAMESPACE);
    globalSingleton.putVariable("Math", globalSingleton.getVariable(LyteStandardFunctions.TOP_LEVEL_NAMESPACE).getProperty("Math"), true);
  }

  public LyteEvaluator(String fileName) throws IOException {
    this(new FileInputStream(fileName), false);
  }

  public LyteEvaluator(File file) throws IOException {
    this(new FileInputStream(file), false);
  }

  public LyteEvaluator(InputStream inputStream, boolean replMode) {
    // Set the parameters
    mReplMode = replMode;
    mReader = new InputStreamReader(inputStream);
    // Initialize our other fields
    mContext = new LyteContext(null, globalSingleton.enter(), new LyteStack());
    mCompiler = new LyteCompiler();
  }

  public LyteValue get(String name) {
    return mContext.get(name);
  }

  public LyteEvaluator(LyteContext context, String program) {
    // We're not in REPL Mode
    mReplMode = false;
    // Instead, we should use the string as our data source
    mReader = new StringReader(program);
    // And just grab the context
    mContext = context;
    // Initializing the compiler as needed
    mCompiler = new LyteCompiler();
  }

  private void runOnce(CharStream charStream, LyteValue... args) {
    // Parse the input stream
    LyteRawBlock globalBlock = mCompiler.compile(charStream);
    // Then run the main block
    LyteBlock main = (LyteBlock) globalBlock.clone(mContext, false);
    for (int i = (args.length - 1); i >= 0; i--) {
      mContext.push(args[i]);
    }
    main.invoke(mContext);
  }

  public void run(LyteValue... args) throws IOException {
    if (!mReplMode) {
      runOnce(new ANTLRInputStream(mReader), args);
    } else {
      Scanner scanner = new Scanner(mReader);
      String data;
      do {
        try {
          System.out.print(">> ");
          data = scanner.nextLine();
          runOnce(new ANTLRInputStream(new StringReader(data)));
          if (!mContext.isEmpty()) {
            mContext.set("%ans", mContext.peek(), false);
          } else {
            mContext.set("%ans", LyteUndefined.UNDEFINED, false);
          }
          System.out.println(mContext.stack);
        } catch (LyteError e) {
          System.err.println("ERROR: " + e.getMessage());
        }
      } while (mReplMode);
    }
  }

  @Override
  public void close() throws Exception {
    mReader.close();
  }
}
