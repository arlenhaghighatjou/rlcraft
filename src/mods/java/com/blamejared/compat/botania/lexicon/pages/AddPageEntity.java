package com.blamejared.compat.botania.lexicon.pages;

import com.blamejared.compat.botania.BotaniaHelper;
import crafttweaker.CraftTweakerAPI;
import crafttweaker.IAction;
import net.minecraft.entity.EntityList;
import net.minecraft.util.ResourceLocation;
import vazkii.botania.api.lexicon.LexiconEntry;
import vazkii.botania.api.lexicon.LexiconPage;
import vazkii.botania.common.lexicon.page.PageEntity;

public class AddPageEntity implements IAction {
   private String name;
   private String entry;
   private int page_number;
   private String entity;
   private int size;

   public AddPageEntity(String name, String entry, int page_number, String entity, int size) {
      this.name = name;
      this.entry = entry;
      this.page_number = page_number;
      this.entity = entity;
      this.size = size;
   }

   public void apply() {
      LexiconEntry lexiconEntry = BotaniaHelper.findEntry(this.entry);
      if (lexiconEntry == null) {
         CraftTweakerAPI.getLogger().logError("Cannot find lexicon entry " + this.entry);
      } else if (this.page_number > lexiconEntry.pages.size()) {
         CraftTweakerAPI.getLogger().logError("Page Number " + this.page_number + " out of bounds for " + this.entry);
      } else if (!EntityList.isRegistered(new ResourceLocation(this.entity))) {
         CraftTweakerAPI.getLogger().logError("No such entity " + this.entity);
      } else {
         LexiconPage page = new PageEntity(this.entity, this.entity, this.size);
         lexiconEntry.pages.add(this.page_number, page);
      }
   }

   public String describe() {
      return "Adding Lexicon Page: " + this.name;
   }
}
