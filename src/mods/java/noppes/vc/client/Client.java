package noppes.vc.client;

import io.netty.buffer.Unpooled;
import java.io.IOException;
import net.minecraft.client.Minecraft;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.common.network.internal.FMLProxyPacket;
import noppes.vc.Server;
import noppes.vc.VariedCommodities;
import noppes.vc.constants.PacketServer;

public class Client {
   public Client() {
   }

   public static void sendData(final PacketServer enu, final Object... obs) {
      Minecraft.getMinecraft().addScheduledTask(new Runnable() {
         public void run() {
            PacketBuffer buffer = new PacketBuffer(Unpooled.buffer());

            try {
               if (!Server.fillBuffer(buffer, enu, obs)) {
                  return;
               }

               VariedCommodities.Channel.sendToServer(new FMLProxyPacket(buffer, "variedcommodities"));
            } catch (IOException var3) {
               var3.printStackTrace();
            }

         }
      });
   }
}
