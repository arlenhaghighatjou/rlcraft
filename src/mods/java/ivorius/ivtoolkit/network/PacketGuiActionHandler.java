package ivorius.ivtoolkit.network;

import net.minecraft.inventory.Container;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketGuiActionHandler extends SchedulingMessageHandler<PacketGuiAction, IMessage> {
   public PacketGuiActionHandler() {
   }

   public void processServer(PacketGuiAction message, MessageContext ctx, WorldServer world) {
      NetHandlerPlayServer netHandler = ctx.getServerHandler();
      Container container = netHandler.player.openContainer;
      if (container instanceof PacketGuiAction.ActionHandler) {
         ((PacketGuiAction.ActionHandler)container).handleAction(message.getContext(), message.getPayload());
      }

   }
}
