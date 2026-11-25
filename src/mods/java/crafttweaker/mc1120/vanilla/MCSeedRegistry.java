package crafttweaker.mc1120.vanilla;

import crafttweaker.api.item.IIngredient;
import crafttweaker.api.item.WeightedItemStack;
import crafttweaker.api.minecraft.CraftTweakerMC;
import crafttweaker.api.vanilla.ISeedRegistry;
import crafttweaker.mc1120.CraftTweaker;
import crafttweaker.mc1120.actions.ActionAddSeed;
import crafttweaker.mc1120.actions.ActionRemoveSeed;
import crafttweaker.mc1120.util.CraftTweakerHacks;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import net.minecraft.util.WeightedRandom.Item;

public class MCSeedRegistry implements ISeedRegistry {
   public static final List<Item> SEEDS = CraftTweakerHacks.getSeeds();

   public MCSeedRegistry() {
   }

   public void addSeed(WeightedItemStack item) {
      CraftTweaker.LATE_ACTIONS.add(new ActionAddSeed(item));
   }

   public void removeSeed(IIngredient pattern) {
      CraftTweaker.LATE_ACTIONS.add(new ActionRemoveSeed(pattern));
   }

   public List<WeightedItemStack> getSeeds() {
      List<? extends Item> entries = SEEDS;
      return (List)entries.stream().map((entry) -> {
         return new WeightedItemStack(CraftTweakerMC.getIItemStack(CraftTweakerHacks.getSeedEntrySeed(entry)), (float)entry.itemWeight);
      }).collect(Collectors.toList());
   }
}
