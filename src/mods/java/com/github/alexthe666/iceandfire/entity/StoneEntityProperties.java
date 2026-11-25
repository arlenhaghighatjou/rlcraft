package com.github.alexthe666.iceandfire.entity;

import net.ilexiconn.llibrary.server.entity.EntityProperties;
import net.minecraft.entity.EntityLiving;
import net.minecraft.nbt.NBTTagCompound;

public class StoneEntityProperties extends EntityProperties<EntityLiving> {
   public boolean isStone;
   public int breakLvl;

   public StoneEntityProperties() {
   }

   public int getTrackingTime() {
      return 20;
   }

   public void saveNBTData(NBTTagCompound compound) {
      compound.setBoolean("TurnedToStone", this.isStone);
      compound.setInteger("StoneBreakLvl", this.breakLvl);
   }

   public void loadNBTData(NBTTagCompound compound) {
      this.isStone = compound.getBoolean("TurnedToStone");
      this.breakLvl = compound.getInteger("StoneBreakLvl");
   }

   public void init() {
      this.isStone = false;
      this.breakLvl = 0;
   }

   public String getID() {
      return "Ice And Fire - Stone Property Tracker";
   }

   public Class<EntityLiving> getEntityClass() {
      return EntityLiving.class;
   }
}
