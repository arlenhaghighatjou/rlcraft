package com.blamejared.compat.botania.lexicon;

import com.blamejared.compat.botania.BotaniaHelper;
import crafttweaker.CraftTweakerAPI;
import crafttweaker.IAction;
import vazkii.botania.api.lexicon.LexiconEntry;
import vazkii.botania.api.lexicon.LexiconPage;

public class RemovePage implements IAction {
   private int page_number;
   private LexiconEntry lexEntry;
   private LexiconPage page;
   private final String entry;

   public RemovePage(String entry, int page_number) {
      this.entry = entry;
      this.page_number = page_number;
   }

   public void apply() {
      this.lexEntry = BotaniaHelper.findEntry(this.entry);
      if (this.lexEntry == null) {
         CraftTweakerAPI.getLogger().logError("Cannot find lexicon entry " + this.entry);
      } else if (this.page_number >= this.lexEntry.pages.size()) {
         CraftTweakerAPI.getLogger().logError("Page Number " + this.page_number + " out of bounds for " + this.entry);
      } else {
         this.page = (LexiconPage)this.lexEntry.pages.get(this.page_number);
         this.lexEntry.pages.remove(this.page);
         CraftTweakerAPI.getLogger().logInfo("Removed Lexicon Page: " + this.page.getUnlocalizedName());
      }
   }

   public String describe() {
      return "Attempting to remove Lexicon Page " + this.page_number + " from entry " + this.entry;
   }
}
