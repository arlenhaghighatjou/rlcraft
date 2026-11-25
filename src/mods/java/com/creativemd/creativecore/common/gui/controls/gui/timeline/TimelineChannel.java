package com.creativemd.creativecore.common.gui.controls.gui.timeline;

import com.creativemd.creativecore.common.utils.type.Pair;
import com.creativemd.creativecore.common.utils.type.PairList;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class TimelineChannel<T> {
   public int index;
   public String name;
   public List<KeyControl<T>> controls = new ArrayList();

   public TimelineChannel(String name) {
      this.name = name;
   }

   public TimelineChannel addKeys(PairList<Integer, T> keys) {
      if (keys != null && !keys.isEmpty()) {
         for(Pair<Integer, T> pair : keys) {
            this.addKey(((Integer)pair.key).intValue(), pair.value);
         }

         return this;
      } else {
         this.addKey(0, this.getDefault());
         return this;
      }
   }

   public TimelineChannel<T> addKeyFixed(int tick, T value) {
      KeyControl control = this.addKey(tick, value);
      control.modifiable = false;
      return this;
   }

   public KeyControl addKey(int tick, T value) {
      KeyControl control = new KeyControl(this, this.controls.size(), tick, value);

      for(int i = 0; i < this.controls.size(); ++i) {
         KeyControl other = (KeyControl)this.controls.get(i);
         if (other.tick == tick) {
            return null;
         }

         if (other.tick > tick) {
            this.controls.add(i, control);
            return control;
         }
      }

      this.controls.add(control);
      return control;
   }

   public void removeKey(KeyControl control) {
      this.controls.remove(control);
   }

   public void movedKey(KeyControl control) {
      Collections.sort(this.controls);
   }

   public boolean isSpaceFor(KeyControl control, int tick) {
      for(int i = 0; i < this.controls.size(); ++i) {
         int otherTick = ((KeyControl)this.controls.get(i)).tick;
         if (otherTick == tick) {
            return false;
         }

         if (otherTick > tick) {
            return true;
         }
      }

      return true;
   }

   public T getValueAt(int tick) {
      if (this.controls.isEmpty()) {
         return (T)this.getDefault();
      } else {
         int higher = this.controls.size();

         for(int i = 0; i < this.controls.size(); ++i) {
            int otherTick = ((KeyControl)this.controls.get(i)).tick;
            if (otherTick == tick) {
               return ((KeyControl)this.controls.get(i)).value;
            }

            if (otherTick > tick) {
               higher = i;
               break;
            }
         }

         if (higher != 0 && higher != this.controls.size()) {
            KeyControl<T> before = (KeyControl)this.controls.get(higher - 1);
            KeyControl<T> after = (KeyControl)this.controls.get(higher);
            double percentage = (double)(tick - before.tick) / (double)(after.tick - before.tick);
            return (T)this.getValueAt(before, after, percentage);
         } else {
            return ((KeyControl)this.controls.get(higher == 0 ? 0 : this.controls.size() - 1)).value;
         }
      }
   }

   protected abstract T getValueAt(KeyControl<T> var1, KeyControl<T> var2, double var3);

   protected abstract T getDefault();

   public PairList<Integer, T> getPairs() {
      if (this.controls.isEmpty()) {
         return null;
      } else {
         boolean fixed = true;
         PairList<Integer, T> list = new PairList<Integer, T>();

         for(KeyControl<T> control : this.controls) {
            if (control.modifiable) {
               fixed = false;
            }

            list.add(Integer.valueOf(control.tick), control.value);
         }

         if (fixed) {
            return null;
         } else {
            return list;
         }
      }
   }

   public static class TimelineChannelDouble extends TimelineChannel<Double> {
      public TimelineChannelDouble(String name) {
         super(name);
      }

      protected Double getValueAt(KeyControl<Double> before, KeyControl<Double> after, double percentage) {
         return (double)Math.round(((((Double)after.value).doubleValue() - ((Double)before.value).doubleValue()) * percentage + ((Double)before.value).doubleValue()) * 100.0D) / 100.0D;
      }

      protected Double getDefault() {
         return 0.0D;
      }
   }

   public static class TimelineChannelInteger extends TimelineChannel<Integer> {
      public TimelineChannelInteger(String name) {
         super(name);
      }

      protected Integer getValueAt(KeyControl<Integer> before, KeyControl<Integer> after, double percentage) {
         int dif = ((Integer)after.value).intValue() - ((Integer)before.value).intValue();
         double current = (double)dif * percentage + (double)((Integer)before.value).intValue();
         return (int)current;
      }

      protected Integer getDefault() {
         return Integer.valueOf(0);
      }
   }
}
