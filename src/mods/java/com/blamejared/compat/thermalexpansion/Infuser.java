package com.blamejared.compat.thermalexpansion;

import cofh.thermalexpansion.util.managers.machine.ChargerManager;
import com.blamejared.ModTweaker;
import com.blamejared.mtlib.helpers.InputHelper;
import com.blamejared.mtlib.helpers.LogHelper;
import com.blamejared.mtlib.utils.BaseAction;
import crafttweaker.CraftTweakerAPI;
import crafttweaker.annotations.ModOnly;
import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.item.IItemStack;
import net.minecraft.item.ItemStack;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

@ZenClass("mods.thermalexpansion.Infuser")
@ModOnly("thermalexpansion")
@ZenRegister
public class Infuser {
   public Infuser() {
   }

   @ZenMethod
   public static void addRecipe(IItemStack output, IItemStack input, int energy) {
      ModTweaker.LATE_ADDITIONS.add(new Infuser.Add(InputHelper.toStack(output), InputHelper.toStack(input), energy));
   }

   @ZenMethod
   public static void removeRecipe(IItemStack input) {
      ModTweaker.LATE_REMOVALS.add(new Infuser.Remove(InputHelper.toStack(input)));
   }

   private static class Add extends BaseAction {
      private ItemStack output;
      private ItemStack input;
      private int energy;

      public Add(ItemStack output, ItemStack input, int energy) {
         super("Infuser");
         this.output = output;
         this.input = input;
         this.energy = energy;
      }

      public void apply() {
         ChargerManager.addRecipe(this.energy, this.input, this.output);
      }

      protected String getRecipeInfo() {
         return LogHelper.getStackDescription(this.output);
      }
   }

   private static class Remove extends BaseAction {
      private ItemStack input;

      public Remove(ItemStack input) {
         super("Infuser");
         this.input = input;
      }

      public void apply() {
         if (!ChargerManager.recipeExists(this.input)) {
            CraftTweakerAPI.logError("No Infuser recipe exists for: " + this.input);
         } else {
            ChargerManager.removeRecipe(this.input);
         }
      }

      protected String getRecipeInfo() {
         return LogHelper.getStackDescription(this.input);
      }
   }
}
