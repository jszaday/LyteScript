package com.lyte.stdlib;

import com.lyte.core.LyteInvokeStatement;
import com.lyte.core.LyteScope;
import com.lyte.core.LyteStack;
import com.lyte.objs.*;

import java.lang.reflect.Field;

/**
 * Created by jszaday on 6/22/15.
 */
public class LyteSTL {

    public static String TOP_LEVEL_NAMESPACE = "Lyte";

    public static LyteNativeBlock coreTrue = new LyteNativeBlock("Core", "True") {
        @Override
        public boolean invoke(LyteValue self, LyteStack stack) {
            stack.push(new LyteBoolean(true));
            return true;
        }
    };

    public static LyteNativeBlock coreFalse = new LyteNativeBlock("Core", "False") {
        @Override
        public boolean invoke(LyteValue self, LyteStack stack) {
            stack.push(new LyteBoolean(false));
            return true;
        }
    };

    public static LyteNativeBlock coreNot = new LyteNativeBlock("Core", "Not") {
        @Override
        public boolean invoke(LyteValue self, LyteStack stack) {
            stack.push(new LyteBoolean(!stack.pop().apply(self).toBoolean()));
            return true;
        }
    };

    public static LyteNativeBlock coreIsUndefined = new LyteNativeBlock("Core", "IsUndefined", "Undefined?") {
        @Override
        public boolean invoke(LyteValue self, LyteStack stack) {
            stack.push(new LyteBoolean(stack.pop() == LyteUndefined.UNDEFINED));
            return true;
        }
    };

    public static LyteNativeBlock coreAdd = new LyteNativeBlock("Core", "Add", "+") {

        @Override
        public boolean invoke(LyteValue self, LyteStack stack) {
            double val1 = stack.pop().apply(self).toNumber();
            double val2 = stack.pop().apply(self).toNumber();
            stack.push(new LyteNumber(val1 + val2));
            return true;
        }
    };

    public static LyteNativeBlock coreApply = new LyteNativeBlock("Core", "Apply") {
        @Override
        public boolean invoke(LyteValue self, LyteStack stack) {
            LyteValue value = stack.pop();

            if (value.typeOf().equals("block")) {
                ((LyteBlock) value).invoke(self, stack);
            } else {
                stack.push(value);
            }

            return true;
        }
    };

    public static LyteNativeBlock coreConcatenate = new LyteNativeBlock("Core", "Concatenate", "++") {
        @Override
        public boolean invoke(LyteValue self, LyteStack stack) {
            LyteValue value2 = stack.pop().apply(self);
            LyteValue value1 = stack.pop().apply(self);

            // TODO Implement other cases
            if (value1.typeOf().equals("string") || value2.typeOf().equals("string")) {
                stack.push(value1.toString() + value2.toString());
            }

            return true;
        }
    };

    public static LyteNativeBlock ioEcho = new LyteNativeBlock("IO", "Echo") {
        @Override
        public boolean invoke(LyteValue self, LyteStack stack) {
            System.out.println(stack.pop().apply(self));
            return true;
        }
    };

    public static LyteNativeBlock coreIf = new LyteNativeBlock("Core", "If") {

        @Override
        public boolean invoke(LyteValue self, LyteStack stack) {
            LyteValue condition = stack.pop();
            LyteBlock trueBlock = (LyteBlock) stack.pop();
            LyteBlock falseBlock = (LyteBlock) stack.pop();

            if (condition.apply(self).toBoolean()) {
                trueBlock.invoke(self, stack);
            } else {
                falseBlock.invoke(self, stack);
            }

            return true;
        }
    };

    public static LyteNativeBlock utilInstantiate = new LyteNativeBlock("Util", "Instantiate") {
        @Override
        public boolean invoke(LyteValue self, LyteStack stack) {
            // TODO this assumes the two things are on the same stack, we have to move scoping out of blocks!!!
            LyteValue value = stack.pop().apply(self);
            if (!value.typeOf().equals("object")) {
                throw new LyteError("Cannot Instantiate a(n) " + value.typeOf() + ".");
            }
            if (!value.hasProperty("__constructor")) {
                throw new LyteError("Error, object has no constructor!");
            }
            LyteObject obj = (LyteObject) value.clone(null);
            try {
                ((LyteBlock) obj.getProperty("__constructor")).invoke(obj, stack);
            } catch(ClassCastException e) {
                throw new LyteError("Expected __constructor to be a block for object " + obj);
            }
            obj.unsetProperty("__constructor");
            stack.push(obj);
            return true;
        }
    };

    public static LyteNativeBlock coreMixWith = new LyteNativeBlock("Core", "MixWith") {
        @Override
        public boolean invoke(LyteValue self, LyteStack stack) {
            LyteValue value2 = stack.pop();
            LyteValue value1 = stack.pop();

            if (!(value1.typeOf().equals("object") && value2.typeOf().equals("object"))) {
                throw new LyteError("Cannot mix " + value1 + " with " + value2);
            }

            ((LyteObject) value1).mixWith((LyteObject) value2);

            return true;
        }
    };

    public static LyteNativeBlock errorTry = new LyteNativeBlock("Error", "Try") {
        @Override
        public boolean invoke(LyteValue self, LyteStack stack) {
            LyteValue value1 = stack.pop();
            LyteValue value2 = stack.pop();

            if (!value1.typeOf().equals("block") || !value2.typeOf().equals("block")) {
                throw new LyteError("Try requires both parameters to be blocks!");
            }

            stack.pushHandler((LyteBlock) value2);

            if (((LyteBlock) value1).invoke(self, stack)) {
                stack.popHandler();
            }

            return true;
        }
    };

    public static LyteNativeBlock errorRaise = new LyteNativeBlock("Error", "Raise") {
        @Override
        public boolean invoke(LyteValue self, LyteStack stack) {
            // Throw the result to the wolves
            throw new LyteError(stack.pop().apply(self));
        }
    };

    public static void injectNatives(LyteScope scope) {
        Field[] fields = LyteSTL.class.getDeclaredFields();
        LytePackage lytePackage = new LytePackage();
        LyteNativeBlock nativeBlock;
        Object obj;
        // For each field in the class
        for (Field field : fields) {
            // Try to get the value as an obj
            try {
                obj = field.get(null);
            } catch (IllegalAccessException e) {
                // but if it fails just continue
                continue;
            }
            // And if that obj is a native block
            if (obj instanceof LyteNativeBlock) {
                // Grab it, casting it accordingly
                nativeBlock = (LyteNativeBlock) obj;
            } else {
                // Oherwise just continue
                continue;
            }
            // Get the namespace
            LytePackage namespace = lytePackage.getNamespace(nativeBlock.namespace);
            // Add the block to the namespace
            namespace.elevatedSet(nativeBlock.fullname, nativeBlock);
            // Then if the block has an alias
            if (nativeBlock.alias != null) {
                // Add it's alias to the scope
                scope.putVariable(nativeBlock.alias, nativeBlock, true);
            }
        }

        scope.putVariable(TOP_LEVEL_NAMESPACE, lytePackage, true);
    }
}
