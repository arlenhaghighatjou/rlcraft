package vazkii.quark.api;

import java.util.function.Supplier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.items.IItemHandler;

public interface IDropoffManager {
   @CapabilityInject(IDropoffManager.class)
   Capability<IDropoffManager> CAPABILITY = null;

   static boolean hasProvider(TileEntity tile) {
      return tile.hasCapability(CAPABILITY, (EnumFacing)null);
   }

   static IDropoffManager getProvider(TileEntity tile) {
      return (IDropoffManager)tile.getCapability(CAPABILITY, (EnumFacing)null);
   }

   boolean acceptsDropoff(EntityPlayer var1);

   default IItemHandler getDropoffItemHandler(Supplier<IItemHandler> defaultSupplier) {
      return (IItemHandler)defaultSupplier.get();
   }
}
