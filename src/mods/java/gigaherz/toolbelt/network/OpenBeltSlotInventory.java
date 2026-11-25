package gigaherz.toolbelt.network;

import gigaherz.toolbelt.ToolBelt;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class OpenBeltSlotInventory implements IMessage {
   public OpenBeltSlotInventory() {
   }

   public void fromBytes(ByteBuf buf) {
   }

   public void toBytes(ByteBuf buf) {
   }

   public static class Handler implements IMessageHandler<OpenBeltSlotInventory, IMessage> {
      public Handler() {
      }

      public IMessage onMessage(OpenBeltSlotInventory message, MessageContext ctx) {
         EntityPlayerMP player = ctx.getServerHandler().player;
         WorldServer world = (WorldServer)player.world;
         world.addScheduledTask(() -> {
            player.openGui(ToolBelt.instance, 1, world, 0, 0, 0);
         });
         return null;
      }
   }
}
