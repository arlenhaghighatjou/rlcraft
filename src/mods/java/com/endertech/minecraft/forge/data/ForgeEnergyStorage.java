package com.endertech.minecraft.forge.data;

import com.endertech.common.IntBounds;
import com.endertech.minecraft.forge.configs.UnitConfig;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.energy.EnergyStorage;

public class ForgeEnergyStorage extends EnergyStorage implements net.minecraftforge.common.util.INBTSerializable<NBTTagCompound> {
   protected static final String TAG_NAME = "ForgeEnergy";

   public ForgeEnergyStorage(int capacity, int maxReceive, int maxExtract, int energy) {
      super(capacity, maxReceive, maxExtract, energy);
   }

   public static ForgeEnergyStorage of(int capacity, int energy) {
      return new ForgeEnergyStorage(capacity, capacity, capacity, energy);
   }

   public static ForgeEnergyStorage of(int capacity) {
      return of(capacity, 0);
   }

   public boolean hasEnoughEnergy(int use) {
      return this.extractEnergy(use, true) == use;
   }

   public NBTTagCompound serializeNBT() {
      NBTTagCompound compound = new NBTTagCompound();
      compound.setInteger("capacity", this.getMaxEnergyStored());
      compound.setInteger("energy", this.getEnergyStored());
      return compound;
   }

   public void deserializeNBT(NBTTagCompound tag) {
      this.capacity = tag.getInteger("capacity");
      this.maxExtract = this.capacity;
      this.maxReceive = this.capacity;
      this.energy = IntBounds.from(Integer.valueOf(0), Integer.valueOf(this.capacity)).enclose(Integer.valueOf(tag.getInteger("energy"))).intValue();
   }

   public void readFromNBT(NBTTagCompound compound) {
      if (compound.hasKey("ForgeEnergy")) {
         this.deserializeNBT(compound.getCompoundTag("ForgeEnergy"));
      }

   }

   public NBTTagCompound writeToNBT(NBTTagCompound compound) {
      compound.setTag("ForgeEnergy", this.serializeNBT());
      return compound;
   }

   public static class Properties {
      public static final ForgeEnergyStorage.Properties EMPTY = new ForgeEnergyStorage.Properties((UnitConfig)null, "", false, 0, 0);
      public final boolean enabled;
      public final int capacity;
      public final int consumption;

      public Properties(UnitConfig config, String headCategory, boolean enabled, int capacity, int consumption) {
         String category = Names.dotted().join(headCategory, "ForgeEnergy");
         this.enabled = UnitConfig.getBool(config, category, "enabled", enabled, "Whether Forge Energy is required or not");
         this.capacity = UnitConfig.getInt(config, category, "capacity", capacity, IntBounds.from(Integer.valueOf(0), Integer.valueOf(100000)), "Maximum amount of energy that can be held");
         this.consumption = UnitConfig.getInt(config, category, "consumption", consumption, IntBounds.from(Integer.valueOf(0), Integer.valueOf(1000)), "Amount of energy consumed per operation or second");
      }
   }
}
