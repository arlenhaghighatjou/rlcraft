package knightminer.inspirations.recipes.tank;

import knightminer.inspirations.common.Config;
import knightminer.inspirations.library.InspirationsRegistry;
import knightminer.inspirations.library.recipe.cauldron.ICauldronRecipe;
import knightminer.inspirations.recipes.tileentity.TileCauldron;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidTankProperties;

public class CauldronTank implements IFluidHandler {
   private TileCauldron cauldron;
   private CauldronTank.Properties[] properties;

   public CauldronTank(TileCauldron cauldron) {
      this.cauldron = cauldron;
      this.properties = new CauldronTank.Properties[]{new CauldronTank.Properties(cauldron)};
   }

   public IFluidTankProperties[] getTankProperties() {
      return this.properties;
   }

   private static int getLevels(int amount) {
      if (Config.enableBiggerCauldron) {
         return amount / 250;
      } else {
         return amount >= 1000 ? 3 : 0;
      }
   }

   private static int getAmount(int levels) {
      if (Config.enableBiggerCauldron) {
         return levels * 250;
      } else {
         return levels == 3 ? 1000 : 0;
      }
   }

   public int fill(FluidStack stack, boolean doFill) {
      if (stack.tag != null) {
         return 0;
      } else {
         int level = this.cauldron.getFluidLevel();
         int max = InspirationsRegistry.getCauldronMax();
         if (level == max) {
            return 0;
         } else {
            ICauldronRecipe.CauldronState state = this.cauldron.getState();
            if (level != max && (level <= 0 || state.getFluid() == stack.getFluid())) {
               int toInsert = Math.min(getLevels(stack.amount), max - level);
               if (toInsert == 0) {
                  return 0;
               } else {
                  if (doFill) {
                     this.cauldron.setState(ICauldronRecipe.CauldronState.fluid(stack.getFluid()), false);
                     this.cauldron.setFluidLevel(toInsert + level);
                  }

                  return getAmount(toInsert);
               }
            } else {
               return 0;
            }
         }
      }
   }

   public FluidStack drain(FluidStack stack, boolean doDrain) {
      if (stack.tag != null) {
         return null;
      } else {
         ICauldronRecipe.CauldronState state = this.cauldron.getState();
         return state.getFluid() != stack.getFluid() ? null : this.drain(stack.amount, doDrain);
      }
   }

   public FluidStack drain(int maxDrain, boolean doDrain) {
      ICauldronRecipe.CauldronState state = this.cauldron.getState();
      if (state.getFluid() == null) {
         return null;
      } else {
         int level = this.cauldron.getFluidLevel();
         if (level == 0) {
            return null;
         } else {
            int toDrain = Math.min(getLevels(maxDrain), level);
            if (toDrain == 0) {
               return null;
            } else {
               if (doDrain) {
                  this.cauldron.setFluidLevel(level - toDrain);
               }

               return new FluidStack(state.getFluid(), getAmount(toDrain));
            }
         }
      }
   }

   private static class Properties implements IFluidTankProperties {
      private TileCauldron cauldron;

      public Properties(TileCauldron cauldron) {
         this.cauldron = cauldron;
      }

      public FluidStack getContents() {
         Fluid fluid = this.cauldron.getState().getFluid();
         if (fluid == null) {
            return null;
         } else {
            int amount = this.cauldron.getFluidLevel();
            return amount == 0 ? null : new FluidStack(fluid, CauldronTank.getAmount(amount));
         }
      }

      public int getCapacity() {
         return 1000;
      }

      public boolean canFill() {
         return true;
      }

      public boolean canDrain() {
         return true;
      }

      public boolean canFillFluidType(FluidStack fluidStack) {
         return fluidStack.tag == null;
      }

      public boolean canDrainFluidType(FluidStack fluidStack) {
         return fluidStack.tag == null;
      }
   }
}
