package tschipp.carryon.common.capabilities;

import net.minecraft.nbt.NBTBase;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;

public class PositionProvider implements ICapabilitySerializable {
   @CapabilityInject(IPosition.class)
   public static final Capability<IPosition> POSITION_CAPABILITY = null;
   private IPosition instance;

   public PositionProvider() {
      this.instance = (IPosition)POSITION_CAPABILITY.getDefaultInstance();
   }

   public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
      return capability == POSITION_CAPABILITY;
   }

   public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
      return (T)(capability == POSITION_CAPABILITY ? POSITION_CAPABILITY.cast(this.instance) : null);
   }

   public NBTBase serializeNBT() {
      return POSITION_CAPABILITY.getStorage().writeNBT(POSITION_CAPABILITY, this.instance, (EnumFacing)null);
   }

   public void deserializeNBT(NBTBase nbt) {
      POSITION_CAPABILITY.getStorage().readNBT(POSITION_CAPABILITY, this.instance, (EnumFacing)null, nbt);
   }
}
