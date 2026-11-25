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
import de.ellpeck.actuallyadditions.api.recipe.BallOfFurReturn;
import java.util.Collections;
import java.util.List;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

@ZenClass("mods.actuallyadditions.BallOfFur")
@ModOnly("actuallyadditions")
@ZenRegister
public class BallOfFur {
   public BallOfFur() {
   }

   @ZenMethod
   public static void addReturn(IItemStack output, int chance) {
      ModTweaker.LATE_ADDITIONS.add(new BallOfFur.Add(Collections.singletonList(new BallOfFurReturn(InputHelper.toStack(output), chance))));
   }

   @ZenMethod
   public static void removeReturn(IItemStack output) {
      ModTweaker.LATE_ADDITIONS.add(new BallOfFur.Remove(output));
   }

   private static class Add extends BaseListAddition<BallOfFurReturn> {
      protected Add(List<BallOfFurReturn> recipes) {
         super("BallOfFur", ActuallyAdditionsAPI.BALL_OF_FUR_RETURN_ITEMS, recipes);
      }

      protected String getRecipeInfo(BallOfFurReturn recipe) {
         return LogHelper.getStackDescription(recipe.returnItem);
      }
   }

   private static class Remove extends BaseListRemoval<BallOfFurReturn> {
      private IItemStack output;

      public Remove(IItemStack output) {
         super("BallOfFur", ActuallyAdditionsAPI.BALL_OF_FUR_RETURN_ITEMS);
         this.output = output;
      }

      public void apply() {
         for(BallOfFurReturn recipe : ActuallyAdditionsAPI.BALL_OF_FUR_RETURN_ITEMS) {
            if (this.output.matches(InputHelper.toIItemStack(recipe.returnItem))) {
               this.recipes.add(recipe);
            }
         }

         super.apply();
      }

      protected String getRecipeInfo(BallOfFurReturn recipe) {
         return LogHelper.getStackDescription(recipe.returnItem);
      }
   }
}
