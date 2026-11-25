package crafttweaker.zenscript.expand;

import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.item.IItemStack;
import crafttweaker.api.item.WeightedItemStack;
import stanhebben.zenscript.annotations.ZenCaster;
import stanhebben.zenscript.annotations.ZenExpansion;

@ZenExpansion("crafttweaker.item.IItemStack")
@ZenRegister
public class ExpandItemStack {
   public ExpandItemStack() {
   }

   @ZenCaster
   public static WeightedItemStack asWeightedItemStack(IItemStack value) {
      return new WeightedItemStack(value, 1.0F);
   }
}
