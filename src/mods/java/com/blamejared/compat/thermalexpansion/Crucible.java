package com.blamejared.compat.thermalexpansion;

import cofh.thermalexpansion.util.managers.machine.CrucibleManager;
import com.blamejared.ModTweaker;
import com.blamejared.mtlib.helpers.InputHelper;
import com.blamejared.mtlib.helpers.LogHelper;
import com.blamejared.mtlib.utils.BaseAction;
import crafttweaker.CraftTweakerAPI;
import crafttweaker.annotations.ModOnly;
import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.item.IItemStack;
import crafttweaker.api.liquid.ILiquidStack;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

@ZenClass("mods.thermalexpansion.Crucible")
@ModOnly("thermalexpansion")
@ZenRegister
public class Crucible {
   public Crucible() {
   }

   @ZenMethod
   public static void addRecipe(ILiquidStack output, IItemStack input, int energy) {
      ModTweaker.LATE_ADDITIONS.add(new Crucible.Add(InputHelper.toFluid(output), InputHelper.toStack(input), energy));
   }

   @ZenMethod
   public static void removeRecipe(IItemStack input) {
      ModTweaker.LATE_REMOVALS.add(new Crucible.Remove(InputHelper.toStack(input)));
   }

   private static class Add extends BaseAction {
      private FluidStack output;
      private ItemStack input;
      private int energy;

      public Add(FluidStack output, ItemStack input, int energy) {
         super("Crucible");
         this.output = output;
         this.input = input;
         this.energy = energy;
      }

      public void apply() {
         CrucibleManager.addRecipe(this.energy, this.input, this.output);
      }

      protected String getRecipeInfo() {
         return LogHelper.getStackDescription(this.output);
      }
   }

   private static class Remove extends BaseAction {
      private ItemStack input;

      public Remove(ItemStack input) {
         super("Crucible");
         this.input = input;
      }

      public void apply() {
         if (!CrucibleManager.recipeExists(this.input)) {
            CraftTweakerAPI.logError("No Crucible recipe exists for: " + this.input);
         } else {
            CrucibleManager.removeRecipe(this.input);
         }
      }

      protected String getRecipeInfo() {
         return LogHelper.getStackDescription(this.input);
      }
   }
}
