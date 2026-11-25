package com.charles445.simpledifficulty.api.thirst;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;

public interface IThirstCapability {
   float getThirstExhaustion();

   int getThirstLevel();

   float getThirstSaturation();

   int getThirstTickTimer();

   int getThirstDamageCounter();

   void setThirstExhaustion(float var1);

   void setThirstLevel(int var1);

   void setThirstSaturation(float var1);

   void setThirstTickTimer(int var1);

   void setThirstDamageCounter(int var1);

   void addThirstExhaustion(float var1);

   void addThirstLevel(int var1);

   void addThirstSaturation(float var1);

   void addThirstTickTimer(int var1);

   void addThirstDamageCounter(int var1);

   boolean isThirsty();

   boolean isDirty();

   void setClean();

   void tickUpdate(EntityPlayer var1, World var2, Phase var3);

   int getPacketTimer();
}
