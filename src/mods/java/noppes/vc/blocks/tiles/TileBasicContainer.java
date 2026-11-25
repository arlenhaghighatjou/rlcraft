package noppes.vc.blocks.tiles;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;

public abstract class TileBasicContainer extends TileBasicRotation implements IInventory {
   public final NonNullList<ItemStack> chestContents = NonNullList.withSize(this.getSizeInventory(), ItemStack.EMPTY);
   public String customName = "";
   public int playerUsing = 0;

   public TileBasicContainer() {
   }

   public void readFromNBT(NBTTagCompound compound) {
      super.readFromNBT(compound);
      NBTTagList nbttaglist = compound.getTagList("Items", 10);
      if (compound.hasKey("CustomName", 8)) {
         this.customName = compound.getString("CustomName");
      }

      this.chestContents.clear();

      for(int i = 0; i < nbttaglist.tagCount(); ++i) {
         NBTTagCompound nbttagcompound1 = nbttaglist.getCompoundTagAt(i);
         int j = nbttagcompound1.getByte("Slot") & 255;
         if (j >= 0 && j < this.chestContents.size()) {
            this.chestContents.set(j, new ItemStack(nbttagcompound1));
         }
      }

   }

   public NBTTagCompound writeToNBT(NBTTagCompound compound) {
      NBTTagList nbttaglist = new NBTTagList();

      for(int i = 0; i < this.chestContents.size(); ++i) {
         NBTTagCompound nbttagcompound1 = new NBTTagCompound();
         nbttagcompound1.setByte("Slot", (byte)i);
         ((ItemStack)this.chestContents.get(i)).writeToNBT(nbttagcompound1);
         nbttaglist.appendTag(nbttagcompound1);
      }

      compound.setTag("Items", nbttaglist);
      if (this.hasCustomName()) {
         compound.setString("CustomName", this.customName);
      }

      return super.writeToNBT(compound);
   }

   public boolean receiveClientEvent(int p_145842_1_, int p_145842_2_) {
      if (p_145842_1_ == 1) {
         this.playerUsing = p_145842_2_;
         return true;
      } else {
         return super.receiveClientEvent(p_145842_1_, p_145842_2_);
      }
   }

   public int getSizeInventory() {
      return 54;
   }

   public ItemStack getStackInSlot(int slot) {
      return (ItemStack)this.chestContents.get(slot);
   }

   public ItemStack decrStackSize(int slot, int count) {
      ItemStack itemstack = ItemStackHelper.getAndSplit(this.chestContents, slot, count);
      if (!itemstack.isEmpty()) {
         this.markDirty();
      }

      return itemstack;
   }

   public ItemStack removeStackFromSlot(int par1) {
      ItemStack item = this.getStackInSlot(par1);
      this.chestContents.remove(par1);
      return item;
   }

   public void setInventorySlotContents(int par1, ItemStack par2ItemStack) {
      this.chestContents.set(par1, par2ItemStack);
      if (!par2ItemStack.isEmpty() && par2ItemStack.getCount() > this.getInventoryStackLimit()) {
         par2ItemStack.setCount(this.getInventoryStackLimit());
      }

      this.markDirty();
   }

   public ITextComponent getDisplayName() {
      return new TextComponentString(this.hasCustomName() ? this.customName : this.getName());
   }

   public abstract String getName();

   public boolean hasCustomName() {
      return !this.customName.isEmpty();
   }

   public int getInventoryStackLimit() {
      return 64;
   }

   public boolean isUsableByPlayer(EntityPlayer player) {
      return !player.isDead && this.world.getTileEntity(this.pos) != this ? false : player.getDistanceSq((double)this.pos.getX() + 0.5D, (double)this.pos.getY() + 0.5D, (double)this.pos.getZ() + 0.5D) <= 64.0D;
   }

   public void openInventory(EntityPlayer player) {
      ++this.playerUsing;
   }

   public void closeInventory(EntityPlayer player) {
      --this.playerUsing;
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
   }

   public boolean isItemValidForSlot(int var1, ItemStack var2) {
      return true;
   }

   public void dropItems(World world, BlockPos pos) {
      InventoryHelper.dropInventoryItems(world, pos, this);
   }

   public boolean isEmpty() {
      for(int slot = 0; slot < this.getSizeInventory(); ++slot) {
         ItemStack item = this.getStackInSlot(slot);
         if (!item.isEmpty()) {
            return false;
         }
      }

      return true;
   }
}
