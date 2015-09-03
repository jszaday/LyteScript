package com.lyte.stdlib;

import com.lyte.core.LyteContext;
import com.lyte.objs.*;
import com.lyte.utils.LyteMemberBlock;
import com.lyte.utils.LyteSimpleInjectable;

import java.util.*;

/**
 * Created by jszaday on 6/29/2015.
 */
public class LyteListMembers extends LyteSimpleInjectable {
  public static LyteNativeBlock listAdd = new LyteMemberBlock<LyteList>("add") {
    @Override
    public void invoke(LyteList self, LyteContext context) {
      self.add(context.apply());
    }
  };
  public static LyteNativeBlock listAddAll = new LyteMemberBlock<LyteList>("addAll") {
    @Override
    public void invoke(LyteList self, LyteContext context) {
      LyteValue other = context.apply();
      if (other.is("list")) {
        self.addAll((LyteList) other);
      } else {
        throw new LyteError("Cannot add all elements of a(n) " + other.typeOf() + " to a List!");
      }
    }
  };
  public static LyteNativeBlock listConcat = new LyteMemberBlock<LyteList>("concat") {
    @Override
    public void invoke(LyteList self, LyteContext context) {
      LyteValue other = context.apply();
      if (other.is("list")) {
        context.push(new LyteList(self, (LyteList) other));
      } else {
        throw new LyteError("Cannot concatenate a(n) " + other.typeOf() + " and a List!");
      }
    }
  };
  public static LyteNativeBlock listPush = new LyteMemberBlock<LyteList>("push") {
    @Override
    public void invoke(LyteList self, LyteContext context) {
      self.push(context.apply());
    }
  };
  public static LyteNativeBlock listPop = new LyteMemberBlock<LyteList>("pop") {
    @Override
    public void invoke(LyteList self, LyteContext context) {
      context.push(self.pop());
    }
  };
  public static LyteNativeBlock listPoll = new LyteMemberBlock<LyteList>("poll") {
    @Override
    public void invoke(LyteList self, LyteContext context) {
      context.push(self.poll());
    }
  };
  public static LyteNativeBlock listPeek = new LyteMemberBlock<LyteList>("peek") {
    @Override
    public void invoke(LyteList self, LyteContext context) {
      context.push(self.peek());
    }
  };
  public static LyteNativeBlock listLength = new LyteMemberBlock<LyteList>("length") {
    @Override
    public void invoke(LyteList self, LyteContext context) {
      context.push(self.size());
    }
  };
  public static LyteNativeBlock listIsEmpty = new LyteMemberBlock<LyteList>("empty?") {
    @Override
    public void invoke(LyteList self, LyteContext context) {
      context.push(self.isEmpty());
    }
  };
  public static LyteNativeBlock listGenerator = new LyteMemberBlock<LyteList>("__generator") {
    @Override
    public void invoke(LyteList self, LyteContext context) {
      for (LyteValue value : self) {
        context.yield(value);
      }
    }
  };
  public static LyteNativeBlock listReverse = new LyteMemberBlock<LyteList>("reverse") {
    @Override
    public void invoke(LyteList self, LyteContext context) {
      Collections.reverse(self);
    }
  };
  public static LyteNativeBlock listRemove = new LyteMemberBlock<LyteList>("remove") {
    @Override
    public void invoke(LyteList self, LyteContext context) {
      self.remove((int) context.apply().toNumber());
    }
  };
  public static LyteNativeBlock listSwap = new LyteMemberBlock<LyteList>("swap") {
    @Override
    public void invoke(LyteList self, LyteContext context) {
      int i = (int) context.apply().toNumber();
      int j = (int) context.apply().toNumber();
      Collections.swap(self, i, j);
    }
  };
  public static LyteNativeBlock listSlice = new LyteMemberBlock<LyteList>("slice") {
    @Override
    public void invoke(LyteList self, LyteContext context) {
      int i = (int) context.apply().toNumber();
      int j = (int) context.apply().toNumber();

      if (i < 0) {
        i += self.size();
      }

      if (j < 0) {
        j += self.size();
      }

      context.push(new LyteList(self.subList(i, j)));
    }
  };
  public static LyteNativeBlock listSearch = new LyteMemberBlock<LyteList>("search") {
    @Override
    public void invoke(LyteList self, LyteContext context) {
      context.push(self.indexOf(context.apply()));
    }
  };
  public static LyteNativeBlock listContains = new LyteMemberBlock<LyteList>("contains") {
    @Override
    public void invoke(LyteList self, LyteContext context) {
      context.push(self.contains(context.apply()));
    }
  };
  public static LyteNativeBlock listJoin = new LyteMemberBlock<LyteList>("join") {
    @Override
    public void invoke(LyteList self, LyteContext context) {
      StringBuilder builder = new StringBuilder();
      Iterator<LyteValue> iterator = self.iterator();
      String seperator = context.apply().toString();

      while(iterator.hasNext()) {
        builder.append(iterator.next());

        if (iterator.hasNext()) {
          builder.append(seperator);
        }
      }

      context.push(builder.toString());
    }
  };
  public static LyteNativeBlock listFilter = new LyteMemberBlock<LyteList>("filter") {

    @Override
    public void invoke(LyteList self, LyteContext context) {
      LyteList filteredList = new LyteList();
      LyteValue condition = context.pop();

      if (!condition.is("block")) {
        throw new LyteError("Filter expected a block not a " + condition.typeOf());
      }

      for (LyteValue value : self) {
        context.push(value);

        if (condition.apply(context).toBoolean()) {
          filteredList.add(value);
        }
      }

      context.push(filteredList);
    }
  };

  public static LyteNativeBlock listMap = new LyteMemberBlock<LyteList>("map") {

    @Override
    public void invoke(LyteList self, LyteContext context) {
      LyteList mappedList = new LyteList();
      LyteValue function = context.pop();

      if (!function.is("block")) {
        throw new LyteError("Map expected a block not a " + function.typeOf());
      }

      for (LyteValue value : self) {
        context.push(value);
        mappedList.add(function.apply(context));
      }

      context.push(mappedList);
    }
  };

  public static LyteNativeBlock listSort = new LyteMemberBlock<LyteList>("sort") {

    @Override
    public void invoke(LyteList self, LyteContext context) {
      LyteList sortedList = new LyteList(self);
      Collections.sort(sortedList, new Comparator<LyteValue>() {
        @Override
        public int compare(LyteValue o1, LyteValue o2) {
          if (!(o1 instanceof LyteComparable && o2 instanceof LyteComparable)) {
            throw new LyteError("Cannot compare a(n) " + o1.typeOf() + " and a(n) " + o2.typeOf());
          } else if (o2.is("object")) {
            return -1 * ((LyteComparable) o2).compareTo(o1);
          } else {
            return ((LyteComparable) o1).compareTo(o2);
          }
        }
      });
      context.push(sortedList);
    }
  };

  public static LyteNativeBlock listUsort = new LyteMemberBlock<LyteList>("usort") {

    @Override
    public void invoke(LyteList self, final LyteContext context) {
      final LyteValue sortFunction = context.pop();
      LyteList sortedList = new LyteList(self);

      if (!sortFunction.is("block")) {
        throw new LyteError("Expected sort function to be a block, not a(n) " + sortFunction.typeOf());
      }

      Collections.sort(sortedList, new Comparator<LyteValue>() {
        @Override
        public int compare(LyteValue o1, LyteValue o2) {
          context.push(o2);
          context.push(o1);
          return (int) sortFunction.apply(context).toNumber();
        }
      });

      context.push(sortedList);
    }
  };
}
