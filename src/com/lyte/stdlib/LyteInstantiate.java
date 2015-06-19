package com.lyte.stdlib;

import com.lyte.core.LyteScope;
import com.lyte.objs.LyteBlock;
import com.lyte.objs.LyteObject;
import com.lyte.objs.LyteValue;

/**
 * Created by a0225785 on 6/19/2015.
 */
public class LyteInstantiate extends LyteNativeBlock {

  public LyteInstantiate(LyteScope parentScope) {
    super(parentScope);
  }

  @Override
  public String getSymbol() {
    return "Instantiate";
  }

  @Override
  public void invoke() {
    // TODO this assumes the two things are on the same stack, we have to move scoping out of blocks!!!
    LyteScope scope = mScope;

    LyteValue value = scope.pop();
    if (value.typeOf().equals("block")) {
      ((LyteBlock) value).invoke();
      value = scope.pop();
    }
    if (!value.typeOf().equals("object")) {
      System.err.println("Cannot Instantiate a(n) " + value.typeOf() + ".");
      return;
    }
    if (!((LyteObject) value).containsKey("__constructor")) {
      System.err.println("Error, object has no constructor!");
    }
    LyteObject obj = (LyteObject) value.clone(scope);
    ((LyteBlock) obj.get("__constructor")).invoke();
    scope.push(obj);
  }
}
