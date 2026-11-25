package com.blamejared.compat.botania.lexicon;

import com.blamejared.mtlib.helpers.InputHelper;
import crafttweaker.CraftTweakerAPI;
import crafttweaker.IAction;
import crafttweaker.api.item.IItemStack;
import net.minecraft.item.ItemStack;
import vazkii.botania.api.lexicon.LexiconRecipeMappings;

public class RemoveRecipeMapping implements IAction {
   private ItemStack stack;
   private IItemStack iStack;

   public RemoveRecipeMapping(IItemStack stack) {
      this.iStack = stack;
   }

   public void apply() {
      if (LexiconRecipeMappings.getDataForStack(this.stack = InputHelper.toStack(this.iStack)) == null) {
         CraftTweakerAPI.getLogger().logError("There isn't a recipe mapping for " + this.iStack);
      } else {
         LexiconRecipeMappings.remove(this.stack);
         CraftTweakerAPI.getLogger().logInfo("Removing Lexicon Recipe Lookup: " + this.stack.getUnlocalizedName());
      }
   }

   public String describe() {
      return "Attempting to remove recipe mapping for " + InputHelper.toStack(this.iStack);
   }
}
