package dev.satyrn.wolfarmor.api.util;

import net.minecraftforge.common.capabilities.CapabilityManager;
import dev.satyrn.wolfarmor.api.IWolfArmorCapability;
import net.minecraftforge.common.capabilities.Capability;

/** @deprecated */
@Deprecated
public abstract class Capabilities {
   public static Capability<IWolfArmorCapability> CAPABILITY_WOLF_ARMOR;

   public Capabilities() {
   }

   public static void registerCapabilities() {
      CapabilityManager.INSTANCE.onRegistered(IWolfArmorCapability.class, capability -> CAPABILITY_WOLF_ARMOR = capability);
   }
}
