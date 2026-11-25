package ivorius.reccomplex.utils;

import net.minecraft.util.WeightedRandom.Item;

public class WeightedObject<V> extends Item {
   public V value;

   public WeightedObject(int weight, V value) {
      super(weight);
      this.value = value;
   }
}
