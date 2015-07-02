package com.lyte;

import com.lyte.core.LyteEvaluator;
import com.lyte.objs.LyteString;
import com.lyte.objs.LyteValue;

import java.io.IOException;
import java.util.ArrayList;
import java.util.NoSuchElementException;

public class Main{
    public static void main(String... args) {
        try {
            LyteEvaluator main;
            if (args.length >= 1) {
                main = new LyteEvaluator(args[0]);
            } else {
                main = new LyteEvaluator(System.in, true);
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
