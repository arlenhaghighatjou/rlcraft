package dev.satyrn.wolfarmor.api.util;

import dev.satyrn.wolfarmor.api.IWolfArmorCapability;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;

/** @deprecated */
@Deprecated
public abstract class Capabilities {
   @CapabilityInject(IWolfArmorCapability.class)
   public static Capability<IWolfArmorCapability> CAPABILITY_WOLF_ARMOR;

   public Capabilities() {
   }
}
