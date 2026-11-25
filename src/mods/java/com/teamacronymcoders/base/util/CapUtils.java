package com.teamacronymcoders.base.util;

import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;

public class CapUtils {
   public CapUtils() {
   }

   @Nullable
   public static <T> T get(ICapabilityProvider provider, Capability<T> capability) {
      return (T)get(provider, capability, (EnumFacing)null);
   }

   @Nullable
   public static <T> T get(ICapabilityProvider provider, Capability<T> capability, @Nullable EnumFacing facing) {
      return (T)(provider.hasCapability(capability, facing) ? provider.getCapability(capability, facing) : null);
   }

   public static <T> Optional<T> getOptional(ICapabilityProvider provider, Capability<T> capability) {
      return Optional.ofNullable(get(provider, capability));
   }
}
