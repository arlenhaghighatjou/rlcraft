package com.blamejared.compat.botania.lexicon.pages;

import com.blamejared.compat.botania.BotaniaHelper;
import com.blamejared.mtlib.helpers.InputHelper;
import crafttweaker.CraftTweakerAPI;
import crafttweaker.IAction;
import crafttweaker.api.item.IIngredient;
import vazkii.botania.api.BotaniaAPI;
import vazkii.botania.api.lexicon.LexiconEntry;
import vazkii.botania.api.lexicon.LexiconPage;
import vazkii.botania.api.recipe.RecipeBrew;
import vazkii.botania.common.lexicon.page.PageBrew;

public class AddPageBrew implements IAction {
   private String name;
   private String entry;
   private int page_number;
   private String brew;
   private IIngredient[] recipe;
   private String bottomText;

   public AddPageBrew(String name, String entry, int page_number, String brew, IIngredient[] recipe, String bottomText) {
      this.name = name;
      this.entry = entry;
      this.page_number = page_number;
      this.brew = brew;
      this.recipe = recipe;
      this.bottomText = bottomText;
   }

   public void apply() {
      LexiconEntry lexiconEntry = BotaniaHelper.findEntry(this.entry);
      if (lexiconEntry == null) {
         CraftTweakerAPI.getLogger().logError("Cannot find lexicon entry " + this.entry);
      } else if (this.page_number > lexiconEntry.pages.size()) {
         CraftTweakerAPI.getLogger().logError("Page Number " + this.page_number + " out of bounds for " + this.entry);
      } else if (BotaniaAPI.getBrewFromKey(this.brew) == null) {
         CraftTweakerAPI.getLogger().logError("Cannot find brew " + this.brew);
      } else {
         RecipeBrew page_recipe = new RecipeBrew(BotaniaAPI.getBrewFromKey(this.brew), InputHelper.toObjects(this.recipe));
         LexiconPage page = new PageBrew(page_recipe, this.name, this.bottomText);
         lexiconEntry.pages.add(this.page_number, page);
      }
   }

   public String describe() {
      return "Adding Lexicon Page: " + this.name;
   }
}
