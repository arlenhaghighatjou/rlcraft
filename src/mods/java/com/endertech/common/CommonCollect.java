package com.endertech.common;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import javax.annotation.Nullable;

public final class CommonCollect {
   public CommonCollect() {
   }

   @Nullable
   public static <T> T getRandomElementFrom(Collection<T> collection) {
      if (collection.isEmpty()) {
         return (T)null;
      } else {
         T[] elements = (T[])(collection.toArray());
         int index = CommonMath.Random.between(0, elements.length - 1);
         return (T)elements[index];
      }
   }

   public static class BlackWhiteList<E> {
      protected final List<E> blackList;
      protected final List<E> whiteList;

      public BlackWhiteList(List<E> black, List<E> white) {
         this.blackList = black;
         this.whiteList = white;
      }

      public boolean isAllowed(E element) {
         boolean valid = false;
         if (this.whiteList.isEmpty()) {
            valid = !this.blackList.contains(element);
         } else {
            valid = this.whiteList.contains(element);
         }

         return valid;
      }

      public boolean isEmpty() {
         return this.blackList.isEmpty() && this.whiteList.isEmpty();
      }
   }

   public interface IWeighted {
      int getWeight();
   }

   public static class WeightedList<T extends CommonCollect.IWeighted> {
      protected List<T> list = new ArrayList();
      protected long totalWeight = 0L;

      public WeightedList() {
      }

      @SafeVarargs
      public static <T extends CommonCollect.IWeighted> CommonCollect.WeightedList<T> withElements(T... elements) {
         CommonCollect.WeightedList<T> list = new CommonCollect.WeightedList<T>();

         for(T element : elements) {
            list.add(element);
         }

         return list;
      }

      public long getTotalWeight() {
         return this.totalWeight;
      }

      public void clear() {
         this.list.clear();
         this.totalWeight = 0L;
      }

      public boolean add(T element) {
         boolean result = this.list.add(element);
         if (result) {
            this.totalWeight += (long)element.getWeight();
         }

         return result;
      }

      public boolean remove(T element) {
         boolean result = this.list.remove(element);
         if (result) {
            this.totalWeight -= (long)element.getWeight();
         }

         return result;
      }

      public boolean contains(T element) {
         return this.list.contains(element);
      }

      public List<T> getElements() {
         return Collections.unmodifiableList(this.list);
      }

      public boolean isEmpty() {
         return this.list.isEmpty();
      }

      @Nullable
      public T getRandom() {
         if (!this.list.isEmpty() && this.getTotalWeight() > 0L) {
            long weight = CommonMath.getFloor(CommonMath.Random.getDouble() * (double)this.getTotalWeight());

            for(T elem : this.list) {
               weight -= (long)elem.getWeight();
               if (weight < 0L) {
                  return elem;
               }
            }

            return (T)null;
         } else {
            return (T)null;
         }
      }
   }
}
