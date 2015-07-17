package com.lyte.utils;

import com.lyte.objs.LytePackage;
import com.lyte.objs.LyteValue;
import com.lyte.objs.LyteNativeBlock;

import java.lang.reflect.Field;

/**
 * Created by a0225785 on 6/29/2015.
 */
public class LyteNativeInjector {
  public static LytePackage getNatives(Class source, Object instance) {
    LytePackage lytePackage = new LytePackage();
    Field[] fields = source.getDeclaredFields();
    LyteNativeBlock nativeBlock;
    Object obj;

    // For each field in the class
    for (Field field : fields) {
      // Try to get the value as an obj
      try {
        obj = field.get(instance);
      } catch (IllegalAccessException e) {
        // but if it fails just continue
        continue;
      }
      // And if that obj is a native block
      if (obj instanceof LyteNativeBlock) {
        // Grab it, casting it accordingly
        nativeBlock = (LyteNativeBlock) obj;
      } else {
        // Otherwise just continue
        continue;
      }
      // Get the namespace
      LytePackage namespace = lytePackage.getNamespace(nativeBlock.namespace);
      // Add the block to the namespace
      namespace.elevatedSet(nativeBlock.fullname, nativeBlock);
    }

    return lytePackage;
  }

  public static void injectPackage(LyteInjectable target, LytePackage lytePackage, String topLevelNamespace) {
    String alias;
    for (LyteValue value : lytePackage.get().values()) {
      if (value instanceof LytePackage) {
        injectPackage(target, (LytePackage) value, null);
      } else if (value instanceof LyteNativeBlock && (alias = ((LyteNativeBlock) value).alias) != null) {
        target.inject(alias, value);
      }
    }

    if (topLevelNamespace != null) {
      target.inject(topLevelNamespace, lytePackage);
    }
  }

  public static void injectNatives(Class source, LyteInjectable target, String topLevelNamespace) {
    injectPackage(target, getNatives(source, null), topLevelNamespace);
  }

  public static void injectNatives(LyteInjectable target) {
    injectPackage(target, getNatives(target.getClass(), target), null);
  }
}
