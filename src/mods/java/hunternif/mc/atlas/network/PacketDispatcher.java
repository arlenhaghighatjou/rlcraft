package hunternif.mc.atlas.network;

import java.util.function.Supplier;
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
      registerMessage(DeleteMarkerPacket.class, DeleteMarkerPacket::new);
      registerMessage(PutBiomeTilePacket.class, PutBiomeTilePacket::new);
      registerMessage(MapDataPacket.class, MapDataPacket::new);
      registerMessage(TileGroupsPacket.class, TileGroupsPacket::new);
      registerMessage(ShortDimensionUpdatePacket.class, ShortDimensionUpdatePacket::new);
      registerMessage(TileNameIDPacket.class, TileNameIDPacket::new);
      registerMessage(ShortTilesPacket.class, ShortTilesPacket::new);
      registerMessage(MarkersPacket.class, MarkersPacket::new);
      registerMessage(DeleteCustomGlobalTilePacket.class, DeleteCustomGlobalTilePacket::new);
      registerMessage(AddMarkerPacket.class, AddMarkerPacket::new);
      registerMessage(RegisterTileIdPacket.class, RegisterTileIdPacket::new);
      registerMessage(BrowsingPositionPacket.class, BrowsingPositionPacket::new);
      registerMessage(IntTilesPacket.class, IntTilesPacket::new);
      registerMessage(IntDimensionUpdatePacket.class, IntDimensionUpdatePacket::new);
      registerMessage(OptionalMarkerPacket.class, OptionalMarkerPacket::new);
   }

   private static <T extends AbstractMessage<T> & IMessageHandler<T, IMessage>> void registerMessage(Class<T> clazz, Supplier<T> factory) {
      T handler = factory.get();
      if (handler instanceof AbstractMessage.AbstractClientMessage) {
         dispatcher.registerMessage(handler, clazz, factory, packetId++, Side.CLIENT);
      } else if (handler instanceof AbstractMessage.AbstractServerMessage) {
         dispatcher.registerMessage(handler, clazz, factory, packetId++, Side.SERVER);
      } else {
         dispatcher.registerMessage(handler, clazz, factory, packetId, Side.CLIENT);
         dispatcher.registerMessage(factory.get(), clazz, factory, packetId++, Side.SERVER);
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
