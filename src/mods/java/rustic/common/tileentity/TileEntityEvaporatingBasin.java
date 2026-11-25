package rustic.common.tileentity;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
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
import net.minecraft.util.ITickable;
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
import rustic.common.crafting.IEvaporatingBasinRecipe;
import rustic.common.crafting.Recipes;

public class TileEntityEvaporatingBasin extends TileFluidHandler implements ITickable {
   private int capacity = 6000;
   private FluidStack evaporatedFluid;
   private int age = 0;
   private int remainder = 0;
   private ItemStackHandler itemStackHandler = new ItemStackHandler(1) {
      protected void onContentsChanged(int slot) {
         IBlockState state = TileEntityEvaporatingBasin.this.world.getBlockState(TileEntityEvaporatingBasin.this.pos);
         TileEntityEvaporatingBasin.this.world.addBlockEvent(TileEntityEvaporatingBasin.this.pos, TileEntityEvaporatingBasin.this.getBlockType(), 1, 0);
         TileEntityEvaporatingBasin.this.getWorld().notifyBlockUpdate(TileEntityEvaporatingBasin.this.pos, state, state, 3);
         TileEntityEvaporatingBasin.this.world.notifyNeighborsOfStateChange(TileEntityEvaporatingBasin.this.pos, TileEntityEvaporatingBasin.this.getBlockType(), true);
         TileEntityEvaporatingBasin.this.markDirty();
      }
   };

   public TileEntityEvaporatingBasin() {
      this.tank = new FluidTank(this.capacity) {
         public boolean canFillFluidType(FluidStack fluid) {
            return fluid != null && Recipes.evaporatingRecipesMap.containsKey(fluid.getFluid()) && this.canFill();
         }
      };
      this.tank.setTileEntity(this);
      this.tank.setCanFill(true);
      this.tank.setCanDrain(true);
      this.evaporatedFluid = null;
   }

   public void update() {
      ++this.age;
      if (this.age >= 1000000) {
         this.age = 0;
      }

      if (this.age % 20 == 0) {
         if (this.getFluid() == null || this.evaporatedFluid != null && this.evaporatedFluid.getFluid() != this.getFluid()) {
            this.evaporatedFluid = null;
            this.remainder = 0;
         }

         int to_drain = 0;
         if (this.getFluid() != null) {
            IEvaporatingBasinRecipe recipe = (IEvaporatingBasinRecipe)Recipes.evaporatingRecipesMap.get(this.getFluid());
            int temp = 20 * recipe.getAmount() + this.remainder;
            to_drain = temp / recipe.getTime();
            this.remainder = temp % recipe.getTime();
         }

         FluidStack drained = this.tank.drain(to_drain, true);
         if (drained != null) {
            if (this.evaporatedFluid == null) {
               this.evaporatedFluid = drained;
            } else if (!this.evaporatedFluid.getFluid().equals(drained.getFluid())) {
               this.evaporatedFluid = drained;
            } else {
               this.evaporatedFluid.amount += drained.amount;
            }

            this.getWorld().notifyBlockUpdate(this.pos, this.getWorld().getBlockState(this.pos), this.getWorld().getBlockState(this.pos), 3);
            this.world.notifyNeighborsOfStateChange(this.pos, this.getBlockType(), true);
            this.markDirty();
         }

         if (this.evaporatedFluid != null && this.evaporatedFluid.amount > 0) {
            IEvaporatingBasinRecipe recipe = (IEvaporatingBasinRecipe)Recipes.evaporatingRecipesMap.get(this.evaporatedFluid.getFluid());
            ItemStack result = recipe.getOutput();
            if (this.evaporatedFluid.amount >= recipe.getAmount() && this.itemStackHandler.insertItem(0, result, true).isEmpty()) {
               this.evaporatedFluid.amount -= recipe.getAmount();
               this.itemStackHandler.insertItem(0, result, false);
               this.getWorld().notifyBlockUpdate(this.pos, this.getWorld().getBlockState(this.pos), this.getWorld().getBlockState(this.pos), 3);
               this.world.notifyNeighborsOfStateChange(this.pos, this.getBlockType(), true);
               this.markDirty();
            }
         }
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

      if (tag.hasKey("EvaporatedFluid")) {
         this.evaporatedFluid = FluidStack.loadFluidStackFromNBT(tag.getCompoundTag("EvaporatedFluid"));
      }

      if (tag.hasKey("remainder")) {
         this.remainder = tag.getInteger("remainder");
      }

   }

   public NBTTagCompound writeToNBT(NBTTagCompound tag) {
      tag = super.writeToNBT(tag);
      tag.setTag("items", this.itemStackHandler.serializeNBT());
      if (this.evaporatedFluid != null) {
         tag.setTag("EvaporatedFluid", this.evaporatedFluid.writeToNBT(new NBTTagCompound()));
      }

      tag.setInteger("remainder", this.remainder);
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

   public boolean activate(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
      ItemStack heldItem = player.getHeldItem(hand);
      if (heldItem != ItemStack.EMPTY) {
         if (FluidUtil.getFluidHandler(heldItem) != null || heldItem.getItem() instanceof ItemBucket || heldItem.getItem() instanceof UniversalBucket) {
            boolean didFill = FluidUtil.interactWithFluidHandler(player, hand, (IFluidHandler)this.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, side));
            if (didFill) {
               this.world.addBlockEvent(this.pos, this.getBlockType(), 1, 0);
               this.getWorld().notifyBlockUpdate(pos, state, state, 3);
               this.world.notifyNeighborsOfStateChange(this.pos, this.getBlockType(), true);
               this.markDirty();
            }

            return true;
         }
      } else if (player.isSneaking() && this.getAmount() > 0) {
         FluidStack drained = this.tank.drainInternal(this.capacity, true);
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
}
