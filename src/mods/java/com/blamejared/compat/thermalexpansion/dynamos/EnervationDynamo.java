package com.blamejared.compat.thermalexpansion.dynamos;

import cofh.thermalexpansion.util.managers.dynamo.EnervationManager;
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

@ZenClass("mods.thermalexpansion.EnervationDynamo")
@ModOnly("thermalexpansion")
@ZenRegister
public class EnervationDynamo {
   public EnervationDynamo() {
   }

   @ZenMethod
   public static void addFuel(IItemStack stack, int energy) {
      ModTweaker.LATE_ADDITIONS.add(new EnervationDynamo.Add(InputHelper.toStack(stack), energy));
   }

   @ZenMethod
   public static void removeFuel(IItemStack stack) {
      ModTweaker.LATE_REMOVALS.add(new EnervationDynamo.Remove(InputHelper.toStack(stack)));
   }

   private static class Add extends BaseAction {
      private ItemStack stack;
      private int energy;

      protected Add(ItemStack stack, int energy) {
         super("EnervationDynamo");
         this.stack = stack;
         this.energy = energy;
      }

      public void apply() {
         EnervationManager.addFuel(this.stack, this.energy);
      }

      protected String getRecipeInfo() {
         return LogHelper.getStackDescription(this.stack);
      }
   }

   private static class Remove extends BaseAction {
      private ItemStack stack;

      protected Remove(ItemStack stack) {
         super("EnervationDynamo");
         this.stack = stack;
      }

      public void apply() {
         EnervationManager.removeFuel(this.stack);
      }

      protected String getRecipeInfo() {
         return LogHelper.getStackDescription(this.stack);
      }
   }
}
