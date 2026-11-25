package com.blamejared.compat.betterwithmods;

import betterwithmods.module.hardcore.crafting.HCFurnace;
import com.blamejared.ModTweaker;
import com.blamejared.mtlib.utils.BaseMapAddition;
import crafttweaker.annotations.ModOnly;
import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.item.IIngredient;
import crafttweaker.api.minecraft.CraftTweakerMC;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.item.crafting.Ingredient;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

@ZenClass("mods.betterwithmods.Misc")
@ModOnly("betterwithmods")
@ZenRegister
public class Misc {
   public Misc() {
   }

   @ZenMethod
   public static void setFurnaceSmeltingTime(IIngredient ingredient, int time) {
      HashMap<Ingredient, Integer> map = new HashMap();
      map.put(CraftTweakerMC.getIngredient(ingredient), Integer.valueOf(time));
      ModTweaker.LATE_ADDITIONS.add(new Misc.SetSmeltingTime(map));
   }

   public static class SetSmeltingTime extends BaseMapAddition<Ingredient, Integer> {
      protected SetSmeltingTime(Map<Ingredient, Integer> map) {
         super("Set HCFurnace Smelting Time", HCFurnace.FURNACE_TIMINGS, map);
      }

      protected String getRecipeInfo(Entry<Ingredient, Integer> recipe) {
         return Arrays.toString(((Ingredient)recipe.getKey()).getMatchingStacks()) + " -> " + recipe.getValue();
      }
   }
}
