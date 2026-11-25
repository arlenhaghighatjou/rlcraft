package tschipp.callablehorses.common.capabilities.storedhorse;

import net.minecraft.nbt.NBTBase;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;

public class HorseProvider implements ICapabilitySerializable {
   @CapabilityInject(IStoredHorse.class)
   public static final Capability<IStoredHorse> HORSE_CAPABILITY = null;
   private IStoredHorse instance;

   public HorseProvider() {
      this.instance = (IStoredHorse)HORSE_CAPABILITY.getDefaultInstance();
   }

   public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
      return capability == HORSE_CAPABILITY;
   }

   public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
      return (T)(capability == HORSE_CAPABILITY ? HORSE_CAPABILITY.cast(this.instance) : null);
   }

   public NBTBase serializeNBT() {
      return HORSE_CAPABILITY.getStorage().writeNBT(HORSE_CAPABILITY, this.instance, (EnumFacing)null);
   }

   public void deserializeNBT(NBTBase nbt) {
      HORSE_CAPABILITY.getStorage().readNBT(HORSE_CAPABILITY, this.instance, (EnumFacing)null, nbt);
   }
}
