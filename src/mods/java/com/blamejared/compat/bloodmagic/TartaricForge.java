package com.blamejared.compat.bloodmagic;

import WayofTime.bloodmagic.api.impl.BloodMagicAPI;
import com.blamejared.ModTweaker;
import com.blamejared.mtlib.helpers.InputHelper;
import com.blamejared.mtlib.utils.BaseAction;
import crafttweaker.CraftTweakerAPI;
import crafttweaker.annotations.ModOnly;
import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.item.IIngredient;
import crafttweaker.api.item.IItemStack;
import net.minecraft.item.ItemStack;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

@ZenClass("mods.bloodmagic.TartaricForge")
@ZenRegister
@ModOnly("bloodmagic")
public class TartaricForge {
   public TartaricForge() {
   }

   @ZenMethod
   public static void addRecipe(IItemStack output, IIngredient[] inputs, double minSouls, double soulDrain) {
      if (inputs.length != 0 && inputs.length <= 4) {
         ModTweaker.LATE_ADDITIONS.add(new TartaricForge.Add(InputHelper.toStack(output), minSouls, soulDrain, InputHelper.toObjects(inputs)));
      } else {
         CraftTweakerAPI.logError("Invalid Input Array! Maximum size is 4!");
      }
   }

   @ZenMethod
   public static void removeRecipe(IItemStack[] inputs) {
      ModTweaker.LATE_REMOVALS.add(new TartaricForge.Remove(InputHelper.toStacks(inputs)));
   }

   public static String[] getStringFromStacks(Object[] arr) {
      String[] retArr = new String[arr.length];

      for(int i = 0; i < arr.length; ++i) {
         retArr[i] = arr[i].toString();
      }

      return retArr;
   }

   private static class Add extends BaseAction {
      private ItemStack output;
      private double minSouls;
      private double soulDrain;
      private Object[] inputs;

      public Add(ItemStack output, double minSouls, double soulDrain, Object[] inputs) {
         super("TartaricForge");
         this.output = output;
         this.minSouls = minSouls;
         this.soulDrain = soulDrain;
         this.inputs = inputs;
      }

      public void apply() {
         BloodMagicAPI.INSTANCE.getRecipeRegistrar().addTartaricForge(this.output, this.minSouls, this.soulDrain, this.inputs);
      }

      public String describe() {
         return "Adding TartaricForge recipe for: " + this.output + " from: [" + String.join(",", TartaricForge.getStringFromStacks(this.inputs)) + "] minSouls: " + this.minSouls + ", soulDrain: " + this.soulDrain;
      }
   }

   private static class Remove extends BaseAction {
      private ItemStack[] inputs;

      public Remove(ItemStack[] inputs) {
         super("TartaricForge");
         this.inputs = inputs;
      }

      public void apply() {
         BloodMagicAPI.INSTANCE.getRecipeRegistrar().removeTartaricForge(this.inputs);
      }

      public String describe() {
         return "Removing TartaricForge recipe for: [" + String.join(",", TartaricForge.getStringFromStacks(this.inputs)) + "]";
      }
   }
}
