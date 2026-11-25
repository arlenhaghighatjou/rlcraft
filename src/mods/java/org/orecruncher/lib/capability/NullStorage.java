package org.orecruncher.lib.capability;

import javax.annotation.Nonnull;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.Capability.IStorage;

public final class NullStorage<T> implements IStorage<T> {
   public NullStorage() {
   }

   @Nonnull
   public NBTBase writeNBT(@Nonnull Capability<T> capability, @Nonnull T instance, @Nonnull EnumFacing side) {
      return new NBTTagCompound();
   }

   public void readNBT(@Nonnull Capability<T> capability, @Nonnull T instance, @Nonnull EnumFacing side, @Nonnull NBTBase nbt) {
   }
}
