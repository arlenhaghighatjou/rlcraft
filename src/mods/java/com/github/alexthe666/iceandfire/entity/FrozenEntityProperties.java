package com.github.alexthe666.iceandfire.entity;

import net.ilexiconn.llibrary.server.entity.EntityProperties;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTTagCompound;

public class FrozenEntityProperties extends EntityProperties<EntityLivingBase> {
   public boolean isFrozen;
   public int ticksUntilUnfrozen;

   public FrozenEntityProperties() {
   }

   public int getTrackingTime() {
      return 20;
   }

   public void saveNBTData(NBTTagCompound compound) {
      compound.setBoolean("IsFrozen", this.isFrozen);
      compound.setInteger("TicksUntilUnfrozen", this.ticksUntilUnfrozen);
   }

   public void loadNBTData(NBTTagCompound compound) {
      this.isFrozen = compound.getBoolean("IsFrozen");
      this.ticksUntilUnfrozen = compound.getInteger("TicksUntilUnfrozen");
   }

   public void init() {
      this.isFrozen = false;
      this.ticksUntilUnfrozen = 0;
   }

   public String getID() {
      return "Ice And Fire - Frozen Property Tracker";
   }

   public Class<EntityLivingBase> getEntityClass() {
      return EntityLivingBase.class;
   }

   public void setFrozenFor(int frozenFor) {
      if (!(this.getEntity() instanceof EntityIceDragon)) {
         this.isFrozen = true;
         this.ticksUntilUnfrozen = frozenFor;
      }

   }
}
