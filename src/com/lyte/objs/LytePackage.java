package com.lyte.objs;

import java.util.HashMap;

/**
 * Created by jszaday on 6/22/15.
 */
public class LytePackage extends LyteObject {
    public LytePackage() {
        super(null, new HashMap<String, LyteValue>());
    }

    public LytePackage getNamespace(String key) {
        if (!hasProperty(key)) {
            super.set(key, new LytePackage());
        }
        return (LytePackage) get(key);
    }

    public LyteValue elevatedSet(String key, LyteValue value) {
        return super.set(key, value);
    }

    @Override
    public LyteValue set(String key, LyteValue value) {
        throw new LyteError("Can't assign to " + key);
    }
}
