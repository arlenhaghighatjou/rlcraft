package de.Whitedraco.switchbow.tileentity;

import de.Whitedraco.switchbow.gui.ContainerArrowDispenser;
import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemArrow;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntityLockableLoot;
import net.minecraft.util.NonNullList;

public class TileEntityArrowDispenser extends TileEntityLockableLoot implements IInventory {
   private static final EaglercraftRandom RNG = new EaglercraftRandom();
   private NonNullList<ItemStack> stacks = NonNullList.withSize(9, ItemStack.EMPTY);
   protected String customName;
   private float shootangle = 45.5F;
   private float range = 0.8F;
   private int count = 9;
   private boolean canshoot = false;

   public TileEntityArrowDispenser() {
   }

   public int getSizeInventory() {
      return 9;
   }

   public boolean isItemValidForSlot(int index, ItemStack stack) {
      return !(stack.getItem() instanceof ItemArrow) ? false : super.isItemValidForSlot(index, stack);
   }

   public int getDispenseSlot() {
      this.fillWithLoot((EntityPlayer)null);
      int i = -1;
      int j = 1;

      for(int k = 0; k < this.stacks.size(); ++k) {
         if (!((ItemStack)this.stacks.get(k)).isEmpty() && RNG.nextInt(j++) == 0) {
            i = k;
         }
      }

      return i;
   }

   public int addItemStack(ItemStack stack) {
      for(int i = 0; i < this.stacks.size(); ++i) {
         if (((ItemStack)this.stacks.get(i)).isEmpty()) {
            this.setInventorySlotContents(i, stack);
            return i;
         }
      }

      return -1;
   }

   public String getName() {
      return this.hasCustomName() ? this.customName : "tile.name.ArrowDispenser";
   }

   public void readFromNBT(NBTTagCompound compound) {
      super.readFromNBT(compound);
      this.stacks = NonNullList.withSize(this.getSizeInventory(), ItemStack.EMPTY);
      if (!this.checkLootAndRead(compound)) {
         ItemStackHelper.loadAllItems(compound, this.stacks);
      }

      if (compound.hasKey("CustomName", 8)) {
         this.customName = compound.getString("CustomName");
      }

      this.shootangle = compound.getFloat("shootangle");
      this.range = compound.getFloat("range");
      this.count = compound.getInteger("count");
      this.canshoot = compound.getBoolean("canshoot");
   }

   public NBTTagCompound writeToNBT(NBTTagCompound compound) {
      super.writeToNBT(compound);
      if (!this.checkLootAndWrite(compound)) {
         ItemStackHelper.saveAllItems(compound, this.stacks);
      }

      if (this.hasCustomName()) {
         compound.setString("CustomName", this.customName);
      }

      compound.setFloat("shootangle", this.shootangle);
      compound.setFloat("range", this.range);
      compound.setInteger("count", this.count);
      compound.setBoolean("canshoot", this.canshoot);
      return compound;
   }

   public int getInventoryStackLimit() {
      return 64;
   }

   public String getGuiID() {
      return "minecraft:dispenser";
   }

   public Container createContainer(InventoryPlayer playerInventory, EntityPlayer playerIn) {
      this.fillWithLoot(playerIn);
      return new ContainerArrowDispenser(playerInventory, this);
   }

   public float getShootangle() {
      return this.shootangle;
   }

   public void setShootangle(float shootangle) {
      this.shootangle = shootangle;
      this.markDirty();
   }

   public float getRange() {
      return this.range;
   }

   public void setRange(float range) {
      this.range = range;
      this.markDirty();
   }

   public int getCount() {
      return this.count;
   }

   public void setCount(int count) {
      this.count = count;
      this.markDirty();
   }

   public boolean isCanshoot() {
      return this.canshoot;
   }

   public void setCanshoot(boolean canshoot) {
      this.canshoot = canshoot;
      this.markDirty();
   }

   public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
      NBTTagCompound tag = pkt.getNbtCompound();
      this.readUpdateTag(tag);
   }

   public SPacketUpdateTileEntity getUpdatePacket() {
      NBTTagCompound tag = new NBTTagCompound();
      this.writeUpdateTag(tag);
      return new SPacketUpdateTileEntity(this.pos, this.getBlockMetadata(), tag);
   }

   public NBTTagCompound getUpdateTag() {
      NBTTagCompound tag = super.getUpdateTag();
      this.writeUpdateTag(tag);
      return tag;
   }

   public void writeUpdateTag(NBTTagCompound tag) {
      tag.setFloat("shootangle", this.shootangle);
      tag.setFloat("range", this.range);
      tag.setInteger("count", this.count);
      tag.setBoolean("canshoot", this.canshoot);
   }

   public void readUpdateTag(NBTTagCompound tag) {
      this.shootangle = tag.getFloat("shootangle");
      this.range = tag.getFloat("range");
      this.count = tag.getInteger("count");
      this.canshoot = tag.getBoolean("canshoot");
   }

   public boolean isEmpty() {
      for(ItemStack itemstack : this.stacks) {
         if (!itemstack.isEmpty()) {
            return false;
         }
      }

      return true;
   }

   protected NonNullList<ItemStack> getItems() {
      return this.stacks;
   }
}
