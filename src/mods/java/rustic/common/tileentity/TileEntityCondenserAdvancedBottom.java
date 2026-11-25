package rustic.common.tileentity;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumFacing.Axis;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.RangedWrapper;
import rustic.common.blocks.BlockCondenser;
import rustic.common.blocks.BlockCondenserAdvanced;
import rustic.common.blocks.ModBlocks;
import rustic.common.crafting.ICondenserRecipe;
import rustic.common.crafting.Recipes;
import rustic.core.Rustic;

public class TileEntityCondenserAdvancedBottom extends TileEntityCondenserBase {
   public static final int SLOT_NUM = 7;
   private IItemHandler handlerBottom;
   private IItemHandler handlerBack;
   private IItemHandler handlerTop;
   private IItemHandler handlerSide;

   protected int getInternalSize() {
      return 7;
   }

   public String getName() {
      return "container.rustic.condenser_advanced";
   }

   public TileEntityCondenserAdvancedBottom() {
      this.handlerBottom = new RangedWrapper(this.externalStackHandler, 0, 1);
      this.handlerBack = new RangedWrapper(this.externalStackHandler, 1, 3);
      this.handlerTop = new RangedWrapper(this.externalStackHandler, 3, 4);
      this.handlerSide = new RangedWrapper(this.externalStackHandler, 4, 7);
   }

   public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing) {
      if (this.world.getBlockState(this.pos).getBlock() == ModBlocks.CONDENSER_ADVANCED && ((BlockCondenserAdvanced)this.world.getBlockState(this.pos).getBlock()).hasRetorts(this.world, this.pos, this.world.getBlockState(this.pos))) {
         return capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY || capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY || super.hasCapability(capability, facing);
      } else {
         return false;
      }
   }

   @Nullable
   public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing) {
      if (this.world.getBlockState(this.pos).getBlock() == ModBlocks.CONDENSER_ADVANCED && ((BlockCondenserAdvanced)this.world.getBlockState(this.pos).getBlock()).hasRetorts(this.world, this.pos, this.world.getBlockState(this.pos))) {
         if (capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) {
            return (T)this.tank;
         } else if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            if (facing == null) {
               return (T)this.externalStackHandler;
            } else if (facing == EnumFacing.DOWN) {
               return (T)this.handlerBottom;
            } else if (facing == EnumFacing.UP) {
               return (T)this.handlerTop;
            } else if (facing == ((EnumFacing)this.world.getBlockState(this.pos).getValue(BlockCondenser.FACING)).getOpposite()) {
               return (T)this.handlerBack;
            } else {
               return (T)(facing.getAxis() != Axis.Y ? this.handlerSide : this.externalStackHandler);
            }
         } else {
            return (T)super.getCapability(capability, facing);
         }
      } else {
         return (T)null;
      }
   }

   protected boolean canBrew() {
      IBlockState state = this.world.getBlockState(this.pos);
      if (state.getBlock() == ModBlocks.CONDENSER_ADVANCED && ((Boolean)state.getValue(BlockCondenserAdvanced.BOTTOM)).booleanValue()) {
         if (this.internalStackHandler.getStackInSlot(4).isEmpty() && this.internalStackHandler.getStackInSlot(5).isEmpty() && this.internalStackHandler.getStackInSlot(6).isEmpty()) {
            return false;
         } else {
            return ((BlockCondenserAdvanced)state.getBlock()).hasRetorts(this.world, this.pos, state);
         }
      } else {
         return false;
      }
   }

   protected void refreshCurrentRecipe() {
      if (this.hasContentChanged) {
         this.hasContentChanged = false;
         Fluid fluid = this.getFluid();
         ItemStack modifier = this.internalStackHandler.getStackInSlot(3);
         ItemStack bottle = this.internalStackHandler.getStackInSlot(2);
         ItemStack[] inputs = new ItemStack[]{this.internalStackHandler.getStackInSlot(4), this.internalStackHandler.getStackInSlot(5), this.internalStackHandler.getStackInSlot(6)};
         if (this.currentRecipe != null && this.currentRecipe.matches(fluid, modifier, bottle, inputs)) {
            return;
         }

         this.brewTime = 0;
         this.currentRecipe = null;

         for(ICondenserRecipe recipe : Recipes.condenserRecipes) {
            if ((recipe.isBasic() || recipe.isAdvanced()) && recipe.matches(fluid, modifier, bottle, inputs)) {
               this.currentRecipe = recipe;
               this.totalBrewTime = recipe.getTime();
               return;
            }
         }
      }

   }

   protected void renderParticles() {
      if (this.world.isRemote && this.world.getBlockState(this.pos).getBlock() == ModBlocks.CONDENSER_ADVANCED) {
         EnumFacing blockFacing = (EnumFacing)this.world.getBlockState(this.pos).getValue(BlockCondenserAdvanced.FACING);
         double yVel = 0.125D;
         switch(blockFacing) {
         case NORTH:
            Rustic.proxy.spawnAlchemySmokeFX(this.world, this.brewTime, (double)this.pos.getX() - 0.5D, (double)this.pos.getY() + 1.0625D, (double)this.pos.getZ() + 0.5D, 0.0D, yVel, 0.0D);
            Rustic.proxy.spawnAlchemySmokeFX(this.world, this.brewTime, (double)this.pos.getX() + 1.5D, (double)this.pos.getY() + 1.0625D, (double)this.pos.getZ() + 0.5D, 0.0D, yVel, 0.0D);
            Rustic.proxy.spawnAlchemySmokeFX(this.world, this.brewTime, (double)this.pos.getX() + 0.5D, (double)this.pos.getY() + 1.0625D, (double)this.pos.getZ() + 1.5D, 0.0D, yVel, 0.0D);
            break;
         case SOUTH:
            Rustic.proxy.spawnAlchemySmokeFX(this.world, this.brewTime, (double)this.pos.getX() - 0.5D, (double)this.pos.getY() + 1.0625D, (double)this.pos.getZ() + 0.5D, 0.0D, yVel, 0.0D);
            Rustic.proxy.spawnAlchemySmokeFX(this.world, this.brewTime, (double)this.pos.getX() + 1.5D, (double)this.pos.getY() + 1.0625D, (double)this.pos.getZ() + 0.5D, 0.0D, yVel, 0.0D);
            Rustic.proxy.spawnAlchemySmokeFX(this.world, this.brewTime, (double)this.pos.getX() + 0.5D, (double)this.pos.getY() + 1.0625D, (double)this.pos.getZ() - 0.5D, 0.0D, yVel, 0.0D);
            break;
         case WEST:
            Rustic.proxy.spawnAlchemySmokeFX(this.world, this.brewTime, (double)this.pos.getX() + 0.5D, (double)this.pos.getY() + 1.0625D, (double)this.pos.getZ() - 0.5D, 0.0D, yVel, 0.0D);
            Rustic.proxy.spawnAlchemySmokeFX(this.world, this.brewTime, (double)this.pos.getX() + 0.5D, (double)this.pos.getY() + 1.0625D, (double)this.pos.getZ() + 1.5D, 0.0D, yVel, 0.0D);
            Rustic.proxy.spawnAlchemySmokeFX(this.world, this.brewTime, (double)this.pos.getX() + 1.5D, (double)this.pos.getY() + 1.0625D, (double)this.pos.getZ() + 0.5D, 0.0D, yVel, 0.0D);
            break;
         case EAST:
            Rustic.proxy.spawnAlchemySmokeFX(this.world, this.brewTime, (double)this.pos.getX() + 0.5D, (double)this.pos.getY() + 1.0625D, (double)this.pos.getZ() - 0.5D, 0.0D, yVel, 0.0D);
            Rustic.proxy.spawnAlchemySmokeFX(this.world, this.brewTime, (double)this.pos.getX() + 0.5D, (double)this.pos.getY() + 1.0625D, (double)this.pos.getZ() + 1.5D, 0.0D, yVel, 0.0D);
            Rustic.proxy.spawnAlchemySmokeFX(this.world, this.brewTime, (double)this.pos.getX() - 0.5D, (double)this.pos.getY() + 1.0625D, (double)this.pos.getZ() + 0.5D, 0.0D, yVel, 0.0D);
         }
      }

   }

   protected void brew() {
      if (this.canBrew()) {
         Fluid fluid = this.getFluid();
         ItemStack modifier = this.internalStackHandler.getStackInSlot(3);
         ItemStack bottle = this.internalStackHandler.getStackInSlot(2);
         ItemStack[] inputs = new ItemStack[]{this.internalStackHandler.getStackInSlot(4), this.internalStackHandler.getStackInSlot(5), this.internalStackHandler.getStackInSlot(6)};
         ICondenserRecipe recipe = this.currentRecipe;
         if (recipe.matches(fluid, modifier, bottle, inputs) && this.getAmount() >= recipe.getFluid().amount) {
            this.internalStackHandler.insertItem(0, recipe.getResult(), false);
            this.internalStackHandler.extractItem(3, recipe.getModifierConsumption(modifier), false);
            int[] consume = recipe.getInputConsumption(inputs);
            this.internalStackHandler.extractItem(4, consume[0], false);
            this.internalStackHandler.extractItem(5, consume[1], false);
            this.internalStackHandler.extractItem(6, consume[2], false);
            this.internalStackHandler.extractItem(2, recipe.getBottleConsumption(bottle), false);
            this.tank.drain(recipe.getFluid().amount, true);
         }
      }

   }
}
