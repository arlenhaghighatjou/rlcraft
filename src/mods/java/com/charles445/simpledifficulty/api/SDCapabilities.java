package com.charles445.simpledifficulty.api;

import net.minecraftforge.common.capabilities.CapabilityManager;
import com.charles445.simpledifficulty.api.temperature.ITemperatureCapability;
import com.charles445.simpledifficulty.api.thirst.IThirstCapability;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;

public class SDCapabilities {
   public static Capability<ITemperatureCapability> TEMPERATURE = null;
   public static final String TEMPERATURE_IDENTIFIER = "temperature";
   public static Capability<IThirstCapability> THIRST = null;
   public static final String THIRST_IDENTIFIER = "thirst";

   public SDCapabilities() {
   }

   public static ITemperatureCapability getTemperatureData(EntityPlayer player) {
      return (ITemperatureCapability)player.getCapability(TEMPERATURE, (EnumFacing)null);
   }

   public static IThirstCapability getThirstData(EntityPlayer player) {
      return (IThirstCapability)player.getCapability(THIRST, (EnumFacing)null);
   }

   public static void registerCapabilities() {
      CapabilityManager.INSTANCE.onRegistered(ITemperatureCapability.class, capability -> TEMPERATURE = capability);
      CapabilityManager.INSTANCE.onRegistered(IThirstCapability.class, capability -> THIRST = capability);
   }
}
