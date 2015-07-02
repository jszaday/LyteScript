package com.lyte.core;

import com.lyte.objs.LyteError;
import com.lyte.objs.LytePackage;
import com.lyte.objs.LyteValue;
import org.apache.commons.collections4.iterators.ArrayIterator;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by a0225785 on 7/2/2015.
 */
public class LyteClassLoader {
  private static final String LYTE_CLASSPATH = System.getenv().get("LYTE_CLASSPATH");

  private static ArrayList<String> classPath;
  private static HashMap<String, LyteValue> cachedClasses;

  static {
    classPath = new ArrayList<>();
    cachedClasses = new HashMap<>();

    if (LYTE_CLASSPATH != null) {
      for (String path : LYTE_CLASSPATH.split(File.pathSeparator)) {
        if (path.endsWith(File.separator)) {
          path = path.substring(0, path.length() - 1);
        }
        classPath.add(path);
      }
    } else {
      classPath.add(".");
    }
  }

  public static void load(LyteContext context, String target) throws IOException {
    String tmp, packagePath = "";
    String[] paths = target.split("\\.");
    ArrayIterator<String> iterator = new ArrayIterator<String>(paths);
    LytePackage workingPackage = null;

    while(iterator.hasNext()) {
      tmp = iterator.next();

      if (workingPackage == null && iterator.hasNext()) {
        packagePath += File.separator + tmp;

        if (context.has(tmp)) {
          try {
            workingPackage = (LytePackage) context.get(tmp);
          } catch (ClassCastException e) {
            throw new LyteError("Cannot override " + tmp);
          }
        } else {
          workingPackage = new LytePackage();
          context.set(tmp, workingPackage);
        }
      } else if (iterator.hasNext()) {
        packagePath += File.separator + tmp;
        workingPackage.elevatedSet(tmp, new LytePackage());
        workingPackage = (LytePackage) workingPackage.getProperty(tmp);
      } else if (tmp.equals("*")) {
        File directory;
        boolean loaded = false;

        for (String classPath : LyteClassLoader.classPath) {
          directory = new File(classPath + packagePath);

          if (directory.exists()) {
            if (!directory.isDirectory()) {
              throw new LyteError("Could not import " + target);
            }

            for (String file : directory.list()) {
              if (!file.endsWith(".lyte")) {
                continue;
              }
              String name = file.substring(0, file.length() - 5);
              if (workingPackage != null) {
                workingPackage.elevatedSet(name, loadFile(new File(directory.getPath() + File.separator + file), name));
              } else {
                context.set(name, loadFile(new File(file), name), true);
              }
            }

            loaded = true;
            break;
          }
        }

        if (!loaded) {
          throw new LyteError("Could not import " + target);
        }
      } else {
        File file;
        boolean loaded = false;

        for (String directory : classPath) {
          file = new File(directory + packagePath + File.separator + tmp + ".lyte");

          if (file.exists()) {
            context.set(tmp, loadFile(file, tmp), true);
            if (workingPackage != null) {
              workingPackage.elevatedSet(tmp, context.get(tmp));
            }
            loaded = true;
            break;
          }
        }

        if (!loaded) {
          throw new LyteError("Could not import " + target);
        }
      }
    }
  }

  public static LyteValue loadFile(File file, String name) throws IOException {
    if (cachedClasses.containsKey(file.getCanonicalPath())) {
      return cachedClasses.get(file.getCanonicalPath());
    } else {
      LyteEvaluator evaluator = new LyteEvaluator(file);
      evaluator.run();
      return cacheClass(file.getCanonicalPath(), evaluator.get(name));
    }
  }

  private static LyteValue cacheClass(String path, LyteValue value) {
    cachedClasses.put(path, value);
    return value;
  }
}
