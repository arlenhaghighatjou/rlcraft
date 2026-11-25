package com.alcatrazescapee.notreepunching.common.capability;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.capabilities.Capability.IStorage;

public final class CapabilityPlayerItem {
   public static final ResourceLocation KEY = new ResourceLocation("notreepunching", "player_item");
   public static Capability<IPlayerItem> CAPABILITY;

   public CapabilityPlayerItem() {
   }

   public static void preInit() {
      CapabilityManager.INSTANCE.onRegistered(IPlayerItem.class, capability -> CAPABILITY = capability);
      CapabilityManager.INSTANCE.register(IPlayerItem.class, new CapabilityPlayerItem.Storage(), CapabilityPlayerItem.Instance::new);
   }

   public static final class Instance implements IPlayerItem, ICapabilityProvider {
      private ItemStack stack;

      public Instance() {
      }

      public ItemStack getStack() {
         return this.stack == null ? ItemStack.EMPTY : this.stack;
      }

      public void setStack(ItemStack stack) {
         this.stack = stack.copy();
      }

      public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing) {
         return capability == CapabilityPlayerItem.CAPABILITY;
      }

      @Nullable
      public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing) {
         return (T)(this.hasCapability(capability, facing) ? this : null);
      }
   }

   private static final class Storage implements IStorage<IPlayerItem> {
      private Storage() {
      }

      @Nonnull
      public NBTBase writeNBT(Capability<IPlayerItem> capability, IPlayerItem instance, EnumFacing facing) {
         NBTTagCompound nbt = new NBTTagCompound();
         return instance.getStack().writeToNBT(nbt);
      }

      public void readNBT(Capability<IPlayerItem> capability, IPlayerItem instance, EnumFacing facing, NBTBase nbt) {
         if (nbt instanceof NBTTagCompound) {
            instance.setStack(new ItemStack((NBTTagCompound)nbt));
         }

      }
   }
}
