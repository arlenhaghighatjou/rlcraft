package cursedflames.bountifulbaubles.network;

import cursedflames.bountifulbaubles.BountifulBaubles;
import cursedflames.bountifulbaubles.network.wormhole.HandlerWormhole;
import cursedflames.bountifulbaubles.network.wormhole.HandlerWormholeClient;
import cursedflames.bountifulbaubles.network.wormhole.HandlerWormholeRequest;
import cursedflames.bountifulbaubles.util.Config;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class HandlerNBTPacket implements IMessageHandler<NBTPacket, IMessage> {
   public HandlerNBTPacket() {
   }

   public IMessage onMessage(NBTPacket message, MessageContext ctx) {
      FMLCommonHandler.instance().getWorldThread(ctx.netHandler).addScheduledTask(() -> {
         this.handleMessage(message, ctx);
      });
      return null;
   }

   private void handleMessage(NBTPacket message, MessageContext ctx) {
      NBTTagCompound tag = message.getTag();
      int id = tag.getByte("id");
      if (id == PacketHandler.HandlerIds.SYNC_SERVER_DATA.id) {
         Config conf = BountifulBaubles.config;
         if (conf != null && tag.hasKey("values")) {
            NBTTagCompound tag1 = tag.getCompoundTag("values");
            conf.loadSyncTag(tag1);
         }
      } else if (id == PacketHandler.HandlerIds.REFORGE.id) {
         HandlerReforge.handleMessage(message, ctx);
      } else if (id == PacketHandler.HandlerIds.WORMHOLE.id) {
         HandlerWormhole.handleWormhole(message, ctx);
      } else if (id == PacketHandler.HandlerIds.WORMHOLE_UPDATE_GUI.id) {
         HandlerWormholeClient.handleWormholeUpdateGui(message, ctx);
      } else if (id == PacketHandler.HandlerIds.WORMHOLE_PIN.id) {
         HandlerWormhole.handlePin(message, ctx);
      } else if (id == PacketHandler.HandlerIds.PRISM_UPDATE_GUI.id) {
         HandlerPrismClient.updateContainer(message, ctx);
      } else if (id == PacketHandler.HandlerIds.PRISM_TOGGLE_VISIBLE.id) {
         HandlerPrism.toggleVisible(message, ctx);
      } else if (id == PacketHandler.HandlerIds.WORMHOLE_REQUEST.id) {
         HandlerWormholeRequest.handleWormholeRequest(message, ctx);
      }

   }
}
