package com.blamejared.compat.betterwithmods;

import crafttweaker.annotations.ModOnly;
import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.item.IIngredient;
import crafttweaker.api.item.IItemStack;
import stanhebben.zenscript.annotations.NotNull;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

/** @deprecated */
@Deprecated
@ZenClass("mods.betterwithmods.StokedCauldron")
@ModOnly("betterwithmods")
@ZenRegister
public class StokedCauldron {
   public StokedCauldron() {
   }

   /** @deprecated */
   @Deprecated
   @ZenMethod
   public static void add(IItemStack output, IItemStack secondaryOutput, @NotNull IIngredient[] inputs) {
      Cauldron.INSTANCE.buildRecipe(inputs, new IItemStack[]{output, secondaryOutput}).setHeat(2).build();
   }

   /** @deprecated */
   @Deprecated
   @ZenMethod
   public static void add(IItemStack output, @NotNull IIngredient[] inputs) {
      Cauldron.INSTANCE.buildRecipe(inputs, new IItemStack[]{output}).setHeat(2).build();
   }

   /** @deprecated */
   @Deprecated
   @ZenMethod
   public static void remove(IItemStack[] output) {
      Cauldron.remove(output);
   }
}
