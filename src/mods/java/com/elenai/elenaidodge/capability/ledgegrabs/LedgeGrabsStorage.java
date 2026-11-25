package com.elenai.elenaidodge.capability.ledgegrabs;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTPrimitive;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.Capability.IStorage;

public class LedgeGrabsStorage implements IStorage<ILedgeGrabs> {
   public LedgeGrabsStorage() {
   }

   public NBTBase writeNBT(Capability<ILedgeGrabs> capability, ILedgeGrabs instance, EnumFacing side) {
      return new NBTTagInt(instance.getLedgeGrabs());
   }

   public void readNBT(Capability<ILedgeGrabs> capability, ILedgeGrabs instance, EnumFacing side, NBTBase nbt) {
      instance.set(((NBTPrimitive)nbt).getInt());
   }
}
