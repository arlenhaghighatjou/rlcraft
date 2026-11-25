package c4.corpsecomplex.common.modules.inventory.capability;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.capabilities.Capability.IStorage;

public class DeathInventory implements IDeathInventory {
   private NBTTagCompound deathInventory = new NBTTagCompound();

   public DeathInventory() {
   }

   public void addStorage(String modid, NBTTagCompound storage) {
      this.deathInventory.setTag(modid, storage);
   }

   public NBTTagCompound getStorage(String modid) {
      return (NBTTagCompound)this.deathInventory.getTag(modid);
   }

   public NBTTagCompound getDeathInventory() {
      return this.deathInventory;
   }

   public void setDeathInventory(NBTTagCompound nbt) {
      this.deathInventory = nbt;
   }

   public static class Provider implements ICapabilitySerializable<NBTBase> {
      @CapabilityInject(IDeathInventory.class)
      public static final Capability<IDeathInventory> DEATH_INV_CAP = null;
      private IDeathInventory instance;

      public Provider() {
         this.instance = (IDeathInventory)DEATH_INV_CAP.getDefaultInstance();
      }

      public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
         return capability == DEATH_INV_CAP;
      }

      public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
         return (T)(capability == DEATH_INV_CAP ? DEATH_INV_CAP.cast(this.instance) : null);
      }

      public NBTBase serializeNBT() {
         return DEATH_INV_CAP.getStorage().writeNBT(DEATH_INV_CAP, this.instance, (EnumFacing)null);
      }

      public void deserializeNBT(NBTBase nbt) {
         DEATH_INV_CAP.getStorage().readNBT(DEATH_INV_CAP, this.instance, (EnumFacing)null, nbt);
      }
   }

   public static class Storage implements IStorage<IDeathInventory> {
      public Storage() {
      }

      public NBTBase writeNBT(Capability<IDeathInventory> capability, IDeathInventory instance, EnumFacing side) {
         return instance.getDeathInventory();
      }

      public void readNBT(Capability<IDeathInventory> capability, IDeathInventory instance, EnumFacing side, NBTBase nbt) {
         instance.setDeathInventory((NBTTagCompound)nbt);
      }
   }
}
