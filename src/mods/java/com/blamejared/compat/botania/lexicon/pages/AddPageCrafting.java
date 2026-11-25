package com.blamejared.compat.botania.lexicon.pages;

import com.blamejared.compat.botania.BotaniaHelper;
import crafttweaker.CraftTweakerAPI;
import crafttweaker.IAction;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.util.ResourceLocation;
import vazkii.botania.api.lexicon.LexiconEntry;
import vazkii.botania.api.lexicon.LexiconPage;
import vazkii.botania.common.lexicon.page.PageCraftingRecipe;

public class AddPageCrafting implements IAction {
   private String name;
   private String entry;
   private int page_number;
   private String[] recipeNames;

   public AddPageCrafting(String name, String entry, int page_number, String[] recipeNames) {
      this.name = name;
      this.entry = entry;
      this.page_number = page_number;
      this.recipeNames = recipeNames;
   }

   public void apply() {
      LexiconEntry lexiconEntry = BotaniaHelper.findEntry(this.entry);
      if (lexiconEntry == null) {
         CraftTweakerAPI.getLogger().logError("Cannot find lexicon entry " + this.entry);
      } else if (this.page_number > lexiconEntry.pages.size()) {
         CraftTweakerAPI.getLogger().logError("Page Number " + this.page_number + " out of bounds for " + this.entry);
      } else {
         List<ResourceLocation> recipes = new ArrayList();

         for(String s : this.recipeNames) {
            recipes.add(new ResourceLocation(s));
         }

         LexiconPage page = new PageCraftingRecipe(this.name, recipes);
         lexiconEntry.pages.add(this.page_number, page);
      }
   }

   public String describe() {
      return "Adding Lexicon Page: " + this.name;
   }
}
