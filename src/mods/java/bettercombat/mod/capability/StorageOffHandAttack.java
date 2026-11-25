package bettercombat.mod.capability;

import bettercombat.mod.combat.IOffHandAttack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTPrimitive;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.Capability.IStorage;

public class StorageOffHandAttack implements IStorage<IOffHandAttack> {
   public StorageOffHandAttack() {
   }

   public NBTBase writeNBT(Capability<IOffHandAttack> capability, IOffHandAttack instance, EnumFacing side) {
      return new NBTTagInt(instance.getOffhandCooldown());
   }

   public void readNBT(Capability<IOffHandAttack> capability, IOffHandAttack instance, EnumFacing side, NBTBase nbt) {
      instance.setOffhandCooldown(((NBTPrimitive)nbt).getInt());
   }
}
