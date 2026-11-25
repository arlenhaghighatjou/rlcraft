package vazkii.quark.api;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;

public interface IPistonCallback {
   @CapabilityInject(IPistonCallback.class)
   Capability<IPistonCallback> CAPABILITY = null;

   static boolean hasCallback(TileEntity tile) {
      return tile.hasCapability(CAPABILITY, (EnumFacing)null);
   }

   static IPistonCallback getCallback(TileEntity tile) {
      return (IPistonCallback)tile.getCapability(CAPABILITY, (EnumFacing)null);
   }

   void onPistonMovementStarted();

   default void onPistonMovementFinished() {
   }
}
