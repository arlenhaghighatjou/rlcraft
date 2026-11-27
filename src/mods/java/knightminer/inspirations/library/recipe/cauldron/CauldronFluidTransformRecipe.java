package knightminer.inspirations.library.recipe.cauldron;

import net.lax1dude.eaglercraft.v1_8.HString;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import knightminer.inspirations.library.InspirationsRegistry;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;
import slimeknights.mantle.util.RecipeMatch;

@ParametersAreNonnullByDefault
public class CauldronFluidTransformRecipe extends CauldronFluidRecipe {
   private ICauldronRecipe.CauldronState result;
   private int maxLevel;

   public CauldronFluidTransformRecipe(RecipeMatch input, @Nullable Fluid fluid, Fluid result, @Nullable Boolean boiling) {
      this(input, fluid, result, boiling, InspirationsRegistry.getCauldronMax());
   }

   public CauldronFluidTransformRecipe(RecipeMatch input, @Nullable Fluid fluid, Fluid result, @Nullable Boolean boiling, int maxLevel) {
      super(input, fluid, ItemStack.EMPTY, boiling);
      this.result = ICauldronRecipe.CauldronState.fluid(result);
      this.maxLevel = maxLevel;
   }

   public boolean matches(ItemStack stack, boolean boiling, int level, ICauldronRecipe.CauldronState state) {
      return level > 0 && level <= this.maxLevel && super.matches(stack, boiling, level, state);
   }

   public int getLevel(int level) {
      return level;
   }

   public ICauldronRecipe.CauldronState getState(ItemStack stack, boolean boiling, int level, ICauldronRecipe.CauldronState state) {
      return this.result;
   }

   public Object getState() {
      return this.result.getFluid();
   }

   public int getInputLevel() {
      return this.maxLevel;
   }

   public String toString() {
      return HString.format("CauldronFluidTransformRecipe: %s from %s", this.result.getFluid().getName(), this.fluid == null ? "water" : this.fluid.getFluid().getName());
   }
}
