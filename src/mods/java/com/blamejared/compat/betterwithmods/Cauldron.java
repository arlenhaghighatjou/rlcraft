package com.blamejared.compat.betterwithmods;

import betterwithmods.common.BWRegistry;
import com.blamejared.compat.betterwithmods.base.bulkrecipes.CookingPotBuilder;
import crafttweaker.annotations.ModOnly;
import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.item.IIngredient;
import crafttweaker.api.item.IItemStack;
import java.util.function.Supplier;
import stanhebben.zenscript.annotations.NotNull;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

@ZenClass("mods.betterwithmods.Cauldron")
@ModOnly("betterwithmods")
@ZenRegister
public class Cauldron {
   public static CookingPotBuilder INSTANCE = new CookingPotBuilder(() -> {
      return BWRegistry.CAULDRON;
   }, "Cauldron");

   public Cauldron() {
   }

   @ZenMethod
   public static CookingPotBuilder builder() {
      return INSTANCE;
   }

   @ZenMethod
   public static void addStoked(IIngredient[] inputs, IItemStack[] outputs) {
      INSTANCE.buildRecipe(inputs, outputs).setHeat(2).build();
   }

   @ZenMethod
   public static void addUnstoked(IIngredient[] inputs, IItemStack[] outputs) {
      INSTANCE.buildRecipe(inputs, outputs).setHeat(1).build();
   }

   /** @deprecated */
   @Deprecated
   @ZenMethod
   public static void add(IItemStack output, IItemStack secondaryOutput, @NotNull IIngredient[] inputs) {
      addUnstoked(inputs, new IItemStack[]{output, secondaryOutput});
   }

   /** @deprecated */
   @Deprecated
   @ZenMethod
   public static void add(IItemStack output, @NotNull IIngredient[] inputs) {
      addUnstoked(inputs, new IItemStack[]{output});
   }

   @ZenMethod
   public static void remove(IItemStack[] output) {
      INSTANCE.removeRecipe(output);
   }

   @ZenMethod
   public static void removeAll() {
      builder().removeAll();
   }
}
