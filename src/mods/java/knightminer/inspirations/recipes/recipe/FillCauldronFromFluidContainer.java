package knightminer.inspirations.recipes.recipe;

import knightminer.inspirations.library.InspirationsRegistry;
import knightminer.inspirations.library.recipe.cauldron.ICauldronRecipe;
import net.minecraft.item.ItemStack;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;

public enum FillCauldronFromFluidContainer implements ICauldronRecipe {
   INSTANCE;

   private FillCauldronFromFluidContainer() {
   }

   public boolean matches(ItemStack stack, boolean boiling, int level, ICauldronRecipe.CauldronState state) {
      if (level != InspirationsRegistry.getCauldronMax() && (level <= 0 || state.getFluid() != null)) {
         IFluidHandlerItem fluidHandler = FluidUtil.getFluidHandler(stack);
         if (fluidHandler == null) {
            return false;
         } else {
            FluidStack fluidStack = fluidHandler.drain(1000, false);
            return ICauldronRecipe.CauldronState.fluidValid(fluidStack) && (level == 0 || fluidStack.getFluid() == state.getFluid());
         }
      } else {
         return false;
      }
   }

   public ItemStack getResult(ItemStack stack, boolean boiling, int level, ICauldronRecipe.CauldronState state) {
      IFluidHandlerItem handler = FluidUtil.getFluidHandler(stack.copy());
      handler.drain(1000, true);
      return handler.getContainer();
   }

   public int getLevel(int level) {
      return InspirationsRegistry.getCauldronMax();
   }

   public ICauldronRecipe.CauldronState getState(ItemStack stack, boolean boiling, int level, ICauldronRecipe.CauldronState state) {
      Fluid fluid = FluidUtil.getFluidHandler(stack).drain(1000, false).getFluid();
      return fluid == state.getFluid() ? state : ICauldronRecipe.CauldronState.fluid(fluid);
   }

   public SoundEvent getSound(ItemStack stack, boolean boiling, int level, ICauldronRecipe.CauldronState state) {
      return FluidUtil.getFluidHandler(stack).drain(1000, false).getFluid().getFillSound();
   }

   public ItemStack getContainer(ItemStack stack) {
      return ItemStack.EMPTY;
   }
}
