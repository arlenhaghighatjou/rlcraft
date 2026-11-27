package knightminer.inspirations.library.recipe.cauldron;

import net.lax1dude.eaglercraft.v1_8.HString;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import slimeknights.mantle.util.RecipeMatch;

@ParametersAreNonnullByDefault
public class CauldronFluidRecipe extends CauldronRecipeMatchRecipe {
   @Nullable
   protected ICauldronRecipe.CauldronState fluid;

   public CauldronFluidRecipe(RecipeMatch input, @Nullable Fluid fluid, ItemStack result, @Nullable Boolean boiling, int levels, SoundEvent sound) {
      super(input, result, boiling, levels, sound);
      this.fluid = fluid == null ? null : ICauldronRecipe.CauldronState.fluid(fluid);
   }

   public CauldronFluidRecipe(RecipeMatch input, @Nullable Fluid fluid, ItemStack result, @Nullable Boolean boiling, int levels) {
      this(input, fluid, result, boiling, levels, SoundEvents.ENTITY_BOBBER_SPLASH);
   }

   public CauldronFluidRecipe(RecipeMatch input, @Nullable Fluid fluid, ItemStack result, @Nullable Boolean boiling, SoundEvent sound) {
      this(input, fluid, result, boiling, 1, sound);
   }

   public CauldronFluidRecipe(RecipeMatch input, @Nullable Fluid fluid, ItemStack result, @Nullable Boolean boiling) {
      this(input, fluid, result, boiling, SoundEvents.ENTITY_BOBBER_SPLASH);
   }

   public CauldronFluidRecipe(RecipeMatch input, ItemStack result, @Nullable Boolean boiling) {
      this(input, (Fluid)null, result, boiling);
   }

   protected boolean matches(ICauldronRecipe.CauldronState state) {
      return this.fluid == null ? state.isWater() : this.fluid.getFluid() == state.getFluid();
   }

   public Object getInputState() {
      return this.fluid == null ? FluidRegistry.WATER : this.fluid.getFluid();
   }

   public String toString() {
      return HString.format("CauldronFluidRecipe: %s from %s", this.getResult().toString(), this.fluid == null ? "water" : this.fluid.getFluid().getName());
   }
}
