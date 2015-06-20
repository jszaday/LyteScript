package com.lyte.core;

import com.lyte.objs.LyteUndefined;
import com.lyte.objs.LyteValue;

import java.util.ArrayDeque;

/**
 * Created by jszaday on 6/19/15.
 */
public class LyteStack extends ArrayDeque<LyteValue> {
    public LyteValue pop() {
        if (!this.isEmpty()) {
            return super.pop();
        } else {
            return LyteUndefined.UNDEFINED;
        }
    }
}
