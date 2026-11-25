package net.mcft.copy.backpacks.misc;

import net.mcft.copy.backpacks.WearableBackpacks;
import net.mcft.copy.backpacks.api.BackpackHelper;
import net.mcft.copy.backpacks.api.BackpackRegistry;
import net.mcft.copy.backpacks.api.IBackpack;
import net.mcft.copy.backpacks.api.IBackpackData;
import net.mcft.copy.backpacks.api.IBackpackType;
import net.mcft.copy.backpacks.config.ModConfig;
import net.mcft.copy.backpacks.misc.util.MiscUtils;
import net.mcft.copy.backpacks.misc.util.NbtUtils;
import net.mcft.copy.backpacks.network.MessageBackpackUpdate;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.capabilities.Capability.IStorage;

public class BackpackCapability implements IBackpack {
   public static final String TAG_STACK = "stack";
   public static final String TAG_TYPE = "type";
   public static final String TAG_DATA = "data";
   public static final String TAG_MAY_DESPAWN = "mayDespawn";
   public static final ResourceLocation IDENTIFIER = new ResourceLocation("wearablebackpacks:backpack");
   public final EntityLivingBase entity;
   public ItemStack stack = ItemStack.EMPTY;
   public IBackpackData data = null;
   public int playersUsing = 0;
   public int lidTicks = 0;
   public int prevLidTicks = 0;
   public IBackpackType lastType = null;
   public BackpackRegistry.BackpackEntry spawnWith = null;
   public boolean mayDespawn = false;

   public BackpackCapability() {
      throw new RuntimeException("Not supported!");
   }

   public BackpackCapability(EntityLivingBase entity) {
      this.entity = entity;
   }

   public boolean isBauble() {
      return this.lastType != null || this.entity instanceof EntityPlayer && BackpackHelper.getBackpackType(BackpackHelper.getBackpackBaubleSlotItemStack((EntityPlayer)this.entity)) != null;
   }

   public ItemStack getStack() {
      if (!this.stack.isEmpty()) {
         return this.stack;
      } else {
         return this.entity instanceof EntityPlayer && BackpackHelper.getBackpackType(BackpackHelper.getBackpackBaubleSlotItemStack((EntityPlayer)this.entity)) != null ? BackpackHelper.getBackpackBaubleSlotItemStack((EntityPlayer)this.entity) : ItemStack.EMPTY;
      }
   }

   public void setStack(ItemStack value) {
      boolean var10000;
      label24: {
         label23: {
            if (this.entity instanceof EntityPlayer) {
               if (!value.isEmpty()) {
                  if (ModConfig.server.equipAsBauble) {
                     break label23;
                  }
               } else if (this.isBauble()) {
                  break label23;
               }
            }

            var10000 = false;
            break label24;
         }

         var10000 = true;
      }

      boolean setBauble = var10000;
      if (setBauble) {
         this.stack = ItemStack.EMPTY;
         this.lastType = BackpackHelper.getBackpackType(value);
         BackpackHelper.setBackpackBaubleSlotItemStack((EntityPlayer)this.entity, value);
         ((EntityPlayer)this.entity).inventoryContainer.detectAndSendChanges();
      } else {
         this.stack = value;
         this.lastType = null;
         WearableBackpacks.CHANNEL.sendToAllTracking(MessageBackpackUpdate.stack(this.entity, this.stack), this.entity, true);
      }

   }

   public IBackpackData getData() {
      return this.data;
   }

   public void setData(IBackpackData value) {
      this.data = value;
   }

   public int getPlayersUsing() {
      return this.playersUsing;
   }

   public void setPlayersUsing(int value) {
      if (value > 0 != this.playersUsing > 0) {
         WearableBackpacks.CHANNEL.sendToAllTracking(MessageBackpackUpdate.open(this.entity, value > 0), this.entity, true);
      }

      this.playersUsing = value;
      this.mayDespawn = false;
   }

   public int getLidTicks() {
      return this.lidTicks;
   }

   public int getPrevLidTicks() {
      return this.prevLidTicks;
   }

   public void setLidTicks(int value) {
      this.prevLidTicks = this.lidTicks;
      this.lidTicks = value;
   }

   public IBackpackType getType() {
      return this.lastType != null ? this.lastType : BackpackHelper.getBackpackType(this.getStack());
   }

   public static class Provider implements ICapabilitySerializable<NBTTagCompound> {
      final BackpackCapability backpack;

      public Provider(EntityLivingBase entity) {
         this.backpack = new BackpackCapability(entity);
      }

      public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
         return capability == IBackpack.CAPABILITY;
      }

      public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
         return (T)(capability == IBackpack.CAPABILITY ? this.backpack : null);
      }

      public void deserializeNBT(NBTTagCompound compound) {
         this.backpack.stack = NbtUtils.readItem(compound.getCompoundTag("stack"));
         IBackpackType type;
         if (this.backpack.stack.isEmpty()) {
            if (!compound.hasKey("type", 8)) {
               return;
            }

            String id = compound.getString("type");
            this.backpack.lastType = type = BackpackHelper.getBackpackType(MiscUtils.getItemFromName(id));
            if (type == null) {
               return;
            }
         } else {
            type = BackpackHelper.getBackpackType(this.backpack.stack);
         }

         if (type == null) {
            WearableBackpacks.LOG.error("Backpack type was null when deserializing backpack capability");
         } else {
            this.backpack.data = type.createBackpackData(this.backpack.getStack());
            NBTBase dataTag = compound.getTag("data");
            if (this.backpack.data != null && dataTag != null) {
               this.backpack.data.deserializeNBT(dataTag);
            }

            this.backpack.mayDespawn = compound.getBoolean("mayDespawn");
         }
      }

      public NBTTagCompound serializeNBT() {
         return NbtUtils.createCompound("stack", !this.backpack.stack.isEmpty() ? this.backpack.stack.serializeNBT() : null, "type", this.backpack.isBauble() ? this.backpack.getStack().getItem().getRegistryName().toString() : null, "data", this.backpack.data != null ? this.backpack.data.serializeNBT() : null, "mayDespawn", this.backpack.mayDespawn ? 1 : null);
      }
   }

   public static class Storage implements IStorage<IBackpack> {
      public Storage() {
      }

      public NBTBase writeNBT(Capability<IBackpack> capability, IBackpack instance, EnumFacing side) {
         BackpackCapability backpack = (BackpackCapability)instance;
         return backpack.stack.isEmpty() && backpack.data == null ? null : NbtUtils.createCompound("stack", !backpack.stack.isEmpty() ? backpack.stack.serializeNBT() : null, "data", backpack.data != null ? backpack.data.serializeNBT() : null, "mayDespawn", backpack.mayDespawn ? 1 : null);
      }

      public void readNBT(Capability<IBackpack> capability, IBackpack instance, EnumFacing side, NBTBase nbt) {
         BackpackCapability backpack = (BackpackCapability)instance;
         if (nbt instanceof NBTTagCompound) {
            NBTTagCompound compound = (NBTTagCompound)nbt;
            ItemStack stack = NbtUtils.readItem(compound.getCompoundTag("stack"));
            backpack.setStack(stack);
            IBackpackType type;
            if (stack.isEmpty()) {
               if (backpack.entity instanceof EntityPlayer) {
                  stack = BackpackHelper.getBackpackBaubleSlotItemStack((EntityPlayer)backpack.entity);
               }

               backpack.lastType = type = BackpackHelper.getBackpackType(stack);
               if (type == null) {
                  return;
               }
            } else {
               type = BackpackHelper.getBackpackType(stack);
            }

            IBackpackData data = type.createBackpackData(stack);
            NBTBase dataTag = compound.getTag("data");
            if (dataTag != null) {
               data.deserializeNBT(dataTag);
            }

            backpack.setData(data);
            backpack.mayDespawn = compound.getBoolean("mayDespawn");
         }
      }
   }
}
