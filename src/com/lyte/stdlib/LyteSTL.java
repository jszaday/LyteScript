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
        public void invoke(LyteObject self, LyteStack stack) {
            stack.push(new LyteBoolean(true));
        }
    };

    public static LyteNativeBlock coreFalse = new LyteNativeBlock("Core", "False") {
        @Override
        public void invoke(LyteObject self, LyteStack stack) {
            stack.push(new LyteBoolean(false));
        }
    };

    public static LyteNativeBlock coreNot = new LyteNativeBlock("Core", "Not") {
        @Override
        public void invoke(LyteObject self, LyteStack stack) {
            stack.push(new LyteBoolean(!stack.pop().toBoolean().get()));
        }
    };

    public static LyteNativeBlock coreIsUndefined = new LyteNativeBlock("Core", "IsUndefined", "Undefined?") {
        @Override
        public void invoke(LyteObject self, LyteStack stack) {
            stack.push(new LyteBoolean(stack.pop() == LyteUndefined.UNDEFINED));
        }
    };

    public static LyteNativeBlock coreAdd = new LyteNativeBlock("Core", "Add", "+") {

        @Override
        public void invoke(LyteObject self, LyteStack stack) {
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
        }
    };

    public static LyteNativeBlock coreApply = new LyteNativeBlock("Core", "Apply") {
        @Override
        public void invoke(LyteObject self, LyteStack stack) {
            LyteValue value = stack.pop();

            if (value.typeOf().equals("block")) {
                ((LyteBlock) value).invoke(self, stack);
            } else {
                stack.push(value);
            }
        }
    };

    public static LyteNativeBlock coreConcatenate = new LyteNativeBlock("Core", "Concatenate", "++") {
        @Override
        public void invoke(LyteObject self, LyteStack stack) {
            LyteValue value2 = stack.pop();
            LyteValue value1 = stack.pop();
            // TODO Implement other cases
            if (value1.typeOf().equals("string")) {
                stack.push(new LyteString(value1.toString() + value2.toString()));
            }
        }
    };

    public static LyteNativeBlock ioEcho = new LyteNativeBlock("IO", "Echo") {
        @Override
        public void invoke(LyteObject self, LyteStack stack) {
            System.out.println(LyteInvokeStatement.applyIfNeeded(stack.pop(), self, stack));
        }
    };

    public static LyteNativeBlock coreIf = new LyteNativeBlock("Core", "If") {

        @Override
        public void invoke(LyteObject self, LyteStack stack) {
            LyteValue condition = stack.pop();
            LyteBlock trueBlock = (LyteBlock) stack.pop();
            LyteBlock falseBlock = (LyteBlock) stack.pop();

            while (condition.typeOf().equals("block")) {
                ((LyteBlock) condition).invoke(self, stack);
                condition = stack.pop();
            }

//            System.out.println("Condition: " + condition + ", Blocks: " + trueBlock + " OR " + falseBlock);

            if (condition.toBoolean().get()) {
                trueBlock.invoke(self, stack);
            } else {
                falseBlock.invoke(self, stack);
            }
        }
    };

    public static LyteNativeBlock utilInstantiate = new LyteNativeBlock("Util", "Instantiate") {
        @Override
        public void invoke(LyteObject self, LyteStack stack) {
            // TODO this assumes the two things are on the same stack, we have to move scoping out of blocks!!!
            LyteValue value = stack.pop();
            if (value.typeOf().equals("block")) {
                ((LyteBlock) value).invoke(self, stack);
                value = stack.pop();
            }
            if (!LyteObject.isObject(value)) {
                System.err.println("Cannot Instantiate a(n) " + value.typeOf() + ".");
                return;
            }
            if (!((LyteObject) value).hasProperty("__constructor")) {
                System.err.println("Error, object has no constructor!");
            }
            LyteObject obj = ((LyteObject) value).clone();
            ((LyteBlock) obj.get("__constructor")).invoke(obj, stack);
            obj.unset("__constructor");
            stack.push(obj);
        }
    };

    public static LyteNativeBlock coreMixWith = new LyteNativeBlock("Core", "MixWith") {
        @Override
        public void invoke(LyteObject self, LyteStack stack) {
            LyteValue value2 = stack.pop();
            LyteValue value1 = stack.pop();

            if (!(LyteObject.isObject(value1) && LyteObject.isObject(value2))) {
                throw new RuntimeException("Cannot mix " + value1 + " with " + value2);
            }

            ((LyteObject) value1).mixWith((LyteObject) value2);
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
