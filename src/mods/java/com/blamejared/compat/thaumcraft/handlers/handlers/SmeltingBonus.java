package com.blamejared.compat.thaumcraft.handlers.handlers;

import com.blamejared.ModTweaker;
import com.blamejared.mtlib.helpers.InputHelper;
import com.blamejared.mtlib.helpers.LogHelper;
import com.blamejared.mtlib.utils.BaseAction;
import crafttweaker.CraftTweakerAPI;
import crafttweaker.annotations.ModOnly;
import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.item.IIngredient;
import crafttweaker.api.item.IItemStack;
import crafttweaker.api.item.WeightedItemStack;
import crafttweaker.api.oredict.IOreDictEntry;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.item.ItemStack;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.ThaumcraftApi.SmeltBonus;
import thaumcraft.api.internal.CommonInternals;

@ZenClass("mods.thaumcraft.SmeltingBonus")
@ZenRegister
@ModOnly("thaumcraft")
public class SmeltingBonus {
   public SmeltingBonus() {
   }

   @ZenMethod
   public static void addSmeltingBonus(IIngredient input, WeightedItemStack stack) {
      if (!(input instanceof IItemStack) && !(input instanceof IOreDictEntry)) {
         CraftTweakerAPI.logError("Invalid input for SmeltingBonus");
      } else {
         ModTweaker.LATE_ADDITIONS.add(new SmeltingBonus.Add(InputHelper.toObject(input), InputHelper.toStack(stack.getStack()), stack.getChance()));
      }
   }

   @ZenMethod
   public static void removeSmeltingBonus(IIngredient input, IItemStack stack) {
      if (!(input instanceof IItemStack) && !(input instanceof IOreDictEntry)) {
         CraftTweakerAPI.logError("Invalid input for SmeltingBonus");
      } else {
         ModTweaker.LATE_ADDITIONS.add(new SmeltingBonus.Remove(InputHelper.toObject(input), stack));
      }
   }

   private static class Add extends BaseAction {
      private ItemStack output;
      private Object input;
      private float chance;

      public Add(Object input, ItemStack output, float chance) {
         super("SmeltingBonus");
         this.output = output;
         this.input = input;
         this.chance = chance;
      }

      public void apply() {
         ThaumcraftApi.addSmeltingBonus(this.input, this.output, this.chance);
      }

      public String describe() {
         return "Adding " + this.getRecipeInfo() + " as a possible smelting bonus for: " + this.input;
      }

      protected String getRecipeInfo() {
         return LogHelper.getStackDescription(this.output);
      }
   }

   private static class Remove extends BaseAction {
      private IItemStack output;
      private Object input;

      public Remove(Object input, IItemStack output) {
         super("SmeltingBonus");
         this.output = output;
         this.input = input;
      }

      public void apply() {
         List<SmeltBonus> remove = new ArrayList();

         for(SmeltBonus bonus : CommonInternals.smeltingBonus) {
            if (bonus.in.equals(this.input) && this.output.matches(InputHelper.toIItemStack(bonus.out))) {
               remove.add(bonus);
            }
         }

         CommonInternals.smeltingBonus.removeAll(remove);
      }

      public String describe() {
         return "Removing " + this.getRecipeInfo() + " as a possible smelting bonus for: " + this.input;
      }

      protected String getRecipeInfo() {
         return LogHelper.getStackDescription(this.output);
      }
   }
}
