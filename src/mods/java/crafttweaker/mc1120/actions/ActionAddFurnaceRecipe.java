package crafttweaker.mc1120.actions;

import crafttweaker.IAction;
import crafttweaker.api.item.IIngredient;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;

public class ActionAddFurnaceRecipe implements IAction {
   private final IIngredient ingredient;
   private final ItemStack[] input;
   private final ItemStack output;
   private final double xp;

   public ActionAddFurnaceRecipe(IIngredient ingredient, ItemStack[] input, ItemStack output, double xp) {
      this.ingredient = ingredient;
      this.input = input;
      this.output = output;
      this.xp = xp;
   }

   public void apply() {
      for(ItemStack inputStack : this.input) {
         FurnaceRecipes.instance().addSmeltingRecipe(inputStack, this.output, (float)this.xp);
      }

   }

   public String describe() {
      return "Adding furnace recipe for " + this.ingredient;
   }
}
