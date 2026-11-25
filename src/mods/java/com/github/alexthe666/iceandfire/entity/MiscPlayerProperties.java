package com.github.alexthe666.iceandfire.entity;

import net.ilexiconn.llibrary.server.entity.EntityProperties;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;

public class MiscPlayerProperties extends EntityProperties<EntityPlayer> {
   public boolean hasDismountedDragon;

   public MiscPlayerProperties() {
   }

   public int getTrackingTime() {
      return 20;
   }

   public void saveNBTData(NBTTagCompound compound) {
      compound.setBoolean("DismountedDragon", this.hasDismountedDragon);
   }

   public void loadNBTData(NBTTagCompound compound) {
      this.hasDismountedDragon = compound.getBoolean("DismountedDragon");
   }

   public void init() {
      this.hasDismountedDragon = false;
   }

   public String getID() {
      return "Ice And Fire - Player Property Tracker";
   }

   public Class<EntityPlayer> getEntityClass() {
      return EntityPlayer.class;
   }
}
