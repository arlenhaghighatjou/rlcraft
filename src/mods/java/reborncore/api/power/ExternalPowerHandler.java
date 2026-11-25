package reborncore.api.power;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;

public interface ExternalPowerHandler extends ICapabilityProvider {
   void tick();

   void unload();

   void invalidate();

   default boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing) {
      return false;
   }

   @Nullable
   default <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing) {
      return (T)null;
   }
}
