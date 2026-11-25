package ivorius.ivtoolkit.random;

import java.util.Iterator;
import java.util.List;
import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;
import java.util.function.ToDoubleFunction;

public class WeightedShuffler {
   public WeightedShuffler() {
   }

   public static <T> Iterator<T> iterateShuffled(final EaglercraftRandom rand, final List<T> items, final ToDoubleFunction<T> weightFunction) {
      return new Iterator<T>() {
         double totalWeight = WeightedSelector.totalWeight(items, weightFunction);

         public boolean hasNext() {
            return items.size() > 0;
         }

         public T next() {
            T next = (T)(this.totalWeight > 1.0E-7D ? WeightedSelector.select(rand, items, weightFunction, this.totalWeight, true) : items.remove(rand.nextInt(items.size())));
            this.totalWeight -= weightFunction.applyAsDouble(next);
            return next;
         }
      };
   }

   public static <T> Iterator<T> iterateShuffled(EaglercraftRandom rand, List<WeightedSelector.SimpleItem<T>> items) {
      final Iterator<WeightedSelector.SimpleItem<T>> iterator = iterateShuffled(rand, items, WeightedSelector.Item::getWeight);
      return new Iterator<T>() {
         public boolean hasNext() {
            return iterator.hasNext();
         }

         public T next() {
            return (T)((WeightedSelector.SimpleItem)iterator.next()).getItem();
         }
      };
   }

   public static <T extends WeightedSelector.Item> Iterator<T> iterateShuffledItems(EaglercraftRandom rand, List<T> items) {
      return iterateShuffled(rand, items, WeightedSelector.Item::getWeight);
   }

   public static <T extends WeightedSelector.Item> void shuffleItems(EaglercraftRandom rand, List<T> items) {
      shuffleItems(rand, items, WeightedSelector.totalWeight(items));
   }

   public static <T extends WeightedSelector.Item> void shuffleItems(EaglercraftRandom rand, List<T> items, double totalWeight) {
      int size = items.size();

      for(int shuffled = 0; shuffled < size - 1; ++shuffled) {
         List<T> subList = items.subList(shuffled, size);
         T selected = totalWeight > 0.0D ? WeightedSelector.selectItem(rand, subList, totalWeight, true) : (WeightedSelector.Item)subList.get(rand.nextInt(subList.size()));
         totalWeight -= selected.getWeight();
         subList.add(0, selected);
      }

   }

   public static <T> void shuffle(EaglercraftRandom rand, List<T> items, ToDoubleFunction<T> weightFunction) {
      shuffle(rand, items, weightFunction, WeightedSelector.totalWeight(items, weightFunction));
   }

   public static <T> void shuffle(EaglercraftRandom rand, List<T> items, ToDoubleFunction<T> weightFunction, double totalWeight) {
      int size = items.size();

      for(int shuffled = 0; shuffled < size - 1; ++shuffled) {
         List<T> subList = items.subList(shuffled, size);
         T selected = (T)(totalWeight > 0.0D ? WeightedSelector.select(rand, subList, weightFunction, totalWeight, true) : subList.get(rand.nextInt(subList.size())));
         totalWeight -= weightFunction.applyAsDouble(selected);
         subList.add(0, selected);
      }

   }
}
