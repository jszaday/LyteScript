package com.lyte;

import com.lyte.core.*;
import com.lyte.gen.LyteBaseVisitor;
import com.lyte.gen.LyteLexer;
import com.lyte.gen.LyteParser;
import com.lyte.objs.*;
import com.lyte.stdlib.*;
import org.antlr.v4.runtime.ANTLRFileStream;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.misc.Interval;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.apache.commons.lang3.StringEscapeUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

public class Main{
    public static void main(String... args) {
        try {
            LyteEnvironment main;
            if (args.length >= 1) {
                main = new LyteEnvironment(args[0]);
            } else {
                main = new LyteEnvironment(System.in, true);
            }
            ArrayList<LyteValue> values = new ArrayList<LyteValue>();
            for (int i = 1; i < args.length; i++) {
                values.add(new LyteString(args[i]));
            }
            main.run(values.toArray(new LyteValue[values.size()]));
        } catch (IOException e) {
            System.err.println("Error, could not open file " + args[0]);
        } catch (NoSuchElementException e) {
            System.out.println();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
