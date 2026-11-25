package crafttweaker.api.oredict;

import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.item.IIngredient;
import crafttweaker.api.item.IWeightedIngredient;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenGetter;

@ZenClass("crafttweaker.oredict.WeightedOreDictEntry")
@ZenRegister
public class WeightedOreDictEntry implements IWeightedIngredient {
   private final IOreDictEntry entry;
   private final float p;

   public WeightedOreDictEntry(IOreDictEntry entry, float p) {
      this.entry = entry;
      this.p = p;
   }

   @ZenGetter("entry")
   public IOreDictEntry getEntry() {
      return this.entry;
   }

   @ZenGetter("ingredient")
   public IIngredient getIngredient() {
      return this.entry;
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
      hash = 29 * hash + (this.entry != null ? this.entry.hashCode() : 0);
      hash = 29 * hash + Float.floatToIntBits(this.p);
      return hash;
   }

   public boolean equals(Object obj) {
      if (obj == null) {
         return false;
      } else if (this.getClass() != obj.getClass()) {
         return false;
      } else {
         WeightedOreDictEntry other = (WeightedOreDictEntry)obj;
         return (this.entry == other.entry || this.entry != null && this.entry.equals(other.entry)) && Float.floatToIntBits(this.p) == Float.floatToIntBits(other.p);
      }
   }
}
