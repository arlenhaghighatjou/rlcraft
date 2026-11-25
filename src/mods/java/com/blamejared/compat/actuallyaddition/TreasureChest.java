package com.blamejared.compat.actuallyaddition;

import com.blamejared.ModTweaker;
import com.blamejared.mtlib.helpers.InputHelper;
import com.blamejared.mtlib.helpers.LogHelper;
import com.blamejared.mtlib.utils.BaseListAddition;
import com.blamejared.mtlib.utils.BaseListRemoval;
import crafttweaker.annotations.ModOnly;
import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.item.IItemStack;
import de.ellpeck.actuallyadditions.api.ActuallyAdditionsAPI;
import de.ellpeck.actuallyadditions.api.recipe.TreasureChestLoot;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

@ZenClass("mods.actuallyadditions.TreasureChest")
@ModOnly("actuallyadditions")
@ZenRegister
public class TreasureChest {
   public TreasureChest() {
   }

   @ZenMethod
   public static void addLoot(IItemStack returnItem, int chance, int minAmount, int maxAmount) {
      ModTweaker.LATE_ADDITIONS.add(new TreasureChest.Add(Collections.singletonList(new TreasureChestLoot(InputHelper.toStack(returnItem), chance, minAmount, maxAmount))));
   }

   @ZenMethod
   public static void removeLoot(IItemStack returnItem) {
      ModTweaker.LATE_REMOVALS.add(new TreasureChest.Remove(returnItem));
   }

   private static class Add extends BaseListAddition<TreasureChestLoot> {
      protected Add(List<TreasureChestLoot> recipes) {
         super("Treasure Chest", ActuallyAdditionsAPI.TREASURE_CHEST_LOOT, recipes);
      }

      protected String getRecipeInfo(TreasureChestLoot recipe) {
         return LogHelper.getStackDescription(recipe.returnItem);
      }
   }

   private static class Remove extends BaseListRemoval<TreasureChestLoot> {
      private IItemStack output;

      protected Remove(IItemStack output) {
         super("Treasure Chest", ActuallyAdditionsAPI.TREASURE_CHEST_LOOT);
         this.output = output;
      }

      public void apply() {
         ActuallyAdditionsAPI.TREASURE_CHEST_LOOT.forEach((recipe) -> {
            if (this.output.matches(InputHelper.toIItemStack(recipe.returnItem))) {
               this.recipes.add(recipe);
            }

         });
         super.apply();
      }

      protected String getRecipeInfo(TreasureChestLoot recipe) {
         return LogHelper.getStackDescription(recipe.returnItem);
      }
   }
}
