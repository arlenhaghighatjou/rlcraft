package net.theawesomegem.fishingmadebetter.common.capability.fishing;

import javax.annotation.Nullable;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.ICapabilityProvider;

public class FishingCapabilityProvider implements ICapabilityProvider {
   @CapabilityInject(IFishingData.class)
   public static final Capability<IFishingData> FISHING_DATA_CAP = null;
   private IFishingData instance;

   public FishingCapabilityProvider() {
      this.instance = (IFishingData)FISHING_DATA_CAP.getDefaultInstance();
   }

   public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing) {
      return capability == FISHING_DATA_CAP;
   }

   @Nullable
   public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing) {
      return (T)(capability == FISHING_DATA_CAP ? FISHING_DATA_CAP.cast(this.instance) : null);
   }
}
