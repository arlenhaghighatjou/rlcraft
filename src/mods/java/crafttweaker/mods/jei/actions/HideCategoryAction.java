package crafttweaker.mods.jei.actions;

import crafttweaker.CraftTweakerAPI;
import crafttweaker.IAction;
import crafttweaker.mods.jei.JEI;

public class HideCategoryAction implements IAction {
   private final String name;

   public HideCategoryAction(String name) {
      this.name = name;
   }

   public void apply() {
      if (this.name != null && !this.name.isEmpty()) {
         JEI.HIDDEN_CATEGORIES.add(this.name);
      } else {
         CraftTweakerAPI.logError("Cannot hide null category!");
      }
   }

   public String describe() {
      return "Hiding category in JEI: " + this.name;
   }
}
