package com.lyte.objs;

import com.lyte.core.LyteScope;
import com.lyte.core.LyteStatement;

import java.util.ArrayList;

/**
 * Created by jszaday on 6/18/15.
 */
public class LyteRawArray implements LyteValue {

    private ArrayList<LyteStatement> mStatements;

    public LyteRawArray() {
        mStatements = new ArrayList<LyteStatement>();
    }

    public boolean add(LyteStatement statement) {
        return mStatements.add(statement);
    }

    @Override
    public String typeOf() {
        return "rawArray";
    }

    @Override
    public String toString() {
        return mStatements.toString();
    }

    @Override
    public LyteValue clone(LyteScope scope) {
        return null;
    }
}
