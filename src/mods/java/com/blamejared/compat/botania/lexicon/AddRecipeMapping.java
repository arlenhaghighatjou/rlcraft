package com.blamejared.compat.botania.lexicon;

import com.blamejared.compat.botania.BotaniaHelper;
import com.blamejared.mtlib.helpers.InputHelper;
import com.blamejared.mtlib.helpers.LogHelper;
import crafttweaker.CraftTweakerAPI;
import crafttweaker.IAction;
import crafttweaker.api.item.IItemStack;
import vazkii.botania.api.lexicon.LexiconEntry;
import vazkii.botania.api.lexicon.LexiconRecipeMappings;

public class AddRecipeMapping implements IAction {
   private IItemStack stack;
   private String entry;
   private int page;

   public AddRecipeMapping(IItemStack stack, String entry, int page) {
      this.stack = stack;
      this.entry = entry;
      this.page = page;
   }

   public void apply() {
      LexiconEntry lexiconentry = BotaniaHelper.findEntry(this.entry);
      if (LexiconRecipeMappings.getDataForStack(InputHelper.toStack(this.stack)) != null) {
         CraftTweakerAPI.getLogger().logError("There is already a recipe mapping for " + this.stack);
      } else if (lexiconentry == null) {
         CraftTweakerAPI.getLogger().logError("Cannot find lexicon entry " + this.entry);
      } else if (lexiconentry.pages.size() < this.page) {
         CraftTweakerAPI.getLogger().logError("Not enough pages in " + this.entry);
      } else {
         LexiconRecipeMappings.map(InputHelper.toStack(this.stack), lexiconentry, this.page);
      }
   }

   public String describe() {
      return "Adding Lexicon Recipe Lookup: " + LogHelper.getStackDescription(this.stack);
   }
}
