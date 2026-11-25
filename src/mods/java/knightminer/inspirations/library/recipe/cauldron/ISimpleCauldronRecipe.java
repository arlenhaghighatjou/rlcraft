package knightminer.inspirations.library.recipe.cauldron;

import java.util.List;
import knightminer.inspirations.library.InspirationsRegistry;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidRegistry;

public interface ISimpleCauldronRecipe extends ICauldronRecipe {
   List<ItemStack> getInput();

   default ItemStack getResult() {
      return ItemStack.EMPTY;
   }

   default boolean isBoiling() {
      return false;
   }

   default Object getInputState() {
      return FluidRegistry.WATER;
   }

   default Object getState() {
      return this.getInputState();
   }

   default int getInputLevel() {
      return InspirationsRegistry.getCauldronMax();
   }

   default ItemStack getResult(ItemStack stack, boolean boiling, int level, ICauldronRecipe.CauldronState state) {
      return this.getResult().copy();
   }
}
