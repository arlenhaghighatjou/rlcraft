package crafttweaker.mc1120.actions;

import crafttweaker.IAction;
import crafttweaker.api.item.IIngredient;
import crafttweaker.api.minecraft.CraftTweakerMC;
import crafttweaker.mc1120.util.CraftTweakerHacks;
import crafttweaker.mc1120.vanilla.MCSeedRegistry;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.item.ItemStack;

public class ActionRemoveSeed implements IAction {
   private final IIngredient pattern;
   private final List<Object> removed;

   public ActionRemoveSeed(IIngredient ingredient) {
      this.pattern = ingredient;
      this.removed = new ArrayList();
   }

   public void apply() {
      this.removed.clear();

      for(Object entry : MCSeedRegistry.SEEDS) {
         ItemStack itemStack = CraftTweakerHacks.getSeedEntrySeed(entry);
         if (this.pattern.matches(CraftTweakerMC.getIItemStackForMatching(itemStack))) {
            this.removed.add(entry);
         }
      }

      MCSeedRegistry.SEEDS.removeAll(this.removed);
   }

   public String describe() {
      return "Removing seeds " + this.pattern;
   }
}
