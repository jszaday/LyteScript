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
        public boolean invoke(LyteObject self, LyteStack stack) {
            stack.push(new LyteBoolean(true));
            return true;
        }
    };

    public static LyteNativeBlock coreFalse = new LyteNativeBlock("Core", "False") {
        @Override
        public boolean invoke(LyteObject self, LyteStack stack) {
            stack.push(new LyteBoolean(false));
            return true;
        }
    };

    public static LyteNativeBlock coreNot = new LyteNativeBlock("Core", "Not") {
        @Override
        public boolean invoke(LyteObject self, LyteStack stack) {
            stack.push(new LyteBoolean(!stack.pop().toBoolean().get()));
            return true;
        }
    };

    public static LyteNativeBlock coreIsUndefined = new LyteNativeBlock("Core", "IsUndefined", "Undefined?") {
        @Override
        public boolean invoke(LyteObject self, LyteStack stack) {
            stack.push(new LyteBoolean(stack.pop() == LyteUndefined.UNDEFINED));
            return true;
        }
    };

    public static LyteNativeBlock coreAdd = new LyteNativeBlock("Core", "Add", "+") {

        @Override
        public boolean invoke(LyteObject self, LyteStack stack) {
            LyteValue value1 = stack.pop();
            if (value1.typeOf().equals("block")) {
                ((LyteBlock) value1).invoke(self, stack);
                value1 = stack.pop();
            }
            LyteValue value2 = stack.pop();
            if (value2.typeOf().equals("block")) {
                ((LyteBlock) value2).invoke(self, stack);
                value2 = stack.pop();
            }
            while (!value1.typeOf().equals("number")) {
                value1 = value1.toNumber();
            }
            while (!value2.typeOf().equals("number")) {
                value2 = value2.toNumber();
            }
            stack.push(new LyteNumber(((LytePrimitive<Double>) value1).get() + ((LytePrimitive<Double>) value2).get()));
            return true;
        }
    };

    public static LyteNativeBlock coreApply = new LyteNativeBlock("Core", "Apply") {
        @Override
        public boolean invoke(LyteObject self, LyteStack stack) {
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
        public boolean invoke(LyteObject self, LyteStack stack) {
            LyteValue value2 = stack.pop();
            LyteValue value1 = stack.pop();
            // TODO Implement other cases
            if (value1.typeOf().equals("string")) {
                stack.push(new LyteString(value1.toString() + value2.toString()));
            }

            return true;
        }
    };

    public static LyteNativeBlock ioEcho = new LyteNativeBlock("IO", "Echo") {
        @Override
        public boolean invoke(LyteObject self, LyteStack stack) {
            System.out.println(LyteInvokeStatement.applyIfNeeded(stack.pop(), self, stack));
            return true;
        }
    };

    public static LyteNativeBlock coreIf = new LyteNativeBlock("Core", "If") {

        @Override
        public boolean invoke(LyteObject self, LyteStack stack) {
            LyteValue condition = stack.pop();
            LyteBlock trueBlock = (LyteBlock) stack.pop();
            LyteBlock falseBlock = (LyteBlock) stack.pop();

            while (condition.typeOf().equals("block")) {
                ((LyteBlock) condition).invoke(self, stack);
                condition = stack.pop();
            }

            if (condition.toBoolean().get()) {
                trueBlock.invoke(self, stack);
            } else {
                falseBlock.invoke(self, stack);
            }

            return true;
        }
    };

    public static LyteNativeBlock utilInstantiate = new LyteNativeBlock("Util", "Instantiate") {
        @Override
        public boolean invoke(LyteObject self, LyteStack stack) {
            // TODO this assumes the two things are on the same stack, we have to move scoping out of blocks!!!
            LyteValue value = stack.pop();
            if (value.typeOf().equals("block")) {
                ((LyteBlock) value).invoke(self, stack);
                value = stack.pop();
            }
            if (!LyteObject.isObject(value)) {
                throw new LyteError("Cannot Instantiate a(n) " + value.typeOf() + ".");
            }
            if (!((LyteObject) value).hasProperty("__constructor")) {
                throw new LyteError("Error, object has no constructor!");
            }
            LyteObject obj = ((LyteObject) value).clone();
            ((LyteBlock) obj.get("__constructor")).invoke(obj, stack);
            obj.unset("__constructor");
            stack.push(obj);
            return true;
        }
    };

    public static LyteNativeBlock coreMixWith = new LyteNativeBlock("Core", "MixWith") {
        @Override
        public boolean invoke(LyteObject self, LyteStack stack) {
            LyteValue value2 = stack.pop();
            LyteValue value1 = stack.pop();

            if (!(LyteObject.isObject(value1) && LyteObject.isObject(value2))) {
                throw new LyteError("Cannot mix " + value1 + " with " + value2);
            }

            ((LyteObject) value1).mixWith((LyteObject) value2);

            return true;
        }
    };

    public static LyteNativeBlock errorTry = new LyteNativeBlock("Error", "Try") {
        @Override
        public boolean invoke(LyteObject self, LyteStack stack) {
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
        public boolean invoke(LyteObject self, LyteStack stack) {
            // Apply whatever is on top of the stack
            coreApply.invoke(self, stack);
            // And throw the result to the wolves
            throw new LyteError(stack.pop());
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
