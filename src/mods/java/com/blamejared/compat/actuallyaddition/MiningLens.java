package com.blamejared.compat.actuallyaddition;

import com.blamejared.ModTweaker;
import com.blamejared.mtlib.utils.BaseListAddition;
import com.blamejared.mtlib.utils.BaseListRemoval;
import crafttweaker.annotations.ModOnly;
import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.oredict.IOreDictEntry;
import de.ellpeck.actuallyadditions.api.ActuallyAdditionsAPI;
import de.ellpeck.actuallyadditions.api.recipe.WeightedOre;
import java.util.Collections;
import java.util.List;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

@ZenClass("mods.actuallyadditions.MiningLens")
@ModOnly("actuallyadditions")
@ZenRegister
public class MiningLens {
   public MiningLens() {
   }

   @ZenMethod
   public static void addStoneOre(IOreDictEntry ore, int weight) {
      ModTweaker.LATE_ADDITIONS.add(new MiningLens.Add(ActuallyAdditionsAPI.STONE_ORES, Collections.singletonList(new WeightedOre(ore.getName(), weight))));
   }

   @ZenMethod
   public static void addNetherOre(IOreDictEntry ore, int weight) {
      ModTweaker.LATE_ADDITIONS.add(new MiningLens.Add(ActuallyAdditionsAPI.NETHERRACK_ORES, Collections.singletonList(new WeightedOre(ore.getName(), weight))));
   }

   @ZenMethod
   public static void removeStoneOre(IOreDictEntry ore) {
      ModTweaker.LATE_REMOVALS.add(new MiningLens.Remove(ActuallyAdditionsAPI.STONE_ORES, ore.getName()));
   }

   @ZenMethod
   public static void removeNetherOre(IOreDictEntry ore) {
      ModTweaker.LATE_REMOVALS.add(new MiningLens.Remove(ActuallyAdditionsAPI.NETHERRACK_ORES, ore.getName()));
   }

   private static class Add extends BaseListAddition<WeightedOre> {
      protected Add(List<WeightedOre> recipes, List<WeightedOre> newRecipes) {
         super("MiningLens", recipes, newRecipes);
      }

      protected String getRecipeInfo(WeightedOre recipe) {
         return recipe.name;
      }
   }

   public static class Remove extends BaseListRemoval<WeightedOre> {
      private String output;

      protected Remove(List<WeightedOre> recipes, String output) {
         super("MiningLens", recipes);
         this.output = output;
      }

      public void apply() {
         for(WeightedOre recipe : this.list) {
            if (recipe.name.equals(this.output)) {
               this.recipes.add(recipe);
            }
         }

         super.apply();
      }

      protected String getRecipeInfo(WeightedOre recipe) {
         return recipe.name;
      }
   }
}
