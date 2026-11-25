package knightminer.inspirations.library.recipe.cauldron;

import com.google.common.collect.ImmutableList;
import java.util.List;
import javax.annotation.ParametersAreNonnullByDefault;
import knightminer.inspirations.library.InspirationsRegistry;
import net.minecraft.init.PotionTypes;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.potion.PotionType;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.fluids.FluidRegistry;

@ParametersAreNonnullByDefault
public class CauldronBrewingRecipe implements ISimpleCauldronRecipe {
   private Ingredient reagent;
   private ICauldronRecipe.CauldronState input;
   private ICauldronRecipe.CauldronState output;

   public CauldronBrewingRecipe(PotionType input, Ingredient reagent, PotionType output) {
      this.input = ICauldronRecipe.CauldronState.potion(input);
      this.reagent = reagent;
      this.output = ICauldronRecipe.CauldronState.potion(output);
   }

   public boolean matches(ItemStack stack, boolean boiling, int level, ICauldronRecipe.CauldronState state) {
      return level > 0 && boiling && (!InspirationsRegistry.expensiveCauldronBrewing() || level < 3 || stack.getCount() > 1) && state.matches(this.input) && this.reagent.apply(stack);
   }

   public ItemStack transformInput(ItemStack stack, boolean boiling, int level, ICauldronRecipe.CauldronState state) {
      stack.shrink(InspirationsRegistry.expensiveCauldronBrewing() && level > 2 ? 2 : 1);
      return stack;
   }

   public ICauldronRecipe.CauldronState getState(ItemStack stack, boolean boiling, int level, ICauldronRecipe.CauldronState state) {
      return this.output;
   }

   public List<ItemStack> getInput() {
      return ImmutableList.copyOf(this.reagent.getMatchingStacks());
   }

   public int getInputLevel() {
      return InspirationsRegistry.expensiveCauldronBrewing() ? 2 : InspirationsRegistry.getCauldronMax();
   }

   public Object getInputState() {
      PotionType potion = this.input.getPotion();
      return potion == PotionTypes.WATER ? FluidRegistry.WATER : potion;
   }

   public Object getState() {
      PotionType potion = this.output.getPotion();
      return potion == PotionTypes.WATER ? FluidRegistry.WATER : potion;
   }

   public boolean isBoiling() {
      return true;
   }

   public SoundEvent getSound(ItemStack stack, boolean boiling, int level, ICauldronRecipe.CauldronState state) {
      return SoundEvents.BLOCK_BREWING_STAND_BREW;
   }

   public String toString() {
      return String.format("CauldronBrewingRecipe: %s from %s", this.output.getPotion().getRegistryName(), this.input.getPotion().getRegistryName());
   }
}
