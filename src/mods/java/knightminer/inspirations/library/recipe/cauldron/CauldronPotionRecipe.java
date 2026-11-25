package knightminer.inspirations.library.recipe.cauldron;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.init.PotionTypes;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionType;
import net.minecraftforge.fluids.FluidRegistry;
import slimeknights.mantle.util.RecipeMatch;

@ParametersAreNonnullByDefault
public class CauldronPotionRecipe extends CauldronRecipeMatchRecipe {
   protected PotionType potion;

   public CauldronPotionRecipe(RecipeMatch input, PotionType potion, ItemStack result, int levels, @Nullable Boolean boiling) {
      super(input, result, boiling, levels);
      this.potion = potion;
   }

   protected boolean matches(ICauldronRecipe.CauldronState state) {
      return state.getPotion() == this.potion;
   }

   public Object getInputState() {
      return this.potion == PotionTypes.WATER ? FluidRegistry.WATER : this.potion;
   }

   public String toString() {
      return String.format("CauldronFluidRecipe: %s from %s", this.getResult().toString(), this.potion.getRegistryName());
   }
}
