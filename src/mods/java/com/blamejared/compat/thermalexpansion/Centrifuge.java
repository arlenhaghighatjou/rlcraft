package com.blamejared.compat.thermalexpansion;

import cofh.core.util.helpers.ItemHelper;
import cofh.thermalexpansion.item.ItemMorb;
import cofh.thermalexpansion.util.managers.machine.CentrifugeManager;
import cofh.thermalfoundation.init.TFFluids;
import com.blamejared.ModTweaker;
import com.blamejared.mtlib.helpers.InputHelper;
import com.blamejared.mtlib.helpers.LogHelper;
import com.blamejared.mtlib.utils.BaseAction;
import crafttweaker.CraftTweakerAPI;
import crafttweaker.annotations.ModOnly;
import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.entity.IEntityDefinition;
import crafttweaker.api.item.IItemStack;
import crafttweaker.api.item.WeightedItemStack;
import crafttweaker.api.liquid.ILiquidStack;
import java.util.ArrayList;
import java.util.Arrays;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

@ZenClass("mods.thermalexpansion.Centrifuge")
@ModOnly("thermalexpansion")
@ZenRegister
public class Centrifuge {
   public Centrifuge() {
   }

   @ZenMethod
   public static void addRecipe(WeightedItemStack[] outputs, IItemStack input, ILiquidStack fluid, int energy) {
      IItemStack[] items = new IItemStack[outputs.length];
      Integer[] chances = new Integer[outputs.length];

      for(int i = 0; i < outputs.length; ++i) {
         items[i] = outputs[i].getStack();
         chances[i] = (int)outputs[i].getPercent();
      }

      ModTweaker.LATE_ADDITIONS.add(new Centrifuge.Add(InputHelper.toStacks(items), chances, InputHelper.toStack(input), energy, InputHelper.toFluid(fluid)));
   }

   @ZenMethod
   public static void addRecipeMob(String entityId, WeightedItemStack[] outputs, ILiquidStack fluid, int energy, int xp) {
      IItemStack[] items = new IItemStack[outputs.length];
      Integer[] chances = new Integer[outputs.length];
      if (energy <= 0) {
         energy = 4000;
      }

      for(int i = 0; i < outputs.length; ++i) {
         items[i] = outputs[i].getStack();
         chances[i] = (int)outputs[i].getPercent();
      }

      ModTweaker.LATE_ADDITIONS.add(new Centrifuge.AddMob(InputHelper.toStacks(items), chances, entityId, energy, InputHelper.toFluid(fluid), xp));
   }

   @ZenMethod
   public static void addRecipeMob(IEntityDefinition entity, WeightedItemStack[] outputs, ILiquidStack fluid, int energy, int xp) {
      addRecipeMob(entity.getId(), outputs, fluid, energy, xp);
   }

   @ZenMethod
   public static void removeRecipe(IItemStack input) {
      ModTweaker.LATE_REMOVALS.add(new Centrifuge.Remove(InputHelper.toStack(input)));
   }

   @ZenMethod
   public static void removeRecipeMob(String entityId) {
      ModTweaker.LATE_REMOVALS.add(new Centrifuge.RemoveMob(entityId));
   }

   @ZenMethod
   public static void removeRecipeMob(IEntityDefinition entity) {
      ModTweaker.LATE_REMOVALS.add(new Centrifuge.RemoveMob(entity.getId()));
   }

   private static class Add extends BaseAction {
      private ItemStack[] outputs;
      private Integer[] chances;
      private ItemStack input;
      private int energy;
      private FluidStack fluid;

      public Add(ItemStack[] outputs, Integer[] chances, ItemStack input, int energy, FluidStack fluid) {
         super("Centrifuge");
         this.outputs = outputs;
         this.chances = chances;
         this.input = input;
         this.energy = energy;
         this.fluid = fluid;
      }

      public void apply() {
         CentrifugeManager.addRecipe(this.energy, this.input, Arrays.asList(this.outputs), Arrays.asList(this.chances), this.fluid);
      }

      protected String getRecipeInfo() {
         return LogHelper.getStackDescription(this.input);
      }
   }

   private static class AddMob extends BaseAction {
      private ItemStack[] outputs;
      private Integer[] chances;
      private String entityId;
      private int energy;
      private FluidStack fluid;
      private int xp;

      public AddMob(ItemStack[] outputs, Integer[] chances, String entityId, int energy, FluidStack fluid, int xp) {
         super("Centrifuge");
         this.outputs = outputs;
         this.chances = chances;
         this.entityId = entityId;
         this.energy = energy;
         this.fluid = fluid;
         this.xp = xp;
      }

      public void apply() {
         if (!ItemMorb.validMobs.contains(this.entityId)) {
            CraftTweakerAPI.logError("Not a valid morb entity: " + this.entityId);
         } else {
            if (this.fluid == null) {
               this.fluid = new FluidStack(TFFluids.fluidExperience, this.xp * 20);
            }

            ArrayList<ItemStack> outputStandard = new ArrayList(Arrays.asList(this.outputs));
            ArrayList<ItemStack> outputReusable = new ArrayList(Arrays.asList(this.outputs));
            ArrayList<Integer> chanceStandard = new ArrayList(Arrays.asList(this.chances));
            ArrayList<Integer> chanceReusable = new ArrayList(Arrays.asList(this.chances));
            outputStandard.add(ItemHelper.cloneStack(ItemMorb.morbStandard));
            outputReusable.add(ItemHelper.cloneStack(ItemMorb.morbReusable));
            chanceStandard.add(Integer.valueOf(25));
            chanceReusable.add(Integer.valueOf(100));
            CentrifugeManager.addRecipeMob(this.energy, ItemMorb.setTag(ItemHelper.cloneStack(ItemMorb.morbStandard), this.entityId, false), outputStandard, chanceStandard, this.fluid);
            CentrifugeManager.addRecipeMob(this.energy, ItemMorb.setTag(ItemHelper.cloneStack(ItemMorb.morbReusable), this.entityId, false), outputReusable, chanceReusable, this.fluid);
         }
      }

      protected String getRecipeInfo() {
         return this.entityId;
      }
   }

   private static class Remove extends BaseAction {
      private ItemStack input;

      public Remove(ItemStack input) {
         super("Centrifuge");
         this.input = input;
      }

      public void apply() {
         if (!CentrifugeManager.recipeExists(this.input)) {
            CraftTweakerAPI.logError("No Centrifuge recipe exists for: " + this.input);
         } else {
            CentrifugeManager.removeRecipe(this.input);
         }
      }

      protected String getRecipeInfo() {
         return LogHelper.getStackDescription(this.input);
      }
   }

   private static class RemoveMob extends BaseAction {
      private String entityId;

      public RemoveMob(String entityId) {
         super("Centrifuge");
         this.entityId = entityId;
      }

      public void apply() {
         ItemStack standard = ItemMorb.setTag(ItemHelper.cloneStack(ItemMorb.morbStandard), this.entityId, false);
         ItemStack reusable = ItemMorb.setTag(ItemHelper.cloneStack(ItemMorb.morbReusable), this.entityId, false);
         if (!CentrifugeManager.recipeExistsMob(standard) && !CentrifugeManager.recipeExistsMob(reusable)) {
            CraftTweakerAPI.logError("No Centrifuge mob recipe exists for entity: " + this.entityId);
         } else {
            if (CentrifugeManager.recipeExistsMob(standard)) {
               CentrifugeManager.removeRecipeMob(standard);
            }

            if (CentrifugeManager.recipeExistsMob(reusable)) {
               CentrifugeManager.removeRecipeMob(reusable);
            }

         }
      }

      protected String getRecipeInfo() {
         return this.entityId;
      }
   }
}
