package crafttweaker.mc1120.network;

import crafttweaker.mc1120.CraftTweaker;
import net.lax1dude.eaglercraft.v1_8.netty.ByteBuf;
import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class MessageOpenBrowser implements IMessage, IMessageHandler<MessageOpenBrowser, IMessage> {
   private String url;

   public MessageOpenBrowser() {
   }

   public MessageOpenBrowser(String url) {
      this.url = url;
   }

   public String getUrl() {
      return this.url;
   }

   public void fromBytes(ByteBuf buf) {
      this.url = ByteBufUtils.readUTF8String(buf);
   }

   public void toBytes(ByteBuf buf) {
      ByteBufUtils.writeUTF8String(buf, this.url);
   }

   @SideOnly(Side.CLIENT)
   public IMessage onMessage(MessageOpenBrowser message, MessageContext ctx) {
      Minecraft.getMinecraft().addScheduledTask(() -> {
         this.handle(message, ctx);
      });
      return null;
   }

   private void handle(MessageOpenBrowser message, MessageContext ctx) {
      if (Desktop.isDesktopSupported()) {
         Desktop desktop = Desktop.getDesktop();

         try {
            desktop.browse(new URI(message.getUrl()));
         } catch (URISyntaxException | IOException var5) {
            ;
         }
      } else {
         CraftTweaker.LOG.error("Desktop not supported");
      }

   }
}
