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
    mContext = new LyteContext(null, LyteScope.newGlobal(), new LyteStack());
    mCompiler = new LyteCompiler();
    // And inject the native functions
    LyteNativeInjector.injectNatives(LyteStandardFunctions.class, mContext, LyteStandardFunctions.TOP_LEVEL_NAMESPACE);
    LyteNativeInjector.injectNatives(LyteReflectionFunctions.class, mContext, LyteStandardFunctions.TOP_LEVEL_NAMESPACE);
    LyteNativeInjector.injectNatives(LyteMathFunctions.class, mContext, LyteStandardFunctions.TOP_LEVEL_NAMESPACE);
    LyteNativeInjector.injectNatives(LyteTestFunctions.class, mContext, LyteStandardFunctions.TOP_LEVEL_NAMESPACE);
    LyteNativeInjector.injectNatives(LyteThreadFunctions.class, mContext, LyteStandardFunctions.TOP_LEVEL_NAMESPACE);
    mContext.set("Math", mContext.get(LyteStandardFunctions.TOP_LEVEL_NAMESPACE).getProperty("Math"), true);
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
