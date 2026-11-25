package org.orecruncher.lib.capability;

import javax.annotation.Nullable;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;

public class CapabilityProviderSimple<H> implements ICapabilityProvider {
   protected final Capability<H> capability;
   protected final EnumFacing facing;
   protected final H instance;

   public CapabilityProviderSimple(Capability<H> capability, @Nullable EnumFacing facing, @Nullable H instance) {
      this.capability = capability;
      this.facing = facing;
      this.instance = instance;
   }

   /** @deprecated */
   @Deprecated
   public CapabilityProviderSimple(@Nullable H instance, Capability<H> capability, @Nullable EnumFacing facing) {
      this(capability, facing, instance);
   }

   public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing) {
      return capability == this.getCapability();
   }

   @Nullable
   public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing) {
      return (T)(capability == this.getCapability() ? this.getCapability().cast(this.getInstance()) : null);
   }

   public final Capability<H> getCapability() {
      return this.capability;
   }

   @Nullable
   public EnumFacing getFacing() {
      return this.facing;
   }

   @Nullable
   public final H getInstance() {
      return this.instance;
   }
}
