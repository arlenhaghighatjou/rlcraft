package com.blamejared.compat.botania.lexicon;

import com.blamejared.compat.botania.BotaniaHelper;
import com.blamejared.mtlib.helpers.InputHelper;
import com.blamejared.mtlib.helpers.LogHelper;
import crafttweaker.CraftTweakerAPI;
import crafttweaker.IAction;
import crafttweaker.api.item.IItemStack;
import vazkii.botania.api.BotaniaAPI;
import vazkii.botania.api.lexicon.LexiconCategory;
import vazkii.botania.api.lexicon.LexiconEntry;

public class AddEntry implements IAction {
   private String entry;
   private String catagory;
   private IItemStack stack;

   public AddEntry(String entry, String catagory, IItemStack stack) {
      this.entry = entry;
      this.catagory = catagory;
      this.stack = stack;
   }

   public void apply() {
      LexiconCategory lexiconCategory = BotaniaHelper.findCatagory(this.catagory);
      if (lexiconCategory == null) {
         CraftTweakerAPI.getLogger().logError("Cannot find lexicon category " + this.catagory);
      } else {
         LexiconEntry lexiconEntry = new UsefulEntry(this.entry, lexiconCategory);
         lexiconEntry.setIcon(InputHelper.toStack(this.stack));
         BotaniaAPI.addEntry(lexiconEntry, lexiconEntry.category);
      }
   }

   public String describe() {
      return "Adding Lexicon entry: " + LogHelper.getStackDescription(this.entry);
   }
}
