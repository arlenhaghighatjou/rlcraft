package crafttweaker.mc1120.network;

import net.lax1dude.eaglercraft.v1_8.netty.ByteBuf;
import java.awt.Desktop;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.StringSelection;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class MessageCopyClipboard implements IMessage, IMessageHandler<MessageCopyClipboard, IMessage> {
   private String data;

   public MessageCopyClipboard() {
   }

   public MessageCopyClipboard(String data) {
      this.data = data;
   }

   public String getData() {
      return this.data;
   }

   public void fromBytes(ByteBuf buf) {
      this.data = ByteBufUtils.readUTF8String(buf);
   }

   public void toBytes(ByteBuf buf) {
      ByteBufUtils.writeUTF8String(buf, this.data);
   }

   @SideOnly(Side.CLIENT)
   public IMessage onMessage(MessageCopyClipboard message, MessageContext ctx) {
      Minecraft.getMinecraft().addScheduledTask(() -> {
         this.handle(message, ctx);
      });
      return null;
   }

   private void handle(MessageCopyClipboard message, MessageContext ctx) {
      if (Desktop.isDesktopSupported()) {
         StringSelection stringSelection = new StringSelection(message.getData());
         Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
         clipboard.setContents(stringSelection, (ClipboardOwner)null);
      }

   }
}
