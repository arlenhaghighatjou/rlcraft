package com.charles445.rltweaker.capability;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;

public class RLCapabilities {
   @CapabilityInject(ITweakerCapability.class)
   public static final Capability<ITweakerCapability> TWEAKER = null;
   public static final String TWEAKER_IDENTIFIER = "tweaker";

   public RLCapabilities() {
   }

   public static ITweakerCapability getTweakerData(EntityPlayer player) {
      return (ITweakerCapability)player.getCapability(TWEAKER, (EnumFacing)null);
   }
}
