package rustic.common.tileentity;

import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityLockableLoot;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fml.common.Optional.Interface;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import rustic.common.blocks.BlockCabinet;
import rustic.common.blocks.ModBlocks;
import rustic.common.inventory.DoubleCabinetItemHandler;
import rustic.common.util.ItemStackHandlerRustic;
import vazkii.quark.api.IDropoffManager;

@Interface(
   modid = "quark",
   iface = "vazkii.quark.api.IDropoffManager",
   striprefs = true
)
public class TileEntityCabinet extends TileEntityLockableLoot implements ITickable, IDropoffManager {
   public float lidAngle = 0.0F;
   public float prevLidAngle = 0.0F;
   public int numPlayersUsing = 0;
   private int ticksSinceSync = 0;
   public ItemStack material = ItemStack.EMPTY;
   private ItemStackHandlerRustic itemStackHandler = new ItemStackHandlerRustic(27) {
      protected void onContentsChanged(int slot) {
         TileEntityCabinet.this.markDirty();
      }
   };
   @SideOnly(Side.CLIENT)
   private AxisAlignedBB renderAABB;
   public DoubleCabinetItemHandler doubleCabinetHandler;

   public TileEntityCabinet() {
   }

   @SideOnly(Side.CLIENT)
   public AxisAlignedBB getRenderBoundingBox() {
      return this.world.getBlockState(this.pos.up()).getBlock() == ModBlocks.CABINET && this.world.getBlockState(this.pos.up()).getPropertyKeys().contains(BlockCabinet.TOP) && ((Boolean)this.world.getBlockState(this.pos.up()).getValue(BlockCabinet.TOP)).booleanValue() ? new AxisAlignedBB((double)this.pos.getX(), (double)this.pos.getY(), (double)this.pos.getZ(), (double)this.pos.getX() + 1.0D, (double)this.pos.getY() + 2.0D, (double)this.pos.getZ() + 1.0D) : new AxisAlignedBB((double)this.pos.getX(), (double)this.pos.getY(), (double)this.pos.getZ(), (double)this.pos.getX() + 1.0D, (double)this.pos.getY() + 1.0D, (double)this.pos.getZ() + 1.0D);
   }

   public void update() {
      int i = this.pos.getX();
      int j = this.pos.getY();
      int k = this.pos.getZ();
      ++this.ticksSinceSync;
      if (!this.world.isRemote && this.numPlayersUsing != 0 && (this.ticksSinceSync + i + j + k) % 200 == 0) {
         int recordedUsers = this.numPlayersUsing;
         this.numPlayersUsing = 0;

         for(EntityPlayer entityplayer : this.world.getEntitiesWithinAABB(EntityPlayer.class, new AxisAlignedBB((double)((float)i - 5.0F), (double)((float)j - 5.0F), (double)((float)k - 5.0F), (double)((float)(i + 1) + 5.0F), (double)((float)(j + 1) + 5.0F), (double)((float)(k + 1) + 5.0F)))) {
            if (entityplayer.openContainer instanceof ContainerCabinet) {
               TileEntityCabinet tec = ((ContainerCabinet)entityplayer.openContainer).getTile();
               if (tec.equals(this)) {
                  ++this.numPlayersUsing;
               }
            } else if (entityplayer.openContainer instanceof ContainerCabinetDouble) {
               TileEntityCabinet tec = ((ContainerCabinetDouble)entityplayer.openContainer).getTileBottom();
               if (tec.equals(this)) {
                  ++this.numPlayersUsing;
               }
            }
         }

         if (this.numPlayersUsing != recordedUsers) {
            this.markDirty();
         }
      }

      this.prevLidAngle = this.lidAngle;
      if (this.numPlayersUsing > 0 && this.lidAngle == 0.0F) {
         this.world.playSound((EntityPlayer)null, (double)i + 0.5D, (double)j + 0.5D, (double)k + 0.5D, SoundEvents.BLOCK_CHEST_OPEN, SoundCategory.BLOCKS, 0.5F, this.world.rand.nextFloat() * 0.1F + 0.9F);
      }

      if (this.numPlayersUsing == 0 && this.lidAngle > 0.0F || this.numPlayersUsing > 0 && this.lidAngle < 1.0F) {
         float f2 = this.lidAngle;
         if (this.numPlayersUsing > 0) {
            this.lidAngle += 0.1F;
         } else {
            this.lidAngle -= 0.1F;
         }

         if (this.lidAngle > 1.0F) {
            this.lidAngle = 1.0F;
         }

         if (this.lidAngle < 0.5F && f2 >= 0.5F) {
            this.world.playSound((EntityPlayer)null, (double)i + 0.5D, (double)j + 0.5D, (double)k + 0.5D, SoundEvents.BLOCK_CHEST_CLOSE, SoundCategory.BLOCKS, 0.5F, this.world.rand.nextFloat() * 0.1F + 0.9F);
         }

         if (this.lidAngle < 0.0F) {
            this.lidAngle = 0.0F;
         }
      }

   }

   public boolean receiveClientEvent(int id, int type) {
      if (id == 1) {
         this.numPlayersUsing = type;
         return true;
      } else {
         return super.receiveClientEvent(id, type);
      }
   }

   public void openInventory(EntityPlayer player) {
      if (!player.isSpectator()) {
         if (this.numPlayersUsing < 0) {
            this.numPlayersUsing = 0;
         }

         ++this.numPlayersUsing;
         this.world.addBlockEvent(this.pos, this.getBlockType(), 1, this.numPlayersUsing);
         this.world.notifyNeighborsOfStateChange(this.pos, this.getBlockType(), false);
         this.markDirty();
      }

   }

   public void closeInventory(EntityPlayer player) {
      if (!player.isSpectator() && this.getBlockType() instanceof BlockCabinet) {
         --this.numPlayersUsing;
         this.world.addBlockEvent(this.pos, this.getBlockType(), 1, this.numPlayersUsing);
         this.world.notifyNeighborsOfStateChange(this.pos, this.getBlockType(), false);
         this.markDirty();
      }

   }

   public void readFromNBT(NBTTagCompound compound) {
      super.readFromNBT(compound);
      if (!this.checkLootAndRead(compound) && compound.hasKey("items")) {
         this.itemStackHandler.deserializeNBT((NBTTagCompound)compound.getTag("items"));
      }

      if (compound.hasKey("CustomName", 8)) {
         this.setCustomName(compound.getString("CustomName"));
      }

      if (compound.hasKey("numUsers")) {
         this.numPlayersUsing = compound.getInteger("numUsers");
      }

      if (compound.hasKey("material")) {
         this.material = new ItemStack(compound.getCompoundTag("material"));
      }

   }

   public NBTTagCompound writeToNBT(NBTTagCompound compound) {
      super.writeToNBT(compound);
      if (!this.checkLootAndWrite(compound)) {
         compound.setTag("items", this.itemStackHandler.serializeNBT());
      }

      if (this.hasCustomName()) {
         compound.setString("CustomName", this.customName);
      }

      compound.setTag("numUsers", new NBTTagInt(this.numPlayersUsing));
      compound.setTag("material", this.material.serializeNBT());
      return compound;
   }

   public boolean canInteractWith(EntityPlayer playerIn) {
      return !this.isInvalid() && playerIn.getDistanceSq(this.pos.add(0.5D, 0.5D, 0.5D)) <= 64.0D;
   }

   public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
      return capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY ? true : super.hasCapability(capability, facing);
   }

   public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
      if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
         if (!this.getWorld().isRemote) {
            this.fillWithLoot((EntityPlayer)null);
         }

         if (this.doubleCabinetHandler == null || this.doubleCabinetHandler.needsRefresh()) {
            this.doubleCabinetHandler = DoubleCabinetItemHandler.get(this);
         }

         if (this.doubleCabinetHandler != null && this.doubleCabinetHandler != DoubleCabinetItemHandler.NO_ADJACENT_CABINETS_INSTANCE) {
            if (!this.getWorld().isRemote) {
               this.doubleCabinetHandler.getOtherCabinet().fillWithLoot((EntityPlayer)null);
            }

            return (T)this.doubleCabinetHandler;
         } else {
            return (T)this.itemStackHandler;
         }
      } else {
         return (T)super.getCapability(capability, facing);
      }
   }

   public IItemHandler getSingleCabinetHandler() {
      return this.itemStackHandler;
   }

   public void updateContainingBlockInfo() {
      super.updateContainingBlockInfo();
      this.doubleCabinetHandler = null;
   }

   public void breakBlock(World world, BlockPos pos, IBlockState state) {
      this.invalidate();
      if (this.itemStackHandler != null && !world.isRemote) {
         this.fillWithLoot((EntityPlayer)null);

         for(int i = 0; i < this.itemStackHandler.getSlots(); ++i) {
            if (this.itemStackHandler.getStackInSlot(i) != null) {
               Block.spawnAsEntity(world, pos, this.itemStackHandler.getStackInSlot(i));
            }
         }
      }

      if (!world.isRemote) {
         ItemStack toDrop = new ItemStack(ModBlocks.CABINET);
         NBTTagCompound tag = new NBTTagCompound();
         tag.setTag("material", this.material.serializeNBT());
         toDrop.setTagCompound(tag);
         if (this.hasCustomName()) {
            toDrop.setStackDisplayName(this.customName);
         }

         Block.spawnAsEntity(world, pos, toDrop);
      }

      world.setTileEntity(pos, (TileEntity)null);
   }

   public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newState) {
      return oldState.getBlock() != newState.getBlock();
   }

   public int getSizeInventory() {
      return this.itemStackHandler.getSlots();
   }

   public boolean isEmpty() {
      for(int i = 0; i < this.itemStackHandler.getSlots(); ++i) {
         if (this.itemStackHandler.getStackInSlot(i) != ItemStack.EMPTY) {
            return false;
         }
      }

      return true;
   }

   public ItemStack getStackInSlot(int index) {
      this.fillWithLoot((EntityPlayer)null);
      return this.itemStackHandler.getStackInSlot(index);
   }

   public ItemStack decrStackSize(int index, int count) {
      this.fillWithLoot((EntityPlayer)null);
      return this.itemStackHandler.extractItem(index, count, false);
   }

   public ItemStack removeStackFromSlot(int index) {
      this.fillWithLoot((EntityPlayer)null);
      ItemStack stack = this.itemStackHandler.getStackInSlot(index);
      this.itemStackHandler.setStackInSlot(index, ItemStack.EMPTY);
      return stack;
   }

   public void setInventorySlotContents(int index, ItemStack stack) {
      this.fillWithLoot((EntityPlayer)null);
      this.itemStackHandler.setStackInSlot(index, stack);
   }

   public int getInventoryStackLimit() {
      return 64;
   }

   public boolean isUsableByPlayer(EntityPlayer player) {
      return true;
   }

   public boolean isItemValidForSlot(int index, ItemStack stack) {
      return true;
   }

   public int getField(int id) {
      return 0;
   }

   public void setField(int id, int value) {
   }

   public int getFieldCount() {
      return 0;
   }

   public void clear() {
      this.fillWithLoot((EntityPlayer)null);

      for(int i = 0; i < this.itemStackHandler.getSlots(); ++i) {
         this.itemStackHandler.setStackInSlot(i, ItemStack.EMPTY);
      }

   }

   public String getName() {
      return this.hasCustomName() ? this.customName : "container.rustic.cabinet";
   }

   public Container createContainer(InventoryPlayer playerInventory, EntityPlayer playerIn) {
      if (!this.world.isRemote) {
         this.fillWithLoot(playerIn);
      }

      if (((Boolean)this.getWorld().getBlockState(this.pos).getValue(BlockCabinet.TOP)).booleanValue()) {
         if (!this.world.isRemote) {
            ((TileEntityCabinet)this.world.getTileEntity(this.pos.down())).fillWithLoot(playerIn);
         }

         return new ContainerCabinetDouble(playerInventory, this, (TileEntityCabinet)this.getWorld().getTileEntity(this.pos.down()), playerIn);
      } else if (this.world.getBlockState(this.pos.up()).getBlock() instanceof BlockCabinet && ((Boolean)this.world.getBlockState(this.pos.up()).getValue(BlockCabinet.TOP)).booleanValue()) {
         if (!this.world.isRemote) {
            ((TileEntityCabinet)this.world.getTileEntity(this.pos.up())).fillWithLoot(playerIn);
         }

         return new ContainerCabinetDouble(playerInventory, (TileEntityCabinet)this.getWorld().getTileEntity(this.pos.up()), this, playerIn);
      } else {
         return new ContainerCabinet(playerInventory, this, playerIn);
      }
   }

   public String getGuiID() {
      return "rustic:cabinet";
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

   protected NonNullList<ItemStack> getItems() {
      return this.itemStackHandler.getStacks();
   }

   public void setLootTable(ResourceLocation file, long seed) {
      super.setLootTable(file, seed);
      this.markDirty();
   }

   public boolean acceptsDropoff(EntityPlayer entityPlayer) {
      return this.canInteractWith(entityPlayer);
   }
}
