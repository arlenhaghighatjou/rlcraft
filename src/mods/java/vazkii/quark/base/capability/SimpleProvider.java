package vazkii.quark.base.capability;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;

public class SimpleProvider<CAP> implements ICapabilityProvider {
   private final Capability<CAP> capability;
   private final CAP instance;

   public SimpleProvider(Capability<CAP> capability, CAP instance) {
      this.capability = capability;
      this.instance = instance;
   }

   public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing) {
      return capability == this.capability;
   }

   @Nullable
   public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing) {
      return (T)(capability == this.capability ? this.capability.cast(this.instance) : null);
   }
}
