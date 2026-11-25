package c4.corpsecomplex.common.modules.spawning.capability;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.capabilities.Capability.IStorage;

public class DeathLocation implements IDeathLocation {
   private BlockPos deathLocation;
   private int deathDimension;
   private boolean usedScroll = false;
   private boolean hasDeathLocation = false;

   public DeathLocation() {
   }

   public BlockPos getDeathLocation() {
      return this.deathLocation;
   }

   public void setDeathLocation(BlockPos pos) {
      this.deathLocation = pos;
   }

   public boolean hasDeathLocation() {
      return this.hasDeathLocation;
   }

   public void setHasDeathLocation(boolean hasDeathLocation) {
      this.hasDeathLocation = hasDeathLocation;
   }

   public int getDeathDimension() {
      return this.deathDimension;
   }

   public void setDeathDimension(int dimension) {
      this.deathDimension = dimension;
   }

   public boolean hasUsedScroll() {
      return this.usedScroll;
   }

   public void setUsedScroll(boolean usedScroll) {
      this.usedScroll = usedScroll;
   }

   public static class Provider implements ICapabilitySerializable<NBTBase> {
      @CapabilityInject(IDeathLocation.class)
      public static final Capability<IDeathLocation> DEATH_LOC_CAP = null;
      private IDeathLocation instance;

      public Provider() {
         this.instance = (IDeathLocation)DEATH_LOC_CAP.getDefaultInstance();
      }

      public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
         return capability == DEATH_LOC_CAP;
      }

      public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
         return (T)(capability == DEATH_LOC_CAP ? DEATH_LOC_CAP.cast(this.instance) : null);
      }

      public NBTBase serializeNBT() {
         return DEATH_LOC_CAP.getStorage().writeNBT(DEATH_LOC_CAP, this.instance, (EnumFacing)null);
      }

      public void deserializeNBT(NBTBase nbt) {
         DEATH_LOC_CAP.getStorage().readNBT(DEATH_LOC_CAP, this.instance, (EnumFacing)null, nbt);
      }
   }

   public static class Storage implements IStorage<IDeathLocation> {
      public Storage() {
      }

      public NBTBase writeNBT(Capability<IDeathLocation> capability, IDeathLocation instance, EnumFacing side) {
         NBTTagCompound compound = new NBTTagCompound();
         compound.setBoolean("usedScroll", instance.hasUsedScroll());
         compound.setBoolean("hasDeathLocation", instance.hasDeathLocation());
         if (instance.hasDeathLocation()) {
            compound.setInteger("dimension", instance.getDeathDimension());
            compound.setInteger("x", instance.getDeathLocation().getX());
            compound.setInteger("y", instance.getDeathLocation().getY());
            compound.setInteger("z", instance.getDeathLocation().getZ());
         }

         return compound;
      }

      public void readNBT(Capability<IDeathLocation> capability, IDeathLocation instance, EnumFacing side, NBTBase nbt) {
         NBTTagCompound compound = (NBTTagCompound)nbt;
         instance.setUsedScroll(compound.getBoolean("usedScroll"));
         instance.setHasDeathLocation(compound.getBoolean("hasDeathLocation"));
         if (compound.getBoolean("hasDeathLocation")) {
            instance.setDeathDimension(compound.getInteger("dimension"));
            instance.setDeathLocation(new BlockPos(compound.getInteger("x"), compound.getInteger("y"), compound.getInteger("z")));
         }

      }
   }
}
