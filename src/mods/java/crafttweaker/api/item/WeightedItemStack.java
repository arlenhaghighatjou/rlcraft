package crafttweaker.api.item;

import crafttweaker.annotations.ZenRegister;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenGetter;

@ZenClass("crafttweaker.item.WeightedItemStack")
@ZenRegister
public final class WeightedItemStack implements IWeightedIngredient {
   private final IItemStack stack;
   private final float p;

   public WeightedItemStack(IItemStack stack, float p) {
      this.stack = stack;
      this.p = p;
   }

   public static List<IItemStack> pickRandomDrops(Random random, WeightedItemStack[] items) {
      ArrayList<IItemStack> result = new ArrayList();

      for(WeightedItemStack item : items) {
         if (random.nextFloat() <= item.getChance()) {
            result.add(item.getStack());
         }
      }

      return result;
   }

   @ZenGetter("ingredient")
   public IIngredient getIngredient() {
      return this.stack;
   }

   @ZenGetter("stack")
   public IItemStack getStack() {
      return this.stack;
   }

   @ZenGetter("chance")
   public float getChance() {
      return this.p;
   }

   @ZenGetter("percent")
   public float getPercent() {
      return this.p * 100.0F;
   }

   public int hashCode() {
      int hash = 7;
      hash = 29 * hash + (this.stack != null ? this.stack.hashCode() : 0);
      hash = 29 * hash + Float.floatToIntBits(this.p);
      return hash;
   }

   public boolean equals(Object obj) {
      if (obj == null) {
         return false;
      } else if (this.getClass() != obj.getClass()) {
         return false;
      } else {
         WeightedItemStack other = (WeightedItemStack)obj;
         return (this.stack == other.stack || this.stack != null && this.stack.equals(other.stack)) && Float.floatToIntBits(this.p) == Float.floatToIntBits(other.p);
      }
   }
}
