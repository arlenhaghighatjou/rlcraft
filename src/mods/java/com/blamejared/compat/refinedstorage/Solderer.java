package com.blamejared.compat.refinedstorage;

import crafttweaker.CraftTweakerAPI;
import crafttweaker.annotations.ModOnly;
import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.item.IItemStack;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

/** @deprecated */
@ZenClass("mods.refinedstorage.Solderer")
@ModOnly("refinedstorage")
@ZenRegister
@Deprecated
public class Solderer {
   public Solderer() {
   }

   /** @deprecated */
   @ZenMethod
   @Deprecated
   public static void addRecipe(String name, IItemStack output, int time, IItemStack[] rows) {
      CraftTweakerAPI.logError("The solderer has been removed from refined storage in 1.6, nothing to do");
   }

   /** @deprecated */
   @ZenMethod
   @Deprecated
   public static void removeRecipe(IItemStack output) {
      CraftTweakerAPI.logError("The solderer has been removed from refined storage in 1.6, nothing to do");
   }
}
