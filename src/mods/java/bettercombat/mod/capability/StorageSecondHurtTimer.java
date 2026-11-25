package bettercombat.mod.capability;

import bettercombat.mod.combat.ISecondHurtTimer;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTPrimitive;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.Capability.IStorage;

public class StorageSecondHurtTimer implements IStorage<ISecondHurtTimer> {
   public StorageSecondHurtTimer() {
   }

   public NBTBase writeNBT(Capability<ISecondHurtTimer> capability, ISecondHurtTimer instance, EnumFacing side) {
      return new NBTTagInt(instance.getHurtTimerBCM());
   }

   public void readNBT(Capability<ISecondHurtTimer> capability, ISecondHurtTimer instance, EnumFacing side, NBTBase nbt) {
      instance.setHurtTimerBCM(((NBTPrimitive)nbt).getInt());
   }
}
