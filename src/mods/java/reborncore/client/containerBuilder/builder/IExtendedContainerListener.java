package reborncore.client.containerBuilder.builder;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fluids.FluidStack;
import reborncore.common.network.NetworkManager;
import reborncore.common.network.packet.PacketSendFluidStack;
import reborncore.common.network.packet.PacketSendLong;
import reborncore.common.network.packet.PacketSendObject;

public interface IExtendedContainerListener {
   default void sendLong(IContainerListener containerListener, Container containerIn, int var, long value) {
      if (containerListener instanceof EntityPlayerMP) {
         NetworkManager.sendToPlayer(new PacketSendLong(var, value, containerIn), (EntityPlayerMP)containerListener);
      }

   }

   default void sendFluidStack(IContainerListener containerListener, Container containerIn, int var, FluidStack value) {
      if (containerListener instanceof EntityPlayerMP) {
         NetworkManager.sendToPlayer(new PacketSendFluidStack(var, value, containerIn), (EntityPlayerMP)containerListener);
      }

   }

   default void sendObject(IContainerListener containerListener, Container containerIn, int var, Object value) {
      if (containerListener instanceof EntityPlayerMP) {
         NetworkManager.sendToPlayer(new PacketSendObject(var, value, containerIn), (EntityPlayerMP)containerListener);
      }

   }

   default void handleLong(int var, long value) {
   }

   default void handleFluidStack(int var, FluidStack value) {
   }

   default void handleObject(int var, Object value) {
   }

   default void handleGuiMessage(NBTTagCompound tag) {
   }
}
