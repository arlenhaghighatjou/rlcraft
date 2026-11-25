package com.blamejared.compat.thermalexpansion;

import cofh.thermalexpansion.util.managers.machine.CompactorManager;
import cofh.thermalexpansion.util.managers.machine.CompactorManager.Mode;
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

@ZenClass("mods.thermalexpansion.Compactor")
@ModOnly("thermalexpansion")
@ZenRegister
public class Compactor {
   public Compactor() {
   }

   @ZenMethod
   public static void addMintRecipe(IItemStack output, IItemStack input, int energy) {
      ModTweaker.LATE_ADDITIONS.add(new Compactor.Add(InputHelper.toStack(output), InputHelper.toStack(input), energy, Mode.COIN));
   }

   @ZenMethod
   public static void addPressRecipe(IItemStack output, IItemStack input, int energy) {
      ModTweaker.LATE_ADDITIONS.add(new Compactor.Add(InputHelper.toStack(output), InputHelper.toStack(input), energy, Mode.ALL));
   }

   @ZenMethod
   public static void addStorageRecipe(IItemStack output, IItemStack input, int energy) {
      ModTweaker.LATE_ADDITIONS.add(new Compactor.Add(InputHelper.toStack(output), InputHelper.toStack(input), energy, Mode.PLATE));
   }

   @ZenMethod
   public static void addGearRecipe(IItemStack output, IItemStack input, int energy) {
      ModTweaker.LATE_ADDITIONS.add(new Compactor.Add(InputHelper.toStack(output), InputHelper.toStack(input), energy, Mode.GEAR));
   }

   @ZenMethod
   public static void removeMintRecipe(IItemStack input) {
      ModTweaker.LATE_REMOVALS.add(new Compactor.Remove(InputHelper.toStack(input), Mode.COIN));
   }

   @ZenMethod
   public static void removePressRecipe(IItemStack input) {
      ModTweaker.LATE_REMOVALS.add(new Compactor.Remove(InputHelper.toStack(input), Mode.ALL));
   }

   @ZenMethod
   public static void removeStorageRecipe(IItemStack input) {
      ModTweaker.LATE_REMOVALS.add(new Compactor.Remove(InputHelper.toStack(input), Mode.PLATE));
   }

   @ZenMethod
   public static void removeGearRecipe(IItemStack input) {
      ModTweaker.LATE_REMOVALS.add(new Compactor.Remove(InputHelper.toStack(input), Mode.GEAR));
   }

   private static class Add extends BaseAction {
      private ItemStack output;
      private ItemStack input;
      private int energy;
      private Mode mode;

      public Add(ItemStack output, ItemStack input, int energy, Mode mode) {
         super("Compactor");
         this.output = output;
         this.input = input;
         this.energy = energy;
         this.mode = mode;
      }

      public void apply() {
         CompactorManager.addRecipe(this.energy, this.input, this.output, this.mode);
      }

      protected String getRecipeInfo() {
         return LogHelper.getStackDescription(this.output) + " in mode: " + this.mode;
      }
   }

   private static class Remove extends BaseAction {
      private ItemStack input;
      private Mode mode;

      public Remove(ItemStack input, Mode mode) {
         super("Compactor");
         this.input = input;
         this.mode = mode;
      }

      public void apply() {
         if (!CompactorManager.recipeExists(this.input, this.mode)) {
            CraftTweakerAPI.logError("No Compactor recipe exists for: " + this.input);
         } else {
            CompactorManager.removeRecipe(this.input, this.mode);
         }
      }

      protected String getRecipeInfo() {
         return LogHelper.getStackDescription(this.input) + " in mode: " + this.mode;
      }
   }
}
