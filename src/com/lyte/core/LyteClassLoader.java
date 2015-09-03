package com.lyte.core;

import com.lyte.objs.LyteError;
import com.lyte.objs.LytePackage;
import com.lyte.objs.LyteValue;
import com.lyte.stdlib.LyteStandardFunctions;
import org.apache.commons.collections4.iterators.ArrayIterator;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

/**
 * LyteClassLoader
 * 	The class loader for importing external .lyte files
 * @author Justin Szaday
 */
public class LyteClassLoader {
  private static final String LYTE_CLASSPATH = System.getenv().get("LYTE_CLASSPATH");

  private static ArrayList<String> classPath;
  private static HashMap<String, LyteValue> cachedClasses;

  static {
    classPath = new ArrayList<>();
    cachedClasses = new HashMap<>();
    // If the class path has a value
    if (LYTE_CLASSPATH != null) {
      // Split the path based the system's path seperator and iterate over each of the parts
      for (String path : LYTE_CLASSPATH.split(File.pathSeparator)) {
        // If the path ends with a / or \ trim it off (for consistency)
        if (path.endsWith(File.separator)) {
          path = path.substring(0, path.length() - 1);
        }
        // Then, add it to the paths list
        classPath.add(path);
      }
    } else {
      // Otherwise, just add the current directory to the path
      classPath.add(".");
    }
  }

  /**
   * Import native functions into a given context
   */
  private static void importNatives(LyteContext context, String[] paths) {
    // Grab the package from the context (since it's already there)
    LyteValue lytePackage = context.get(paths[0]);
    int i;

    // "Recursively" get all the last property from the first package
    for (i = 1; i < (paths.length - 1); i++) {
      lytePackage = lytePackage.getProperty(paths[i]);
    }

    // If there are still paths left to resolve
    if (i < paths.length) {
      // Then, if the path is a wildcard
      if (paths[i].equals("*")) {
        // We'll have to grab all of the properties and add them to the path
        for (String property : (Set<String>) lytePackage.getProperties()) {
          if (!context.has(property)) {
            context.set(property, lytePackage.getProperty(property), true);
          }
        }
      } else if (!context.has(paths[i])) {
        // Otherwise, just set the variable (but don't override any existing definitions)
        context.set(paths[i], lytePackage.getProperty(paths[i]), true);
      }
    }

    return;
  }

  public static void load(LyteContext context, String target) throws IOException {
    String tmp, packagePath = "";
    String[] paths = target.split("\\.");
    ArrayIterator<String> iterator = new ArrayIterator<String>(paths);
    LytePackage workingPackage = null;


    if (paths.length > 0 && paths[0].equals(LyteStandardFunctions.TOP_LEVEL_NAMESPACE)) {
      importNatives(context, paths);
      return;
    }

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

  /**
   * Loads a class (parsing it or pulling it from a cache)
   */
  public static LyteValue loadFile(File file, String name) throws IOException {
    if (cachedClasses.containsKey(file.getCanonicalPath())) {
      return cachedClasses.get(file.getCanonicalPath());
    } else {
      LyteEvaluator evaluator = new LyteEvaluator(file);
      evaluator.run();
      return cacheClass(file.getCanonicalPath(), evaluator.get(name));
    }
  }

  /**
   * Adds a class to the cache (saving it so you don't have to parse it again later)
   */
  private static LyteValue cacheClass(String path, LyteValue value) {
    cachedClasses.put(path, value);
    return value;
  }
}
