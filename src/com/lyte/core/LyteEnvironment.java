package com.lyte.core;

import com.lyte.gen.LyteBaseVisitor;
import com.lyte.gen.LyteLexer;
import com.lyte.gen.LyteParser;
import com.lyte.objs.*;
import com.lyte.stdlib.LyteStandardFunctions;
import com.lyte.utils.LyteNativeInjector;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.apache.commons.lang3.StringEscapeUtils;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Created by jszaday on 6/22/15.
 */
public class LyteEnvironment extends LyteBaseVisitor<Object> {

  // Scoping Stuff
  private LyteRawBlock mGlobalBlock;
  private LyteRawBlock mCurrentBlock;
  private boolean mReplMode = true;
  private InputStream mInputStream;
  private LyteScope mGlobalScope;
  private LyteStack mStack;
  // Parsing stuff
  private CommonTokenStream mTokenStream;

  public LyteEnvironment(String fileName) throws IOException {
    this(new FileInputStream(fileName), false);
  }

  public LyteEnvironment(InputStream inputStream, boolean replMode) {
    // Set the parameters
    mReplMode = replMode;
    mInputStream = inputStream;
    // Initialize our other fields
    mGlobalScope = LyteScope.newGlobal();
    mStack = new LyteStack();
    // And inject the native functions
    LyteNativeInjector.injectNatives(LyteStandardFunctions.class, mGlobalScope, LyteStandardFunctions.TOP_LEVEL_NAMESPACE);
  }

  private void runOnce(CharStream charStream, LyteValue... args) throws IOException {
    // Reinitialize the Blocks
    mCurrentBlock = mGlobalBlock = LyteRawBlock.newGlobal();
    // Setup our parser w/ the correct input
    LyteLexer lexer = new LyteLexer(charStream);
    mTokenStream = new CommonTokenStream(lexer);
    LyteParser parser = new LyteParser(mTokenStream);
    // Then grab the parse tree and begin to visit it
    ParseTree parseTree = parser.program();
    this.visit(parseTree);
    // Then run the main block
    LyteBlock main = (LyteBlock) mGlobalBlock.clone(mGlobalScope, false);
    main.invoke(null, mStack, args);
  }

  public void run(LyteValue... args) throws IOException {
    if (!mReplMode) {
      runOnce(new ANTLRInputStream(mInputStream), args);
    } else {
      Scanner scanner = new Scanner(mInputStream);
      String data;
      do {
        System.out.print(">> ");
        data = scanner.nextLine();
        runOnce(new ANTLRInputStream(new StringReader(data)));
        System.out.println(mStack);
      } while (mReplMode);
    }
  }

  public void printIntermediateResults() {
    for (LyteStatement statement : mGlobalBlock.getStatements()) {
      System.out.println(statement);
    }
  }

  @Override
  public Object visitStatement(LyteParser.StatementContext ctx) {
    LyteStatement statement;
    // If it is a pushable
    if (ctx.pushable() != null) {
      // use the pushable statement visitor to handle it accordingly
      statement = (LyteStatement) visitPushableStatement(ctx.pushable());
    } else {
      // otherwise, simply get the statement as a result from visiting the kids
      statement = (LyteStatement) visitChildren(ctx);
    }
    // Add the newly generated statement to the current block
    mCurrentBlock.addStatement(statement);
    // And finally, return it too
    return statement;
  }

  public Object visitPushableStatement(LyteParser.PushableContext ctx) {
    // If this is an invokable expression
    if (ctx.invokable() != null) {
      // Handle it accordingly
      return visitInvokeStatement(ctx.invokable());
    } else {
      // Otherwise, visit the kids and generate a statement from the result
      return new LytePushStatement(getLineNumber(ctx), (LyteValue) visitChildren(ctx));
    }
  }

  @Override
  public Object visitLambdaExpression(LyteParser.LambdaExpressionContext ctx) {
    List<TerminalNode> nodes = ctx.lambdaArgsList().Identifier();
    mCurrentBlock = mCurrentBlock.enter();
    for (TerminalNode node : nodes) {
      mCurrentBlock.addArg(node.getText());
    }
    return visitBlock(ctx.block(), false);
  }

  @Override
  public Object visitObjectLiteral(LyteParser.ObjectLiteralContext ctx) {
    LyteRawObject rawObject = new LyteRawObject();
    if (ctx.keyValueList() != null) {
      List<LyteParser.KeyValuePairContext> keyValueList = ctx.keyValueList().keyValuePair();
      // Visit each of the properties
      for (LyteParser.KeyValuePairContext keyValue : keyValueList) {
        // Adding it to the raw object as it goes
        rawObject.set(keyValue.Identifier().getText(), (LyteStatement) visitPushableStatement(keyValue.pushable()));
      }
    }
    return rawObject;
  }

  @Override
  public Object visitRange(LyteParser.RangeContext ctx) {
    LyteRawBlock block = mCurrentBlock.enter();

    for (int i = (ctx.numericLiteral().size() - 1); i >= 0; i--) {
      block.addStatement(new LytePushStatement(getLineNumber(ctx), (LyteValue) visitNumericLiteral(ctx.numericLiteral(i))));
    }

    block.addStatement(new LyteInvokeStatement(getLineNumber(ctx), "Range" + ctx.numericLiteral().size()));

    return block;
  }

  @Override
  public Object visitArrayLiteral(LyteParser.ArrayLiteralContext ctx) {
    LyteRawArray array = new LyteRawArray();
    LyteParser.ValueListContext valueList = ctx.valueList();
    if (valueList != null) {
      for (LyteParser.PushableContext pushable : valueList.pushable()) {
        array.add((LyteStatement) visitPushableStatement(pushable));
      }
    }
    return array;
  }

  @Override
  public Object visitNumericLiteral(LyteParser.NumericLiteralContext ctx) {
    String number = ctx.getText();
    int radix;
    // Decide on the radix and get rid of any extraneous characters
    if (ctx.BinaryIntegerLiteral() != null) {
      radix = 2;
      number = number.substring(2, number.length());
    } else if (ctx.OctalIntegerLiteral() != null) {
      radix = 8;
    } else if (ctx.HexIntegerLiteral() != null) {
      radix = 16;
      number = number.substring(2, number.length());
    } else {
      // Unless it is simply a decimal value, at which point just parse it directly
      return new LyteNumber(number);
    }
    // Otherwise, parse it as an integer with the determined radix
    return new LyteNumber(number, radix);
  }

  @Override
  public Object visitStringLiteral(LyteParser.StringLiteralContext ctx) {
    String string = ctx.getText();
    // Grab the String w/o Quotes
    string = string.substring(1, string.length() - 1);
    // And then Unescape it
    string = StringEscapeUtils.unescapeEcmaScript(string);
    // Finally, return a Lyte String created from it
    return new LyteString(string);
  }

  @Override
  public Object visitBlock(LyteParser.BlockContext ctx) {
    return visitBlock(ctx, true);
  }

  public Object visitBlock(LyteParser.BlockContext ctx, boolean enterNewBlock) {
    LyteRawBlock block;
    // If we have enter a new block, do so
    if (enterNewBlock) {
      mCurrentBlock = mCurrentBlock.enter();
    }
    // Visit the kids
    visitChildren(ctx);
    // Then leave
    block = mCurrentBlock;
    mCurrentBlock = mCurrentBlock.leave();
    // Returning the newly populated block
    return block;
  }

  public Object visitInvokeStatement(LyteParser.InvokableContext ctx) {
    // If there is only one child
    String primaryIdentifier = ctx.Identifier().getText();
    if (ctx.getChildCount() == 1) {
      // Simply return a simple invokable
      return new LyteInvokeStatement(getLineNumber(ctx), primaryIdentifier);
    } else {
      // Otherwise, construct the list of designators
      List<LyteInvokeStatement.LyteSpecifier> designators = new ArrayList<LyteInvokeStatement.LyteSpecifier>();
      LyteInvokeStatement.LyteSpecifier designator;

      for (int i = 1; i < ctx.getChildCount(); i++) {
        if ((designator = (LyteInvokeStatement.LyteSpecifier) visit(ctx.getChild(i))) != null) {
          designators.add(designator);
        }
      }

      return new LyteInvokeStatement(getLineNumber(ctx), primaryIdentifier, designators);
    }
  }

  public Object visitBindStatement(LyteParser.InvokableContext ctx) {
    return new LyteBindStatement(getLineNumber(ctx), (LyteInvokeStatement) visitInvokeStatement(ctx));
  }

  @Override
  public Object visitParameters(LyteParser.ParametersContext ctx) {
    if (ctx.parameterList() != null) {
      return visitParameterList(ctx.parameterList());
    } else {
      return null;
    }
  }

  @Override
  public Object visitParameterList(LyteParser.ParameterListContext ctx) {
    List<LyteRawBlock> parameters = new ArrayList<LyteRawBlock>();
    ParseTree child;
    mCurrentBlock = mCurrentBlock.enter();
    for (int i = 0; i < ctx.getChildCount(); i++) {
      child = ctx.getChild(i);
      // If we hit a comma
      if (child.getText().equals(",")) {
        // Add the simplified block to the parameters list
        parameters.add(mCurrentBlock.simplify());
        // Leave back to the parent scope and re-enter a new child
        mCurrentBlock = mCurrentBlock.leave().enter();
      } else {
        // Otherwise, It'll get added automagically by visiting the kid
        visit(child);
      }
    }
    // Add the final block was to the parameters
    parameters.add(mCurrentBlock.simplify());
    // Then leave it
    mCurrentBlock = mCurrentBlock.leave();
    // And return a designator object
    return new LyteInvokeStatement.LyteSpecifier(parameters);
  }

  @Override
  public Object visitDesignator(LyteParser.DesignatorContext ctx) {
    if (ctx.Identifier() != null) {
      return new LyteInvokeStatement.LyteSpecifier(ctx.Identifier().toString());
    } else {
      return new LyteInvokeStatement.LyteSpecifier((LyteStatement) visitPushableStatement(ctx.pushable()));
    }
  }

  @Override
  public Object visitInfixExpression(LyteParser.InfixExpressionContext ctx) {
    // First, visit the infix expression's statement
    visitStatement(ctx.statement());
    // Then, visit the invokable
    return visitInvokeStatement(ctx.invokable());
  }

  @Override
  public Object visitLeftBindingExpression(LyteParser.LeftBindingExpressionContext ctx) {
    // Visit the pushable
    mCurrentBlock.addStatement((LyteStatement) visitPushableStatement(ctx.pushable()));
    // Then visit the invokable destination
    return visitBindStatement(ctx.invokable());
  }

  @Override
  public Object visitRightBindingExpression(LyteParser.RightBindingExpressionContext ctx) {
    return visitBindStatement(ctx.invokable());
  }

  private String getLineNumber(ParserRuleContext ctx) {
    Token token = mTokenStream.get(ctx.getSourceInterval().a);
    return token.getLine() + ":" + token.getCharPositionInLine();
  }
}
