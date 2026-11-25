package knightminer.inspirations.utility.dispenser;

import net.minecraft.block.BlockDispenser;
import net.minecraft.dispenser.BehaviorDefaultDispenseItem;
import net.minecraft.dispenser.IBehaviorDispenseItem;
import net.minecraft.dispenser.IBlockSource;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityDispenser;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidActionResult;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.IFluidHandler;

public class DispenseFluidTank extends BehaviorDefaultDispenseItem {
   private static final BehaviorDefaultDispenseItem DEFAULT = new BehaviorDefaultDispenseItem();
   private IBehaviorDispenseItem fallback;

   public DispenseFluidTank(IBehaviorDispenseItem fallback) {
      this.fallback = fallback;
   }

   protected ItemStack dispenseStack(IBlockSource source, ItemStack stack) {
      EnumFacing side = (EnumFacing)source.getBlockState().getValue(BlockDispenser.FACING);
      BlockPos pos = source.getBlockPos().offset(side);
      World world = source.getWorld();
      IFluidHandler handler = FluidUtil.getFluidHandler(world, pos, side.getOpposite());
      if (handler != null) {
         FluidStack fluid = FluidUtil.getFluidContained(stack);
         FluidActionResult result;
         if (fluid != null) {
            result = FluidUtil.tryEmptyContainer(stack, handler, Integer.MAX_VALUE, (EntityPlayer)null, true);
         } else {
            result = FluidUtil.tryFillContainer(stack, handler, Integer.MAX_VALUE, (EntityPlayer)null, true);
         }

         if (result.isSuccess()) {
            ItemStack resultStack = result.getResult();
            SoundEvent sound;
            if (fluid != null) {
               sound = fluid.getFluid().getEmptySound(fluid);
            } else {
               FluidStack resultFluid = FluidUtil.getFluidContained(resultStack);
               sound = resultFluid.getFluid().getFillSound(resultFluid);
            }

            world.playSound((EntityPlayer)null, pos, sound, SoundCategory.BLOCKS, 1.0F, 1.0F);
            if (stack.getCount() == 1) {
               return resultStack;
            } else {
               if (!resultStack.isEmpty() && ((TileEntityDispenser)source.getBlockTileEntity()).addItemStack(resultStack) < 0) {
                  DEFAULT.dispense(source, resultStack);
               }

               ItemStack shrink = stack.copy();
               shrink.shrink(1);
               return shrink;
            }
         } else {
            return DEFAULT.dispense(source, stack);
         }
      } else {
         return this.fallback.dispense(source, stack);
      }
   }
}
