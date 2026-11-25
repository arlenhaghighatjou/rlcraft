package com.blamejared.compat.thermalexpansion;

import cofh.thermalexpansion.util.managers.machine.InsolatorManager;
import cofh.thermalexpansion.util.managers.machine.InsolatorManager.Type;
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

@ZenClass("mods.thermalexpansion.Insolator")
@ModOnly("thermalexpansion")
@ZenRegister
public class Insolator {
   public Insolator() {
   }

   @ZenMethod
   public static void addRecipe(IItemStack primaryOutput, IItemStack primaryInput, IItemStack secondaryInput, int energy, @Optional IItemStack secondaryOutput, @Optional int secondaryChance, @Optional(valueLong = -1L) int water) {
      ModTweaker.LATE_ADDITIONS.add(new Insolator.Add(InputHelper.toStack(primaryOutput), InputHelper.toStack(primaryInput), InputHelper.toStack(secondaryInput), energy, InputHelper.toStack(secondaryOutput), secondaryChance, water, false));
   }

   @ZenMethod
   public static void addRecipeSaplingInfuser(IItemStack primaryOutput, IItemStack primaryInput, IItemStack secondaryInput, int energy, @Optional IItemStack secondaryOutput, @Optional int secondaryChance, @Optional(valueLong = -1L) int water) {
      ModTweaker.LATE_ADDITIONS.add(new Insolator.Add(InputHelper.toStack(primaryOutput), InputHelper.toStack(primaryInput), InputHelper.toStack(secondaryInput), energy, InputHelper.toStack(secondaryOutput), secondaryChance, water, true));
   }

   @ZenMethod
   public static void removeRecipe(IItemStack primaryInput, IItemStack secondaryInput) {
      ModTweaker.LATE_REMOVALS.add(new Insolator.Remove(InputHelper.toStack(primaryInput), InputHelper.toStack(secondaryInput)));
   }

   private static class Add extends BaseAction {
      private final ItemStack primaryOutput;
      private final ItemStack primaryInput;
      private final ItemStack secondaryInput;
      private final ItemStack secondaryOutput;
      private final int energy;
      private final int secondaryChance;
      private final int water;
      private final boolean isSapling;

      public Add(ItemStack primaryOutput, ItemStack primaryInput, ItemStack secondaryInput, int energy, ItemStack secondaryOutput, int secondaryChance, int water, boolean isSapling) {
         super("PhytogenicInsolator");
         this.primaryOutput = primaryOutput;
         this.primaryInput = primaryInput;
         this.secondaryInput = secondaryInput;
         this.secondaryOutput = secondaryOutput;
         this.energy = energy;
         this.water = water;
         this.isSapling = isSapling;
         this.secondaryChance = !secondaryOutput.isEmpty() && secondaryChance <= 0 ? 100 : secondaryChance;
      }

      public void apply() {
         Type type = this.isSapling ? Type.TREE : Type.STANDARD;
         if (this.water == -1) {
            InsolatorManager.addRecipe(this.energy, this.primaryInput, this.secondaryInput, this.primaryOutput, this.secondaryOutput, this.secondaryChance, type);
         } else {
            InsolatorManager.addRecipe(this.energy, this.water, this.primaryInput, this.secondaryInput, this.primaryOutput, this.secondaryOutput, this.secondaryChance, type);
         }

      }

      protected String getRecipeInfo() {
         return LogHelper.getStackDescription(this.primaryOutput);
      }
   }

   private static class Remove extends BaseAction {
      private ItemStack primaryInput;
      private ItemStack secondaryInput;

      public Remove(ItemStack primaryInput, ItemStack secondaryInput) {
         super("PhytogenicInsolator");
         this.primaryInput = primaryInput;
         this.secondaryInput = secondaryInput;
      }

      public void apply() {
         if (!InsolatorManager.recipeExists(this.primaryInput, this.secondaryInput)) {
            CraftTweakerAPI.logError("No Insolator recipe exists for: " + this.primaryInput + " and " + this.secondaryInput);
         } else {
            InsolatorManager.removeRecipe(this.primaryInput, this.secondaryInput);
         }
      }

      protected String getRecipeInfo() {
         return LogHelper.getStackDescription(this.primaryInput) + " and " + LogHelper.getStackDescription(this.secondaryInput);
      }
   }
}
