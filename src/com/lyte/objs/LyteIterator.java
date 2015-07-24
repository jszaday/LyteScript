package com.lyte.objs;

import com.lyte.stdlib.LyteIteratorMembers;

import java.util.Iterator;
import java.util.Set;

/**
 * Created by a0225785 on 7/24/2015.
 */
public class LyteIterator extends LytePrimitive<Iterator<LyteValue>> implements Iterator<LyteValue>, LyteIterable {

  private static final LyteIteratorMembers ITERATOR_MEMBERS = new LyteIteratorMembers();

  public LyteIterator(LyteIterable value) {
    super(value.iterator());
  }

  @Override
  public Set<String> getProperties() {
    return ITERATOR_MEMBERS.getProperties();
  }

  @Override
  public LyteValue getProperty(String property) {
    return ITERATOR_MEMBERS.getProperty(property);
  }

  @Override
  public boolean toBoolean() {
    return hasNext();
  }

  @Override
  public double toNumber() {
    throw new LyteError("Cannot convert a(n) " + typeOf() + " to a number");
  }

  @Override
  public String toString() {
    return typeOf();
  }

  @Override
  public String toJSONString() {
    throw new LyteError("Cannot encode a(n) " + typeOf() + " as a JSON string");
  }

  @Override
  public boolean hasNext() {
    return get().hasNext();
  }

  @Override
  public LyteValue next() {
    return get().next();
  }

  @Override
  public void remove() {
    get().remove();
  }

  @Override
  public LyteBlock generator() {
    return (LyteBlock) ITERATOR_MEMBERS.get("__generator");
  }

  @Override
  public Iterator<LyteValue> iterator() {
    return this;
  }
}
