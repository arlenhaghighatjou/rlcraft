package crafttweaker.mc1120.actions;

import crafttweaker.IAction;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

public class ActionOreDictAddAll implements IAction {
   private final String idTarget;
   private final String idSource;

   public ActionOreDictAddAll(String idTarget, String idSource) {
      this.idTarget = idTarget;
      this.idSource = idSource;
   }

   public void apply() {
      for(ItemStack stack : OreDictionary.getOres(this.idSource)) {
         OreDictionary.registerOre(this.idTarget, stack);
      }

   }

   public String describe() {
      return "Copying contents of ore dictionary entry " + this.idSource + " to " + this.idTarget;
   }
}
