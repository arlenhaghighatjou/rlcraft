package com.blamejared.compat.botania.lexicon;

import com.blamejared.compat.botania.BotaniaHelper;
import crafttweaker.CraftTweakerAPI;
import crafttweaker.IAction;
import vazkii.botania.api.BotaniaAPI;
import vazkii.botania.api.lexicon.LexiconEntry;

public class RemoveEntry implements IAction {
   private LexiconEntry lexEntry;
   private final String entry;

   public RemoveEntry(String entry) {
      this.entry = entry;
   }

   public void apply() {
      this.lexEntry = BotaniaHelper.findEntry(this.entry);
      if (this.lexEntry == null) {
         CraftTweakerAPI.getLogger().logError("Cannot find lexicon entry " + this.entry);
      } else {
         this.lexEntry.category.entries.remove(this.lexEntry);
         BotaniaAPI.getAllEntries().remove(this.lexEntry);
         CraftTweakerAPI.getLogger().logInfo("Removing Lexicon entry: " + this.lexEntry.getUnlocalizedName());
      }
   }

   public String describe() {
      return "Attempting to remove Lexicon entry " + this.entry;
   }
}
