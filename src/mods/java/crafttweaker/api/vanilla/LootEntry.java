package crafttweaker.api.vanilla;

import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.item.WeightedItemStack;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenGetter;

@ZenClass("vanilla.LootEntry")
@ZenRegister
public class LootEntry {
   private final WeightedItemStack item;
   private final int minAmount;
   private final int maxAmount;

   public LootEntry(WeightedItemStack item, int minAmount, int maxAmount) {
      this.item = item;
      this.minAmount = minAmount;
      this.maxAmount = maxAmount;
   }

   @ZenGetter("item")
   public WeightedItemStack getItem() {
      return this.item;
   }

   @ZenGetter("minAmount")
   public int getMinAmount() {
      return this.minAmount;
   }

   @ZenGetter("maxAmount")
   public int getMaxAmount() {
      return this.maxAmount;
   }

   public String toString() {
      StringBuilder message = new StringBuilder();
      message.append(this.getItem().getStack());
      message.append(" (weight ");
      message.append((int)this.getItem().getChance());
      if (this.getMinAmount() != 0 || this.getMaxAmount() != 1) {
         message.append(", ");
         message.append(this.getMinAmount());
         message.append("-");
         message.append(this.getMaxAmount());
      }

      message.append(")");
      return message.toString();
   }
}
