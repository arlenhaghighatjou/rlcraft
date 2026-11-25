package com.blamejared.compat.botania.lexicon;

import com.blamejared.compat.botania.BotaniaHelper;
import crafttweaker.CraftTweakerAPI;
import crafttweaker.IAction;
import vazkii.botania.api.lexicon.LexiconCategory;

public class SetCategoryPriority implements IAction {
   private LexiconCategory category;
   private int oldPriority;
   private int newPriority;
   private final String name;

   public SetCategoryPriority(String name, int priority) {
      this.name = name;
      this.newPriority = priority;
   }

   public void apply() {
      this.category = BotaniaHelper.findCatagory(this.name);
      if (this.category == null) {
         CraftTweakerAPI.getLogger().logError("Cannot find lexicon category " + this.name);
      } else {
         this.oldPriority = this.category.getSortingPriority();
         this.category.setPriority(this.newPriority);
         CraftTweakerAPI.getLogger().logInfo("Setting Lexicon Category priority from " + this.oldPriority + " to: " + this.category.getUnlocalizedName());
      }
   }

   public String describe() {
      return "Attempting to set the priority for Lexicon Category " + this.name + " from " + this.oldPriority + "to " + this.newPriority;
   }
}
