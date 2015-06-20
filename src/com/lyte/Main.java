package com.lyte;

import com.lyte.core.*;
import com.lyte.gen.LyteBaseVisitor;
import com.lyte.gen.LyteLexer;
import com.lyte.gen.LyteParser;
import com.lyte.objs.*;
import com.lyte.stdlib.*;
import org.antlr.v4.runtime.ANTLRFileStream;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.apache.commons.lang3.StringEscapeUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Main extends LyteBaseVisitor<Object> {

  // Scoping Stuff
  private LyteRawBlock mGlobal;
  private LyteRawBlock mCurrentBlock;

  public Main(String fileName) throws IOException {
    // Setup our parser w/ the correct input
    CharStream inputStream = new ANTLRFileStream(fileName);
    LyteLexer lexer = new LyteLexer(inputStream);
    CommonTokenStream tokenStream = new CommonTokenStream(lexer);
    LyteParser parser = new LyteParser(tokenStream);
    // Initialize our other fields
    mCurrentBlock = mGlobal = LyteRawBlock.newGlobal();
    // Then grab the parse tree and begin to visit it
    ParseTree parseTree = parser.program();
    this.visit(parseTree);
  }

  public static void main(String... args) {
    if (args.length < 1) {
      System.out.println("Error, expected argument {inputFile}");
      return;
    }

    try {
      Main main = new Main(args[0]);
//      main.printIntermediateResults();
      main.run();
    } catch (IOException e) {
      System.err.println("Error, could not open file " + args[0]);
    }
  }

  public void printIntermediateResults() {
    for (LyteStatement statement : mGlobal.getStatements()) {
      System.out.println(statement);
    }
  }

  public void run(LyteValue... args) {
    LyteScope global = LyteScope.newGlobal();
    LyteStack stack = new LyteStack();
    global.injectNative(LyteInstantiate.class, LyteIf.class, LyteAdd.class, LyteEcho.class);
    LyteBlock main = (LyteBlock) mGlobal.clone(global);
    main.invoke(stack, args);
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
      return new LytePushStatement((LyteValue) visitChildren(ctx));
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
    List<LyteParser.KeyValuePairContext> keyValueList = ctx.keyValueList().keyValuePair();
    // Visit each of the properties
    for (LyteParser.KeyValuePairContext keyValue : keyValueList) {
      // Adding it to the raw object as it goes
      rawObject.set(keyValue.Identifier().getText(), (LyteStatement) visitPushableStatement(keyValue.pushable()));
    }
    return rawObject;
  }

  @Override
  public Object visitArrayLiteral(LyteParser.ArrayLiteralContext ctx) {
    LyteRawArray array = new LyteRawArray();
    LyteParser.ValueListContext valueList = ctx.valueList();
    for (LyteParser.PushableContext pushable : valueList.pushable()) {
      array.add((LyteStatement) visitPushableStatement(pushable));
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
    return new LytePrimitive<String>(string);
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
      return new LyteInvokeStatement(primaryIdentifier);
    } else {
      // Otherwise, construct the list of designators
      List<LyteInvokeStatement.LyteSpecifier> designators = new ArrayList<LyteInvokeStatement.LyteSpecifier>();
      LyteInvokeStatement.LyteSpecifier designator;

      for (int i = 1; i < ctx.getChildCount(); i++) {
        if ((designator = (LyteInvokeStatement.LyteSpecifier) visit(ctx.getChild(i))) != null) {
          designators.add(designator);
        }
      }

      return new LyteInvokeStatement(primaryIdentifier, designators);
    }
  }

  public Object visitBindStatement(LyteParser.InvokableContext ctx) {
    return new LyteBindStatement((LyteInvokeStatement) visitInvokeStatement(ctx));
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
      if (child.getText().equals(",")) {
        parameters.add(mCurrentBlock);
        // Leave back to the parent scope and re-enter a new child
        mCurrentBlock = mCurrentBlock.leave().enter();
      } else {
        // As a statement it'll get added automagically
        visit(child);
      }
    }
    // Add the final block was to the parameters
    parameters.add(mCurrentBlock);
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
}
