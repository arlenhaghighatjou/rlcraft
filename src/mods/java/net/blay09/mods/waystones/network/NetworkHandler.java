package net.blay09.mods.waystones.network;

import net.blay09.mods.waystones.network.handler.HandlerConfig;
import net.blay09.mods.waystones.network.handler.HandlerEditWaystone;
import net.blay09.mods.waystones.network.handler.HandlerFreeWarpReturn;
import net.blay09.mods.waystones.network.handler.HandlerOpenWaystoneSelection;
import net.blay09.mods.waystones.network.handler.HandlerRemoveWaystone;
import net.blay09.mods.waystones.network.handler.HandlerSortWaystone;
import net.blay09.mods.waystones.network.handler.HandlerTeleportEffect;
import net.blay09.mods.waystones.network.handler.HandlerTeleportToGlobal;
import net.blay09.mods.waystones.network.handler.HandlerTeleportToWaystone;
import net.blay09.mods.waystones.network.handler.HandlerWaystones;
import net.blay09.mods.waystones.network.message.MessageConfig;
import net.blay09.mods.waystones.network.message.MessageEditWaystone;
import net.blay09.mods.waystones.network.message.MessageFreeWarpReturn;
import net.blay09.mods.waystones.network.message.MessageOpenWaystoneSelection;
import net.blay09.mods.waystones.network.message.MessageRemoveWaystone;
import net.blay09.mods.waystones.network.message.MessageSortWaystone;
import net.blay09.mods.waystones.network.message.MessageTeleportEffect;
import net.blay09.mods.waystones.network.message.MessageTeleportToGlobal;
import net.blay09.mods.waystones.network.message.MessageTeleportToWaystone;
import net.blay09.mods.waystones.network.message.MessageWaystones;
import net.minecraft.client.Minecraft;
import net.minecraft.util.IThreadListener;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class NetworkHandler {
   public static final SimpleNetworkWrapper channel = NetworkRegistry.INSTANCE.newSimpleChannel("waystones");

   public NetworkHandler() {
   }

   public static void init() {
      channel.registerMessage(HandlerConfig.class, MessageConfig.class, 0, Side.CLIENT);
      channel.registerMessage(HandlerWaystones.class, MessageWaystones.class, 1, Side.CLIENT);
      channel.registerMessage(HandlerFreeWarpReturn.class, MessageFreeWarpReturn.class, 2, Side.SERVER);
      channel.registerMessage(HandlerEditWaystone.class, MessageEditWaystone.class, 3, Side.SERVER);
      channel.registerMessage(HandlerTeleportToWaystone.class, MessageTeleportToWaystone.class, 4, Side.SERVER);
      channel.registerMessage(HandlerTeleportEffect.class, MessageTeleportEffect.class, 5, Side.CLIENT);
      channel.registerMessage(HandlerSortWaystone.class, MessageSortWaystone.class, 6, Side.SERVER);
      channel.registerMessage(HandlerRemoveWaystone.class, MessageRemoveWaystone.class, 7, Side.SERVER);
      channel.registerMessage(HandlerTeleportToGlobal.class, MessageTeleportToGlobal.class, 8, Side.SERVER);
      channel.registerMessage(HandlerOpenWaystoneSelection.class, MessageOpenWaystoneSelection.class, 9, Side.CLIENT);
   }

   public static IThreadListener getThreadListener(MessageContext ctx) {
      return (IThreadListener)(ctx.side == Side.SERVER ? (WorldServer)ctx.getServerHandler().player.world : getClientThreadListener());
   }

   @SideOnly(Side.CLIENT)
   public static IThreadListener getClientThreadListener() {
      return Minecraft.getMinecraft();
   }
}
