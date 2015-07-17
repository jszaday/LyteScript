package com.lyte.stdlib;

import com.lyte.core.LyteContext;
import com.lyte.objs.LyteError;
import com.lyte.objs.LyteNativeBlock;
import com.lyte.objs.LyteStream;
import com.lyte.objs.LyteValue;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Created by a0225785 on 7/17/2015.
 */
public class LyteIOFunctions {

  private static final LyteStream stdErr = new LyteStream(System.err);
  private static final LyteStream stdIn = new LyteStream(System.in);
  private static final LyteStream stdOut = new LyteStream(System.out);

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

  public static LyteNativeBlock ioStdErr = new LyteNativeBlock("IO", "StdErr") {
    @Override
    public void invoke(LyteContext context) {
      context.push(stdErr);
    }
  };

  public static LyteNativeBlock ioEchoLn = new LyteNativeBlock("IO", "EchoLn") {
    @Override
    public void invoke(LyteContext context) {
      if (!context.isEmpty()) {
        LyteValue value1 = context.apply();
        if (value1.is("stream")) {
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
      if (value1.is("stream")) {
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
}
