package com.blamejared.compat.thermalexpansion;

import cofh.thermalexpansion.util.managers.machine.SmelterManager;
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

@ZenClass("mods.thermalexpansion.InductionSmelter")
@ModOnly("thermalexpansion")
@ZenRegister
public class InductionSmelter {
   public InductionSmelter() {
   }

   @ZenMethod
   public static void addRecipe(IItemStack primaryOutput, IItemStack primaryInput, IItemStack secondaryInput, int energy, @Optional IItemStack secondaryOutput, @Optional int secondaryChance) {
      ModTweaker.LATE_ADDITIONS.add(new InductionSmelter.Add(InputHelper.toStack(primaryOutput), InputHelper.toStack(primaryInput), InputHelper.toStack(secondaryInput), energy, InputHelper.toStack(secondaryOutput), secondaryChance));
   }

   @ZenMethod
   public static void removeRecipe(IItemStack primaryInput, IItemStack secondaryInput) {
      ModTweaker.LATE_REMOVALS.add(new InductionSmelter.Remove(InputHelper.toStack(primaryInput), InputHelper.toStack(secondaryInput)));
   }

   private static class Add extends BaseAction {
      private ItemStack primaryOutput;
      private ItemStack primaryInput;
      private ItemStack secondaryInput;
      private ItemStack secondaryOutput;
      private int energy;
      private int secondaryChance;

      public Add(ItemStack primaryOutput, ItemStack primaryInput, ItemStack secondaryInput, int energy, ItemStack secondaryOutput, int secondaryChance) {
         super("InductionSmelter");
         this.primaryOutput = primaryOutput;
         this.primaryInput = primaryInput;
         this.secondaryInput = secondaryInput;
         this.secondaryOutput = secondaryOutput;
         this.energy = energy;
         this.secondaryChance = secondaryChance;
         if (!secondaryOutput.isEmpty() && secondaryChance <= 0) {
            this.secondaryChance = 100;
         }

      }

      public void apply() {
         SmelterManager.addRecipe(this.energy, this.primaryInput, this.secondaryInput, this.primaryOutput, this.secondaryOutput, this.secondaryChance);
      }

      protected String getRecipeInfo() {
         return LogHelper.getStackDescription(this.primaryOutput);
      }
   }

   private static class Remove extends BaseAction {
      private ItemStack primaryInput;
      private ItemStack secondaryInput;

      public Remove(ItemStack primaryInput, ItemStack secondaryInput) {
         super("InductionSmelter");
         this.primaryInput = primaryInput;
         this.secondaryInput = secondaryInput;
      }

      public void apply() {
         if (!SmelterManager.recipeExists(this.primaryInput, this.secondaryInput) && !SmelterManager.recipeExists(this.secondaryInput, this.primaryInput)) {
            CraftTweakerAPI.logError("No InductionSmelter recipe exists for: " + this.primaryInput + " and " + this.secondaryInput);
         } else {
            if (SmelterManager.recipeExists(this.primaryInput, this.secondaryInput)) {
               SmelterManager.removeRecipe(this.primaryInput, this.secondaryInput);
            }

            if (SmelterManager.recipeExists(this.primaryInput, this.secondaryInput)) {
               SmelterManager.removeRecipe(this.secondaryInput, this.primaryInput);
            }

            if (SmelterManager.recipeExists(this.primaryInput, this.secondaryInput)) {
               CraftTweakerAPI.logError("InductionSmelter recipes for: " + this.primaryInput + " and " + this.secondaryInput + " still exists after reversing fields.");
            }

         }
      }

      protected String getRecipeInfo() {
         return LogHelper.getStackDescription(this.primaryInput) + " and " + LogHelper.getStackDescription(this.secondaryInput);
      }
   }
}
