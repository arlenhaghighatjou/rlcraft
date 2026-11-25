package crafttweaker.api.liquid;

import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.item.IIngredient;
import crafttweaker.api.item.IWeightedIngredient;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenGetter;

@ZenClass("crafttweaker.item.WeightedLiquidStack")
@ZenRegister
public final class WeightedLiquidStack implements IWeightedIngredient {
   private final ILiquidStack stack;
   private final float p;

   public WeightedLiquidStack(ILiquidStack stack, float p) {
      this.stack = stack;
      this.p = p;
   }

   public static List<ILiquidStack> pickRandomDrops(Random random, WeightedLiquidStack[] items) {
      ArrayList<ILiquidStack> result = new ArrayList();

      for(WeightedLiquidStack item : items) {
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
   public ILiquidStack getStack() {
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
      int hash = 17;
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
         WeightedLiquidStack other = (WeightedLiquidStack)obj;
         return (this.stack == other.stack || this.stack != null && this.stack.equals(other.stack)) && Float.floatToIntBits(this.p) == Float.floatToIntBits(other.p);
      }
   }
}
