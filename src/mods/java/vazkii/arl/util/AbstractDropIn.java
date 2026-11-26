package vazkii.arl.util;

import javax.annotation.Nonnull;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import vazkii.arl.interf.IDropInItem;

public abstract class AbstractDropIn implements ICapabilityProvider, IDropInItem {
   public AbstractDropIn() {
   }

   public boolean hasCapability(@Nonnull Capability<?> capability, EnumFacing facing) {
      return capability == DropInHandler.DROP_IN_CAPABILITY;
   }

   public <T> T getCapability(@Nonnull Capability<T> capability, EnumFacing facing) {
      return (T)(capability == DropInHandler.DROP_IN_CAPABILITY ? DropInHandler.DROP_IN_CAPABILITY.cast(this) : null);
   }
}
