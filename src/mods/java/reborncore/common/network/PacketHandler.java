package reborncore.common.network;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.NetworkRegistry.TargetPoint;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;
import reborncore.common.network.packets.VariableMessage;

public class PacketHandler {
   public static final SimpleNetworkWrapper INSTANCE = NetworkRegistry.INSTANCE.newSimpleChannel("reborncore_packet_handler");

   public PacketHandler() {
   }

   public static void sendTo(IMessage message, EntityPlayerMP player) {
      INSTANCE.sendTo(message, player);
   }

   public static void sendToAll(IMessage message) {
      INSTANCE.sendToAll(message);
   }

   public static void sendToAllAround(IMessage message, TargetPoint point) {
      INSTANCE.sendToAllAround(message, point);
   }

   public static void sendToAllAround(IMessage message, int dimension, double x, double y, double z, double range) {
      sendToAllAround(message, new TargetPoint(dimension, x, y, z, range));
   }

   public static void sendToAllAround(IMessage message, EntityPlayer player, double range) {
      sendToAllAround(message, player.world.provider.getDimension(), player.posX, player.posY, player.posZ, range);
   }

   public static void sendToDimension(IMessage message, int dimensionId) {
      INSTANCE.sendToDimension(message, dimensionId);
   }

   public static void sendToServer(IMessage message) {
      INSTANCE.sendToServer(message);
   }

   static {
      INSTANCE.registerMessage(VariableMessage.VariableMessageHandler.class, VariableMessage.class, 1, Side.CLIENT);
      INSTANCE.registerMessage(VariableMessage.VariableMessageHandler.class, VariableMessage.class, 1, Side.SERVER);
   }
}
