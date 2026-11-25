package crafttweaker.api.recipes;

import crafttweaker.api.item.IItemStack;

public class FurnaceRecipe implements IFurnaceRecipe {
   private final IItemStack input;
   private final IItemStack output;
   private final float xp;

   public FurnaceRecipe(IItemStack input, IItemStack output, float xp) {
      this.input = input;
      this.output = output;
      this.xp = xp;
   }

   public String toCommandString() {
      return String.format("furnace.addRecipe(%s, %s, %f)", this.output.toCommandString(), this.input.toCommandString(), this.xp);
   }

   public IItemStack getInput() {
      return this.input;
   }

   public IItemStack getOutput() {
      return this.output;
   }

   public float getXp() {
      return this.xp;
   }
}
