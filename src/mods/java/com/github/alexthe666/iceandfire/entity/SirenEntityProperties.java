package com.github.alexthe666.iceandfire.entity;

import net.ilexiconn.llibrary.server.entity.EntityProperties;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

public class SirenEntityProperties extends EntityProperties<EntityLivingBase> {
   public boolean isCharmed;
   public int sirenID;
   public int singTime;

   public SirenEntityProperties() {
   }

   public int getTrackingTime() {
      return 20;
   }

   public void saveNBTData(NBTTagCompound compound) {
      compound.setBoolean("CharmedBySiren", this.isCharmed);
      compound.setInteger("SirenID", this.sirenID);
   }

   public void loadNBTData(NBTTagCompound compound) {
      this.isCharmed = compound.getBoolean("CharmedBySiren");
      this.sirenID = compound.getInteger("SirenID");
   }

   public EntitySiren getSiren(World world) {
      Entity entity = world.getEntityByID(this.sirenID);
      return entity != null && entity instanceof EntitySiren ? (EntitySiren)entity : null;
   }

   public void init() {
      this.isCharmed = false;
   }

   public String getID() {
      return "Ice And Fire - Siren Property Tracker";
   }

   public Class<EntityLivingBase> getEntityClass() {
      return EntityLivingBase.class;
   }
}
