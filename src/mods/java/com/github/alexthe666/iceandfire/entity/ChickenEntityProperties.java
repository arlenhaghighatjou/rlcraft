package com.github.alexthe666.iceandfire.entity;

import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;
import net.ilexiconn.llibrary.server.entity.EntityProperties;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.nbt.NBTTagCompound;

public class ChickenEntityProperties extends EntityProperties<EntityAnimal> {
   public int timeUntilNextEgg = 1;
   private static EaglercraftRandom rand = new EaglercraftRandom();

   public ChickenEntityProperties() {
   }

   public int getTrackingTime() {
      return 20;
   }

   public void saveNBTData(NBTTagCompound compound) {
      compound.setInteger("TimeUntilNextEgg", this.timeUntilNextEgg);
   }

   public void loadNBTData(NBTTagCompound compound) {
      this.timeUntilNextEgg = compound.getInteger("TimeUntilNextEgg");
   }

   public void init() {
      this.timeUntilNextEgg = this.generateTime();
   }

   public String getID() {
      return "Ice And Fire - Chicken Property Tracker";
   }

   public Class<EntityAnimal> getEntityClass() {
      return EntityAnimal.class;
   }

   public int generateTime() {
      return rand.nextInt(6000) + 6000;
   }
}
