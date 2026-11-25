package com.charles445.simpledifficulty.api.temperature;

import com.google.common.collect.ImmutableMap;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;

public interface ITemperatureCapability {
   int getTemperatureLevel();

   int getTemperatureTickTimer();

   int getTemperatureDamageCounter();

   ImmutableMap<String, TemporaryModifier> getTemporaryModifiers();

   void setTemperatureLevel(int var1);

   void setTemperatureTickTimer(int var1);

   void setTemporaryModifier(String var1, float var2, int var3);

   void setTemperatureDamageCounter(int var1);

   void addTemperatureLevel(int var1);

   void addTemperatureTickTimer(int var1);

   void addTemperatureDamageCounter(int var1);

   void clearTemporaryModifiers();

   TemperatureEnum getTemperatureEnum();

   void tickUpdate(EntityPlayer var1, World var2, Phase var3);

   boolean isDirty();

   void setClean();

   int getPacketTimer();
}
