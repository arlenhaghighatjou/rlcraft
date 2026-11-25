package tschipp.callablehorses.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.IThreadListener;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import tschipp.callablehorses.common.HorseManager;

public class PressKeyPacket implements IMessage, IMessageHandler<PressKeyPacket, IMessage> {
   private int key;

   public PressKeyPacket() {
   }

   public PressKeyPacket(int key) {
      this.key = key;
   }

   public void fromBytes(ByteBuf buf) {
      this.key = buf.readInt();
   }

   public void toBytes(ByteBuf buf) {
      buf.writeInt(this.key);
   }

   public IMessage onMessage(final PressKeyPacket message, final MessageContext ctx) {
      IThreadListener mainThread = (WorldServer)ctx.getServerHandler().player.world;
      mainThread.addScheduledTask(new Runnable() {
         EntityPlayerMP player;

         {
            this.player = ctx.getServerHandler().player;
         }

         public void run() {
            switch(message.key) {
            case 0:
               HorseManager.callHorse(this.player);
               break;
            case 1:
               HorseManager.setHorse(this.player);
               break;
            case 2:
               HorseManager.showHorseStats(this.player);
            }

         }
      });
      return null;
   }
}
