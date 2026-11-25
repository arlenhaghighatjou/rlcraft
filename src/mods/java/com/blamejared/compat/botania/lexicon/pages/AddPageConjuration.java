package com.blamejared.compat.botania.lexicon.pages;

import com.blamejared.compat.botania.BotaniaHelper;
import com.blamejared.mtlib.helpers.InputHelper;
import crafttweaker.CraftTweakerAPI;
import crafttweaker.IAction;
import crafttweaker.api.item.IIngredient;
import crafttweaker.api.item.IItemStack;
import java.util.ArrayList;
import java.util.List;
import vazkii.botania.api.lexicon.LexiconEntry;
import vazkii.botania.api.lexicon.LexiconPage;
import vazkii.botania.api.recipe.RecipeManaInfusion;
import vazkii.botania.common.lexicon.page.PageManaInfusionRecipe;

public class AddPageConjuration implements IAction {
   private String name;
   private String entry;
   private int page_number;
   private IItemStack[] outputs;
   private IIngredient[] inputs;
   private int[] mana;

   public AddPageConjuration(String name, String entry, int page_number, IItemStack[] outputs, IIngredient[] inputs, int[] mana) {
      this.name = name;
      this.entry = entry;
      this.page_number = page_number;
      this.outputs = outputs;
      this.inputs = inputs;
      this.mana = mana;
   }

   public void apply() {
      LexiconEntry lexiconEntry = BotaniaHelper.findEntry(this.entry);
      if (lexiconEntry == null) {
         CraftTweakerAPI.getLogger().logError("Cannot find lexicon entry " + this.entry);
      } else if (this.page_number > lexiconEntry.pages.size()) {
         CraftTweakerAPI.getLogger().logError("Page Number " + this.page_number + " out of bounds for " + this.entry);
      } else if (this.outputs.length == this.inputs.length && this.outputs.length == this.mana.length) {
         List<RecipeManaInfusion> recipes = new ArrayList();

         for(int i = 0; i < this.outputs.length; ++i) {
            RecipeManaInfusion current_recipe = new RecipeManaInfusion(InputHelper.toStack(this.outputs[i]), InputHelper.toObject(this.inputs[i]), this.mana[i]);
            current_recipe.setCatalyst(RecipeManaInfusion.conjurationState);
            recipes.add(current_recipe);
         }

         LexiconPage page = new PageManaInfusionRecipe(this.name, recipes);
         lexiconEntry.pages.add(this.page_number, page);
      } else {
         CraftTweakerAPI.getLogger().logError("Length of input and output must match");
      }
   }

   public String describe() {
      return "Adding Lexicon Page: " + this.name;
   }
}
