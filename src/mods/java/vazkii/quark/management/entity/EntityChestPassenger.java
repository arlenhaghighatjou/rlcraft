package vazkii.quark.management.entity;

import javax.annotation.Nonnull;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;

public class EntityChestPassenger extends Entity implements IInventory {
   private final NonNullList<ItemStack> items;
   private static final DataParameter<ItemStack> CHEST_TYPE = EntityDataManager.createKey(EntityChestPassenger.class, DataSerializers.ITEM_STACK);
   private static final String TAG_CHEST_TYPE = "chestType";
   private boolean itemsCanceled;

   public EntityChestPassenger(World worldIn) {
      super(worldIn);
      this.items = NonNullList.withSize(27, ItemStack.EMPTY);
      this.itemsCanceled = false;
   }

   public EntityChestPassenger(World worldIn, ItemStack stack) {
      this(worldIn);
      ItemStack newStack = stack.copy();
      newStack.setCount(1);
      this.dataManager.set(CHEST_TYPE, newStack);
   }

   protected void entityInit() {
      this.noClip = true;
      this.dataManager.register(CHEST_TYPE, new ItemStack(Blocks.CHEST));
   }

   public void onUpdate() {
      super.onUpdate();
      if (!this.isDead) {
         if (!this.isRiding()) {
            if (!this.world.isRemote) {
               this.setDead();
            }

         } else {
            Entity riding = this.getRidingEntity();
            if (riding != null) {
               this.rotationYaw = riding.prevRotationYaw;
            }

            this.rotationPitch = 0.0F;
         }
      }
   }

   public int getSizeInventory() {
      return this.items.size();
   }

   public boolean isEmpty() {
      for(ItemStack itemstack : this.items) {
         if (!itemstack.isEmpty()) {
            return false;
         }
      }

      return true;
   }

   @Nonnull
   public ItemStack getStackInSlot(int index) {
      return (ItemStack)this.items.get(index);
   }

   @Nonnull
   public ItemStack decrStackSize(int index, int count) {
      return ItemStackHelper.getAndSplit(this.items, index, count);
   }

   @Nonnull
   public ItemStack removeStackFromSlot(int index) {
      ItemStack itemstack = (ItemStack)this.items.get(index);
      if (itemstack.isEmpty()) {
         return ItemStack.EMPTY;
      } else {
         this.items.set(index, ItemStack.EMPTY);
         return itemstack;
      }
   }

   public void setInventorySlotContents(int index, @Nonnull ItemStack stack) {
      this.items.set(index, stack);
   }

   public int getInventoryStackLimit() {
      return 64;
   }

   public void markDirty() {
   }

   public boolean isUsableByPlayer(@Nonnull EntityPlayer player) {
      return !this.isDead && player.getDistanceSq(this) <= 64.0D;
   }

   public void openInventory(@Nonnull EntityPlayer player) {
   }

   public void closeInventory(@Nonnull EntityPlayer player) {
   }

   public boolean isItemValidForSlot(int index, @Nonnull ItemStack stack) {
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
      this.items.clear();
   }

   public void setDropItemsWhenDead(boolean dropWhenDead) {
      this.itemsCanceled = dropWhenDead;
   }

   public void setDead() {
      if (!this.world.isRemote && !this.itemsCanceled) {
         InventoryHelper.dropInventoryItems(this.world, this, this);
         InventoryHelper.spawnItemStack(this.world, this.posX, this.posY, this.posZ, this.getChestType());
      }

      super.setDead();
   }

   protected void writeEntityToNBT(@Nonnull NBTTagCompound compound) {
      ItemStackHelper.saveAllItems(compound, this.items);
      NBTTagCompound itemCmp = new NBTTagCompound();
      ((ItemStack)this.dataManager.get(CHEST_TYPE)).writeToNBT(itemCmp);
      compound.setTag("chestType", itemCmp);
   }

   protected void readEntityFromNBT(@Nonnull NBTTagCompound compound) {
      ItemStackHelper.loadAllItems(compound, this.items);
      NBTTagCompound itemCmp = compound.getCompoundTag("chestType");
      ItemStack stack = new ItemStack(itemCmp);
      if (!stack.isEmpty()) {
         this.dataManager.set(CHEST_TYPE, stack);
      }

   }

   public ItemStack getChestType() {
      return (ItemStack)this.dataManager.get(CHEST_TYPE);
   }
}
