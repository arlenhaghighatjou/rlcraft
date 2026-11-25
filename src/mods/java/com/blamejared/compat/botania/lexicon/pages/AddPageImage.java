package com.blamejared.compat.botania.lexicon.pages;

import com.blamejared.compat.botania.BotaniaHelper;
import crafttweaker.CraftTweakerAPI;
import crafttweaker.IAction;
import vazkii.botania.api.lexicon.LexiconEntry;
import vazkii.botania.api.lexicon.LexiconPage;
import vazkii.botania.common.lexicon.page.PageImage;

public class AddPageImage implements IAction {
   private String name;
   private String entry;
   private int page_number;
   private String resource;

   public AddPageImage(String name, String entry, int page_number, String resource) {
      this.name = name;
      this.entry = entry;
      this.page_number = page_number;
      this.resource = resource;
   }

   public void apply() {
      LexiconEntry lexiconEntry = BotaniaHelper.findEntry(this.entry);
      if (lexiconEntry == null) {
         CraftTweakerAPI.getLogger().logError("Cannot find lexicon entry " + this.entry);
      } else if (this.page_number > lexiconEntry.pages.size()) {
         CraftTweakerAPI.getLogger().logError("Page Number " + this.page_number + " out of bounds for " + this.entry);
      } else {
         LexiconPage page = new PageImage(this.name, this.resource);
         lexiconEntry.pages.add(this.page_number, page);
      }
   }

   public String describe() {
      return "Adding Lexicon Page: " + this.name;
   }
}
