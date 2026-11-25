package crafttweaker.mc1120.actions;

import crafttweaker.IAction;
import crafttweaker.api.item.IItemStack;
import crafttweaker.api.item.WeightedItemStack;
import crafttweaker.mc1120.util.CraftTweakerHacks;
import crafttweaker.mc1120.vanilla.MCSeedRegistry;
import net.minecraft.util.WeightedRandom.Item;

public class ActionAddSeed implements IAction {
   private final IItemStack item;
   private final Item entry;

   public ActionAddSeed(WeightedItemStack item) {
      this.item = item.getStack();
      this.entry = CraftTweakerHacks.constructSeedEntry(item);
   }

   public void apply() {
      MCSeedRegistry.SEEDS.add(this.entry);
   }

   public String describe() {
      return "Adding seed entry " + this.item;
   }
}
