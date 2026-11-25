package com.blamejared.compat.thermalexpansion.dynamos;

import cofh.thermalexpansion.util.managers.dynamo.ReactantManager;
import com.blamejared.ModTweaker;
import com.blamejared.mtlib.helpers.InputHelper;
import com.blamejared.mtlib.helpers.LogHelper;
import com.blamejared.mtlib.utils.BaseAction;
import crafttweaker.annotations.ModOnly;
import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.item.IItemStack;
import crafttweaker.api.liquid.ILiquidStack;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

@ZenClass("mods.thermalexpansion.ReactantDynamo")
@ModOnly("thermalexpansion")
@ZenRegister
public class ReactantDynamo {
   public ReactantDynamo() {
   }

   @ZenMethod
   public static void addReaction(IItemStack stack, ILiquidStack fluid, int energy) {
      ModTweaker.LATE_ADDITIONS.add(new ReactantDynamo.Add(InputHelper.toStack(stack), InputHelper.toFluid(fluid).getFluid(), energy));
   }

   @ZenMethod
   public static void addReactionElemental(IItemStack stack, ILiquidStack fluid, int energy) {
      ModTweaker.LATE_ADDITIONS.add(new ReactantDynamo.AddElemental(InputHelper.toStack(stack), InputHelper.toFluid(fluid).getFluid(), energy));
   }

   @ZenMethod
   public static void removeReaction(IItemStack stack, ILiquidStack fluid) {
      ModTweaker.LATE_REMOVALS.add(new ReactantDynamo.Remove(InputHelper.toStack(stack), InputHelper.toFluid(fluid).getFluid()));
   }

   @ZenMethod
   public static void removeReactionElemental(IItemStack stack, ILiquidStack fluid) {
      ModTweaker.LATE_REMOVALS.add(new ReactantDynamo.RemoveElemental(InputHelper.toStack(stack), InputHelper.toFluid(fluid).getFluid()));
   }

   private static class Add extends BaseAction {
      private ItemStack stack;
      private Fluid fluid;
      private int energy;

      protected Add(ItemStack stack, Fluid fluid, int energy) {
         super("ReactantDynamo");
         this.stack = stack;
         this.fluid = fluid;
         this.energy = energy;
      }

      public void apply() {
         ReactantManager.addReaction(this.stack, this.fluid, this.energy);
      }

      protected String getRecipeInfo() {
         return LogHelper.getStackDescription(this.stack) + " " + LogHelper.getStackDescription(this.fluid);
      }
   }

   private static class AddElemental extends BaseAction {
      private ItemStack stack;
      private Fluid fluid;
      private int energy;

      protected AddElemental(ItemStack stack, Fluid fluid, int energy) {
         super("ReactantDynamoElemental");
         this.stack = stack;
         this.fluid = fluid;
         this.energy = energy;
      }

      public void apply() {
         ReactantManager.addElementalReaction(this.stack, this.fluid, this.energy);
      }

      protected String getRecipeInfo() {
         return LogHelper.getStackDescription(this.stack) + " " + LogHelper.getStackDescription(this.fluid);
      }
   }

   private static class Remove extends BaseAction {
      private ItemStack stack;
      private Fluid fluid;

      protected Remove(ItemStack stack, Fluid fluid) {
         super("ReactantDynamo");
         this.stack = stack;
         this.fluid = fluid;
      }

      public void apply() {
         ReactantManager.removeReaction(this.stack, this.fluid);
      }

      protected String getRecipeInfo() {
         return LogHelper.getStackDescription(this.stack) + " " + LogHelper.getStackDescription(this.fluid);
      }
   }

   private static class RemoveElemental extends BaseAction {
      private ItemStack stack;
      private Fluid fluid;

      protected RemoveElemental(ItemStack stack, Fluid fluid) {
         super("ReactantDynamo");
         this.stack = stack;
         this.fluid = fluid;
      }

      public void apply() {
         ReactantManager.removeElementalReaction(this.stack, this.fluid);
      }

      protected String getRecipeInfo() {
         return LogHelper.getStackDescription(this.stack) + " " + LogHelper.getStackDescription(this.fluid);
      }
   }
}
