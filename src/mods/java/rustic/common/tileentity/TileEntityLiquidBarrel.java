package rustic.common.tileentity;

import javax.annotation.Nullable;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBucket;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.UniversalBucket;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.TileFluidHandler;
import rustic.common.blocks.ModBlocks;

public class TileEntityLiquidBarrel extends TileFluidHandler {
   public static int capacity = 16000;
   public static final int MAX_TEMP = 573;

   public TileEntityLiquidBarrel() {
      this.tank = new FluidTank(capacity) {
         protected void onContentsChanged() {
            TileEntityLiquidBarrel.this.markDirty();
            TileEntityLiquidBarrel.this.getWorld().notifyBlockUpdate(TileEntityLiquidBarrel.this.pos, TileEntityLiquidBarrel.this.getWorld().getBlockState(TileEntityLiquidBarrel.this.pos), TileEntityLiquidBarrel.this.getWorld().getBlockState(TileEntityLiquidBarrel.this.pos), 2);
         }
      };
      this.tank.setTileEntity(this);
      this.tank.setCanFill(true);
      this.tank.setCanDrain(true);
   }

   public NBTTagCompound getUpdateTag() {
      return this.writeToNBT(new NBTTagCompound());
   }

   @Nullable
   public SPacketUpdateTileEntity getUpdatePacket() {
      return new SPacketUpdateTileEntity(this.getPos(), 0, this.getUpdateTag());
   }

   public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
      this.readFromNBT(pkt.getNbtCompound());
   }

   public int getCapacity() {
      return this.tank.getCapacity();
   }

   public int getAmount() {
      return this.tank.getFluidAmount();
   }

   public FluidTank getTank() {
      return this.tank;
   }

   public Fluid getFluid() {
      return this.tank.getFluid() != null ? this.tank.getFluid().getFluid() : null;
   }

   public boolean activate(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
      ItemStack heldItem = player.getHeldItem(hand);
      if (heldItem != ItemStack.EMPTY && (FluidUtil.getFluidHandler(heldItem) != null || heldItem.getItem() instanceof ItemBucket || heldItem.getItem() instanceof UniversalBucket)) {
         FluidStack f = FluidUtil.getFluidContained(heldItem);
         if (f != null && !f.getFluid().isGaseous() && f.getFluid().getTemperature() <= 573 || f == null) {
            boolean didFill = FluidUtil.interactWithFluidHandler(player, hand, (IFluidHandler)this.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, side));
            if (didFill) {
               this.world.addBlockEvent(this.pos, this.getBlockType(), 1, 0);
               this.getWorld().notifyBlockUpdate(pos, state, state, 3);
               this.world.notifyNeighborsOfStateChange(this.pos, this.getBlockType(), true);
               this.markDirty();
            }
         }

         return true;
      } else {
         return false;
      }
   }

   public void breakBlock(World world, BlockPos pos, IBlockState state, EntityPlayer player) {
      this.invalidate();
      if (!world.isRemote && !player.capabilities.isCreativeMode) {
         ItemStack toDrop = new ItemStack(ModBlocks.LIQUID_BARREL, 1);
         if (this.getTank().getFluidAmount() > 0) {
            NBTTagCompound tag = new NBTTagCompound();
            this.getTank().writeToNBT(tag);
            toDrop.setTagCompound(tag);
         }

         world.spawnEntity(new EntityItem(world, (double)pos.getX() + 0.5D, (double)pos.getY() + 0.5D, (double)pos.getZ() + 0.5D, toDrop));
      }

      world.setTileEntity(pos, (TileEntity)null);
   }

   public boolean receiveClientEvent(int id, int type) {
      if (id == 1) {
         this.getWorld().notifyBlockUpdate(this.pos, this.getWorld().getBlockState(this.pos), this.getWorld().getBlockState(this.pos), 3);
         this.world.notifyNeighborsOfStateChange(this.pos, this.getBlockType(), true);
         this.markDirty();
         return true;
      } else {
         return super.receiveClientEvent(id, type);
      }
   }
}
