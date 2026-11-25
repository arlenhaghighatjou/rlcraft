package sereneseasons.network.message;

import net.lax1dude.eaglercraft.v1_8.netty.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import sereneseasons.handler.season.SeasonHandler;

public class MessageSyncSeasonCycle implements IMessage, IMessageHandler<MessageSyncSeasonCycle, IMessage> {
   public int dimension;
   public int seasonCycleTicks;

   public MessageSyncSeasonCycle() {
   }

   public MessageSyncSeasonCycle(int dimension, int seasonCycleTicks) {
      this.dimension = dimension;
      this.seasonCycleTicks = seasonCycleTicks;
   }

   public void fromBytes(ByteBuf buf) {
      this.dimension = buf.readInt();
      this.seasonCycleTicks = buf.readInt();
   }

   public void toBytes(ByteBuf buf) {
      buf.writeInt(this.dimension);
      buf.writeInt(this.seasonCycleTicks);
   }

   public IMessage onMessage(MessageSyncSeasonCycle message, MessageContext ctx) {
      if (ctx.side == Side.CLIENT) {
         if (Minecraft.getMinecraft().player == null) {
            return null;
         }

         int playerDimension = Minecraft.getMinecraft().player.dimension;
         if (playerDimension == message.dimension) {
            SeasonHandler.clientSeasonCycleTicks.replace(Integer.valueOf(playerDimension), Integer.valueOf(message.seasonCycleTicks));
         }
      }

      return null;
   }
}
