package net.ilexiconn.llibrary.server.network;

import io.netty.buffer.ByteBuf;
import net.ilexiconn.llibrary.LLibrary;
import net.ilexiconn.llibrary.server.snackbar.Snackbar;
import net.ilexiconn.llibrary.server.snackbar.SnackbarPosition;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class SnackbarMessage extends AbstractMessage<SnackbarMessage> {
   private Snackbar snackbar;

   public SnackbarMessage() {
   }

   public SnackbarMessage(Snackbar snackbar) {
      this.snackbar = snackbar;
   }

   @SideOnly(Side.CLIENT)
   public void onClientReceived(Minecraft client, SnackbarMessage message, EntityPlayer player, MessageContext messageContext) {
      LLibrary.PROXY.showSnackbar(message.snackbar);
   }

   public void onServerReceived(MinecraftServer server, SnackbarMessage message, EntityPlayer player, MessageContext messageContext) {
   }

   public void fromBytes(ByteBuf byteBuf) {
      Snackbar snackbar = Snackbar.create(ByteBufUtils.readUTF8String(byteBuf));
      snackbar.setDuration(byteBuf.readInt());
      snackbar.setColor(byteBuf.readInt());
      snackbar.setPosition(SnackbarPosition.values()[byteBuf.readInt()]);
   }

   public void toBytes(ByteBuf byteBuf) {
      ByteBufUtils.writeUTF8String(byteBuf, this.snackbar.getMessage());
      byteBuf.writeInt(this.snackbar.getDuration());
      byteBuf.writeInt(this.snackbar.getColor());
      byteBuf.writeInt(this.snackbar.getPosition().ordinal());
   }
}
