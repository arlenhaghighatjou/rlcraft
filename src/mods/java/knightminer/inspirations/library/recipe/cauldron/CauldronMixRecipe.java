package knightminer.inspirations.library.recipe.cauldron;

import net.lax1dude.eaglercraft.v1_8.HString;
import com.google.common.collect.ImmutableList;
import java.util.List;
import knightminer.inspirations.library.InspirationsRegistry;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;

public class CauldronMixRecipe implements ISimpleCauldronRecipe {
   private ICauldronRecipe.CauldronState input1;
   private ICauldronRecipe.CauldronState input2;
   private ItemStack result;

   public CauldronMixRecipe(Fluid input1, Fluid input2, ItemStack result) {
      this.result = result;
      this.input1 = ICauldronRecipe.CauldronState.fluid(input1);
      this.input2 = ICauldronRecipe.CauldronState.fluid(input2);
   }

   public boolean matches(ItemStack stack, boolean boiling, int level, ICauldronRecipe.CauldronState state) {
      if (level == InspirationsRegistry.getCauldronMax() && state.getFluid() != null) {
         IFluidHandlerItem fluidHandler = FluidUtil.getFluidHandler(stack);
         if (fluidHandler == null) {
            return false;
         } else {
            FluidStack fluidStack = fluidHandler.drain(1000, false);
            if (!ICauldronRecipe.CauldronState.fluidValid(fluidStack)) {
               return false;
            } else {
               Fluid fluid = fluidStack.getFluid();
               return this.input1.matches(state) && this.input2.getFluid() == fluid || this.input2.matches(state) && this.input1.getFluid() == fluid;
            }
         }
      } else {
         return false;
      }
   }

   public int getLevel(int level) {
      return 0;
   }

   public Fluid getInputState() {
      return this.input1.getFluid();
   }

   public List<ItemStack> getInput() {
      return ImmutableList.of(FluidUtil.getFilledBucket(this.input2.getFluidStack()));
   }

   public ItemStack getResult() {
      return this.result;
   }

   public String toString() {
      return HString.format("CauldronMixRecipe: %s from %s and %s", this.getResult().toString(), this.input1.getFluid().getName(), this.input2.getFluid().getName());
   }
}
