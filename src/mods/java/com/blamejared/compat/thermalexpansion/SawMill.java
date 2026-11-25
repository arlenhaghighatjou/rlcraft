package com.blamejared.compat.thermalexpansion;

import cofh.thermalexpansion.util.managers.machine.SawmillManager;
import com.blamejared.ModTweaker;
import com.blamejared.mtlib.helpers.InputHelper;
import com.blamejared.mtlib.helpers.LogHelper;
import com.blamejared.mtlib.utils.BaseAction;
import crafttweaker.CraftTweakerAPI;
import crafttweaker.annotations.ModOnly;
import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.item.IItemStack;
import net.minecraft.item.ItemStack;
import stanhebben.zenscript.annotations.Optional;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

@ZenClass("mods.thermalexpansion.Sawmill")
@ModOnly("thermalexpansion")
@ZenRegister
public class SawMill {
   public SawMill() {
   }

   @ZenMethod
   public static void addRecipe(IItemStack output, IItemStack input, int energy, @Optional IItemStack secondaryOutput, @Optional int secondaryChance) {
      ModTweaker.LATE_ADDITIONS.add(new SawMill.Add(InputHelper.toStack(output), InputHelper.toStack(input), energy, InputHelper.toStack(secondaryOutput), secondaryChance));
   }

   @ZenMethod
   public static void removeRecipe(IItemStack input) {
      ModTweaker.LATE_REMOVALS.add(new SawMill.Remove(InputHelper.toStack(input)));
   }

   private static class Add extends BaseAction {
      private ItemStack output;
      private ItemStack input;
      private ItemStack secondaryOutput;
      private int energy;
      private int secondaryChance;

      public Add(ItemStack output, ItemStack input, int energy, ItemStack secondaryOutput, int secondaryChance) {
         super("Sawmill");
         this.output = output;
         this.input = input;
         this.secondaryOutput = secondaryOutput;
         this.energy = energy;
         this.secondaryChance = secondaryChance;
         if (!secondaryOutput.isEmpty() && secondaryChance <= 0) {
            this.secondaryChance = 100;
         }

      }

      public void apply() {
         SawmillManager.addRecipe(this.energy, this.input, this.output, this.secondaryOutput, this.secondaryChance);
      }

      protected String getRecipeInfo() {
         return LogHelper.getStackDescription(this.output);
      }
   }

   private static class Remove extends BaseAction {
      private ItemStack input;

      public Remove(ItemStack input) {
         super("Sawmill");
         this.input = input;
      }

      public void apply() {
         if (!SawmillManager.recipeExists(this.input)) {
            CraftTweakerAPI.logError("No Sawmill recipe exists for: " + this.input);
         } else {
            SawmillManager.removeRecipe(this.input);
         }
      }

      protected String getRecipeInfo() {
         return LogHelper.getStackDescription(this.input);
      }
   }
}
