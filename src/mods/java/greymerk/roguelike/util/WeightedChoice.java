package greymerk.roguelike.util;

import java.util.Objects;
import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;

public class WeightedChoice<T> implements IWeighted<T> {
   private final T item;
   private final int weight;

   public WeightedChoice(T toAdd, int weight) {
      this.item = toAdd;
      this.weight = weight;
   }

   public int getWeight() {
      return this.weight;
   }

   public T get(EaglercraftRandom rand) {
      return this.item;
   }

   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (o != null && this.getClass() == o.getClass()) {
         WeightedChoice<?> that = (WeightedChoice)o;
         return this.weight == that.weight && Objects.equals(this.item, that.item);
      } else {
         return false;
      }
   }

   public int hashCode() {
      return Objects.hash(new Object[]{this.item, this.weight});
   }

   public String toString() {
      return "WeightedChoice(item=" + this.item + ", weight=" + this.getWeight() + ")";
   }
}
