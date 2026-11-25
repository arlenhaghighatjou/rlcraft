package com.blamejared.compat.botania.handlers;

import com.blamejared.compat.botania.LocalizationHelper;
import crafttweaker.CraftTweakerAPI;
import crafttweaker.IAction;
import crafttweaker.annotations.ModOnly;
import crafttweaker.annotations.ZenRegister;
import net.minecraft.util.text.TextFormatting;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;
import vazkii.botania.api.BotaniaAPI;

@ZenClass("mods.botania.Knowledge")
@ModOnly("botania")
@ZenRegister
public class Knowledge {
   private static final String typeName = "Botania Knowledge Variable:";

   public Knowledge() {
   }

   @ZenMethod
   public static void registerKnowledgeType(String unlocalized, String localized, String color, boolean autoUnlock) {
      if (unlocalized != null && !unlocalized.isEmpty()) {
         if (color != null && !color.isEmpty()) {
            LocalizationHelper.setLocale("botania.knowledge." + unlocalized, localized);
            CraftTweakerAPI.apply(new Knowledge.Add(unlocalized, color, autoUnlock));
         } else {
            CraftTweakerAPI.logError("Found null String in Botania Knowledge Variable: color");
         }
      } else {
         CraftTweakerAPI.logError("Found null String in Botania Knowledge Variable: name");
      }

   }

   private static class Add implements IAction {
      String unlocalized;
      String color;
      boolean autoUnlock;

      public Add(String name, String color, boolean autoUnlock) {
         this.unlocalized = name;
         this.color = color;
         this.autoUnlock = autoUnlock;
      }

      public void apply() {
         BotaniaAPI.registerKnowledgeType(this.unlocalized, TextFormatting.getValueByName(this.color.toUpperCase()), this.autoUnlock);
      }

      public String describe() {
         String Output;
         if (this.autoUnlock) {
            Output = "Enabled";
         } else {
            Output = "Disabled";
         }

         return "Adding Knowledge Type: " + this.unlocalized + " With Auto-Unlock: " + Output;
      }
   }
}
