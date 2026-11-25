package ivorius.ivtoolkit.network;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketTileEntityClientEventHandler extends SchedulingMessageHandler<PacketTileEntityClientEvent, IMessage> {
   public PacketTileEntityClientEventHandler() {
   }

   public WorldServer getServerWorld(PacketTileEntityClientEvent message, MessageContext ctx) {
      return ctx.getServerHandler().player.getServerWorld().getMinecraftServer().getWorld(message.getDimension());
   }

   public void processServer(PacketTileEntityClientEvent message, MessageContext ctx, WorldServer world) {
      TileEntity entity = world.getTileEntity(message.getPos());
      if (entity != null) {
         ((ClientEventHandler)entity).onClientEvent(message.getPayload(), message.getContext(), ctx.getServerHandler().player);
      }

   }
}
