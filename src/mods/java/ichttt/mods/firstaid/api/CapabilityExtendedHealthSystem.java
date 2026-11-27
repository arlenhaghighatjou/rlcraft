package ichttt.mods.firstaid.api;

import net.minecraftforge.common.capabilities.CapabilityManager;
import ichttt.mods.firstaid.api.damagesystem.AbstractPlayerDamageModel;
import net.minecraftforge.common.capabilities.Capability;

public class CapabilityExtendedHealthSystem {
   public static Capability<AbstractPlayerDamageModel> INSTANCE;

   public CapabilityExtendedHealthSystem() {
   }

   public static void registerCapabilities() {
      CapabilityManager.INSTANCE.onRegistered(AbstractPlayerDamageModel.class, capability -> INSTANCE = capability);
   }
}
