package org.orecruncher.lib.capability;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.Capability.IStorage;
import net.minecraftforge.common.util.INBTSerializable;

public final class SimpleStorage<T extends INBTSerializable<NBTTagCompound>> implements IStorage<T> {
   public SimpleStorage() {
   }

   @Nonnull
   public NBTBase writeNBT(@Nonnull Capability<T> capability, @Nonnull T instance, @Nullable EnumFacing side) {
      return instance.serializeNBT();
   }

   public void readNBT(@Nonnull Capability<T> capability, @Nonnull T instance, @Nullable EnumFacing side, @Nonnull NBTBase nbt) {
      instance.deserializeNBT((NBTTagCompound)nbt);
   }
}
