package com.blamejared.compat.bloodmagic;

import WayofTime.bloodmagic.api.impl.BloodMagicAPI;
import com.blamejared.ModTweaker;
import com.blamejared.mtlib.helpers.InputHelper;
import com.blamejared.mtlib.utils.BaseAction;
import crafttweaker.annotations.ModOnly;
import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.item.IItemStack;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

@ZenClass("mods.bloodmagic.BloodAltar")
@ZenRegister
@ModOnly("bloodmagic")
public class BloodAltar {
   public BloodAltar() {
   }

   @ZenMethod
   public static void addRecipe(IItemStack output, IItemStack input, int minimumTier, int syphon, int consumeRate, int drainRate) {
      ModTweaker.LATE_ADDITIONS.add(new BloodAltar.Add(Ingredient.fromStacks(new ItemStack[]{InputHelper.toStack(input)}), InputHelper.toStack(output), minimumTier, syphon, consumeRate, drainRate));
   }

   @ZenMethod
   public static void removeRecipe(IItemStack input) {
      ModTweaker.LATE_REMOVALS.add(new BloodAltar.Remove(InputHelper.toStack(input)));
   }

   private static class Add extends BaseAction {
      private Ingredient input;
      private ItemStack output;
      private int minTier;
      private int syphon;
      private int consumeRate;
      private int drainRate;

      public Add(Ingredient input, ItemStack output, int minTier, int syphon, int consumeRate, int drainRate) {
         super("BloodAltar");
         this.input = input;
         this.output = output;
         this.minTier = minTier;
         this.syphon = syphon;
         this.consumeRate = consumeRate;
         this.drainRate = drainRate;
      }

      public void apply() {
         BloodMagicAPI.INSTANCE.getRecipeRegistrar().addBloodAltar(this.input, this.output, this.minTier, this.syphon, this.consumeRate, this.drainRate);
      }

      public String describe() {
         return "Adding Blood Altar recipe for: " + this.output + " from: " + this.input + " minTier: " + this.minTier + ", syphon: " + this.syphon + ", consumeRate: " + this.consumeRate + ", drainRate: " + this.drainRate;
      }
   }

   private static class Remove extends BaseAction {
      private ItemStack input;

      public Remove(ItemStack input) {
         super("BloodAltar");
         this.input = input;
      }

      public void apply() {
         BloodMagicAPI.INSTANCE.getRecipeRegistrar().removeBloodAltar(this.input);
      }

      public String describe() {
         return "Removing Blood Altar recipe for: " + this.input;
      }
   }
}
