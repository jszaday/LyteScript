package com.lyte.objs;

import com.lyte.core.LyteContext;
import com.lyte.stdlib.LyteStreamFunctions;

import java.io.*;
import java.util.Set;

/**
 * Created by a0225785 on 7/1/2015.
 */
public class LyteStream implements LyteValue<Closeable>, Closeable {

  private static LyteStreamFunctions streamFunctions = new LyteStreamFunctions();

  private boolean mReadable;

  private BufferedReader mReader;
  private BufferedWriter mWriter;

  public LyteStream(InputStream inputStream) {
    this(new InputStreamReader(inputStream));
  }

  public LyteStream(Reader reader) {
    mReader = new BufferedReader(reader);
    mReadable = true;
  }

  public LyteStream(OutputStream outputStream) {
    this(new OutputStreamWriter(outputStream));
  }

  public LyteStream(Writer writer) {
    mWriter = new BufferedWriter(writer);
    mReadable = false;
  }

  public boolean isReadable() {
    return mReadable;
  }

  @Override
  public Closeable get() {
    if (mReadable) {
      return mReader;
    } else {
      return mWriter;
    }
  }

  public int read() {
    if (mReadable) {
      try {
        return mReader.read();
      } catch (IOException e) {
        throw new LyteError(e.getMessage());
      }
    } else {
      throw new LyteError("Cannot read from an output stream!");
    }
  }

  public String readLine() {
    if (mReadable) {
      try {
        return mReader.readLine();
      } catch (IOException e) {
        throw new LyteError(e.getMessage());
      }
    } else {
      throw new LyteError("Cannot read from an output stream!");
    }
  }

  public void write(int c) {
    if (!mReadable) {
      try {
        mWriter.write(c);
      } catch (IOException e) {
        throw new LyteError(e.getMessage());
      }
    } else {
      throw new LyteError("Cannot write to an input stream!");
    }
  }

  public void write(String text) {
    if (!mReadable) {
      try {
        mWriter.write(text);
      } catch (IOException e) {
        throw new LyteError(e.getMessage());
      }
    } else {
      throw new LyteError("Cannot write to an input stream!");
    }
  }

  public void writeLine(String text) {
    if (!mReadable) {
      try {
        mWriter.write(text);
        mWriter.newLine();
      } catch (IOException e) {
        throw new LyteError(e.getMessage());
      }
    } else {
      throw new LyteError("Cannot write to an input stream!");
    }
  }

  public void flush() {
    if (!mReadable) {
      try {
        mWriter.flush();
      } catch (IOException e) {
        throw new LyteError(e.getMessage());
      }
    } else {
      throw new LyteError("Cannot flush an input stream!");
    }
  }

  @Override
  public LyteValue getProperty(String property) {
    return streamFunctions.getProperty(property);
  }

  @Override
  public void setProperty(String property, LyteValue newValue) {
    throw new LyteError("Cannot set the property " + property + " of a stream!");
  }

  @Override
  public boolean hasProperty(String property) {
    return streamFunctions.hasProperty(property);
  }

  @Override
  public boolean toBoolean() {
    return true;
  }

  @Override
  public double toNumber() {
    return hashCode();
  }

  @Override
  public String toString() {
    return mReadable ? "InputStream" : "OutputStream";
  }

  @Override
  public String typeOf() {
    return "stream";
  }

  @Override
  public LyteValue<Closeable> clone(LyteContext context) {
    return this;
  }

  @Override
  public LyteValue apply(LyteContext context) {
    return this;
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
    return this == other;
  }

  @Override
  public boolean isSimpleComparison() {
    return false;
  }

  @Override
  public boolean is(String type) {
    return typeOf().equals(type);
  }

  @Override
  public Set<String> getProperties() {
    return streamFunctions.getProperties();
  }

  @Override
  public void close() throws IOException {
    get().close();
  }

  @Override
  public String toJSONString() {
    throw new LyteError("Cannot encode a stream as JSON!");
  }

  @Override
  public LyteBlock generator() {
    throw new LyteError("Cannot iterate over a(n) " + typeOf());
  }
}
