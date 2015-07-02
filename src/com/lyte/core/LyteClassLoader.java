package com.lyte.core;

import com.lyte.objs.LyteError;
import com.lyte.objs.LytePackage;
import com.lyte.objs.LyteValue;
import org.apache.commons.collections4.iterators.ArrayIterator;

import java.awt.*;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by a0225785 on 7/2/2015.
 */
public class LyteClassLoader {
  private ArrayList<String> mClassPath;

  public LyteClassLoader() {
    mClassPath = new ArrayList<>();
    String lytePath = System.getenv().get("LYTE_CLASSPATH");

    if (lytePath != null) {
      for (String path : lytePath.split(File.pathSeparator)) {
        if (path.endsWith(File.separator)) {
          path = path.substring(0, path.length() - 1);
        }
        mClassPath.add(path);
      }
    } else {
      mClassPath.add(".");
    }
  }

  public void load(LyteContext context, String target) throws IOException {
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

        for (String classPath : mClassPath) {
          directory = new File(classPath + packagePath);
          if (directory.exists()) {
            if (!directory.isDirectory()) {
              throw new LyteError("Could not import " + target);
            }
            for (String file : directory.list(new FilenameFilter() {
              @Override
              public boolean accept(File dir, String name) {
                return name.endsWith(".lyte");
              }
            })) {
              String name = file.substring(0, file.length() - 5);
              if (workingPackage != null) {
                workingPackage.elevatedSet(name, loadFile(new File(directory.getPath() + File.separator + file), name));
              } else {
                context.set(name, loadFile(new File(file), name), true);
              }
              loaded = true;
              break;
            }
          }
        }

        if (!loaded) {
          throw new LyteError("Could not import " + target);
        }
      } else {
        File file;
        boolean loaded = false;

        for (String directory : mClassPath) {
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

  private LyteValue loadFile(File file, String name) throws IOException {
    LyteEvaluator evaluator = new LyteEvaluator(file);
    evaluator.run();
    return evaluator.get(name);
  }
}
