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
      channel.registerMessage(new HandlerConfig(), MessageConfig.class, MessageConfig::new, 0, Side.CLIENT);
      channel.registerMessage(new HandlerWaystones(), MessageWaystones.class, MessageWaystones::new, 1, Side.CLIENT);
      channel.registerMessage(new HandlerFreeWarpReturn(), MessageFreeWarpReturn.class, MessageFreeWarpReturn::new, 2, Side.SERVER);
      channel.registerMessage(new HandlerEditWaystone(), MessageEditWaystone.class, MessageEditWaystone::new, 3, Side.SERVER);
      channel.registerMessage(new HandlerTeleportToWaystone(), MessageTeleportToWaystone.class, MessageTeleportToWaystone::new, 4, Side.SERVER);
      channel.registerMessage(new HandlerTeleportEffect(), MessageTeleportEffect.class, MessageTeleportEffect::new, 5, Side.CLIENT);
      channel.registerMessage(new HandlerSortWaystone(), MessageSortWaystone.class, MessageSortWaystone::new, 6, Side.SERVER);
      channel.registerMessage(new HandlerRemoveWaystone(), MessageRemoveWaystone.class, MessageRemoveWaystone::new, 7, Side.SERVER);
      channel.registerMessage(new HandlerTeleportToGlobal(), MessageTeleportToGlobal.class, MessageTeleportToGlobal::new, 8, Side.SERVER);
      channel.registerMessage(new HandlerOpenWaystoneSelection(), MessageOpenWaystoneSelection.class, MessageOpenWaystoneSelection::new, 9, Side.CLIENT);
   }

   public static IThreadListener getThreadListener(MessageContext ctx) {
      return (IThreadListener)(ctx.side == Side.SERVER ? (WorldServer)ctx.getServerHandler().player.world : getClientThreadListener());
   }

   @SideOnly(Side.CLIENT)
   public static IThreadListener getClientThreadListener() {
      return Minecraft.getMinecraft();
   }
}
