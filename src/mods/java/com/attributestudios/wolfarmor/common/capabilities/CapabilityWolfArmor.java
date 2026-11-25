package com.attributestudios.wolfarmor.common.capabilities;

import com.attributestudios.wolfarmor.api.IWolfArmorCapability;
import com.attributestudios.wolfarmor.api.util.Capabilities;
import dev.satyrn.wolfarmor.api.util.CreatureFoodStats;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.Capability.IStorage;

/** @deprecated */
@Deprecated
public class CapabilityWolfArmor implements IWolfArmorCapability {
   public static final CapabilityWolfArmor.Storage STORAGE = new CapabilityWolfArmor.Storage();
   private final InventoryBasic inventory = new InventoryBasic("", false, this.getMaxSizeInventory());
   private final CreatureFoodStats foodStats = new CreatureFoodStats();

   public CapabilityWolfArmor() {
   }

   /** @deprecated */
   public boolean processInteract(@Nonnull EntityPlayer player, @Nonnull EnumHand hand) {
      return false;
   }

   public boolean damageArmor(float damage) {
      return false;
   }

   @Nonnull
   public CreatureFoodStats getFoodStats() {
      return this.foodStats;
   }

   public void addExhaustion(float exhaustion) {
   }

   @Nullable
   public Item getChestType() {
      return null;
   }

   public void setChestType(@Nonnull ItemStack chestType) {
   }

   public boolean getHasChest() {
      return false;
   }

   public void setHasChest(boolean hasChest) {
   }

   public int getMaxSizeInventory() {
      return 1;
   }

   @Nonnull
   public InventoryBasic getInventory() {
      return this.inventory;
   }

   @Nonnull
   public ItemStack getArmorItemStack() {
      return ItemStack.EMPTY;
   }

   public void setArmorItemStack(@Nonnull ItemStack armorItemStack) {
   }

   public boolean getHasArmor() {
      return false;
   }

   public boolean canEquipItem(@Nonnull ItemStack armorItemStack) {
      return false;
   }

   public void equipArmor(@Nonnull ItemStack armorItemStack) {
   }

   public void setInventoryItem(int index, @Nonnull ItemStack itemStack) {
   }

   public void dropEquipment() {
   }

   public void dropChest() {
   }

   public void dropInventoryContents() {
   }

   public void onInventoryChanged(@Nonnull IInventory invBasic) {
   }

   public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing) {
      return false;
   }

   @Nullable
   public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing) {
      return (T)null;
   }

   @Nonnull
   public NBTTagCompound serializeNBT() {
      return new NBTTagCompound();
   }

   public void deserializeNBT(NBTTagCompound nbt) {
   }

   public static class Storage implements IStorage<IWolfArmorCapability> {
      public Storage() {
      }

      @Nullable
      public NBTBase writeNBT(Capability<IWolfArmorCapability> capability, IWolfArmorCapability instance, EnumFacing side) {
         return capability != Capabilities.CAPABILITY_WOLF_ARMOR ? null : instance.serializeNBT();
      }

      public void readNBT(Capability<IWolfArmorCapability> capability, IWolfArmorCapability instance, EnumFacing side, NBTBase nbt) {
         if (capability == Capabilities.CAPABILITY_WOLF_ARMOR) {
            if (nbt instanceof NBTTagCompound) {
               instance.deserializeNBT((NBTTagCompound)nbt);
            }

         }
      }
   }
}
