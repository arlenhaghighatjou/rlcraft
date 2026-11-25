package com.blamejared.compat.botania.lexicon;

import crafttweaker.IAction;
import vazkii.botania.api.BotaniaAPI;
import vazkii.botania.api.lexicon.LexiconCategory;

public class AddCategory implements IAction {
   private LexiconCategory category;

   public AddCategory(String category) {
      this.category = new LexiconCategory(category);
   }

   public void apply() {
      BotaniaAPI.addCategory(this.category);
   }

   public String describe() {
      return "Adding Lexicon Category: " + this.category.getUnlocalizedName();
   }
}
