package tschipp.carryon.network.server;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.IThreadListener;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import tschipp.carryon.client.keybinds.CarryOnKeybinds;

public class SyncKeybindPacketHandler implements IMessageHandler<SyncKeybindPacket, IMessage> {
   public SyncKeybindPacketHandler() {
   }

   public IMessage onMessage(final SyncKeybindPacket message, final MessageContext ctx) {
      IThreadListener mainThread = (WorldServer)ctx.getServerHandler().player.world;
      mainThread.addScheduledTask(new Runnable() {
         EntityPlayerMP player;

         {
            this.player = ctx.getServerHandler().player;
         }

         public void run() {
            CarryOnKeybinds.setKeyPressed(this.player, message.pressed);
         }
      });
      return null;
   }
}
