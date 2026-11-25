package slimeknights.mantle.network.book;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.util.EnumHand;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import slimeknights.mantle.client.book.BookHelper;
import slimeknights.mantle.network.AbstractPacket;

public class PacketUpdateSavedPage extends AbstractPacket {
   private String pageName;

   public PacketUpdateSavedPage() {
   }

   public PacketUpdateSavedPage(String pageName) {
      this.pageName = pageName;
   }

   public IMessage handleClient(NetHandlerPlayClient netHandler) {
      return null;
   }

   public IMessage handleServer(NetHandlerPlayServer netHandler) {
      if (netHandler.player != null && this.pageName != null) {
         EntityPlayer player = netHandler.player;
         ItemStack is = player.getHeldItem(EnumHand.MAIN_HAND);
         if (!is.isEmpty()) {
            BookHelper.writeSavedPage(is, this.pageName);
         }
      }

      return null;
   }

   public void fromBytes(ByteBuf buf) {
      this.pageName = ByteBufUtils.readUTF8String(buf);
   }

   public void toBytes(ByteBuf buf) {
      ByteBufUtils.writeUTF8String(buf, this.pageName);
   }
}
