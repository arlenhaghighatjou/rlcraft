package com.charles445.simpledifficulty.api;

import com.charles445.simpledifficulty.api.temperature.ITemperatureCapability;
import com.charles445.simpledifficulty.api.thirst.IThirstCapability;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;

public class SDCapabilities {
   @CapabilityInject(ITemperatureCapability.class)
   public static final Capability<ITemperatureCapability> TEMPERATURE = null;
   public static final String TEMPERATURE_IDENTIFIER = "temperature";
   @CapabilityInject(IThirstCapability.class)
   public static final Capability<IThirstCapability> THIRST = null;
   public static final String THIRST_IDENTIFIER = "thirst";

   public SDCapabilities() {
   }

   public static ITemperatureCapability getTemperatureData(EntityPlayer player) {
      return (ITemperatureCapability)player.getCapability(TEMPERATURE, (EnumFacing)null);
   }

   public static IThirstCapability getThirstData(EntityPlayer player) {
      return (IThirstCapability)player.getCapability(THIRST, (EnumFacing)null);
   }
}
