package com.blamejared.compat.thermalexpansion;

import cofh.thermalexpansion.util.managers.machine.EnchanterManager;
import cofh.thermalexpansion.util.managers.machine.EnchanterManager.Type;
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

@ZenClass("mods.thermalexpansion.Enchanter")
@ModOnly("thermalexpansion")
@ZenRegister
public class Enchanter {
   public Enchanter() {
   }

   @ZenMethod
   public static void addRecipe(IItemStack output, IItemStack input, IItemStack secondInput, int energy, int experience, boolean empowered) {
      ModTweaker.LATE_ADDITIONS.add(new Enchanter.Add(InputHelper.toStack(output), InputHelper.toStack(input), InputHelper.toStack(secondInput), energy, experience, empowered ? Type.EMPOWERED : Type.STANDARD));
   }

   @ZenMethod
   public static void removeRecipe(IItemStack input, IItemStack secondInput) {
      ModTweaker.LATE_REMOVALS.add(new Enchanter.Remove(InputHelper.toStack(input), InputHelper.toStack(secondInput)));
   }

   private static class Add extends BaseAction {
      private ItemStack output;
      private ItemStack input;
      private ItemStack inputSecondary;
      private int energy;
      private int experience;
      private Type type;

      public Add(ItemStack output, ItemStack input, ItemStack inputSecondary, int energy, int experience, Type type) {
         super("Enchanter");
         this.output = output;
         this.input = input;
         this.inputSecondary = inputSecondary;
         this.energy = energy;
         this.experience = experience;
         this.type = type;
      }

      public void apply() {
         EnchanterManager.addRecipe(this.energy, this.input, this.inputSecondary, this.output, this.experience, this.type);
      }

      protected String getRecipeInfo() {
         return LogHelper.getStackDescription(this.input) + " and " + LogHelper.getStackDescription(this.inputSecondary);
      }
   }

   private static class Remove extends BaseAction {
      private ItemStack input;
      private ItemStack inputSecondary;

      public Remove(ItemStack input, ItemStack inputSecondary) {
         super("Enchanter");
         this.input = input;
         this.inputSecondary = inputSecondary;
      }

      public void apply() {
         if (!EnchanterManager.recipeExists(this.input, this.inputSecondary)) {
            CraftTweakerAPI.logError("No Enchanter recipe exists for: " + this.input + " and " + this.inputSecondary);
         } else {
            EnchanterManager.removeRecipe(this.input, this.inputSecondary);
         }
      }

      protected String getRecipeInfo() {
         return LogHelper.getStackDescription(this.input) + " and " + LogHelper.getStackDescription(this.inputSecondary);
      }
   }
}
