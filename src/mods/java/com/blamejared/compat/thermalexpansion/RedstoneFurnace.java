package com.blamejared.compat.thermalexpansion;

import cofh.thermalexpansion.util.managers.machine.FurnaceManager;
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

@ZenClass("mods.thermalexpansion.RedstoneFurnace")
@ModOnly("thermalexpansion")
@ZenRegister
public class RedstoneFurnace {
   public RedstoneFurnace() {
   }

   @ZenMethod
   public static void addRecipe(IItemStack output, IItemStack input, int energy) {
      ModTweaker.LATE_ADDITIONS.add(new RedstoneFurnace.Add(InputHelper.toStack(output), InputHelper.toStack(input), energy));
   }

   @ZenMethod
   public static void addPyrolysisRecipe(IItemStack output, IItemStack input, int energy, int creosote) {
      ModTweaker.LATE_ADDITIONS.add(new RedstoneFurnace.AddPyrolysis(InputHelper.toStack(output), InputHelper.toStack(input), energy, creosote));
   }

   @ZenMethod
   public static void removeRecipe(IItemStack input) {
      ModTweaker.LATE_REMOVALS.add(new RedstoneFurnace.Remove(InputHelper.toStack(input)));
   }

   @ZenMethod
   public static void removePyrolysisRecipe(IItemStack input) {
      ModTweaker.LATE_REMOVALS.add(new RedstoneFurnace.RemovePyrolysis(InputHelper.toStack(input)));
   }

   private static class Add extends BaseAction {
      private ItemStack output;
      private ItemStack input;
      private int energy;

      public Add(ItemStack output, ItemStack input, int energy) {
         super("RedstoneFurnace");
         this.output = output;
         this.input = input;
         this.energy = energy;
      }

      public void apply() {
         FurnaceManager.addRecipe(this.energy, this.input, this.output);
      }

      protected String getRecipeInfo() {
         return LogHelper.getStackDescription(this.output);
      }
   }

   private static class AddPyrolysis extends BaseAction {
      private ItemStack output;
      private ItemStack input;
      private int energy;
      private int creosote;

      public AddPyrolysis(ItemStack output, ItemStack input, int energy, int creosote) {
         super("RedstoneFurnace");
         this.output = output;
         this.input = input;
         this.energy = energy;
         this.creosote = creosote;
      }

      public void apply() {
         FurnaceManager.addRecipePyrolysis(this.energy, this.input, this.output, this.creosote);
      }

      protected String getRecipeInfo() {
         return LogHelper.getStackDescription(this.output);
      }
   }

   private static class Remove extends BaseAction {
      private ItemStack input;

      public Remove(ItemStack input) {
         super("RedstoneFurnace");
         this.input = input;
      }

      public void apply() {
         if (!FurnaceManager.recipeExists(this.input, false)) {
            CraftTweakerAPI.logError("No Furnace recipe exists for: " + this.input);
         } else {
            FurnaceManager.removeRecipe(this.input);
         }
      }

      protected String getRecipeInfo() {
         return LogHelper.getStackDescription(this.input);
      }
   }

   private static class RemovePyrolysis extends BaseAction {
      private ItemStack input;

      public RemovePyrolysis(ItemStack input) {
         super("RedstoneFurnace");
         this.input = input;
      }

      public void apply() {
         if (!FurnaceManager.recipeExists(this.input, true)) {
            CraftTweakerAPI.logError("No Furnace Pyrolysis recipe exists for: " + this.input);
         } else {
            FurnaceManager.removeRecipePyrolysis(this.input);
         }
      }

      protected String getRecipeInfo() {
         return LogHelper.getStackDescription(this.input);
      }
   }
}
