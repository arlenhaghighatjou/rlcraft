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
import rustic.common.blocks.ModBlocks;
import rustic.common.crafting.ICondenserRecipe;
import rustic.common.crafting.Recipes;
import rustic.core.Rustic;

public class TileEntityCondenser extends TileEntityCondenserBase {
   public static int SLOT_NUM = 5;
   private IItemHandler handlerBottom;
   private IItemHandler handlerBack;
   private IItemHandler handlerSide;

   protected int getInternalSize() {
      return SLOT_NUM;
   }

   public String getName() {
      return "container.rustic.condenser";
   }

   public TileEntityCondenser() {
      this.handlerBottom = new RangedWrapper(this.externalStackHandler, 0, 1);
      this.handlerBack = new RangedWrapper(this.externalStackHandler, 1, 3);
      this.handlerSide = new RangedWrapper(this.externalStackHandler, 3, 5);
   }

   public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing) {
      if (this.world.getBlockState(this.pos).getBlock() == ModBlocks.CONDENSER && ((BlockCondenser)this.world.getBlockState(this.pos).getBlock()).hasRetorts(this.world, this.pos, this.world.getBlockState(this.pos))) {
         return capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY || capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY || super.hasCapability(capability, facing);
      } else {
         return false;
      }
   }

   @Nullable
   public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing) {
      if (this.world.getBlockState(this.pos).getBlock() == ModBlocks.CONDENSER && ((BlockCondenser)this.world.getBlockState(this.pos).getBlock()).hasRetorts(this.world, this.pos, this.world.getBlockState(this.pos))) {
         if (capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) {
            return (T)this.tank;
         } else if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            if (facing == null) {
               return (T)this.externalStackHandler;
            } else if (facing == EnumFacing.DOWN) {
               return (T)this.handlerBottom;
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
      if (state.getBlock() == ModBlocks.CONDENSER && ((Boolean)state.getValue(BlockCondenser.BOTTOM)).booleanValue()) {
         if (this.internalStackHandler.getStackInSlot(3).isEmpty() && this.internalStackHandler.getStackInSlot(4).isEmpty()) {
            return false;
         } else {
            return ((BlockCondenser)state.getBlock()).hasRetorts(this.world, this.pos, state);
         }
      } else {
         return false;
      }
   }

   protected void refreshCurrentRecipe() {
      if (this.hasContentChanged) {
         this.hasContentChanged = false;
         Fluid fluid = this.getFluid();
         ItemStack bottle = this.internalStackHandler.getStackInSlot(2);
         ItemStack[] inputs = new ItemStack[]{this.internalStackHandler.getStackInSlot(3), this.internalStackHandler.getStackInSlot(4)};
         if (this.currentRecipe != null && this.currentRecipe.matches(fluid, ItemStack.EMPTY, bottle, inputs)) {
            return;
         }

         this.brewTime = 0;
         this.currentRecipe = null;

         for(ICondenserRecipe recipe : Recipes.condenserRecipes) {
            if (recipe.isBasic() && recipe.matches(fluid, ItemStack.EMPTY, bottle, inputs)) {
               this.currentRecipe = recipe;
               this.totalBrewTime = recipe.getTime();
               return;
            }
         }
      }

   }

   protected void renderParticles() {
      if (this.world.isRemote && this.world.getBlockState(this.pos).getBlock() == ModBlocks.CONDENSER) {
         EnumFacing blockFacing = (EnumFacing)this.world.getBlockState(this.pos).getValue(BlockCondenser.FACING);
         double yVel = 0.125D;
         if (blockFacing != EnumFacing.NORTH && blockFacing != EnumFacing.SOUTH) {
            if (blockFacing == EnumFacing.EAST || blockFacing == EnumFacing.WEST) {
               Rustic.proxy.spawnAlchemySmokeFX(this.world, this.brewTime, (double)this.pos.getX() + 0.5D, (double)this.pos.getY() + 1.0625D, (double)this.pos.getZ() - 0.5D, 0.0D, yVel, 0.0D);
               Rustic.proxy.spawnAlchemySmokeFX(this.world, this.brewTime, (double)this.pos.getX() + 0.5D, (double)this.pos.getY() + 1.0625D, (double)this.pos.getZ() + 1.5D, 0.0D, yVel, 0.0D);
            }
         } else {
            Rustic.proxy.spawnAlchemySmokeFX(this.world, this.brewTime, (double)this.pos.getX() - 0.5D, (double)this.pos.getY() + 1.0625D, (double)this.pos.getZ() + 0.5D, 0.0D, yVel, 0.0D);
            Rustic.proxy.spawnAlchemySmokeFX(this.world, this.brewTime, (double)this.pos.getX() + 1.5D, (double)this.pos.getY() + 1.0625D, (double)this.pos.getZ() + 0.5D, 0.0D, yVel, 0.0D);
         }
      }

   }

   protected void brew() {
      if (this.canBrew()) {
         ItemStack[] inputs = new ItemStack[]{this.internalStackHandler.getStackInSlot(3), this.internalStackHandler.getStackInSlot(4)};
         ItemStack bottle = this.internalStackHandler.getStackInSlot(2);
         ICondenserRecipe recipe = this.currentRecipe;
         if (recipe.matches(this.getFluid(), ItemStack.EMPTY, bottle, inputs) && this.getAmount() >= recipe.getFluid().amount) {
            this.internalStackHandler.insertItem(0, recipe.getResult(), false);
            int[] consume = recipe.getInputConsumption(inputs);
            this.internalStackHandler.extractItem(3, consume[0], false);
            this.internalStackHandler.extractItem(4, consume[1], false);
            this.internalStackHandler.extractItem(2, recipe.getBottleConsumption(bottle), false);
            this.tank.drain(recipe.getFluid().amount, true);
         }
      }

   }
}
