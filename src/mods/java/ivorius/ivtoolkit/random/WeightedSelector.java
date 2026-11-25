package ivorius.ivtoolkit.random;

import com.google.common.collect.Collections2;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;
import java.util.function.DoubleBinaryOperator;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.ToDoubleFunction;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class WeightedSelector {
   public WeightedSelector() {
   }

   public static <T extends WeightedSelector.Item> double totalWeight(Collection<T> items) {
      return items.stream().mapToDouble(WeightedSelector.Item::getWeight).reduce(0.0D, (l, r) -> {
         return l + r;
      });
   }

   public static <T> double totalWeight(Collection<T> items, ToDoubleFunction<T> weightFunction) {
      return items.stream().mapToDouble(weightFunction).reduce(0.0D, (l, r) -> {
         return l + r;
      });
   }

   public static boolean canSelect(Collection<? extends WeightedSelector.Item> items) {
      return items.stream().anyMatch((item) -> {
         return item.getWeight() > 0.0D;
      });
   }

   public static <T> boolean canSelect(Collection<T> items, ToDoubleFunction<T> weightFunction) {
      return items.stream().anyMatch((item) -> {
         return weightFunction.applyAsDouble(item) > 0.0D;
      });
   }

   public static <T> T selectWeightless(EaglercraftRandom rand, Collection<T> items, int counted) {
      counted = rand.nextInt(counted);
      Iterator<T> iterator = items.iterator();

      T item;
      while(true) {
         item = (T)iterator.next();
         if (counted-- == 0 || !iterator.hasNext()) {
            break;
         }
      }

      return item;
   }

   public static <T extends WeightedSelector.Item> T selectItem(EaglercraftRandom rand, Collection<T> items) {
      return (T)selectItem(rand, items, totalWeight(items));
   }

   public static <T extends WeightedSelector.Item> T selectItem(EaglercraftRandom rand, Collection<T> items, double totalWeight) {
      return (T)selectItem(rand, items, totalWeight, false);
   }

   public static <T extends WeightedSelector.Item> T selectItem(EaglercraftRandom rand, Collection<T> items, boolean remove) {
      return (T)selectItem(rand, items, totalWeight(items), remove);
   }

   public static <T extends WeightedSelector.Item> T selectItem(EaglercraftRandom rand, Collection<T> items, double totalWeight, boolean remove) {
      if (items.size() == 0) {
         throw new IndexOutOfBoundsException();
      } else {
         double random = rand.nextDouble() * totalWeight;
         int counted = 0;

         for(Iterator<T> iterator = items.iterator(); iterator.hasNext(); ++counted) {
            T t = iterator.next();
            random -= t.getWeight();
            if (random <= 0.0D) {
               if (remove) {
                  iterator.remove();
               }

               return t;
            }
         }

         return (T)(selectWeightless(rand, items, counted));
      }
   }

   public static <T> T select(EaglercraftRandom rand, Collection<T> items, ToDoubleFunction<T> weightFunction) {
      return (T)select(rand, items, weightFunction, totalWeight(items, weightFunction));
   }

   public static <T> T select(EaglercraftRandom rand, Collection<T> items, ToDoubleFunction<T> weightFunction, double totalWeight) {
      return (T)select(rand, items, weightFunction, totalWeight, false);
   }

   public static <T> T select(EaglercraftRandom rand, Collection<T> items, ToDoubleFunction<T> weightFunction, boolean remove) {
      return (T)select(rand, items, weightFunction, totalWeight(items, weightFunction), remove);
   }

   public static <T> T select(EaglercraftRandom rand, Collection<T> items, ToDoubleFunction<T> weightFunction, double totalWeight, boolean remove) {
      if (items.size() == 0) {
         throw new IndexOutOfBoundsException();
      } else {
         double random = rand.nextDouble() * totalWeight;
         int counted = 0;

         for(Iterator<T> iterator = items.iterator(); iterator.hasNext(); ++counted) {
            T t = (T)iterator.next();
            random -= weightFunction.applyAsDouble(t);
            if (random <= 0.0D) {
               if (remove) {
                  iterator.remove();
               }

               return t;
            }
         }

         return (T)selectWeightless(rand, items, counted);
      }
   }

   public static <T> T select(EaglercraftRandom rand, Collection<WeightedSelector.SimpleItem<T>> items) {
      return (T)((WeightedSelector.SimpleItem)selectItem(rand, items)).getItem();
   }

   public static <T> T select(EaglercraftRandom rand, Collection<WeightedSelector.SimpleItem<T>> items, double totalWeight) {
      return (T)((WeightedSelector.SimpleItem)selectItem(rand, items, totalWeight)).getItem();
   }

   public static <T> T select(EaglercraftRandom rand, Collection<WeightedSelector.SimpleItem<T>> items, boolean remove) {
      return (T)((WeightedSelector.SimpleItem)selectItem(rand, items, remove)).getItem();
   }

   public static <T> T select(EaglercraftRandom rand, Collection<WeightedSelector.SimpleItem<T>> items, double totalWeight, boolean remove) {
      return (T)((WeightedSelector.SimpleItem)selectItem(rand, items, totalWeight, remove)).getItem();
   }

   public interface Item {
      double getWeight();
   }

   public static class ItemComparator implements Comparator<WeightedSelector.Item> {
      public ItemComparator() {
      }

      public int compare(WeightedSelector.Item o1, WeightedSelector.Item o2) {
         return Double.compare(o1.getWeight(), o2.getWeight());
      }
   }

   public static class SimpleItem<T> implements WeightedSelector.Item, Comparable<WeightedSelector.Item> {
      protected final double weight;
      protected final T item;

      public SimpleItem(double weight, T item) {
         this.item = item;
         this.weight = weight;
      }

      public static <T> WeightedSelector.SimpleItem<T> of(double weight, T item) {
         return new WeightedSelector.SimpleItem<T>(weight, item);
      }

      public static <T> List<WeightedSelector.SimpleItem<T>> apply(Stream<T> items, ToDoubleFunction<T> weightFunction) {
         return (List)items.map((input) -> {
            return new WeightedSelector.SimpleItem<WeightedSelector.SimpleItem<T>>(weightFunction.applyAsDouble(input), input);
         }).collect(Collectors.toList());
      }

      public static <T> Collection<WeightedSelector.SimpleItem<T>> apply(Collection<T> items, ToDoubleFunction<T> weightFunction) {
         return Collections2.transform(items, (item) -> {
            return new WeightedSelector.SimpleItem<WeightedSelector.SimpleItem<T>>(weightFunction.applyAsDouble(item), item);
         });
      }

      public static <T> List<WeightedSelector.SimpleItem<T>> apply(List<T> items, ToDoubleFunction<T> weightFunction) {
         return apply(items.stream(), weightFunction);
      }

      public T getItem() {
         return this.item;
      }

      public double getWeight() {
         return this.weight;
      }

      public boolean equals(Object o) {
         if (this == o) {
            return true;
         } else if (o != null && this.getClass() == o.getClass()) {
            WeightedSelector.SimpleItem that = (WeightedSelector.SimpleItem)o;
            if (Double.compare(that.weight, this.weight) != 0) {
               return false;
            } else {
               if (this.item != null) {
                  if (!this.item.equals(that.item)) {
                     return false;
                  }
               } else if (that.item != null) {
                  return false;
               }

               return true;
            }
         } else {
            return false;
         }
      }

      public int hashCode() {
         long temp = Double.doubleToLongBits(this.weight);
         int result = (int)(temp ^ temp >>> 32);
         result = 31 * result + (this.item != null ? this.item.hashCode() : 0);
         return result;
      }

      public String toString() {
         return "SimpleItem{weight=" + this.weight + ", item=" + this.item + '}';
      }

      public int compareTo(WeightedSelector.Item o) {
         return Double.compare(this.weight, o.getWeight());
      }
   }
}
