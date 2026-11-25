package crafttweaker.mc1120.actions;

import crafttweaker.IAction;
import crafttweaker.api.item.IItemStack;
import crafttweaker.mc1120.furnace.MCFurnaceManager;
import crafttweaker.mc1120.furnace.SetFuelPattern;

public class ActionSetFuel implements IAction {
   private final SetFuelPattern pattern;

   public ActionSetFuel(SetFuelPattern pattern) {
      this.pattern = pattern;
   }

   public void apply() {
      for(IItemStack stack : this.pattern.getPattern().getItems()) {
         MCFurnaceManager.fuelMap.put(stack, Integer.valueOf(this.pattern.getValue()));
      }

   }

   public String describe() {
      return "Setting fuel for " + this.pattern.getPattern();
   }
}
