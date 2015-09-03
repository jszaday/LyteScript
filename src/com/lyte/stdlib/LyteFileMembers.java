package com.lyte.stdlib;

import com.lyte.core.LyteContext;
import com.lyte.objs.*;
import com.lyte.utils.LyteMemberBlock;
import com.lyte.utils.LyteSimpleInjectable;

import java.io.File;
import java.io.IOException;

/**
 * Created by jszaday on 7/17/2015.
 */
public class LyteFileMembers extends LyteSimpleInjectable {

  public static final String EXECUTABLE = "executable";
  public static final String READABLE = "readable";
  public static final String WRITABLE = "writable";
  public static final String READ_ONLY = "readOnly";
  public static final String NAME = "name";
  public static final String LAST_MODIFIED = "lastModified";

  public static LyteNativeBlock fileIsExecutable = new LyteMemberBlock<LyteFile>(EXECUTABLE + "?") {
    @Override
    public void invoke(LyteFile self, LyteContext context) {
      context.push(self.canExecute());
    }
  };

  public static LyteNativeBlock fileIsReadable = new LyteMemberBlock<LyteFile>(READABLE + "?") {
    @Override
    public void invoke(LyteFile self, LyteContext context) {
      context.push(self.canRead());
    }
  };

  public static LyteNativeBlock fileIsWritable = new LyteMemberBlock<LyteFile>(WRITABLE + "?") {
    @Override
    public void invoke(LyteFile self, LyteContext context) {
      context.push(self.canWrite());
    }
  };

  public static LyteNativeBlock fileExists = new LyteMemberBlock<LyteFile>("exists?") {
    @Override
    public void invoke(LyteFile self, LyteContext context) {
      context.push(self.exists());
    }
  };

  public static LyteNativeBlock fileIsAbsolute = new LyteMemberBlock<LyteFile>("absolute?") {
    @Override
    public void invoke(LyteFile self, LyteContext context) {
      context.push(self.isAbsolute());
    }
  };

  public static LyteNativeBlock fileIsDirectory = new LyteMemberBlock<LyteFile>("directory?") {
    @Override
    public void invoke(LyteFile self, LyteContext context) {
      context.push(self.isDirectory());
    }
  };

  public static LyteNativeBlock fileIsFile = new LyteMemberBlock<LyteFile>("file?") {
    @Override
    public void invoke(LyteFile self, LyteContext context) {
      context.push(self.isFile());
    }
  };


  public static LyteNativeBlock fileIsHidden = new LyteMemberBlock<LyteFile>("hidden?") {
    @Override
    public void invoke(LyteFile self, LyteContext context) {
      context.push(self.isHidden());
    }
  };

  public static LyteNativeBlock fileLastModified = new LyteMemberBlock<LyteFile>(LAST_MODIFIED) {
    @Override
    public void invoke(LyteFile self, LyteContext context) {
      context.push(self.lastModified());
    }
  };

  public static LyteNativeBlock fileLength = new LyteMemberBlock<LyteFile>("length") {
    @Override
    public void invoke(LyteFile self, LyteContext context) {
      context.push(self.length());
    }
  };

  public static LyteNativeBlock fileCreateNewFile = new LyteMemberBlock<LyteFile>("createNewFile") {

    @Override
    public void invoke(LyteFile self, LyteContext context) {
      try {
        context.push(self.createNewFile());
      } catch (IOException e) {
        throw new LyteError("Cannot write to " + self.getName() + ", " + e.getMessage());
      }
    }
  };

  public static LyteNativeBlock fileDelete = new LyteMemberBlock<LyteFile>("delete") {

    @Override
    public void invoke(LyteFile self, LyteContext context) {
      context.push(self.delete());
    }
  };

  public static LyteNativeBlock fileMkdir = new LyteMemberBlock<LyteFile>("mkdir") {

    @Override
    public void invoke(LyteFile self, LyteContext context) {
      context.push(self.mkdir());
    }
  };

  public static LyteNativeBlock fileMkdirs = new LyteMemberBlock<LyteFile>("mkdirs") {

    @Override
    public void invoke(LyteFile self, LyteContext context) {
      context.push(self.mkdirs());
    }
  };

  public static LyteNativeBlock fileList = new LyteMemberBlock<LyteFile>("list") {

    @Override
    public void invoke(LyteFile self, LyteContext context) {
      String[] files = self.list();
      if (files == null) {
        context.push(LyteUndefined.NULL);
      } else {
        LyteList list = new LyteList();
        for (String file : files) {
          list.add(new LyteString(file));
        }
        context.push(list);
      }
    }
  };

  public static LyteNativeBlock fileListFiles = new LyteMemberBlock<LyteFile>("listFiles") {

    @Override
    public void invoke(LyteFile self, LyteContext context) {
      File[] files = self.listFiles();
      if (files == null) {
        context.push(LyteUndefined.NULL);
      } else {
        LyteList list = new LyteList();
        for (File file : files) {
          list.add(new LyteFile(file));
        }
        context.push(list);
      }
    }
  };

  public static LyteNativeBlock fileName = new LyteMemberBlock<LyteFile>(NAME) {

    @Override
    public void invoke(LyteFile self, LyteContext context) {
      context.push(self.getName());
    }
  };

  public static LyteNativeBlock filePath = new LyteMemberBlock<LyteFile>("path") {

    @Override
    public void invoke(LyteFile self, LyteContext context) {
      context.push(self.getPath());
    }
  };

  public static LyteNativeBlock fileParent = new LyteMemberBlock<LyteFile>("parent") {

    @Override
    public void invoke(LyteFile self, LyteContext context) {
      String parent = self.getParent();
      if (parent == null) {
        context.push(LyteUndefined.NULL);
      } else {
        context.push(parent);
      }
    }
  };

  public static LyteNativeBlock fileAbsolutePath = new LyteMemberBlock<LyteFile>("absolutePath") {

    @Override
    public void invoke(LyteFile self, LyteContext context) {
      context.push(self.getAbsolutePath());
    }
  };

  public static LyteNativeBlock fileCanonicalPath = new LyteMemberBlock<LyteFile>("canonicalPath") {

    @Override
    public void invoke(LyteFile self, LyteContext context) {
      try {
        context.push(self.getCanonicalPath());
      } catch (IOException e) {
        throw new LyteError(e);
      }
    }
  };
}
