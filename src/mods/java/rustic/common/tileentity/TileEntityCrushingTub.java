package rustic.common.tileentity;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemBucket;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.UniversalBucket;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.TileFluidHandler;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import rustic.common.crafting.ICrushingTubRecipe;
import rustic.common.crafting.Recipes;

public class TileEntityCrushingTub extends TileFluidHandler {
   public static int capacity = 8000;
   public ItemStackHandler itemStackHandler = new ItemStackHandler(1) {
      protected void onContentsChanged(int slot) {
         IBlockState state = TileEntityCrushingTub.this.world.getBlockState(TileEntityCrushingTub.this.pos);
         TileEntityCrushingTub.this.world.addBlockEvent(TileEntityCrushingTub.this.pos, TileEntityCrushingTub.this.getBlockType(), 1, 0);
         TileEntityCrushingTub.this.getWorld().notifyBlockUpdate(TileEntityCrushingTub.this.pos, state, state, 3);
         TileEntityCrushingTub.this.world.notifyNeighborsOfStateChange(TileEntityCrushingTub.this.pos, TileEntityCrushingTub.this.getBlockType(), true);
         TileEntityCrushingTub.this.markDirty();
      }
   };

   public TileEntityCrushingTub() {
      this.tank = new FluidTank(capacity) {
         protected void onContentsChanged() {
            TileEntityCrushingTub.this.markDirty();
            TileEntityCrushingTub.this.getWorld().notifyBlockUpdate(TileEntityCrushingTub.this.pos, TileEntityCrushingTub.this.getWorld().getBlockState(TileEntityCrushingTub.this.pos), TileEntityCrushingTub.this.getWorld().getBlockState(TileEntityCrushingTub.this.pos), 2);
         }
      };
      this.tank.setTileEntity(this);
      this.tank.setCanFill(false);
      this.tank.setCanDrain(true);
   }

   public void crush(EntityLivingBase entity) {
      if (!this.itemStackHandler.getStackInSlot(0).isEmpty()) {
         ItemStack stack = this.itemStackHandler.getStackInSlot(0);

         for(ICrushingTubRecipe recipe : Recipes.crushingTubRecipes) {
            if (recipe.matches(stack)) {
               FluidStack output = recipe.getResult();
               if (this.getAmount() <= this.getCapacity() - output.amount) {
                  this.tank.fillInternal(output, true);
                  this.itemStackHandler.extractItem(0, 1, false);
                  ItemStack by = recipe.getByproduct().copy();
                  if (!by.isEmpty()) {
                     Block.spawnAsEntity(this.world, this.pos, by);
                  }

                  this.world.playSound((EntityPlayer)null, (double)this.pos.getX() + 0.5D, (double)this.pos.getY() + 0.5D, (double)this.pos.getZ() + 0.5D, SoundEvents.BLOCK_SLIME_FALL, SoundCategory.BLOCKS, 0.5F, this.world.rand.nextFloat() * 0.1F + 0.9F);
                  IBlockState state = this.world.getBlockState(this.pos);
                  this.world.addBlockEvent(this.pos, this.getBlockType(), 1, 0);
                  this.getWorld().notifyBlockUpdate(this.pos, state, state, 3);
                  this.world.notifyNeighborsOfStateChange(this.pos, this.getBlockType(), true);
                  this.markDirty();
               }
            }
         }
      }

   }

   public boolean activate(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
      ItemStack heldItem = player.getHeldItem(hand);
      if (heldItem != ItemStack.EMPTY) {
         if (FluidUtil.getFluidHandler(heldItem) == null && !(heldItem.getItem() instanceof ItemBucket) && !(heldItem.getItem() instanceof UniversalBucket) || FluidUtil.getFluidContained(heldItem) != null) {
            player.setHeldItem(hand, this.itemStackHandler.insertItem(0, heldItem, false));
            this.world.addBlockEvent(this.pos, this.getBlockType(), 1, 0);
            this.getWorld().notifyBlockUpdate(pos, state, state, 3);
            this.world.notifyNeighborsOfStateChange(this.pos, this.getBlockType(), true);
            this.markDirty();
            return true;
         }

         boolean didFill = FluidUtil.interactWithFluidHandler(player, hand, (IFluidHandler)this.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, side));
         if (didFill) {
            this.world.addBlockEvent(this.pos, this.getBlockType(), 1, 0);
            this.getWorld().notifyBlockUpdate(pos, state, state, 3);
            this.world.notifyNeighborsOfStateChange(this.pos, this.getBlockType(), true);
            this.markDirty();
            return true;
         }
      } else if (player.isSneaking() && this.getAmount() > 0) {
         FluidStack drained = this.tank.drainInternal(capacity, true);
         SoundEvent soundevent = drained.getFluid().getEmptySound(drained);
         this.world.playSound((EntityPlayer)null, this.pos, soundevent, SoundCategory.BLOCKS, 1.0F, 1.0F);
         this.world.addBlockEvent(this.pos, this.getBlockType(), 1, 0);
         this.getWorld().notifyBlockUpdate(pos, state, state, 3);
         this.world.notifyNeighborsOfStateChange(this.pos, this.getBlockType(), true);
         this.markDirty();
         return true;
      }

      if (this.itemStackHandler.getStackInSlot(0) != ItemStack.EMPTY && !world.isRemote) {
         world.spawnEntity(new EntityItem(world, player.posX, player.posY, player.posZ, this.itemStackHandler.getStackInSlot(0)));
         this.itemStackHandler.setStackInSlot(0, ItemStack.EMPTY);
         this.world.addBlockEvent(this.pos, this.getBlockType(), 1, 0);
         this.getWorld().notifyBlockUpdate(pos, state, state, 3);
         this.world.notifyNeighborsOfStateChange(this.pos, this.getBlockType(), true);
         this.markDirty();
         return true;
      } else {
         return false;
      }
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

   public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing) {
      return capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY || capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY || super.hasCapability(capability, facing);
   }

   @Nullable
   public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing) {
      if (capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) {
         return (T)this.tank;
      } else {
         return (T)(capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY ? this.itemStackHandler : super.getCapability(capability, facing));
      }
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

   public void readFromNBT(NBTTagCompound tag) {
      super.readFromNBT(tag);
      if (tag.hasKey("items")) {
         this.itemStackHandler.deserializeNBT((NBTTagCompound)tag.getTag("items"));
      }

   }

   public NBTTagCompound writeToNBT(NBTTagCompound tag) {
      tag = super.writeToNBT(tag);
      tag.setTag("items", this.itemStackHandler.serializeNBT());
      return tag;
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

   public void breakBlock(World world, BlockPos pos, IBlockState state) {
      this.invalidate();
      if (this.itemStackHandler != null && !world.isRemote) {
         for(int i = 0; i < this.itemStackHandler.getSlots(); ++i) {
            if (this.itemStackHandler.getStackInSlot(i) != null) {
               Block.spawnAsEntity(world, pos, this.itemStackHandler.getStackInSlot(i));
            }
         }
      }

      world.setTileEntity(pos, (TileEntity)null);
   }
}
