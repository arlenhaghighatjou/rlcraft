package com.teamacronymcoders.base.modules.tool;

import com.teamacronymcoders.base.Capabilities;
import com.teamacronymcoders.base.api.ITool;
import javax.annotation.Nonnull;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;

public class CapabilityProviderTool implements ICapabilityProvider {
   private ITool spanner;

   public CapabilityProviderTool() {
      this(new ITool() {
      });
   }

   public CapabilityProviderTool(ITool cap) {
      this.spanner = cap;
   }

   public boolean hasCapability(@Nonnull Capability<?> capability, EnumFacing facing) {
      return capability == Capabilities.TOOL;
   }

   public <T> T getCapability(@Nonnull Capability<T> capability, EnumFacing facing) {
      return (T)(capability == Capabilities.TOOL ? Capabilities.TOOL.cast(this.spanner) : null);
   }
}
