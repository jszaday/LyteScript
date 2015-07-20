package com.lyte.objs;

import com.lyte.core.LyteContext;
import com.lyte.stdlib.LyteFileMembers;
import org.json.simple.JSONValue;

import java.io.File;
import java.net.URI;
import java.util.Set;

/**
 * Created by a0225785 on 7/17/2015.
 */
public class LyteFile extends File implements LyteValue<File> {

  private static final LyteFileMembers FILE_MEMBERS = new LyteFileMembers();

  public LyteFile(String pathname) {
    super(pathname);
  }

  public LyteFile(File file) {
    super(file.getPath());
  }

  @Override
  public File get() {
    return this;
  }

  @Override
  public LyteValue getProperty(String property) {
    return FILE_MEMBERS.getProperty(property);
  }

  @Override
  public void setProperty(String property, LyteValue newValue) {
    boolean result;

    switch (property) {
      case LyteFileMembers.EXECUTABLE:
        result = setExecutable(newValue.toBoolean());
        break;
      case LyteFileMembers.READABLE:
        result = setReadable(newValue.toBoolean());
        break;
      case LyteFileMembers.WRITABLE:
        result = setWritable(newValue.toBoolean());
        break;
      case LyteFileMembers.READ_ONLY:
        result = setReadOnly();
        break;
      case LyteFileMembers.NAME:
        File file = newValue.is(typeOf()) ? (LyteFile) newValue : new File(newValue.toString());
        result = renameTo(file);
        break;
      case LyteFileMembers.LAST_MODIFIED:
        result = setLastModified((long) newValue.toNumber());
        break;
      default:
        throw new LyteError("Cannot set the property " + property + " of a " + typeOf());
    }

    if (!result) {
      throw new LyteError("Unable to set the value of " + property + " to " + newValue);
    }
  }

  @Override
  public boolean hasProperty(String property) {
    return FILE_MEMBERS.hasProperty(property);
  }

  @Override
  public boolean toBoolean() {
    return exists();
  }

  @Override
  public double toNumber() {
    return hashCode();
  }

  @Override
  public String toString() {
    return getPath();
  }

  @Override
  public LyteValue<File> clone(LyteContext context) {
    return new LyteFile(getPath());
  }

  @Override
  public boolean equals(LyteValue other) {
    if (other.is(typeOf())) {
      return equalsStrict(other);
    } else if (other.isSimpleComparison()) {
      return other.equals(this);
    } else {
      return false;
    }
  }

  @Override
  public boolean equalsStrict(LyteValue other) {
    return super.equals(other);
  }

  @Override
  public boolean isSimpleComparison() {
    return false;
  }

  @Override
  public Set<String> getProperties() {
    return FILE_MEMBERS.getProperties();
  }

  @Override
  public String toJSONString() {
    return JSONValue.toJSONString(this.getPath());
  }

  @Override
  public LyteValue apply(LyteContext context) {
    return this;
  }

  @Override
  public boolean is(String type) {
    return typeOf().equals(type);
  }

  @Override
  public String typeOf() {
    return "file";
  }
}
