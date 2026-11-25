package com.blamejared.compat.thaumcraft.handlers.handlers;

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
import thaumcraft.api.ThaumcraftApi;

@ZenClass("mods.thaumcraft.Warp")
@ZenRegister
@ModOnly("thaumcraft")
public class Warp {
   public Warp() {
   }

   @ZenMethod
   public static void setWarp(IItemStack stack, int amount) {
      ModTweaker.LATE_ADDITIONS.add(new Warp.Set(InputHelper.toStack(stack), amount));
   }

   private static class Set extends BaseAction {
      private ItemStack stack;
      private int amount;

      public Set(ItemStack stack, int amount) {
         super("Warp");
         this.stack = stack;
         this.amount = amount;
      }

      public void apply() {
         ThaumcraftApi.addWarpToItem(this.stack, this.amount);
      }

      public String describe() {
         return "Adding " + this.amount + " warp to item: " + this.getRecipeInfo();
      }

      protected String getRecipeInfo() {
         return LogHelper.getStackDescription(this.stack);
      }
   }
}
