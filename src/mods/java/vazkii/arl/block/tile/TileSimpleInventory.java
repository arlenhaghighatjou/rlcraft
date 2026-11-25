package vazkii.arl.block.tile;

import javax.annotation.Nonnull;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.wrapper.SidedInvWrapper;

public abstract class TileSimpleInventory extends TileMod implements ISidedInventory {
   protected NonNullList<ItemStack> inventorySlots = NonNullList.withSize(this.getSizeInventory(), ItemStack.EMPTY);
   private String name = null;

   public TileSimpleInventory() {
   }

   public void readSharedNBT(NBTTagCompound par1NBTTagCompound) {
      if (this.needsToSyncInventory()) {
         NBTTagList var2 = par1NBTTagCompound.getTagList("Items", 10);
         this.clear();

         for(int var3 = 0; var3 < var2.tagCount(); ++var3) {
            NBTTagCompound var4 = var2.getCompoundTagAt(var3);
            byte var5 = var4.getByte("Slot");
            if (var5 >= 0 && var5 < this.inventorySlots.size()) {
               this.inventorySlots.set(var5, new ItemStack(var4));
            }
         }

      }
   }

   public void writeSharedNBT(NBTTagCompound par1NBTTagCompound) {
      if (this.needsToSyncInventory()) {
         NBTTagList var2 = new NBTTagList();

         for(int var3 = 0; var3 < this.inventorySlots.size(); ++var3) {
            if (!((ItemStack)this.inventorySlots.get(var3)).isEmpty()) {
               NBTTagCompound var4 = new NBTTagCompound();
               var4.setByte("Slot", (byte)var3);
               ((ItemStack)this.inventorySlots.get(var3)).writeToNBT(var4);
               var2.appendTag(var4);
            }
         }

         par1NBTTagCompound.setTag("Items", var2);
      }
   }

   protected boolean needsToSyncInventory() {
      return true;
   }

   @Nonnull
   public ItemStack getStackInSlot(int i) {
      return (ItemStack)this.inventorySlots.get(i);
   }

   @Nonnull
   public ItemStack decrStackSize(int i, int j) {
      if (!((ItemStack)this.inventorySlots.get(i)).isEmpty()) {
         if (((ItemStack)this.inventorySlots.get(i)).getCount() <= j) {
            ItemStack stackAt = (ItemStack)this.inventorySlots.get(i);
            this.inventorySlots.set(i, ItemStack.EMPTY);
            this.inventoryChanged(i);
            return stackAt;
         } else {
            ItemStack stackAt = ((ItemStack)this.inventorySlots.get(i)).splitStack(j);
            if (((ItemStack)this.inventorySlots.get(i)).getCount() == 0) {
               this.inventorySlots.set(i, ItemStack.EMPTY);
            }

            this.inventoryChanged(i);
            return stackAt;
         }
      } else {
         return ItemStack.EMPTY;
      }
   }

   @Nonnull
   public ItemStack removeStackFromSlot(int i) {
      ItemStack stack = this.getStackInSlot(i);
      this.setInventorySlotContents(i, ItemStack.EMPTY);
      this.inventoryChanged(i);
      return stack;
   }

   public void setInventorySlotContents(int i, @Nonnull ItemStack itemstack) {
      this.inventorySlots.set(i, itemstack);
      this.inventoryChanged(i);
   }

   public int getInventoryStackLimit() {
      return 64;
   }

   public boolean isEmpty() {
      for(int i = 0; i < this.getSizeInventory(); ++i) {
         ItemStack stack = this.getStackInSlot(i);
         if (!stack.isEmpty()) {
            return false;
         }
      }

      return true;
   }

   public boolean isUsableByPlayer(@Nonnull EntityPlayer entityplayer) {
      return this.getWorld().getTileEntity(this.getPos()) == this && entityplayer.getDistanceSq((double)this.pos.getX() + 0.5D, (double)this.pos.getY() + 0.5D, (double)this.pos.getZ() + 0.5D) <= 64.0D;
   }

   public boolean hasCapability(@Nonnull Capability<?> capability, EnumFacing facing) {
      return capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY;
   }

   public <T> T getCapability(@Nonnull Capability<T> capability, EnumFacing facing) {
      return (T)(capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY ? CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.cast(new SidedInvWrapper(this, facing)) : null);
   }

   public boolean isItemValidForSlot(int i, @Nonnull ItemStack itemstack) {
      return true;
   }

   public boolean hasCustomName() {
      return false;
   }

   public void openInventory(@Nonnull EntityPlayer player) {
   }

   public void closeInventory(@Nonnull EntityPlayer player) {
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
      this.inventorySlots = NonNullList.withSize(this.getSizeInventory(), ItemStack.EMPTY);
   }

   @Nonnull
   public ITextComponent getDisplayName() {
      return new TextComponentTranslation(this.getName(), new Object[0]);
   }

   @Nonnull
   public String getName() {
      if (this.name == null) {
         this.name = (new ItemStack(this.world.getBlockState(this.getPos()).getBlock())).getUnlocalizedName() + ".name";
      }

      return this.name;
   }

   public void inventoryChanged(int i) {
   }

   public boolean isAutomationEnabled() {
      return true;
   }

   public boolean canExtractItem(int index, @Nonnull ItemStack stack, @Nonnull EnumFacing direction) {
      return this.isAutomationEnabled();
   }

   public boolean canInsertItem(int index, @Nonnull ItemStack itemStackIn, @Nonnull EnumFacing direction) {
      return this.isAutomationEnabled();
   }

   @Nonnull
   public int[] getSlotsForFace(@Nonnull EnumFacing side) {
      if (!this.isAutomationEnabled()) {
         return new int[0];
      } else {
         int[] slots = new int[this.getSizeInventory()];

         for(int i = 0; i < slots.length; slots[i] = i++) {
            ;
         }

         return slots;
      }
   }
}
