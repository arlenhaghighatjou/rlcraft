package de.mennomax.astikorcarts.capabilities;

import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;

public class PullProvider implements ICapabilityProvider {
   public static Capability<IPull> PULL = null;
   private IPull instance;

   public PullProvider() {
      this.instance = (IPull)PULL.getDefaultInstance();
   }

   public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
      return capability == PULL;
   }

   public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
      return (T)(capability == PULL ? PULL.cast(this.instance) : null);
   }

   public static void registerCapabilities() {
      CapabilityManager.INSTANCE.onRegistered(IPull.class, capability -> PULL = capability);
   }
}
