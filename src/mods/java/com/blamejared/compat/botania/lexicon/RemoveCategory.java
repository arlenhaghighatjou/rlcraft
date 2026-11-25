package com.blamejared.compat.botania.lexicon;

import com.blamejared.compat.botania.BotaniaHelper;
import crafttweaker.CraftTweakerAPI;
import crafttweaker.IAction;
import vazkii.botania.api.BotaniaAPI;
import vazkii.botania.api.lexicon.LexiconCategory;

public class RemoveCategory implements IAction {
   private final String name;
   private LexiconCategory category;

   public RemoveCategory(String name) {
      this.name = name;
   }

   public void apply() {
      this.category = BotaniaHelper.findCatagory(this.name);
      if (this.category == null) {
         CraftTweakerAPI.getLogger().logError("Cannot find lexicon category " + this.name);
      } else {
         BotaniaAPI.getAllCategories().remove(this.category);
         CraftTweakerAPI.getLogger().logInfo("Removing Lexicon Category: " + this.category.getUnlocalizedName());
      }
   }

   public String describe() {
      return "Attempting to remove Lexicon Category " + this.name;
   }
}
