package com.blamejared.compat.thermalexpansion.dynamos;

import cofh.thermalexpansion.util.managers.dynamo.NumismaticManager;
import com.blamejared.ModTweaker;
import com.blamejared.mtlib.helpers.InputHelper;
import com.blamejared.mtlib.helpers.LogHelper;
import com.blamejared.mtlib.utils.BaseAction;
import crafttweaker.annotations.ModOnly;
import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.item.IItemStack;
import net.minecraft.item.ItemStack;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

@ZenClass("mods.thermalexpansion.NumisticDynamo")
@ModOnly("thermalexpansion")
@ZenRegister
public class NumisticDynamo {
   public NumisticDynamo() {
   }

   @ZenMethod
   public static void addFuel(IItemStack stack, int energy) {
      ModTweaker.LATE_ADDITIONS.add(new NumisticDynamo.Add(InputHelper.toStack(stack), energy));
   }

   @ZenMethod
   public static void removeFuel(IItemStack stack) {
      ModTweaker.LATE_REMOVALS.add(new NumisticDynamo.Remove(InputHelper.toStack(stack)));
   }

   @ZenMethod
   public static void addGemFuel(IItemStack stack, int energy) {
      ModTweaker.LATE_ADDITIONS.add(new NumisticDynamo.AddGem(InputHelper.toStack(stack), energy));
   }

   @ZenMethod
   public static void removeGemFuel(IItemStack stack) {
      ModTweaker.LATE_REMOVALS.add(new NumisticDynamo.RemoveGem(InputHelper.toStack(stack)));
   }

   private static class Add extends BaseAction {
      private ItemStack stack;
      private int energy;

      protected Add(ItemStack stack, int energy) {
         super("NumisticDynamo");
         this.stack = stack;
         this.energy = energy;
      }

      public void apply() {
         NumismaticManager.addFuel(this.stack, this.energy);
      }

      protected String getRecipeInfo() {
         return LogHelper.getStackDescription(this.stack);
      }
   }

   private static class AddGem extends BaseAction {
      private ItemStack stack;
      private int energy;

      protected AddGem(ItemStack stack, int energy) {
         super("NumisticDynamoGem");
         this.stack = stack;
         this.energy = energy;
      }

      public void apply() {
         NumismaticManager.addGemFuel(this.stack, this.energy);
      }

      protected String getRecipeInfo() {
         return LogHelper.getStackDescription(this.stack);
      }
   }

   private static class Remove extends BaseAction {
      private ItemStack stack;

      protected Remove(ItemStack stack) {
         super("NumisticDynamo");
         this.stack = stack;
      }

      public void apply() {
         NumismaticManager.removeFuel(this.stack);
      }

      protected String getRecipeInfo() {
         return LogHelper.getStackDescription(this.stack);
      }
   }

   private static class RemoveGem extends BaseAction {
      private ItemStack stack;

      protected RemoveGem(ItemStack stack) {
         super("NumisticDynamoGem");
         this.stack = stack;
      }

      public void apply() {
         NumismaticManager.removeGemFuel(this.stack);
      }

      protected String getRecipeInfo() {
         return LogHelper.getStackDescription(this.stack);
      }
   }
}
