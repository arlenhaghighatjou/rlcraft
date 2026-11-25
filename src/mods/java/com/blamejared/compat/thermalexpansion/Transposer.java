package com.blamejared.compat.thermalexpansion;

import cofh.thermalexpansion.util.managers.machine.TransposerManager;
import com.blamejared.ModTweaker;
import com.blamejared.mtlib.helpers.InputHelper;
import com.blamejared.mtlib.helpers.LogHelper;
import com.blamejared.mtlib.utils.BaseAction;
import crafttweaker.CraftTweakerAPI;
import crafttweaker.annotations.ModOnly;
import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.item.IItemStack;
import crafttweaker.api.item.WeightedItemStack;
import crafttweaker.api.liquid.ILiquidStack;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import stanhebben.zenscript.annotations.Optional;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

@ZenClass("mods.thermalexpansion.Transposer")
@ModOnly("thermalexpansion")
@ZenRegister
public class Transposer {
   public Transposer() {
   }

   @ZenMethod
   public static void addExtractRecipe(ILiquidStack output, IItemStack input, int energy, @Optional WeightedItemStack itemOut) {
      ItemStack stack = ItemStack.EMPTY;
      int percent = 0;
      if (itemOut != null) {
         stack = InputHelper.toStack(itemOut.getStack());
         percent = (int)itemOut.getPercent();
      }

      ModTweaker.LATE_ADDITIONS.add(new Transposer.AddExtract(InputHelper.toFluid(output), InputHelper.toStack(input), energy, stack, percent));
   }

   @ZenMethod
   public static void removeExtractRecipe(IItemStack input) {
      ModTweaker.LATE_REMOVALS.add(new Transposer.RemoveExtract(InputHelper.toStack(input)));
   }

   @ZenMethod
   public static void addFillRecipe(IItemStack output, IItemStack input, ILiquidStack fluid, int energy) {
      ModTweaker.LATE_ADDITIONS.add(new Transposer.AddFill(InputHelper.toFluid(fluid), InputHelper.toStack(input), energy, InputHelper.toStack(output)));
   }

   @ZenMethod
   public static void removeFillRecipe(IItemStack input, ILiquidStack fluid) {
      ModTweaker.LATE_REMOVALS.add(new Transposer.RemoveFill(InputHelper.toStack(input), InputHelper.toFluid(fluid)));
   }

   private static class AddExtract extends BaseAction {
      private FluidStack output;
      private ItemStack input;
      private int energy;
      private ItemStack itemOut;
      private int chance;

      public AddExtract(FluidStack output, ItemStack input, int energy, ItemStack itemOut, int chance) {
         super("Transposer Extract");
         this.output = output;
         this.input = input;
         this.energy = energy;
         this.itemOut = itemOut;
         this.chance = chance;
      }

      public void apply() {
         TransposerManager.addExtractRecipe(this.energy, this.input, this.itemOut, this.output, this.chance, false);
      }

      protected String getRecipeInfo() {
         return LogHelper.getStackDescription(this.output);
      }
   }

   private static class AddFill extends BaseAction {
      private FluidStack fluid;
      private ItemStack input;
      private int energy;
      private ItemStack output;

      public AddFill(FluidStack output, ItemStack input, int energy, ItemStack itemOut) {
         super("Transposer Fill");
         this.fluid = output;
         this.input = input;
         this.energy = energy;
         this.output = itemOut;
      }

      public void apply() {
         TransposerManager.addFillRecipe(this.energy, this.input, this.output, this.fluid, false);
      }

      protected String getRecipeInfo() {
         return LogHelper.getStackDescription(this.fluid);
      }
   }

   private static class RemoveExtract extends BaseAction {
      private ItemStack input;

      public RemoveExtract(ItemStack input) {
         super("Transposer Extract");
         this.input = input;
      }

      public void apply() {
         if (!TransposerManager.extractRecipeExists(this.input)) {
            CraftTweakerAPI.logError("No Transposer Extraction recipe exists for: " + this.input);
         } else {
            TransposerManager.removeExtractRecipe(this.input);
         }
      }

      protected String getRecipeInfo() {
         return LogHelper.getStackDescription(this.input);
      }
   }

   private static class RemoveFill extends BaseAction {
      private ItemStack input;
      private FluidStack fluid;

      public RemoveFill(ItemStack input, FluidStack fluid) {
         super("Transposer Fill");
         this.input = input;
         this.fluid = fluid;
      }

      public void apply() {
         if (!TransposerManager.fillRecipeExists(this.input, this.fluid)) {
            CraftTweakerAPI.logError("No Transposer Fill recipe exists for: " + this.input);
         } else {
            TransposerManager.removeFillRecipe(this.input, this.fluid);
         }
      }

      protected String getRecipeInfo() {
         return LogHelper.getStackDescription(this.input);
      }
   }
}
