package com.tmtravlr.qualitytools.reforging;

import com.google.common.base.Predicate;
import com.tmtravlr.qualitytools.QualityToolsHelper;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntityLockable;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraft.util.SoundCategory;

public class TileEntityReforgingStation extends TileEntityLockable implements ISidedInventory {
   private NonNullList<ItemStack> stationItems = NonNullList.withSize(2, ItemStack.EMPTY);
   private String customName;

   public TileEntityReforgingStation() {
   }

   public void reforgeTool() {
      if (!this.world.isRemote && this.canReforge()) {
         ItemStack tool = this.getStackInSlot(0);
         QualityToolsHelper.generateQualityTag(tool, true);
         ItemStack material = this.getStackInSlot(1);
         if (!material.isEmpty()) {
            Item materialItem = material.getItem();
            ItemStack container = materialItem.getContainerItem(material);
            material.shrink(1);
            if (!container.isEmpty()) {
               if (material.isEmpty()) {
                  this.setInventorySlotContents(1, container);
               } else if (ItemStack.areItemsEqual(material, container) && ItemStack.areItemStackTagsEqual(material, container)) {
                  material.grow(1);
               } else {
                  EntityItem droppedItem = new EntityItem(this.world, (double)this.pos.getX() + 0.5D, (double)this.pos.getY() + 1.2D, (double)this.pos.getZ() + 0.5D, container);
                  this.world.spawnEntity(droppedItem);
               }
            }
         }

         this.world.playSound((EntityPlayer)null, (double)this.pos.getX(), (double)this.pos.getY(), (double)this.pos.getZ(), SoundEvents.BLOCK_ANVIL_USE, SoundCategory.BLOCKS, 1.0F, 1.0F);
      }

   }

   public void readFromNBT(NBTTagCompound compound) {
      super.readFromNBT(compound);
      NBTTagList nbttaglist = compound.getTagList("Items", 10);

      for(int i = 0; i < nbttaglist.tagCount(); ++i) {
         NBTTagCompound nbttagcompound = nbttaglist.getCompoundTagAt(i);
         int j = nbttagcompound.getByte("Slot");
         if (j >= 0 && j < this.stationItems.size()) {
            this.stationItems.set(j, new ItemStack(nbttagcompound));
         }
      }

      if (compound.hasKey("CustomName", 8)) {
         this.customName = compound.getString("CustomName");
      }

   }

   public NBTTagCompound writeToNBT(NBTTagCompound compound) {
      super.writeToNBT(compound);
      NBTTagList nbttaglist = new NBTTagList();

      for(int i = 0; i < this.stationItems.size(); ++i) {
         if (this.getStackInSlot(i) != null) {
            NBTTagCompound nbttagcompound = new NBTTagCompound();
            nbttagcompound.setByte("Slot", (byte)i);
            ((ItemStack)this.stationItems.get(i)).writeToNBT(nbttagcompound);
            nbttaglist.appendTag(nbttagcompound);
         }
      }

      compound.setTag("Items", nbttaglist);
      if (this.hasCustomName()) {
         compound.setString("CustomName", this.customName);
      }

      return compound;
   }

   @Nullable
   public ItemStack getStackInSlot(int index) {
      return index >= 0 && index < this.stationItems.size() ? (ItemStack)this.stationItems.get(index) : ItemStack.EMPTY;
   }

   @Nullable
   public ItemStack decrStackSize(int index, int count) {
      this.markDirty();
      return ItemStackHelper.getAndSplit(this.stationItems, index, count);
   }

   @Nullable
   public ItemStack removeStackFromSlot(int index) {
      this.markDirty();
      return ItemStackHelper.getAndRemove(this.stationItems, index);
   }

   public void setInventorySlotContents(int index, ItemStack stack) {
      this.markDirty();
      if (index >= 0 && index < this.stationItems.size()) {
         this.stationItems.set(index, stack);
      }

   }

   public void markDirty() {
      super.markDirty();
      if (!this.world.isRemote) {
         List<EntityPlayerMP> playersNear = this.world.getPlayers(EntityPlayerMP.class, (player) -> {
            return true;
         });
         playersNear.forEach((player) -> {
            if (!(player.openContainer instanceof ContainerReforgingStation)) {
               player.connection.sendPacket(this.getUpdatePacket());
            }

         });
      }

   }

   public int getInventoryStackLimit() {
      return 64;
   }

   public boolean isUsableByPlayer(EntityPlayer player) {
      return this.world.getTileEntity(this.pos) != this ? false : player.getDistanceSq((double)this.pos.getX() + 0.5D, (double)this.pos.getY() + 0.5D, (double)this.pos.getZ() + 0.5D) <= 64.0D;
   }

   public SPacketUpdateTileEntity getUpdatePacket() {
      return new SPacketUpdateTileEntity(this.pos, 0, this.getUpdateTag());
   }

   public NBTTagCompound getUpdateTag() {
      return this.writeToNBT(new NBTTagCompound());
   }

   public void onDataPacket(NetworkManager manager, SPacketUpdateTileEntity packet) {
      this.readFromNBT(packet.getNbtCompound());
   }

   public void openInventory(EntityPlayer player) {
   }

   public void closeInventory(EntityPlayer player) {
   }

   public boolean isItemValidForSlot(int index, ItemStack stack) {
      return index != 0 || QualityToolsHelper.hasQualityTag(stack);
   }

   public String getGuiID() {
      return "qualitytools:reforging_station";
   }

   public Container createContainer(InventoryPlayer playerInventory, EntityPlayer playerIn) {
      return new ContainerReforgingStation(playerInventory, this);
   }

   public int getField(int id) {
      if (id == 0) {
         return this.canReforge() ? 1 : 0;
      } else {
         return 0;
      }
   }

   public void setField(int id, int value) {
   }

   public int getFieldCount() {
      return 1;
   }

   public int[] getSlotsForFace(EnumFacing side) {
      return side == EnumFacing.UP ? new int[]{0} : new int[]{1};
   }

   public boolean canInsertItem(int index, ItemStack itemStackIn, EnumFacing direction) {
      int[] validSlots = this.getSlotsForFace(direction);
      return this.isItemValidForSlot(index, itemStackIn) && validSlots != null && Arrays.asList(validSlots).contains(Integer.valueOf(index));
   }

   public boolean canExtractItem(int index, ItemStack stack, EnumFacing direction) {
      return true;
   }

   public String getName() {
      return this.hasCustomName() ? this.customName : "container.reforging_station";
   }

   public boolean hasCustomName() {
      return this.customName != null && !this.customName.isEmpty();
   }

   public void setName(String name) {
      this.customName = name;
   }

   public int getSizeInventory() {
      return this.stationItems.size();
   }

   public void clear() {
      this.stationItems.clear();
   }

   public boolean isEmpty() {
      return this.getStackInSlot(0).isEmpty() && this.getStackInSlot(1).isEmpty();
   }

   private boolean canReforge() {
      return QualityToolsHelper.canReforgeWith(this.getStackInSlot(0), this.getStackInSlot(1));
   }
}
