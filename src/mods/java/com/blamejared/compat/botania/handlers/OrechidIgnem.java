package com.blamejared.compat.botania.handlers;

import com.blamejared.ModTweaker;
import crafttweaker.IAction;
import crafttweaker.annotations.ModOnly;
import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.oredict.IOreDictEntry;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;
import vazkii.botania.api.BotaniaAPI;

@ZenClass("mods.botania.OrechidIgnem")
@ModOnly("botania")
@ZenRegister
public class OrechidIgnem {
   public OrechidIgnem() {
   }

   @ZenMethod
   public static void addOre(IOreDictEntry oreDict, int weight) {
      ModTweaker.LATE_ADDITIONS.add(new OrechidIgnem.Add(oreDict.getName(), weight));
   }

   @ZenMethod
   public static void addOre(String oreDict, int weight) {
      ModTweaker.LATE_ADDITIONS.add(new OrechidIgnem.Add(oreDict, weight));
   }

   @ZenMethod
   public static void removeOre(IOreDictEntry oreDict) {
      ModTweaker.LATE_REMOVALS.add(new OrechidIgnem.Remove(oreDict.getName()));
   }

   @ZenMethod
   public static void removeOre(String oreDict) {
      ModTweaker.LATE_REMOVALS.add(new OrechidIgnem.Remove(oreDict));
   }

   private static class Add implements IAction {
      String oreDict;
      int weight;

      public Add(String ore, int prop) {
         this.oreDict = ore;
         this.weight = prop;
      }

      public void apply() {
         BotaniaAPI.addOreWeightNether(this.oreDict, this.weight);
      }

      public String describe() {
         return "Adding OrechidIgnem Ore Weight: " + this.oreDict + ":" + this.weight;
      }
   }

   private static class Remove implements IAction {
      String oreDict;

      public Remove(String ore) {
         this.oreDict = ore;
      }

      public void apply() {
         BotaniaAPI.oreWeightsNether.remove(this.oreDict);
      }

      public String describe() {
         return "Removing Orechid Ore: " + this.oreDict;
      }
   }
}
