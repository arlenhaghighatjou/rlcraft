package com.blamejared.compat.extrautils2;

import com.blamejared.ModTweaker;
import com.blamejared.mtlib.helpers.InputHelper;
import com.blamejared.mtlib.helpers.LogHelper;
import com.blamejared.mtlib.helpers.StackHelper;
import com.blamejared.mtlib.utils.BaseAction;
import com.rwtema.extrautils2.crafting.ResonatorRecipe;
import com.rwtema.extrautils2.tile.TileResonator;
import crafttweaker.annotations.ModOnly;
import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.item.IItemStack;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.item.ItemStack;
import stanhebben.zenscript.annotations.Optional;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

@ZenClass("mods.extrautils2.Resonator")
@ModOnly("extrautils2")
@ZenRegister
public class Resonator {
   public Resonator() {
   }

   @ZenMethod
   public static void add(IItemStack output, IItemStack input, int energy, @Optional boolean addOwnerTag) {
      ModTweaker.LATE_ADDITIONS.add(new Resonator.Add(InputHelper.toStack(output), InputHelper.toStack(input), energy, addOwnerTag));
   }

   @ZenMethod
   public static void remove(IItemStack output) {
      ModTweaker.LATE_REMOVALS.add(new Resonator.Remove(output));
   }

   private static class Add extends BaseAction {
      private ItemStack output;
      private ItemStack input;
      private int energy;
      private boolean ownerTag;

      public Add(ItemStack output, ItemStack input, int energy, boolean ownerTag) {
         super("Resonator");
         this.output = output;
         this.input = input;
         this.energy = energy;
         this.ownerTag = ownerTag;
      }

      public void apply() {
         TileResonator.register(this.input, this.output, this.energy, this.ownerTag);
      }

      protected String getRecipeInfo() {
         return LogHelper.getStackDescription(this.output);
      }
   }

   private static class Remove extends BaseAction {
      private IItemStack output;

      public Remove(IItemStack output) {
         super("Resonator");
         this.output = output;
      }

      public void apply() {
         List<ResonatorRecipe> list = new ArrayList();

         for(ResonatorRecipe recipe : TileResonator.resonatorRecipes) {
            if (StackHelper.matches(this.output, InputHelper.toIItemStack(recipe.output))) {
               list.add(recipe);
            }
         }

         TileResonator.resonatorRecipes.removeAll(list);
      }

      protected String getRecipeInfo() {
         return LogHelper.getStackDescription(this.output);
      }
   }
}
