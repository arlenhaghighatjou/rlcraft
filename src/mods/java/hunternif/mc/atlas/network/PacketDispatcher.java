package hunternif.mc.atlas.network;

import hunternif.mc.atlas.network.bidirectional.DeleteMarkerPacket;
import hunternif.mc.atlas.network.bidirectional.PutBiomeTilePacket;
import hunternif.mc.atlas.network.client.DeleteCustomGlobalTilePacket;
import hunternif.mc.atlas.network.client.IntDimensionUpdatePacket;
import hunternif.mc.atlas.network.client.IntTilesPacket;
import hunternif.mc.atlas.network.client.MapDataPacket;
import hunternif.mc.atlas.network.client.MarkersPacket;
import hunternif.mc.atlas.network.client.OptionalMarkerPacket;
import hunternif.mc.atlas.network.client.ShortDimensionUpdatePacket;
import hunternif.mc.atlas.network.client.ShortTilesPacket;
import hunternif.mc.atlas.network.client.TileGroupsPacket;
import hunternif.mc.atlas.network.client.TileNameIDPacket;
import hunternif.mc.atlas.network.server.AddMarkerPacket;
import hunternif.mc.atlas.network.server.BrowsingPositionPacket;
import hunternif.mc.atlas.network.server.RegisterTileIdPacket;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.NetworkRegistry.TargetPoint;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

public class PacketDispatcher {
   private static byte packetId = 0;
   private static final SimpleNetworkWrapper dispatcher = NetworkRegistry.INSTANCE.newSimpleChannel("antiqueatlas");

   private PacketDispatcher() {
   }

   public static void registerPackets() {
      registerMessage(DeleteMarkerPacket.class);
      registerMessage(PutBiomeTilePacket.class);
      registerMessage(MapDataPacket.class);
      registerMessage(TileGroupsPacket.class);
      registerMessage(ShortDimensionUpdatePacket.class);
      registerMessage(TileNameIDPacket.class);
      registerMessage(ShortTilesPacket.class);
      registerMessage(MarkersPacket.class);
      registerMessage(DeleteCustomGlobalTilePacket.class);
      registerMessage(AddMarkerPacket.class);
      registerMessage(RegisterTileIdPacket.class);
      registerMessage(BrowsingPositionPacket.class);
      registerMessage(IntTilesPacket.class);
      registerMessage(IntDimensionUpdatePacket.class);
      registerMessage(OptionalMarkerPacket.class);
   }

   private static <T extends AbstractMessage<T> & IMessageHandler<T, IMessage>> void registerMessage(Class<T> clazz) {
      if (AbstractMessage.AbstractClientMessage.class.isAssignableFrom(clazz)) {
         dispatcher.registerMessage(clazz, clazz, packetId++, Side.CLIENT);
      } else if (AbstractMessage.AbstractServerMessage.class.isAssignableFrom(clazz)) {
         dispatcher.registerMessage(clazz, clazz, packetId++, Side.SERVER);
      } else {
         dispatcher.registerMessage(clazz, clazz, packetId, Side.CLIENT);
         dispatcher.registerMessage(clazz, clazz, packetId++, Side.SERVER);
      }

   }

   public static void sendToAll(IMessage message) {
      dispatcher.sendToAll(message);
   }

   public static void sendTo(IMessage message, EntityPlayerMP player) {
      dispatcher.sendTo(message, player);
   }

   private static void sendToAllAround(IMessage message, TargetPoint point) {
      dispatcher.sendToAllAround(message, point);
   }

   private static void sendToAllAround(IMessage message, int dimension, double x, double y, double z, double range) {
      sendToAllAround(message, new TargetPoint(dimension, x, y, z, range));
   }

   public static void sendToAllAround(IMessage message, EntityPlayer player, double range) {
      sendToAllAround(message, player.getEntityWorld().provider.getDimension(), player.posX, player.posY, player.posZ, range);
   }

   public static void sendToDimension(IMessage message, int dimensionId) {
      dispatcher.sendToDimension(message, dimensionId);
   }

   public static void sendToServer(IMessage message) {
      dispatcher.sendToServer(message);
   }
}
