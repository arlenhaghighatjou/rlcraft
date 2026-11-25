package org.orecruncher.lib;

import java.util.Random;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.orecruncher.lib.collections.ObjectArray;
import org.orecruncher.lib.random.XorShiftRandom;

public class WeightTable<T> extends ObjectArray<WeightTable.IItem<? extends T>> {
   protected static final Random RANDOM = XorShiftRandom.current();
   protected int totalWeight = 0;

   public WeightTable() {
   }

   @SafeVarargs
   public WeightTable(@Nonnull WeightTable.IEntrySource<? extends T>... src) {
      super(src.length);

      for(int i = 0; i < src.length; ++i) {
         if (src[i].matches()) {
            this.add(src[i].getEntry());
         }
      }

   }

   public boolean add(@Nonnull final T e, final int weight) {
      return this.add(new WeightTable.IItem<T>() {
         public int getWeight() {
            return weight;
         }

         public T getItem() {
            return e;
         }
      });
   }

   public boolean add(@Nonnull WeightTable.IItem<? extends T> entry) {
      this.totalWeight += entry.getWeight();
      return super.add(entry);
   }

   @Nullable
   public T next() {
      if (this.totalWeight <= 0) {
         return (T)null;
      } else {
         int targetWeight = RANDOM.nextInt(this.totalWeight);
         WeightTable.IItem<T> selected = null;
         int i = -1;

         while(true) {
            ++i;
            selected = (WeightTable.IItem)this.data[i];
            targetWeight -= selected.getWeight();
            if (targetWeight < 0) {
               break;
            }
         }

         return selected.getItem();
      }
   }

   public interface IEntrySource<T> {
      WeightTable.IItem<T> getEntry();

      boolean matches();
   }

   public interface IItem<T> {
      int getWeight();

      T getItem();
   }
}
