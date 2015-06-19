package com.lyte.core;

/**
 * Created by jszaday on 6/18/15.
 */
public class LyteBindStatement implements LyteStatement {

    private LyteInvokeStatement mTarget;

    public LyteBindStatement(LyteInvokeStatement target) {
        mTarget = target;
    }

    @Override
    public String toString() {
        return "Bind: " + mTarget.toString(false);
    }

    @Override
    public void applyTo(LyteScope scope) {

    }
}
